package jfcraft.packet;

/** Packet with 3 Floats + 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketTeleport2 extends Packet {
  public float f1, f2, f3;
  public int i1;

  public PacketTeleport2() {}

  public PacketTeleport2(byte cmd) {
    super(cmd);
  }

  public PacketTeleport2(byte cmd, float f1, float f2, float f3, int i1) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    Static.log("TELEPORT2");
    client.player.dim = i1;
    client.player.pos.x = f1;
    client.player.pos.y = f2;
    client.player.pos.z = f3;
    client.teleport = false;  //start loading new chunks
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeInt(i1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    i1 = buffer.readInt();
    return true;
  }
}
