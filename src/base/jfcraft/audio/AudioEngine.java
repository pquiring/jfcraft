package jfcraft.audio;

/** Audio Engine
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.media.*;

import jfcraft.data.*;
import static jfcraft.audio.Sounds.*;
import static jfcraft.audio.Songs.*;

public class AudioEngine {
  private Music music = new Music();

  public void start() {
    music.start(50, 4);
  }

  public void stop() {
    music.stop();
  }

  private AssetAudio sounds[] = new AssetAudio[127];
  private AssetMusic songs[] = new AssetMusic[127];

  public void registerSound(int idx, AssetAudio audio) {
    sounds[idx] = audio;
    audio.idx = music.soundLoad(audio.wav.samples16, -1, -1, -1, -1, 0, 0);
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
    songs[FUR_ELISE] = Assets.getMusic("FurElise");
  }

  /** Add a sound to play
   *
   * @param idx = sound index
   * @param freq = freq rate (1=normal 2=fast -1=backwards, etc.)
   * @param vol = volume level (0-100)
   */
  public synchronized void addSound(int idx, int freq, int vol) {
    float volL = vol;
    volL /= 100.0f;
    float volR = vol;
    volR /= 100.0f;
    music.soundPlay(sounds[idx].idx, volL, volR, 0);
  }

  public void playMusic(int idx) {
    music.load(songs[idx].song);
    music.playSong(true);
  }

  public void stopMusic() {
    music.stopMusic();
  }
}
