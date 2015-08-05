package jfcraft.server;

/** VoIP Server (PBX)
 *
 * Code borrowed from jPBXLite
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.voip.*;

import jfcraft.data.*;

public class VoIPServer implements SIPServerInterface, VoIPEventHandler {

  private SIPServer ss;

  public void start() {
    Static.log("Starting VoIP:SIP Server on UDP port 5060");
    if (ss != null) stop();
    Static.log("Setting VoIP:RTP UDP port range 33000-33999");
    RTP.setPortRange(33000, 33999);
    ss = new SIPServer();
    ss.init(5060, this, SIP.Transport.UDP);
  }

  public void stop() {
    if (ss != null) {
      ss.uninit();
      ss = null;
    }
  }

  private int localport = 33000;
  private int getlocalport() {
    int ret = localport;
    localport+=2;
    return ret;
  }

  /* Conference Code */

  private void addMember(VoIPCallDetails cd) {
    Static.log("Conf:" + cd.dialed + ":add member:" + cd.user);
    VoIPConference.Member member = new VoIPConference.Member();
    cd.confmember = member;
    member.cd = cd;
    int siz;
    synchronized(VoIPConference.lock) {
      //check conf list for cd.dialed
      Vector<VoIPConference.Member> memberList = VoIPConference.list.get(cd.dialed);
      if (memberList == null) {
        //create new list
        memberList = new Vector<VoIPConference.Member>();
        VoIPConference.list.put(cd.dialed, memberList);
      }
      member.memberList = memberList;
      memberList.add(member);
      //add new member to all members jitter idxs buffers
      siz = memberList.size();
      for(int a=0;a<siz;a++) {
        member = memberList.get(a);
        if (member.idxs == null) {
          member.idxs = new int[siz];
        } else {
          member.idxs = Arrays.copyOf(member.idxs, siz);
        }
      }
    }
    Static.log("Conf:" + cd.dialed + ":add member:" + cd.user + ":size=" + siz);
  }

  private void delMember(VoIPCallDetails cd) {
    Static.log("Conf:" + cd.dialed + ":del member:" + cd.user);
    if (cd.confmember.dropped) {
      Static.log("Conf:" + cd.dialed + ":del member:" + cd.user + ":already done");
      return;  //already done
    }
    cd.confmember.dropped = true;
    VoIPConference.Member member;
    int actcnt = 0;
    synchronized(VoIPConference.lock) {
      Vector<VoIPConference.Member> memberList = VoIPConference.list.get(cd.dialed);
      int idx = memberList.indexOf(cd.confmember);
      if (idx == -1) {
        Static.log("Conf:del member:failed:idx==-1");
        return;
      }
      memberList.remove(cd.confmember);
      int size = memberList.size();
      for(int a=0;a<size;a++) {
        member = memberList.get(a);
        if (!member.dropped) {
          actcnt++;
        }
        if (member.idxs == null) continue;
        member.idxs = JF.copyOfExcluding(member.idxs, idx);
      }
      if (actcnt == 0) {
        //last member dropped
        VoIPConference.list.remove(cd.dialed);
        Static.log("Conf:" + cd.dialed + ":del member:last member dropped");
        return;
      }
    }
    Static.log("Conf:" + cd.dialed + ":del member:" + cd.user + ":actcnt=" + actcnt);
  }

  //SIPServerInterface

  public CallDetailsServer createCallDetailsServer() {
    return new VoIPCallDetails();
  }

  public String getPassword(String user) {
    return "12345";  //bad password
  }

  public String getTrunkRegister(String ip) {
    return null;
  }

  public void onRegister(String user, int expires, String ip, int port) {
  }

  public void onInvite(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
    if (!cd.dialed.equals("1234")) {
      ss.reply(cd, 404, "NOT FOUND", null, false, true);
      return;
    }
    if (cd.invited) {
      //reINVITE
      SDP.Stream astream = cd.src.sdp.getFirstAudioStream();
      cd.audioRelay.change_src(astream);
      cd.pbxsrc.sdp.getFirstAudioStream().codecs = astream.codecs;
      cd.sip.buildsdp(cd, cd.pbxsrc);
      ss.reply(cd, 200, "OK", null, true, true);
      return;
    }
    //start audio with client and send success
    cd.pbxsrc.to = cd.src.to.clone();
    cd.pbxsrc.from = cd.src.from.clone();
    if (cd.audioRelay == null) {
      cd.audioRelay = new VoIPRTPRelay();
      cd.audioRelay.init();
    }
    cd.audioRelay.init(cd, this);
    cd.pbxsrc.host = cd.src.host;
    cd.pbxsrc.sdp = new SDP();
    cd.pbxsrc.sdp.ip = cd.localhost;
    SDP.Stream astream = cd.pbxsrc.sdp.addStream(SDP.Type.audio);
    astream.codecs = cd.src.sdp.getFirstAudioStream().codecs;
    astream.port = cd.audioRelay.getPort_src();
    cd.sip.buildsdp(cd, cd.pbxsrc);
    cd.invited = true;
    cd.connected = true;
    cd.pbxsrc.to = SIP.replacetag(cd.pbxsrc.to, SIP.generatetag());  //assign tag
    ss.reply(cd, 200, "OK", null, true, true);
    //IVR.start(CallDetailsPBX cd, SQL sql);
    cd.audioRelay.setRawMode(false);
    SDP.Stream stream = cd.src.sdp.getFirstAudioStream();
//    if (api.getExtension(cd.fromnumber) != null) {
      //NAT src
      stream.port = -1;
//    }
    cd.audioRelay.start_src(stream);
    addMember(cd);
  }

  public void onCancel(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  public void onError(CallDetailsServer _cd, int errCode, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  public void onBye(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
    if (!src) return;
    ss.reply(cd, 200, "OK", null, false, true);
    if (cd.audioRelay != null) {
      cd.audioRelay.uninit();
      cd.audioRelay = null;
    }
    delMember(cd);
  }

  public void onSuccess(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  public void onRinging(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  public void onTrying(CallDetailsServer _cd, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  public void onFeature(CallDetailsServer _cd, String cmd, String data, boolean src) {
    VoIPCallDetails cd = (VoIPCallDetails)_cd;
  }

  //VoIPEventHandler interface

  public void event(VoIPCallDetails cd, int type, char digit, boolean interrupted) {
    Static.log("VoIP Event:" + type + "," + digit);
  }

  public void samples(VoIPCallDetails cd, short[] sam) {
    VoIPConference.Member member, myMember = cd.confmember;
//    Static.log("samples:" + cd.user + ",idx=" + myMember.idx);
    synchronized(VoIPConference.lock) {
      Vector<VoIPConference.Member> memberList = VoIPConference.list.get(cd.dialed);
      //record samples for mixing
      System.arraycopy(sam, 0, myMember.buf[inc(myMember.idx)], 0, 160);
      myMember.idx = inc(myMember.idx);
      //mix samples
      System.arraycopy(VoIPRTPRelay.silence, 0, sam, 0, 160);
      for(int a=0;a<memberList.size();a++) {
        member = memberList.get(a);
        if (member == myMember) continue;  //don't want to hear yourself
        if (myMember.idxs[a] == inc(member.idx)) {
//          Static.log("skip (too slow):" + myMember.cd.user + " from " + member.cd.user);
          myMember.idxs[a] = dec(member.idx);  //reset to head-1
        }
        mix(sam, member.buf[myMember.idxs[a]]);
        myMember.idxs[a] = inc(myMember.idxs[a]);
      }
    }
  }

  /* Increment buffer index */
  private int inc(int in) {
    in++;
    if (in == VoIPConference.bufs) {
      in = 0;
    }
    return in;
  }

  /* Decrement buffer index */
  private int dec(int in) {
    in--;
    if (in == -1) {
      in = VoIPConference.bufs-1;
    }
    return in;
  }

  private void mix(short out[], short in[]) {
    //mix 'in' into 'out'
    for(int a=0;a<160;a++) {
      out[a] += in[a];
    }
  }
}
