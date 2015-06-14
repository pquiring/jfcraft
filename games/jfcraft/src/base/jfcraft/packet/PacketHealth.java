package jfcraft.packet;

/** Packet with 1 Int + 1 Float
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketHealth extends Packet {
  public int i1;
  public float f1;

  public PacketHealth() {}

  public PacketHealth(byte cmd) {
    super(cmd);
  }

  public PacketHealth(byte cmd, int i1, float f1) {
    super(cmd);
    this.i1 = i1;
    this.f1 = f1;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    float health = f1;
    CreatureBase e;
    if (uid == client.player.uid) {
      e = client.player;
      if (health == 0) {
        Static.video.setScreen(Static.screens.screens[Client.DEAD]);
      }
    } else {
      e = (CreatureBase)client.world.getEntity(uid);
    }
    if (e == null) return;
    e.health = health;
    if (e.cracks()) {
      e.buildBuffers(e.getDest(), null);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeFloat(f1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    f1 = buffer.readFloat();
    return true;
  }
}
