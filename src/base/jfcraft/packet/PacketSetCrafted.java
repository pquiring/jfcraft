package jfcraft.packet;

/** Packet with Item
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;

public class PacketSetCrafted extends Packet {
  public Item item;

  public PacketSetCrafted() {}

  public PacketSetCrafted(byte cmd) {
    super(cmd);
  }

  public PacketSetCrafted(byte cmd, Item item) {
    super(cmd);
    this.item = item;
  }

  //process on client side
  public void process(Client client) {
    client.crafted = item;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    if (item == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      item.write(buffer, file);
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    boolean hasItem = buffer.readBoolean();
    if (hasItem) {
      item = new Item();
      item.read(buffer, file);
    }
    return true;
  }
}
