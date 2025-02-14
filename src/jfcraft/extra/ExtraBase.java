package jfcraft.extra;

/** Used to store extra info for a block.
 *
 * @author pquiring
 */

import jfcraft.data.*;

public abstract class ExtraBase implements SerialClass, Cloneable {
  public byte id;
  public short x,y,z;

  public abstract String getName();

  /** Override in derived extras. */
  public void update(ExtraBase update) {}

  public void convertIDs(char blockIDs[], char itemIDs[]) {}

  private static final byte ver = 0;

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeByte(id);
    buffer.writeShort(x);
    buffer.writeShort(y);
    buffer.writeShort(z);
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    id = buffer.readByte();
    x = buffer.readShort();
    y = buffer.readShort();
    z = buffer.readShort();
    return true;
  }

  public ExtraBase clone() {
    try {
      return (ExtraBase)super.clone();
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }

  public String toString() {
    return "Extra:type=" + id + "@" + x + "," + y + "," + z;
  }
}
