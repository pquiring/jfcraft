package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketRiding extends Packet {
  public int i1;

  public PacketRiding() {}

  public PacketRiding(byte cmd) {
    super(cmd);
  }

  public PacketRiding(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    if (uid == -1) {
      client.player.vehicle = null;
    } else {
      EntityBase e = client.world.getEntity(uid);
      if (e == null) return;
      e.occupant = client.player;
      client.player.vehicle = e;
      client.player.pos.x = e.pos.x;
      client.player.pos.y = e.pos.y;
      client.player.pos.z = e.pos.z;
    }
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
