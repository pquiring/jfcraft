package jfcraft.packet;

/** Packet with 5 Ints
 *
 * @author pquiring
 */

import java.util.*;

import jfcraft.client.Client;
import jfcraft.block.*;
import jfcraft.opengl.*;
import jfcraft.entity.*;
import jfcraft.move.*;
import jfcraft.data.*;

public class PacketClearBlock extends Packet {
  public int i1, i2, i3, i4, i5;
  public boolean particles;

  public PacketClearBlock() {}

  public PacketClearBlock(byte cmd) {
    super(cmd);
  }

  public PacketClearBlock(byte cmd, int i1, int i2, int i3, int i4, int i5, boolean particles) {
    super(cmd);
    this.i1 = i1;
    this.i2 = i2;
    this.i3 = i3;
    this.i4 = i4;
    this.i5 = i5;
    this.particles = particles;
  }

  private static Random r = new Random();

  //process on client side
  public void process(Client client) {
    //i = cx,cz gx,gy,gz
    int cx = i1;
    int cz = i2;
    int gx = i3;
    int gy = i4;
    int gz = i5;
    Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx,cz);
    if (chunk == null) return;
    int bits = chunk.getBits(gx, gy, gz);
    int var = Chunk.getVar(bits);
    char oldid = chunk.clearBlock(gx, gy, gz);
    chunk.delCrack(gx, gy, gz);
    if (particles) {
      //generate particles
      float x = cx * 16f + gx;
      float y = gy;
      float z = cz * 16f + gz;
      BlockBase block = Static.blocks.blocks[oldid];
      SubTexture st = block.getDestroyTexture(var);
      if (st != null) {
        for(int a=0;a<10;a++) {
          Particle p = new Particle(x + r.nextFloat(), y + r.nextFloat(), z + r.nextFloat(), st, false);
          p.init(chunk.world);
          p.createVelocity();
          p.maxAge = r.nextInt(20) + 20;
          p.scale = r.nextFloat() / 20f + 0.1f;
          p.isGreen = block.isGreen;
          p.setMove(new MoveGravity());
          chunk.addEntity(p);
        }
      }
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    buffer.writeInt(i3);
    buffer.writeInt(i4);
    buffer.writeInt(i5);
    buffer.writeBoolean(particles);
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
    particles = buffer.readBoolean();
    return true;
  }
}
