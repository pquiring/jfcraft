package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketSetFlags extends Packet {
  public int i1, i2;


  public PacketSetFlags() {}

  public PacketSetFlags(byte cmd) {
    super(cmd);
  }

  public PacketSetFlags(byte cmd, int i1, int i2) {
    super(cmd);
    this.i1 = i1;
    this.i2 = i2;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    EntityBase e;
    e = client.world.getEntity(uid);
    if (e == null) {
      Static.log("Error:PacketSetFlags:Entity not found:" + uid);
      return;
    }
    e.setFlags(i2);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    return true;
  }
}
