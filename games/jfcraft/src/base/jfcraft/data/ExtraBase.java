package jfcraft.data;

/** Used to store extra info for a block.
 *
 * @author pquiring
 */

public abstract class ExtraBase implements SerialClass {
  public byte id;
  public short x,y,z;

  public abstract String getName();

  public Class getIDClass() {
    return Extras.class;
  }

  /** Override in derived extras. */
  public void update(ExtraBase update) {}

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeByte(id);
    buffer.writeShort(x);
    buffer.writeShort(y);
    buffer.writeShort(z);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    id = buffer.readByte();
    x = buffer.readShort();
    y = buffer.readShort();
    z = buffer.readShort();
    return true;
  }

  public String toString() {
    return "Extra:type=" + id + "@" + x + "," + y + "," + z;
  }
}
