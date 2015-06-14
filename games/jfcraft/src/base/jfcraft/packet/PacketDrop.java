package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketDrop extends Packet {

  public PacketDrop() {}

  public PacketDrop(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    Item item = client.player.items[client.activeSlot];
    if (item.count == 0) return;
    Item drop = (Item)item.clone();
    drop.count = 1;
    client.dropItem(drop);
    item.count--;
    if (item.count == 0) {
      item.clear();
    }
    client.serverTransport.setInvItem(client.activeSlot, item);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    return true;
  }
}
