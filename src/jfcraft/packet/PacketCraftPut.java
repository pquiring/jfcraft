package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftPut extends Packet {
  public byte b1, b2;

  public PacketCraftPut() {}

  public PacketCraftPut(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    byte count = b2;
    if (count <= 0 || count > 64) {
      Static.log("invalid count");
      return;
    }
    synchronized(client.lock) {
      if (client.hand == null) {
        Static.log("but hand is empty");
        return;
      }
      if (count > client.hand.count) {
        Static.log("count > hand.count");
        return;
      }
      ItemBase itembase = Static.items.items[client.hand.id];
      if (itembase.isDamaged) {
        if (client.craft[idx].count != 0) {
          Static.log(":not empty");
          return;
        }
        client.craft[idx] = client.hand;
        client.serverTransport.setCraftItem(idx, client.craft[idx]);
        client.hand = null;
      } else {
        int max = Static.items.items[client.hand.id].maxStack;
        int cc = client.craft[idx].count;
        if (cc > 0) {
          if (!client.craft[idx].equals(client.hand)) {
            Static.log("items not same");
            return;
          }
          if (cc + count > max) {
            count = (byte)(max - cc);
            if (count == 0) return;  //inv slot full (not an error)
          }
          client.craft[idx].count += count;
        } else {
          if (count > max) {
            count = (byte)max;
          }
          client.craft[idx].copy(client.hand, count);
        }
        client.serverTransport.setCraftItem(idx, client.craft[idx]);
        client.hand.count -= count;
        if (client.hand.count == 0) {
          client.hand = null;
        }
      }
      client.serverTransport.setHand(client.hand);
    }
    server.updateCrafted(client);
  }

  public PacketCraftPut(byte cmd, byte b1, byte b2) {
    super(cmd);
    this.b1 = b1;
    this.b2 = b2;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(b1);
    buffer.writeByte(b2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b1 = buffer.readByte();
    b2 = buffer.readByte();
    return true;
  }
}
