package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftExchange extends Packet {
  public byte b1;

  public PacketCraftExchange() {}

  public PacketCraftExchange(byte cmd) {
    super(cmd);
  }

  public PacketCraftExchange(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    synchronized(client.lock) {
      if (client.hand == null) {
        Static.log(":but hand empty");
        return;
      }
      if (client.craft[idx].count == 0) {
        Static.log("but item empty");
        return;
      }
      Item tmp = client.hand;
      client.hand = client.craft[idx];
      client.craft[idx] = tmp;
      client.serverTransport.setInvItem(idx, client.craft[idx]);
      client.serverTransport.setHand(client.hand);
    }
    server.updateCrafted(client);
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
