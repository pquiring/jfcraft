package jfcraft.data;

import java.util.zip.*;

import javaforce.*;

/** Possible java serializer replacement.
 *
 * @author pquiring
 *
 * Created : Jul 25, 2014
 */

public class SerialCoder {
  private SerialBuffer objBuffer = new SerialBuffer();
  private SerialBuffer comBuffer = new SerialBuffer();

  /** Encodes and compressed a command */
  public synchronized byte[] encodeObject(SerialClass obj, boolean file) {
//    Static.log("encode type=" + obj.getClass().getName());
    try {
      //write object to buffer
      objBuffer.reset();
      if (!obj.write(objBuffer, file)) return null;
      //compress buffer
      Deflater compress = new Deflater();
      compress.setInput(objBuffer.data, 0, objBuffer.pos);
      compress.finish();
      int bufsiz;
      comBuffer.reset();
      do {
        bufsiz = compress.deflate(comBuffer.data, comBuffer.pos, comBuffer.sizeLeft());
        if (bufsiz == 0) break;
        comBuffer.pos += bufsiz;
      } while (true);
      if (comBuffer.pos == 0) return null;
      return comBuffer.toArray();
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }

  private SerialBuffer decomBuffer = new SerialBuffer();

  /** Decompress and decode a command */
  public synchronized Object decodeObject(byte data[], SerialCreator creator, boolean file) {
    try {
      //decompress data
      Inflater decompress = new Inflater();
      decompress.setInput(data);
      int bufsiz;
      decomBuffer.reset();
      do {
        bufsiz = decompress.inflate(decomBuffer.data, decomBuffer.pos, decomBuffer.sizeLeft());
        if (bufsiz == 0) break;
        decomBuffer.pos += bufsiz;
      } while (true);
      //read object
      decomBuffer.length = decomBuffer.pos;
      decomBuffer.pos = 0;
      SerialClass obj = (SerialClass)creator.create(decomBuffer);
//      Static.log("decoded packet=" + obj.getClass().getName() + ",data=" + decomBuffer.data[0] + ",thread=" + Thread.currentThread().getName());
      if (!obj.read(decomBuffer, file)) return null;
      return obj;
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }
}
