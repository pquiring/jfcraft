package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftSelect extends Packet {
  public byte b1;

  public PacketCraftSelect() {}

  public PacketCraftSelect(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.villager != null) {
      Static.log("server:villger.trade_index=" + b1);
      client.villager_trade_index = (int)b1;
    }
  }

  public PacketCraftSelect(byte cmd, byte b1) {
    super(cmd);
    this.b1 = b1;
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
