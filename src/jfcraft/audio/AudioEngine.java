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
    if (audio == null) return;
    sounds[idx] = audio;
    audio.idx = music.soundLoad(audio.samples, -1, -1, -1, -1, 0, 0);
  }

  public void registerDefault() {
    registerSound(SOUND_BREAK, Assets.getAudio("dig/stone1"));
    registerSound(SOUND_STEP, Assets.getAudio("step/grass1"));
    registerSound(SOUND_GET, Assets.getAudio("get"));
    registerSound(SOUND_DOOR, Assets.getAudio("door"));
    registerSound(SOUND_PISTON, Assets.getAudio("tile/piston/in"));
    registerSound(SOUND_COW, Assets.getAudio("mob/cow/say1"));
    registerSound(SOUND_PIG, Assets.getAudio("mob/pig/say1"));
    registerSound(SOUND_SHEEP, Assets.getAudio("mob/sheep/say1"));
    registerSound(SOUND_ZOMBIE, Assets.getAudio("zombie"));
    registerSound(SOUND_INTRO, Assets.getAudio("intro"));
    songs[FUR_ELISE] = Assets.getMusic("FurElise");
  }

  /** Add a sound to play
   *
   * @param idx = sound index
   * @param freq = freq rate (1=normal 2=fast -1=backwards, etc.)
   * @param vol = volume level (0-100)
   * @return channel number (ch)
   */
  public synchronized int soundPlay(int idx, int freq, int vol) {
    float volL = vol;
    volL /= 100.0f;
    float volR = vol;
    volR /= 100.0f;
    return music.soundPlay(sounds[idx].idx, volL, volR, 0);
  }

  public void channelStop(int ch) {
    music.channelStop(ch);
  }

  public void playMusic(int idx, int vol) {
    if (!Static.optionMusic) return;
    music.load(songs[idx].song);
    float volL = vol;
    volL /= 100.0f;
    float volR = vol;
    volR /= 100.0f;
    music.setMasterMusicVolume(volL, volR);
    music.playSong(true);
  }

  public void stopMusic() {
    music.stopMusic();
  }

  public boolean soundExists(int idx) {
    return sounds[idx] != null;
  }
}
