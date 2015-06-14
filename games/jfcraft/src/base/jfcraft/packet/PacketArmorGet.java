package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.server.Server;
import jfcraft.data.*;
import jfcraft.item.*;

public class PacketArmorGet extends Packet {
  public byte b1;

  public PacketArmorGet() {}

  public PacketArmorGet(byte cmd) {
    super(cmd);
  }

  public PacketArmorGet(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    synchronized(client.lock) {
      if (client.hand != null) {
        Static.log("but hand is not empty");
        return;
      }
      if (client.player.armors[idx].count == 0) {
        Static.log("slot is empty");
        return;
      }
      client.hand = new Item();
      client.hand.copy(client.player.armors[idx]);
      client.player.armors[idx].clear();
      client.serverTransport.setArmorItem(idx, client.player.armors[idx]);
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
