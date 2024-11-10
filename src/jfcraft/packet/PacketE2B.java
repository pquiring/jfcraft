package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketE2B extends Packet {
  public int i1;

  public PacketE2B() {}

  public PacketE2B(byte cmd) {
    super(cmd);
  }

  public PacketE2B(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    MovingBlock mb = (MovingBlock)client.world.getEntity(uid);
//          Static.log("E2B:" + uid);
    if (mb == null) return;
    int x = (int)mb.pos.x;
    int y = (int)mb.pos.y;
    int z = (int)mb.pos.z;
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Chunk chunk = client.world.chunks.getChunk(client.player.dim,cx,cz);
    chunk.setBlock(gx, gy, gz, mb.blockid, Chunk.makeBits(mb.dir, mb.blockvar));
    chunk.delEntity(mb);
    client.world.delEntity(uid);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    return true;
  }
}
