package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketInvGet extends Packet {
  public byte b1, b2;

  public PacketInvGet() {}

  public PacketInvGet(byte cmd) {
    super(cmd);
  }

  public PacketInvGet(byte cmd, byte b1, byte b2) {
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
      if (client.hand != null) {
        Static.log("invget but hand is not empty");
        return;
      }
      int cc = client.player.items[idx].count;
      if (count > cc) {
        Static.log("invget count > cc");
        return;
      }
      client.hand = new Item();
      client.hand.copy(client.player.items[idx], count);
      if (count == cc) {
        client.player.items[idx].clear();
      } else {
        client.player.items[idx].count = (byte)(cc - count);
      }
      client.serverTransport.setInvItem(idx, client.player.items[idx]);
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
