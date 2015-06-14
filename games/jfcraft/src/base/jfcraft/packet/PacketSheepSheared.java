package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketSheepSheared extends Packet {
  public int i1;

  public PacketSheepSheared() {}

  public PacketSheepSheared(byte cmd) {
    super(cmd);
  }

  public PacketSheepSheared(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    Sheep e;
    e = (Sheep)client.world.getEntity(uid);
    if (e == null) {
      Static.log("C:Error:SHEEPSHEARED:Entity not found:" + uid);
      return;
    }
    e.hasFur = false;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    return true;
  }
}
