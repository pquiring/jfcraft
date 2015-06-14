package jfcraft.packet;

/** Packet with two Ints
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketChunkRequest extends Packet {
  public int i1, i2;

  public PacketChunkRequest() {}

  public PacketChunkRequest(byte cmd) {
    super(cmd);
  }

  public PacketChunkRequest(byte cmd, int b1, int b2) {
    super(cmd);
    this.i1 = b1;
    this.i2 = b2;
  }

  //process on server side
  public void process(Server server, Client client) {
    server.chunkWorker.add(Server.CHUNK, client.player.dim, i1, i2, client.serverTransport);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    return true;
  }
}
