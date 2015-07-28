package jfcraft.packet;

/** Packet with 3 Floats
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketMoveBack extends Packet {
  public float f1, f2, f3;

  public PacketMoveBack() {}

  public PacketMoveBack(byte cmd) {
    super(cmd);
  }

  public PacketMoveBack(byte cmd, float f1, float f2, float f3) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
  }

  //process on client side
  public void process(Client client) {
    //TODO : need to sync with tick()
    synchronized(Static.renderLock) {
      client.player.pos.x = f1;
      client.player.pos.y = f2;
      client.player.pos.z = f3;
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    return true;
  }
}
