package jfcraft.packet;

/** Packet with two Ints
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketChunkRequest extends Packet {
  public int cx, cz;
  public boolean load;

  public PacketChunkRequest() {}

  public PacketChunkRequest(byte cmd) {
    super(cmd);
  }

  public PacketChunkRequest(byte cmd, int cx, int cz, boolean load) {
    super(cmd);
    this.cx = cx;
    this.cz = cz;
    this.load = load;
  }

  //process on server side
  public void process(Server server, Client client) {
    if (load) {
      server.chunkWorker.add(client.player.dim, cx, cz, client.serverTransport);
      client.loadChunk(cx, cz);
    } else {
      client.unloadChunk(cx, cz);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(cx);
    buffer.writeInt(cz);
    buffer.writeBoolean(load);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    cx = buffer.readInt();
    cz = buffer.readInt();
    load = buffer.readBoolean();
    return true;
  }
}
