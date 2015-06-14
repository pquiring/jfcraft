package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketSetActiveSlot extends Packet {
  public byte b1;

  public PacketSetActiveSlot() {}

  public PacketSetActiveSlot(byte cmd) {
    super(cmd);
  }

  public PacketSetActiveSlot(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on client side
  public void process(Client client) {
    byte idx = b1;
    client.activeSlot = idx;
    Item item = client.player.items[client.activeSlot];
    ItemBase itembase = Static.items.items[item.id];
    itembase.animateReset();
    client.itemTextTime = 5 * 20;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    if (idx < 0 || idx > 8) {
      Static.log("invalid slot index");
      return;
    }
    client.activeSlot = idx;
    client.serverTransport.setActiveSlot(client.activeSlot);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b1 = buffer.readByte();
    return true;
  }
}
