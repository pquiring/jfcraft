package jfcraft.audio;

/** Audio Engine
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.media.*;

import jfcraft.data.*;
import static jfcraft.audio.Sounds.*;

public class AudioEngine {
  private AudioOutput output;
  private Timer timer;

  public void start() {
    output = new AudioOutput();
    if (!output.start(1, 44100, 16, 44100 * 2 / 20/* bytes */, null)) {
      Static.log("Error:Failed to start audio system");
      return;
    }
    //prime the output
    output.write(buffer);
    output.write(buffer);
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {public void run() {
      process();
    }}, 50, 50);
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    if (output != null) {
      output.stop();
      output = null;
    }
  }

  private AssetAudio sounds[] = new AssetAudio[127];

  public void registerSound(int idx, AssetAudio audio) {
    sounds[idx] = audio;
  }

  public void registerDefault() {
    registerSound(SOUND_BREAK, Assets.getAudio("break"));
    registerSound(SOUND_STEP, Assets.getAudio("step"));
    registerSound(SOUND_GET, Assets.getAudio("get"));
    registerSound(SOUND_DOOR, Assets.getAudio("door"));
    registerSound(SOUND_PISTON, Assets.getAudio("piston"));
    registerSound(SOUND_COW, Assets.getAudio("cow"));
    registerSound(SOUND_PIG, Assets.getAudio("pig"));
    registerSound(SOUND_SHEEP, Assets.getAudio("sheep"));
    registerSound(SOUND_ZOMBIE, Assets.getAudio("zombie"));
  }

  /** Add a sound to play
   *
   * @param idx = sound index
   * @param freq = freq rate (1=normal 2=fast -1=backwards, etc.)
   * @param vol = volume level (0-100)
   */
  public synchronized void addSound(int idx, int freq, int vol) {
    for(int a=0;a<chs.length;a++) {
      if (chs[a] == null) {
        Channel c = new Channel();
        c.wav = sounds[idx].wav;
        if (c.wav.samples16.length == 0) {
          Static.log("AudioEngine:no samples:" + idx);
          return;
        }
        c.freq = freq;
        c.vol = vol;
        if (freq < 0) {
          c.pos = c.wav.samples16.length-1;
        }
        chs[a] = c;
        return;
      }
    }
    Static.log("AudioEngine:Failed to addSound:" + idx);
  }

  private Channel chs[] = new Channel[16];

  private static final int bufsize = 44100 / 20;
  private short buffer[] = new short[bufsize];

  //50ms timer
  private void process() {
    Arrays.fill(buffer, (short)0);
    for(int a=0;a<chs.length;a++) {
      Channel c = chs[a];
      if (c == null) continue;
      for(int s=0;s<bufsize;s++) {
        int sample = c.wav.samples16[c.pos];
        if (c.vol < 100) {
          sample *= c.vol;
          sample /= 100;
        }
        buffer[s] += (short)sample;
        c.pos += c.freq;
        if ((c.pos < 0) || (c.pos >= c.wav.samples16.length)) {
          chs[a] = null;
          break;
        }
      }
    }
    output.write(buffer);
  }
}
