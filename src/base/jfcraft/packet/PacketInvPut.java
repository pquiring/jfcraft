package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketInvPut extends Packet {
  public byte b1, b2;

  public PacketInvPut() {}

  public PacketInvPut(byte cmd) {
    super(cmd);
  }

  public PacketInvPut(byte cmd, byte b1, byte b2) {
    super(cmd);
    this.b1 = b1;
    this.b2 = b2;
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
        Static.log("invput but hand is empty");
        return;
      }
      if (count > client.hand.count) {
        Static.log("invput count > hand.count");
        return;
      }
      ItemBase itembase = Static.items.items[client.hand.id];
      if (itembase.isDamaged) {
        if (client.player.items[idx].count != 0) {
          Static.log("invput slot not empty");
          return;
        }
        client.player.items[idx] = client.hand;
        client.hand = null;
        client.serverTransport.setInvItem(idx, client.player.items[idx]);
      } else {
        int max = Static.items.items[client.hand.id].maxStack;
        int cc = client.player.items[idx].count;
        if (cc > 0) {
          if (!client.player.items[idx].equals(client.hand)) {
            Static.log("items not same");
            return;
          }
          if (cc + count > max) {
            count = (byte)(max - cc);
            if (count == 0) return;  //inv slot full (not an error)
          }
          client.player.items[idx].count += count;
        } else {
          if (count > max) {
            count = (byte)max;
          }
          client.player.items[idx].copy(client.hand, count);
        }
        client.serverTransport.setInvItem(idx, client.player.items[idx]);
        client.hand.count -= count;
        if (client.hand.count == 0) {
          client.hand = null;
        }
      }
      client.serverTransport.setHand(client.hand);
    }
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
