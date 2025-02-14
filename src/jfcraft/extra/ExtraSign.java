package jfcraft.extra;

/** Extra sign info
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class ExtraSign extends ExtraBase {
  public String txt[];
  public float dir;  //standing direction if block.dir == B

  public String getName() {
    return "sign";
  }

  public ExtraSign() {
    this.id = Extras.SIGN;
    txt = new String[4];
  }

  public ExtraSign(int x,int y,int z) {
    this.id = Extras.SIGN;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
    txt = new String[4];
    for(int a=0;a<4;a++) {
      txt[a] = "";
    }
  }

  public void update(ExtraBase update) {
    this.txt = ((ExtraSign)update).txt;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    for(int a=0;a<4;a++) {
      String t = txt[a];
      buffer.writeByte((byte)t.length());
      buffer.writeBytes(t.getBytes());
    }
    buffer.writeFloat(dir);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    for(int a=0;a<4;a++) {
      byte len = buffer.readByte();
      byte str[] = new byte[len];
      buffer.readBytes(str);
      txt[a] = new String(str);
    }
    dir = buffer.readFloat();
    return true;
  }
}
