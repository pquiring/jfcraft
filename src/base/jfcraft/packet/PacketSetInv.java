package jfcraft.packet;

/** Packet with one Byte and Item
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;

public class PacketSetInv extends Packet {
  public byte b1;
  public Item item;

  public PacketSetInv() {}

  public PacketSetInv(byte cmd) {
    super(cmd);
  }

  public PacketSetInv(byte cmd, byte b, Item item) {
    super(cmd);
    this.b1 = b;
    this.item = item;
  }

  //process on client side
  public void process(Client client) {
    int idx = b1;
    client.player.items[idx] = item;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(b1);
    item.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b1 = buffer.readByte();
    item = new Item();
    item.read(buffer, file);
    return true;
  }
}
