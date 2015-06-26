package jfcraft.packet;

/** Packet with 2 Ints & 1 Boolean
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketRiding extends Packet {
  public int i1, i2;
  public boolean mount;

  public PacketRiding() {}

  public PacketRiding(byte cmd) {
    super(cmd);
  }

  public PacketRiding(byte cmd, int vehicle,int occupant,boolean mount) {
    super(cmd);
    this.i1 = vehicle;
    this.i2 = occupant;
    this.mount = mount;
  }

  //process on client side
  public void process(Client client) {
    VehicleBase v = (VehicleBase)client.world.getEntity(i1);
    if (v == null) {
      Static.log("Error:PacketRiding:VehicleBase not found");
      return;
    }
    CreatureBase o;
    if (i2 == client.player.uid) {
      o = client.player;
    } else {
      o = (CreatureBase)client.world.getEntity(i2);
    }
    if (o == null) {
      Static.log("Error:PacketRiding:CreatureBase not found");
      return;
    }
//    Static.log("Riding:" + v + "," + o + ":" + mount);
    if (mount) {
      v.occupant = o;
      o.vehicle = v;
      o.pos.x = v.pos.x;
      o.pos.y = v.pos.y;
      o.pos.z = v.pos.z;
    } else {
      v.occupant = null;
      o.vehicle = null;
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    buffer.writeBoolean(mount);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    mount = buffer.readBoolean();
    return true;
  }
}
