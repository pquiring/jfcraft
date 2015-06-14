package jfcraft.entity;

/**
 *
 * @author pquiring
 */

import jfcraft.data.*;

public abstract class BlockEntity extends CreatureBase {
  public int gx, gy, gz;  //chunk position of block entity (Piston, Chest, etc.)

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeInt(gx);
    buffer.writeInt(gy);
    buffer.writeInt(gz);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    gx = buffer.readInt();
    gy = buffer.readInt();
    gz = buffer.readInt();
    return true;
  }
}
