package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketInvExchange extends Packet {
  public byte b1;

  public PacketInvExchange() {}

  public PacketInvExchange(byte cmd) {
    super(cmd);
  }

  public PacketInvExchange(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    synchronized(client.lock) {
      if (client.hand == null) {
        Static.log("invx but hand empty");
        return;
      }
      if (client.player.items[idx].count == 0) {
        Static.log("invx but item empty");
        return;
      }
      Item tmp = client.hand;
      client.hand = client.player.items[idx];
      client.player.items[idx] = tmp;
      client.serverTransport.setInvItem(idx, client.player.items[idx]);
      client.serverTransport.setHand(client.hand);
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
