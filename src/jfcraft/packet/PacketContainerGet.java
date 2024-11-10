package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketContainerGet extends Packet {
  public byte b1, b2;

  public PacketContainerGet() {}

  public PacketContainerGet(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      if (client.container == null) {
        Static.log("not in container menu");
        return;
      }
      if (client.hand != null) {
        Static.log("hand not empty");
        return;
      }
      byte idx = b1;
      byte count = b2;
      synchronized(client.container) {
        client.container.get(server, client, idx, count);
      }
    }
  }

  public PacketContainerGet(byte cmd, byte idx, byte count) {
    super(cmd);
    this.b1 = idx;
    this.b2 = count;
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
