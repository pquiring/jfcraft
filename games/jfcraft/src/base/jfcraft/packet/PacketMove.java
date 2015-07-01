package jfcraft.packet;

/** Packet with 6 Floats and 2 Ints
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketMove extends Packet {
  public float f1, f2, f3, f4, f5, f6;
  public int i1, i2;

  public PacketMove() {};

  public PacketMove(byte cmd) {
    super(cmd);
  }

  public PacketMove(byte cmd, float f1, float f2, float f3, float f4, float f5, float f6, int i1, int i2) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.f4 = f4;
    this.f5 = f5;
    this.f6 = f6;
    this.i1 = i1;
    this.i2 = i2;
  }

  //process on client side
  public void process(Client client) {
    int uid = i1;
    EntityBase e = client.world.getEntity(uid);
    if (e == null) {
//      Static.log("C:MOVE:Unknown entity:" + pm.uid);
      return;
    }
    synchronized(Static.renderLock) {  //TODO : need to eliminate this
      Chunk chunk1 = e.getChunk();
      e.pos.x = f1;
      e.pos.y = f2;
      e.pos.z = f3;
      Chunk chunk2 = e.getChunk();
      if (chunk1 != chunk2) {
        if (chunk1 != null) {  //should never be null (???)
          chunk1.delEntity(e);
        } else {
//          Static.log("C:Error:MOVE:chunk1 == null");
        }
        if (chunk2 != null) {
          //entity moved into unknown area
          chunk2.addEntity(e);
        } else {
//          Static.log("C:Error:MOVE:chunk2 == null");
        }
      }
    }
    if (uid != client.uid) {
      e.ang.x = f4;
      e.ang.y = f5;
      e.ang.z = f6;
    }
    e.mode = i2;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeFloat(f4);
    buffer.writeFloat(f5);
    buffer.writeFloat(f6);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    f4 = buffer.readFloat();
    f5 = buffer.readFloat();
    f6 = buffer.readFloat();
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    return true;
  }
}
