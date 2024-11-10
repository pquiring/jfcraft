package jfcraft.server;

/** Base class for transports.
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.io.*;
import java.net.*;

import jfcraft.data.*;

public abstract class Transport extends Thread {
  public abstract boolean send(byte data[]);
  public abstract void process(byte data[]);
  public abstract void run();
  public abstract void close();

  public static SerialCoder coder = new SerialCoder();  //used to read/write objects on different threads!!!

  public static byte[] readAll(InputStream in, int len) throws Exception {
    byte ret[] = new byte[len];
    int pos = 0;
    while (pos < len) {
      int read = in.read(ret, pos, len - pos);
      if (read <= 0) {
        return null;
      }
      pos += read;
    }
    return ret;
  }
}
