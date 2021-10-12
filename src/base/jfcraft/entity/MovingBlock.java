package jfcraft.entity;

/** Moving block (falling or pushed by piston)
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class MovingBlock extends EntityBase {
  public char blockid;
  public int blockvar;
  public int type;  //FALL or PUSH
  public int dir;

  public RenderDest obj;
  public int buffersIdx;
  public TextureMap texture;

  //types
  public static final byte FALL = 0;
  public static final byte PUSH = 1;
  //...

  public MovingBlock() {
    id = Entities.MOVINGBLOCK;
  }

  public String getName() {
    return "MOVINGBLOCK";
  }

  public void initInstance() {
    super.initInstance();
    if (blockid == 0) return;
    obj = new RenderDest(2);  //DEST_NORMAL + DEST_ALPHA
    BlockBase block = Static.blocks.blocks[blockid];
    RenderData data = new RenderData();
    data.dir[X] = dir;
    data.sl[X] = 1.0f;
    data.bl[X] = 0.0f;
    data.crack = -1;
    block.buildBuffers(obj, data);
    obj.getBuffers(block.buffersIdx).copyBuffers();
    buffersIdx = block.buffersIdx;
    texture = block.getTexture(data).texture;
  }
  public void bindTexture() {
    texture.bind();
  }
  public void render() {
    mat.setIdentity();
    mat.addTranslate(pos.x, pos.y, pos.z);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);
    obj.getBuffers(buffersIdx).bindBuffers();
    obj.getBuffers(buffersIdx).render();
  }

  public float getBuoyant() {
    return 0f;
  }

  public void tick() {
    Chunk chunk1, chunk2 = null;
    chunk1 = getChunk();
    switch (dir) {
      case A: pos.y += Static._1_16 * 2.0f; break;
      case B: pos.y -= Static._1_16 * 2.0f; break;
      case N: pos.z -= Static._1_16 * 2.0f; break;
      case E: pos.x += Static._1_16 * 2.0f; break;
      case S: pos.z += Static._1_16 * 2.0f; break;
      case W: pos.x -= Static._1_16 * 2.0f; break;
    }
    Static.server.broadcastMoveBlock(dim, uid, pos.x, pos.y, pos.z);
    chunk2 = getChunk();
    if (chunk1 != chunk2) {
      chunk1.delEntity(this);
      chunk2.addEntity(this);
    }
    if ( (Math.abs(pos.x % 1.0f) < Static._1_32)
      && (Math.abs(pos.y % 1.0f) < Static._1_32)
      && (Math.abs(pos.z % 1.0f) < Static._1_32) )
    {
      int gx = Static.floor(pos.x % 16.0f);
      if (pos.x < 0 && gx != 0) gx = 16 + gx;
      int gy = Static.floor(pos.y);
      int gz = Static.floor(pos.z % 16.0f);
      if (pos.z < 0 && gz != 0) gz = 16 + gz;
      //see if we can stop moving here
      synchronized(chunk2) {
        if (type == PUSH || (type == FALL && (chunk2.getBlock(gx, gy-1, gz) != 0))) {
          //convert back to block
          chunk2.delEntity(this);
          Static.server.world.delEntity(uid);
          chunk2.setBlock(gx, gy, gz, blockid, Chunk.makeBits(dir, blockvar));
          if (chunk2.getBlock2(gx, gy-1, gz) != 0) {
            //replace water, etc.
            chunk2.clearBlock(gx, gy, gz);
          }
          Static.server.broadcastE2B(dim, pos.x,pos.y,pos.z, uid);
          if (type == PUSH) {
            //check if block could now fall
            chunk2.addTick(gx, gy, gz, false);
          }
        }
      }
    }
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeChar(blockid);
    buffer.writeByte((byte)blockvar);
    buffer.writeByte((byte)dir);
    buffer.writeByte((byte)type);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    blockid = buffer.readChar();
    blockvar = buffer.readByte();
    dir = buffer.readByte();
    type = buffer.readByte();
    return true;
  }
}
