package jfcraft.packet;

/** Packet with 3 Floats + 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketB2E extends Packet {
  public float f1, f2, f3;
  public int i1;

  public PacketB2E() {}

  public PacketB2E(byte cmd) {
    super(cmd);
  }

  //process on client side
  public void process(Client client) {
    //x,y,z,uid
    float x = f1;
    float y = f2;
    float z = f3;
    int uid = i1;
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
//          Static.log("B2E:" + uid);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Chunk chunk = client.world.chunks.getChunk(client.player.dim,cx,cz);
    synchronized(chunk) {
      char id = chunk.getID(gx, gy, gz);
      int bits = chunk.getBits(gx, gy, gz);
      if (id == 0) {
        Static.log("C:B2E=0:" + cx +"," + cz + ":" + gx + "," + gy + ","+ gz);
        return;
      }
      chunk.clearBlock(gx, gy, gz);
      MovingBlock mb = new MovingBlock();
      mb.dim = client.player.dim;
      mb.uid = uid;
      mb.pos.x = x;
      mb.pos.y = y;
      mb.pos.z = z;
      mb.blockid = id;
      mb.dir = Chunk.getDir(bits);
      mb.blockvar = Chunk.getVar(bits);
      client.world.addEntity(mb);
      chunk.addEntity(mb);
    }
  }

  public PacketB2E(byte cmd, float f1, float f2, float f3, int i1) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.i1 = i1;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeInt(i1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    i1 = buffer.readInt();
    return true;
  }
}
