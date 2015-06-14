package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketContainerPut extends Packet {
  public byte b1, b2;

  public PacketContainerPut() {}

  public PacketContainerPut(byte cmd) {
    super(cmd);
  }

  public PacketContainerPut(byte cmd, byte b1, byte b2) {
    super(cmd);
    this.b1 = b1;
    this.b2 = b2;
  }

  //process on server side
  public void process(Server server, Client client) {
//          Static.log("container put");
    synchronized(client.lock) {
      if (client.container == null) {
        Static.log("client doesn't have container object");
        return;
      }
      synchronized(client.container) {
        byte idx = b1;
        byte count = b2;
        client.container.put(server, client, idx, count);
      }
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
