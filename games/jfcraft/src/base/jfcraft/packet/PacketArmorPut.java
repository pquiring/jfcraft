package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.server.Server;
import jfcraft.data.*;
import jfcraft.item.*;

public class PacketArmorPut extends Packet {
  public byte b1;

  public PacketArmorPut() {}

  public PacketArmorPut(byte cmd) {
    super(cmd);
  }

  public PacketArmorPut(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    synchronized(client.lock) {
      if (client.hand == null) {
        Static.log("armor put but hand is empty");
        return;
      }
      ItemBase handitem = Static.items.items[client.hand.id];
      if (!handitem.isArmor) {
        Static.log("armorput hand item is not armor");
        return;
      }
      if (client.player.armors[idx].count != 0) {
        Static.log("not empty");
        return;
      }
      if (handitem.armor != idx) {
        Static.log("wrong armor uid");
        return;
      }
      client.player.armors[idx] = client.hand;
      client.serverTransport.setArmorItem(idx, client.player.armors[idx]);
      client.hand = null;
      client.serverTransport.setHand(null);
    }
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
