package jfcraft.packet;

/** Packet (WorldItem Set Count)
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.item.*;

public class PacketWorldItemSetCount extends Packet {
  public int i1;
  public byte b1;

  public PacketWorldItemSetCount() {}

  public PacketWorldItemSetCount(byte cmd) {
    super(cmd);
  }

  public PacketWorldItemSetCount(byte cmd, int uid, byte count) {
    super(cmd);
    this.i1 = uid;
    this.b1 = count;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    WorldItem e = (WorldItem)client.world.getEntity(uid);
    if (e == null) {
      Static.log("Entity not found:" + uid);
      return;
    }
    Static.log("worlditem.count=" + b1);
    e.item.count = b1;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeByte(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    b1 = buffer.readByte();
    return true;
  }
}
