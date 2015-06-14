package jfcraft.data;

/** Tick coords
 *
 * @author pquiring
 *
 * Created : Apr 15, 2014
 */

public class Tick implements SerialClass, SerialCreator {
  public short x,y,z;  //chunk coords (gx,gy,gz)
  public boolean isBlocks2;  //extra plane
  public byte t1;  //misc timer

  public Coords toWorldCoords(Chunk chunk, Coords c) {
    float fx = x + chunk.cx * 16.0f;
    float fy = y;
    float fz = z + chunk.cz * 16.0f;
    c.chunk = chunk;
    c.cx = (int)Math.floor(fx / 16.0f);
    c.cz = (int)Math.floor(fz / 16.0f);
    c.gx = x % 16;
    if (x < 0) c.gx = 15 - c.gx;
    c.gy = y;
    c.gz = z % 16;
    if (z < 0) c.gz = 15 - c.gz;
    c.x = (int)fx;
    c.y = (int)fy;
    c.z = (int)fz;
    return c;
  }

  public String toString() {
    return "Tick:" + x + "," + y + "," + z;
  }

  public static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeShort(x);
    buffer.writeShort(y);
    buffer.writeShort(z);
    buffer.writeBoolean(isBlocks2);
    buffer.writeByte(t1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    x = buffer.readShort();
    y = buffer.readShort();
    z = buffer.readShort();
    isBlocks2 = buffer.readBoolean();
    t1 = buffer.readByte();
    return true;
  }

  @Override
  public SerialClass create(SerialBuffer buffer) {
    return new Tick();
  }
}
