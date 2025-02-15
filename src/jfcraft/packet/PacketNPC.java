package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketNPC extends Packet {
  public int i1;

  public PacketNPC() {}

  public PacketNPC(byte cmd) {
    super(cmd);
  }

  public PacketNPC(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    Static.log("NPC Talk:" + uid);
    CreatureBase e;
    e = (CreatureBase)client.world.getEntity(uid);
    if (e == null) {
      Static.log("NPC not found:" + uid);
      return;
    }
    client.npc = (NPC)e;
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
