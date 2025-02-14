package jfcraft.data;

/** Serialize buffer
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;
import javaforce.LE;

public class SerialBuffer {
  private int size = 1024 * 1024;  //1MB

  public byte data[] = new byte[size];
  public int pos;
  public int length;  //when reading only

  public int sizeLeft() {
    return size - pos;
  }

  public void reset() {
    pos = 0;
    length = 0;
  }

  public void rewind() {
    length = pos;
    pos = 0;
  }

  public byte[] toArray() {
    return Arrays.copyOf(data, pos);
  }

  private static final byte TRUE = 1;
  private static final byte FALSE = 0;

  private void checkSize(int add) {
    if ((pos+add) >= size) {
      size <<= 1;
      data = Arrays.copyOf(data, size);
      Static.log("SerialBuffer size:" + size);
    }
  }

  public void writeBoolean(boolean state) {
    writeByte(state ? TRUE : FALSE);
  }

  public void writeByte(byte v) {
    checkSize(1);
    data[pos++] = v;
  }

  public void writeBytes(byte v[], int vpos, int len) {
    checkSize(len);
    System.arraycopy(v, vpos, data, pos, len);
    pos += len;
  }

  public void writeBytes(byte v[]) {
    int len = v.length;
    checkSize(len);
    writeBytes(v, 0, len);
  }

  public void writeString(String str) {
    writeInt(str.length());
    writeBytes(str.getBytes());
  }

  public void writeChar(char v) {
    checkSize(2);
    LE.setuint16(data, pos, v);
    pos += 2;
  }

  public void writeChars(char v[]) {
    int cnt = v.length;
    checkSize(cnt*2);
    for(int a=0;a<cnt;a++) {
      LE.setuint16(data, pos, v[a]);
      pos += 2;
    }
  }

  public void writeShort(short v) {
    checkSize(2);
    LE.setuint16(data, pos, v);
    pos += 2;
  }

  public void writeInt(int v) {
    checkSize(4);
    LE.setuint32(data, pos, v);
    pos += 4;
  }

  public void writeLong(long v) {
    checkSize(8);
    LE.setuint64(data, pos, v);
    pos += 8;
  }

  public void writeFloat(float f) {
    checkSize(4);
    LE.setuint32(data, pos, Float.floatToIntBits(f));
    pos += 4;
  }

  public void writeFloats(float f[]) {
    int cnt = f.length;
    checkSize(cnt*4);
    for(int a=0;a<cnt;a++) {
      LE.setuint32(data, pos, Float.floatToIntBits(f[a]));
      pos += 4;
    }
  }

  public boolean peekBoolean() {
    return data[pos] != 0;
  }

  public boolean peekBoolean(int off) {
    return data[pos + off] != 0;
  }

  public byte peekByte() {
    return data[pos];
  }

  public byte peekByte(int off) {
    return data[pos + off];
  }

  public int peekInt() {
    return LE.getuint32(data, pos);
  }

  public int peekInt(int off) {
    return LE.getuint32(data, pos + off);
  }

  public void setByte(byte value) {
    data[pos] = value;
  }

  public void setByte(byte value, int off) {
    data[pos + off] = value;
  }

  public void setInt(int value) {
    LE.setuint32(data, pos, value);
  }

  public void setInt(int value, int off) {
    LE.setuint32(data, pos + off, value);
  }

  public boolean readBoolean() {
    return readByte() == TRUE;
  }

  public byte readByte() {
    return data[pos++];
  }

  public int readBytes(byte v[]) {
    return readBytes(v, 0, v.length);
  }

  public int readBytes(byte v[], int vpos, int len) {
    System.arraycopy(data, pos, v, vpos, len);
    pos += len;
    return len;
  }

  public String readString() {
    int len = readInt();
    byte[] bytes = new byte[len];
    readBytes(bytes);
    return new String(bytes);
  }

  public short readShort() {
    short ret = (short)LE.getuint16(data, pos);
    pos += 2;
    return ret;
  }

  public char readChar() {
    char ret = (char)LE.getuint16(data, pos);
    pos += 2;
    return ret;
  }

  public void readChars(char v[]) {
    int cnt = v.length;
    for(int a=0;a<cnt;a++) {
      v[a] = (char)LE.getuint16(data, pos);
      pos += 2;
    }
  }

  public int readInt() {
    int ret = LE.getuint32(data, pos);
    pos += 4;
    return ret;
  }

  public float readFloat() {
    float f = Float.intBitsToFloat(LE.getuint32(data, pos));
    pos += 4;
    return f;
  }

  public void readFloats(float f[]) {
    int cnt = f.length;
    for(int a=0;a<cnt;a++) {
      f[a] = Float.intBitsToFloat(LE.getuint32(data, pos));
      pos += 4;
    }
  }

  public long readLong() {
    long ret = LE.getuint64(data, pos);
    pos += 8;
    return ret;
  }
/*
  public OutputStream getOutputStream() {
    length = 0;
    pos = 0;
    return new OutputStream() {
      public void write(int b) throws IOException {
        writeByte((byte)b);
      }
      public void write(byte b[], int off, int len) throws IOException {
        writeBytes(b,off,len);
      }
    };
  }

  public InputStream getInputStream() {
    length = pos;
    pos = 0;
    return new InputStream() {
      public int read() throws IOException {
        return ((int)readByte()) & 0xff;
      }
      public int read(byte b[], int off, int len) throws IOException {
        return readBytes(b,off,len);
      }
    };
  }
*/
}
