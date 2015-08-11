package jfcraft.packet;

/** Packet with 3 Floats + 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketMoveBlock extends Packet {
  public float f1, f2, f3;
  public int i1;

  public PacketMoveBlock() {}

  public PacketMoveBlock(byte cmd) {
    super(cmd);
  }

  public PacketMoveBlock(byte cmd, float f1, float f2, float f3, int i1) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    MovingBlock mb = (MovingBlock)client.world.getEntity(i1);
    if (mb == null) return;
    Chunk chunk1 = mb.getChunk();
    mb.pos.x = f1;
    mb.pos.y = f2;
    mb.pos.z = f3;
    Chunk chunk2 = mb.getChunk();
    if (chunk1 != chunk2) {
      chunk1.delEntity(mb);
      chunk2.addEntity(mb);
    }
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
