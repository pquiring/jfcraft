package jfcraft.audio;

/** Media Reader
 *
 * Currently reads .mp3 files.
 *
 * @author pquiring
 */

import java.io.*;

import javaforce.*;
import javaforce.media.*;
import javaforce.voip.*;

public class MediaReader implements MediaIO {

  private JFArrayShort buffer = new JFArrayShort();
  private InputStream is;

  public int read(MediaCoder mc, byte[] bytes) {
    try {
      return is.read(bytes);
    } catch (Exception e) {
      return 0;
    }
  }

  public int write(MediaCoder mc, byte[] bytes) {
    return 0;
  }

  public long seek(MediaCoder mc, long pos, int from) {
    return 0;
  }

  public short[] read(InputStream is) {
    this.is = is;
    MediaInput media = new MediaInput();
    media.open(this);
    MediaAudioDecoder decoder = media.createAudioDecoder(1, 44100);
    while (true) {
      Packet packet = media.readPacket();
      if (packet == null || packet.length == 0) break;
      short[] samples = decoder.decode(packet);
      if (samples != null) {
        buffer.append(samples);
      }
    }
    media.close();
    return buffer.toArray();
  }
}
