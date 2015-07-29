package jfcraft.packet;

/** Packet with 3 Floats + 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketSound extends Packet {
  public float f1, f2, f3;
  public int i1, i2;

  public PacketSound() {}

  public PacketSound(byte cmd) {
    super(cmd);
  }

  public PacketSound(byte cmd, float f1, float f2, float f3, int i1, int i2) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.i1 = i1;
    this.i2 = i2;
  }

  //process on client side
  public void process(Client client) {
    float x = f1;
    float y = f2;
    float z = f3;
    int idx = i1;
    int freq = i2;
    float dx = x - client.player.pos.x;
    float dy = y - client.player.pos.y;
    float dz = z - client.player.pos.z;
    float dist = (float)Math.sqrt(dx * dx + dy * dy + dz * dz) * 4f;
    int vol = 100 - (int)dist;
    if (vol > 100) vol = 100;
    if (vol < 0) return;
    Static.audio.addSound(idx, freq, vol);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    return true;
  }
}
