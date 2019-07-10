package jfcraft.client;

/** VoIP Client
 *
 * Code borrowed from jPhoneLite
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.media.*;
import javaforce.voip.*;

import jfcraft.data.*;

public class VoIPClient implements SIPClientInterface, RTPInterface {
  private SIPClient sip;
  private boolean invited;
  private boolean success;
  private RTP rtp;
  private Timer timer;
  private String callid;

  private AudioOutput output;
  private String outputDevice;
  private String outputDevices[];
  private AudioInput input;
  private String inputDevice;
  private String inputDevices[];

  public String errmsg;

  public void start(String server) {
    RTP.setPortRange(34000, 34999);
    if (!mic_init()) {
      errmsg = "VoIP:Error:Mic init() failed";
      Static.log(errmsg);
      return;
    }
    if (!spk_init()) {
      errmsg = "VoIP:Error:Speaker init() failed";
      Static.log(errmsg);
      return;
    }
    mute = Settings.current.ptt;
    sip = new SIPClient();
    int port = 5061;
    int fail = 0;
    while (!sip.init(server, 5060, port, this, TransportType.UDP)) {
      port++;
      fail++;
      if (fail == 5) {
        errmsg = "VoIP:Error:Failed to init SIP";
        Static.log(errmsg);
        return;
      }
    }
    sip.register(Settings.current.player, Settings.current.player, null, "12345");  //12345 = bad password
  }

  public void stop() {
    if (timer != null) {
      stopTimer();
    }
    if (sip != null) {
      if (callid != null) {
        sip.bye(callid);
        callid = null;
      }
      sip.unregister();
      sip.uninit();
      sip = null;
    }
  }

  public boolean mic_init() {
    input = new AudioInput();
    inputDevices = input.listDevices();
    int idx = -1;
    inputDevice = Settings.current.mic;
    if (inputDevice == null) inputDevice = "<default>";
    for(int a=0;a<inputDevices.length;a++) {
      if (inputDevices[a].equals(inputDevice)) {
        idx = a;
        break;
      }
    }
    if (idx == -1) {
      inputDevice = "<default>";
    }
    return true;
  }

  public boolean mic_start() {
    if (input == null) return false;
    return input.start(1, 44100, 16, 882 * 2, inputDevice);
  }

  public boolean mic_stop() {
    if (input == null) return false;
    return input.stop();
  }

  public boolean mic_read(short samples[]) {
    if (input == null) return false;
    return input.read(samples);
  }

  public boolean spk_init() {
    output = new AudioOutput();
    outputDevices = output.listDevices();
    int idx = -1;
    outputDevice = Settings.current.spk;
    if (outputDevice == null) outputDevice = "<default>";
    for(int a=0;a<outputDevices.length;a++) {
      if (outputDevices[a].equals(outputDevice)) {
        idx = a;
        break;
      }
    }
    if (idx == -1) {
      outputDevice = "<default>";
    }
    return true;
  }

  public boolean spk_start() {
    if (output == null) return false;
    return output.start(1, 44100, 16, 882 * 2, Settings.current.spk);
  }

  public boolean spk_stop() {
    if (output == null) return false;
    return output.stop();
  }

  public boolean spk_write(short samples[]) {
    if (output == null) return false;
    return output.write(samples);
  }

  private SDP getLocalSDPInvite() {
    SDP sdp = new SDP();
    SDP.Stream stream = sdp.addStream(SDP.Type.audio);
    stream.content = "audio1";
    stream.port = rtp.getlocalrtpport();
//    stream.addCodec(RTP.CODEC_G729a);  //CPU intense
    stream.addCodec(RTP.CODEC_G711u);  //standard codec
//    stream.addCodec(RTP.CODEC_G711a);  //European
//    stream.addCodec(RTP.CODEC_G722);  //Higher quality
    return sdp;
  }

  private void startRTPoutbound(SDP sdp) {
    try {
      SDP.Stream astream = sdp.getFirstAudioStream();
      if ( (!astream.hasCodec(RTP.CODEC_G729a))
        && (!astream.hasCodec(RTP.CODEC_G711u))
        && (!astream.hasCodec(RTP.CODEC_G711a))
        && (!astream.hasCodec(RTP.CODEC_G722)) )
      {
        throw new Exception("VoIP:Error:No compatible codec selected");
      }
      if (!rtp.start()) {
        throw new Exception("VoIP:Error:RTP.start() failed");
      }
      if (rtp.createChannel(astream) == null) {
        throw new Exception("VoIP:Error:RTP.createChannel() failed");
      }
      if (!rtp.getDefaultChannel().start()) {
        throw new Exception("VoIP:Error:RTPChannel.start() failed");
      }
    } catch (Exception e) {
      e.printStackTrace();
      sip.bye(callid);
      callid = null;
    }
  }

  private void startTimer() {
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        process();
      }
    }, 20, 20);
  }

  private void stopTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  private short silence[] = new short[882];
  private short mixed[] = new short[882];
  private short indata8[] = new short[160];
  private short indata16[] = new short[320];
  private short outdata[] = new short[882];  //read from mic
  private short data8[] = new short[160];
  private short data16[] = new short[320];

  private int underBufferCount;

  private char dtmfchar = 'x';
  private boolean dtmfend;
  private boolean mute;

  private DTMF dtmf = new DTMF(44100);
  public void setDTMF(char digit) {
    dtmfchar = digit;
  }
  public void clrDTMF() {
    dtmfend = true;
  }

  /** PTT (mute mic) */
  public void setMute(boolean state) {
    mute = state;
  }

  /** These samples hold the last sample of a buffer used to interpolate the next
   * block of samples.
   */

  private short lastSamples[] = new short[48];

  /** Mixes 'in' samples into 'out' samples.
   * Uses linear interpolation if out.length != in.length
   *
   * bufIdx : array index into lastSamples to store last sample used in interpolation
   *
   */

  public void mix(short out[], short in[], int bufIdx) {
    int outLength = out.length;
    int inLength = in.length;
    if (outLength == inLength) {
      //no interpolation
      for (int a = 0; a < outLength; a++) {
        out[a] += in[a];
      }
    } else {
      //linear interpolation
      //there is a one sample delay due to interpolation
      float d = ((float)inLength) / ((float)outLength);
      float p1 = 1.0f;
      float p2 = 0.0f;
      short s1 = lastSamples[bufIdx];
      short s2 = in[0];
      int inIdx = 1;
      int outIdx = 0;
      while (true) {
        out[outIdx++] += (s1 * p1 + s2 * p2);
        if (outIdx == outLength) break;
        p1 -= d;
        p2 += d;
        while (p1 < 0.0f) {
          s1 = s2;
          s2 = in[inIdx++];
          p1 += 1.0f;
          p2 -= 1.0f;
        }
      }
      lastSamples[bufIdx] = s2;
    }
  }

  /** Interpolate 'in' onto 'out' (does not mix) */

  public void interpolate(short out[], short in[], int bufIdx) {
    int outLength = out.length;
    int inLength = in.length;
    if (outLength == inLength) {
      //no interpolation
      for (int a = 0; a < outLength; a++) {
        out[a] = in[a];
      }
    } else {
      //linear interpolation
      //there is a one sample delay due to interpolation
      float d = ((float)inLength) / ((float)outLength);
      float p1 = 1.0f;
      float p2 = 0.0f;
      short s1 = lastSamples[bufIdx];
      short s2 = in[0];
      int inIdx = 1;
      int outIdx = 0;
      while (true) {
        out[outIdx++] = (short)(s1 * p1 + s2 * p2);
        if (outIdx == outLength) break;
        p1 -= d;
        p2 += d;
        while (p1 < 0.0f) {
          s1 = s2;
          s2 = in[inIdx++];
          p1 += 1.0f;
          p2 -= 1.0f;
        }
      }
      lastSamples[bufIdx] = s2;
    }
  }

  //20ms timer (50 per second)
  private void process() {
    if (timer == null) return;
    try {
      //do playback
      System.arraycopy(silence, 0, mixed, 0, 882);
      if (dtmfchar != 'x') mix(mixed, dtmf.getSamples(dtmfchar), 8);  //to hear dtmf locally
      RTPChannel channel = rtp.getDefaultChannel();
      int rate = channel.coder.getSampleRate();
      switch (rate) {
        case 8000:  //G729, G711
          if (channel.getSamples(indata8)) {
            mix(mixed, indata8, 9);
          }
          break;
        case 16000:  //G722
          if (channel.getSamples(indata16)) {
            mix(mixed, indata16, 9);
          }
          break;
      }
//      if (inRinging) mix(mixed, getCallWaiting(), 10);
      spk_write(mixed);
      //do recording
      byte encoded[];
      if (!mic_read(outdata)) {
        underBufferCount++;
        if (underBufferCount > 10) {  //a few is normal
          Static.log("Sound:mic underbuffer");
        }
        System.arraycopy(silence, 0, outdata, 0, 882);
      }

      if (mute) {
        System.arraycopy(silence, 0, outdata, 0, 882);
      }

      encoded = encode(rtp.getDefaultChannel().coder, outdata, 42 + 0);

      if (dtmfend) {
        rtp.getDefaultChannel().writeDTMF(dtmfchar, true);
        dtmfchar = 'x';
        dtmfend = false;
      } else if (dtmfchar != 'x') {
        rtp.getDefaultChannel().writeDTMF(dtmfchar, false);
      } else {
        rtp.getDefaultChannel().writeRTP(encoded,0,encoded.length);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private byte[] encode(Coder coder, short in[], int bufIdx) {
    byte encoded[] = null;
    int rate = coder.getSampleRate();
    switch (rate) {
      case 8000:
        interpolate(data8, in, bufIdx);
        encoded = coder.encode(data8);
        break;
      case 16000:
        interpolate(data16, in, bufIdx);
        encoded = coder.encode(data16);
        break;
    }
    return encoded;
  }

  //SIPClientInterface

  public void onRegister(SIPClient sip, boolean status) {
    if (!status) {
      //register failed?
      errmsg = "VoIP:Error:Register failed";
      Static.log(errmsg);
      return;
    }
    if (!invited) {
      invited = true;
      rtp = new RTP();
      if (!rtp.init(this)) {
        errmsg = "VoIP:Error:RTP init failed";
        Static.log(errmsg);
        return;
      }
      SDP sdp = getLocalSDPInvite();
      sip.invite("1234", sdp);
    }
  }

  public void onTrying(SIPClient sip, String callid) {
  }

  public void onRinging(SIPClient sip, String callid) {
  }

  public void onSuccess(SIPClient sip, String callid, SDP sdp, boolean complete) {
    if (!complete) return; //183?
    if (success) return; //already done
    success = true;
    this.callid = callid;
    startRTPoutbound(sdp);
    if (!mic_start()) {
      errmsg = "VoIP:Error:Mic start() failed";
      Static.log(errmsg);
      return;
    }
    if (!spk_start()) {
      errmsg = "VoIP:Error:Speaker start() failed";
      Static.log(errmsg);
      return;
    }
    startTimer();
  }

  public void onBye(SIPClient sip, String callid) {
    stop();
  }

  public int onInvite(SIPClient sip, String callid, String fromid, String fromnumber, SDP sdp) {
    //no inbound calls please
    return 404;
  }

  public void onCancel(SIPClient sip, String callid, int errCode) {
    stop();
  }

  public void onRefer(SIPClient sip, String callid) {
  }

  public void onNotify(SIPClient sip, String callid, String cmd, String data) {
  }

  public void onAck(SIPClient sip, String callid, SDP sdp) {
  }

  //RTPInterface

  public void rtpSamples(RTPChannel rtp) {
  }

  public void rtpDigit(RTPChannel rtp, char digit) {
  }

  public void rtpPacket(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtcpPacket(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpH263(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpH263_1998(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpH263_2000(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpH264(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpVP8(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpJPEG(RTPChannel rtp, byte[] bytes, int off, int length) {
  }

  public void rtpInactive(RTPChannel rtp) {
  }
}
