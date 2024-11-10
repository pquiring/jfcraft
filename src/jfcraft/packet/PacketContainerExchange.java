package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketContainerExchange extends Packet {
  public byte b1;

  public PacketContainerExchange() {}

  public PacketContainerExchange(byte cmd) {
    super(cmd);
  }

  public PacketContainerExchange(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte idx = b1;
    synchronized(client.lock) {
      if (client.container == null) return;
      synchronized(client.container) {
        client.container.exchange(server, client, idx);
      }
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
