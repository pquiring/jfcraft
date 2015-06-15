package jfcraft.packet;

/** Packet with Chunk
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketChunk extends Packet {
  public Chunk chunk;

  public PacketChunk() {}

  public PacketChunk(byte cmd) {
    super(cmd);
  }

  public PacketChunk(byte cmd, Chunk chunk) {
    super(cmd);
    this.chunk = chunk;
  }

  //process on client side
  public void process(Client client) {
//    Static.log("got Chunk:"+chunk.cx + "," + chunk.cz);
    client.world.chunks.addChunk(chunk);
    client.removeChunkPending(chunk.cx, chunk.cz);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    chunk.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    chunk = new Chunk();
    chunk.read(buffer, file);
    return true;
  }
}
