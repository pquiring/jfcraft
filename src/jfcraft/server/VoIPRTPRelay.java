package jfcraft.server;

import javaforce.*;
import javaforce.voip.*;

import jfcraft.data.*;

/** Relays RTP packets between calling parties or from one party to local service (VM, IVR). */

public class VoIPRTPRelay implements RTPInterface {
  private volatile boolean active_src, active_dst;
  private RTP rtp_src, rtp_dst;

  private VoIPCallDetails cd;
  private short samples[];
  private byte samples8[];
  private VoIPEventHandler eh;
  private String lang = "en";
  private boolean recv_src = true, recv_dst = true;  //can recv from
  private boolean send_src = true, send_dst = true;  //can send to
  private String content;  //name of stream

  public final static short silence[] = new short[160];

  /** Init RTPRelay.  Ports may be -1 for NATing. */

  public boolean init() {
    rtp_src = new RTP();
    rtp_src.init(this);
    rtp_dst = new RTP();
    rtp_dst.init(this);
    return true;
  }

  public boolean init(VoIPCallDetails cd, VoIPEventHandler eh) {
    this.cd = cd;
    this.eh = eh;
    samples = new short[160];
    samples8 = new byte[160*2];
    return true;
  }

  public int getPort_src() {return rtp_src.getlocalrtpport();}

  public int getPort_dst() {return rtp_dst.getlocalrtpport();}

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getContent() {return content;}
  public void setContent(String in) {content = in;}

  /* Raw mode controls if RTPInterface.rtpPacket (if true) or rtpSamples (if false) is called. */
  public void setRawMode(boolean state) {
    rtp_src.setRawMode(state);
    rtp_dst.setRawMode(state);
  }

  public boolean start_src(SDP.Stream stream) {
    if (active_src) return true;
    active_src = true;
    rtp_src.start();
    if (rtp_src.createChannel(stream) == null) return false;
/*
    if (stream.isSecure()) {
      SRTPChannel channel = (SRTPChannel)rtp_src.getDefaultChannel();
      if (stream.keyExchange == SDP.KeyExchange.DTLS) {
        channel.setDTLS(true, RTP.genIceufrag(), RTP.genIcepwd());
      } else if (stream.keyExchange == SDP.KeyExchange.SDP) {
        //TODO!!!
        Static.log("RTPRelay:TODO:SDP Key Exchange");
      } else {
        Static.log("RTPRelay:Error:RTP Channel is secure but key exchange is undefined.");
      }
    }
*/
    rtp_src.getDefaultChannel().start();
    return true;
  }

  public boolean start_dst(SDP.Stream stream) {
    if (active_dst) return true;
    active_dst = true;
    rtp_dst.start();
    if (rtp_dst.createChannel(stream) == null) return false;
/*
    if (stream.isSecure()) {
      SRTPChannel channel = (SRTPChannel)rtp_dst.getDefaultChannel();
      if (stream.keyExchange == SDP.KeyExchange.DTLS) {
        channel.setDTLS(false, RTP.genIceufrag(), RTP.genIcepwd());
      } else if (stream.keyExchange == SDP.KeyExchange.SDP) {
        //TODO!!!
        Static.log("RTPRelay:TODO:SDP Key Exchange");
      } else {
        Static.log("RTPRelay:Error:RTP Channel is secure but key exchange is undefined.");
      }
    }
*/
    rtp_dst.getDefaultChannel().start();
    return true;
  }

  public boolean start(SDP.Stream src, SDP.Stream dst) {
    //NOTE:No codecs are needed since packets are only relayed as is
//    Static.log("RTPRelay:start:src.ice=" + src.sdp.hashCode() + ":dst.ice=" + dst.sdp.hashCode());
    if (active_src)
      change_src(src);
    else
      start_src(src);
    if (active_dst)
      change_dst(dst);
    else
      start_dst(dst);
    return true;
  }

  /** Swap src and dst. */
  public void swap() {
    RTP rtp;
    rtp = rtp_src;
    rtp_src = rtp_dst;
    rtp_dst = rtp;
    boolean sendrecv;
    sendrecv = recv_dst;
    recv_dst = recv_src;
    recv_src = sendrecv;
    sendrecv = send_dst;
    send_dst = send_src;
    send_src = sendrecv;
  }

