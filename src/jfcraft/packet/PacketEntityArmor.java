package jfcraft.packet;

/** Packet (Entity Armor Change)
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.item.*;

public class PacketEntityArmor extends Packet {
  public int i1;
  public byte b1;
  public Item item;

  public PacketEntityArmor() {}

  public PacketEntityArmor(byte cmd) {
    super(cmd);
  }

  public PacketEntityArmor(byte cmd, int uid, Item item, byte idx) {
    super(cmd);
    this.i1 = uid;
    this.item = item;
    this.b1 = idx;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    HumaniodBase e = (HumaniodBase)client.world.getEntity(uid);
    if (e == null) {
      Static.log("Entity not found:" + uid);
      return;
    }
    e.armors[b1] = item;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    item.write(buffer, file);
    buffer.writeByte(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    item = new Item();
    item.read(buffer, file);
    b1 = buffer.readByte();
    return true;
  }
}
