package jfcraft.packet;

/** Packet with 3 Floats
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketDelExtra extends Packet {
  public float f1, f2, f3;
  public byte b1;

  public PacketDelExtra() {}

  public PacketDelExtra(byte cmd) {
    super(cmd);
  }

  public PacketDelExtra(byte cmd, float f1, float f2, float f3, byte type) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.b1 = type;
  }

  //process on client side
  public void process(Client client) {
    float x = f1;
    float y = f2;
    float z = f3;
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx, cz);
    if (chunk != null) {
      chunk.delExtra(gx, gy, gz, b1);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeByte(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    b1 = buffer.readByte();
    return true;
  }
}