  /** Swaps the dst RTP in two relays. */
  public static void swap(VoIPRTPRelay r1, VoIPRTPRelay r2) {
    RTP rtp;
    rtp = r1.rtp_dst;
    r1.rtp_dst = r2.rtp_dst;
    r2.rtp_dst = rtp;
    //reset interfaces
    r1.rtp_dst.setInterface(r1);
    r2.rtp_dst.setInterface(r2);
  }

  public void uninit() {
    uninit_src();
    uninit_dst();
  }

  public void uninit_src() {
    if (active_src) {
      active_src = false;
      rtp_src.uninit();
    }
    cleanup();
  }

  public void uninit_dst() {
    if (active_dst) {
      active_dst = false;
      rtp_dst.uninit();
    }
    cleanup();
  }

  public void cleanup() {
  }

  public void change_src(SDP.Stream stream) {
    rtp_src.getDefaultChannel().change(stream);
    recv_src = stream.canRecv();
    send_src = stream.canSend();
  }

  public void change_dst(SDP.Stream stream) {
    rtp_dst.getDefaultChannel().change(stream);
    recv_dst = stream.canRecv();
    send_dst = stream.canSend();
  }

  /* Convert samples to samples8. BE -> LE.*/
  private void short2byte() {
    for (int a = 0; a < 160; a++) {
      samples8[a * 2 + 1] = (byte) (samples[a] >>> 8);
      samples8[a * 2] = (byte) (samples[a] & 0xff);
    }
  }

  /* Convert samples8 to samples. LE -> BE. */
  private void byte2short() {
    for (int a = 0; a < 160; a++) {
      samples[a] = (short) ((((short) (samples8[a * 2 + 1])) << 8) + (((short) (samples8[a * 2])) & 0xff));
    }
  }

  public void rtpSamples(RTPChannel channel) {
    {
      channel.getSamples(samples);
      if (eh != null) {
        eh.samples(cd, samples);
      } else {
        System.arraycopy(silence, 0, samples, 0, 160);
      }
    }
    try {
      byte packet[] = channel.coder.encode(samples);
      channel.writeRTP(packet, 0, packet.length);
//      api.log(cd, "VM : Sent samples, length=" + packet.length);
    } catch (Exception e) {
//      Static.log(e);
    }
  }

  public void rtpDigit(RTPChannel channel, char digit) {
    eh.event(cd, eh.DIGIT, digit, true);
  }

  public void rtcpPacket(RTPChannel channel, byte data[], int off, int len) {
    if (channel.rtp == rtp_src) {
      rtp_dst.getDefaultChannel().writeRTCP(data, off, len);
    } else if (channel.rtp == rtp_dst) {
      rtp_src.getDefaultChannel().writeRTCP(data, off, len);
    }
  }

  public void rtpPacket(RTPChannel channel, byte data[], int off, int len) {
//    Static.log("rtpPacket:" + channel + ",len=" + len);
    try {
      if (channel.rtp == rtp_src) {
        if (!recv_dst) {
//          moh_src.getSamples(samples);
          byte packet[] = rtp_src.getDefaultChannel().coder.encode(samples);
          rtp_src.getDefaultChannel().writeRTP(packet, 0, packet.length);
        }
        if (send_dst) {
//          Static.log("sending to:" + rtp_dst.getDefaultChannel());
          rtp_dst.getDefaultChannel().writeRTP(data, off, len);
        }
      } else if (channel.rtp == rtp_dst) {
        if (!recv_src) {
//          moh_dst.getSamples(samples);
          byte packet[] = rtp_dst.getDefaultChannel().coder.encode(samples);
          rtp_dst.getDefaultChannel().writeRTP(packet, 0, packet.length);
        }
        if (send_src) {
//          Static.log("sending to:" + rtp_src.getDefaultChannel());
          rtp_src.getDefaultChannel().writeRTP(data, off, len);
        }
      } else {
        Static.log("Error: Unknown RTP Packet:" + channel.rtp);
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void rtpInactive(RTPChannel rtp) {}

  public String toString() {
    return "RTPRelay:{src=" + rtp_src + ",dst=" + rtp_dst + "}";
  }

  public void rtpPacket(RTPChannel channel, int codec, byte[] bytes, int offset, int length) {
    switch (codec) {
      case CodecType.RTP: rtpPacket(channel, bytes, offset, length); break;
      case CodecType.RTCP: rtcpPacket(channel, bytes, offset, length); break;
    }
  }
}
