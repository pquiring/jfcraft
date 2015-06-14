package jfcraft.packet;

/** Packet with 6 Ints
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketSetBlock extends Packet {
  public int i1, i2, i3, i4, i5, i6;

  public PacketSetBlock() {}

  public PacketSetBlock(byte cmd) {
    super(cmd);
  }

  public PacketSetBlock(byte cmd, int i1, int i2, int i3, int i4, int i5, int i6) {
    super(cmd);
    this.i1 = i1;
    this.i2 = i2;
    this.i3 = i3;
    this.i4 = i4;
    this.i5 = i5;
    this.i6 = i6;
  }

  //process on client side
  public void process(Client client) {
    //i = cx,cz gx,gy,gz [id bits]
    int cx = i1;
    int cz = i2;
    int gx = i3;
    int gy = i4;
    int gz = i5;
    char id = (char)(i6 >>> 16);
    int bits = i6 & 0xffff;
    Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx,cz);
    if (chunk == null) return;
    chunk.setBlock(gx, gy, gz, id, bits);
    chunk.delCrack(gx, gy, gz);
    client.chunkWorker.add(Client.LIGHT, chunk, gx,gy,gz);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    buffer.writeInt(i3);
    buffer.writeInt(i4);
    buffer.writeInt(i5);
    buffer.writeInt(i6);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    i3 = buffer.readInt();
    i4 = buffer.readInt();
    i5 = buffer.readInt();
    i6 = buffer.readInt();
    return true;
  }
}
