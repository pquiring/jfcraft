package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketDespawn extends Packet {
  public int i1;

  public PacketDespawn() {}

  public PacketDespawn(byte cmd) {
    super(cmd);
  }

  public PacketDespawn(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    //NOTE : player does not get despawn for itself (see Server.broadcastEntityDespawn)
    int uid = i1;
    EntityBase e = client.world.getEntity(uid);
    if (e == null) {
      Static.log("Despawn not found:" + uid);
      return;
    }
    Chunk chunk = e.getChunk();
    if (chunk != null) {
      chunk.delEntity(e);
    }
    client.world.delEntity(e.uid);
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
