package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftGet extends Packet {
  public byte b1, b2;

  public PacketCraftGet() {}

  public PacketCraftGet(byte cmd) {
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
      if (client.hand != null) {
        Static.log("but hand is not empty");
        return;
      }
      int cc = client.craft[idx].count;
      if (count > cc) {
        Static.log(":count > cc");
        return;
      }
      client.hand = new Item();
      client.hand.copy(client.craft[idx], count);
      if (count == cc) {
        client.craft[idx].clear();
      } else {
        client.craft[idx].count = (byte)(cc - count);
      }
      client.serverTransport.setCraftItem(idx, client.craft[idx]);
      client.serverTransport.setHand(client.hand);
    }
    server.updateCrafted(client);
  }

  public PacketCraftGet(byte cmd, byte b1, byte b2) {
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
