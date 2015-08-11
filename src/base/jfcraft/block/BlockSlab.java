package jfcraft.block;

/** Slab
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import jfcraft.item.*;

public class BlockSlab extends BlockBase {
  private static GLModel model;
  public BlockSlab(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isVar = true;
    if (model == null) {
      model = Assets.getModel("slab").model;
    }
  }

  //dir bits
  private static final int AB = 0x1;
  private static final int BB = 0x2;

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    int dir = data.dir[X];
    if (dir == 0) {
      dir = BB;  //inventory
    }
    if ((dir & AB) != 0) {
      buildBuffers(model.getObject("UPPER_AB"), buf, data, textures[0]);
      buildBuffers(model.getObject("UPPER_SIDES"), buf, data, textures[1]);
    }
    if ((dir & BB) != 0) {
      buildBuffers(model.getObject("LOWER_AB"), buf, data, textures[0]);
      buildBuffers(model.getObject("LOWER_SIDES"), buf, data, textures[1]);
    }
  }

  public boolean canPlace(Coords c) {
    BlockBase block1 = Static.blocks.blocks[c.chunk.getID(c.gx,c.gy,c.gz)];
    BlockBase block2 = Static.blocks.blocks[c.chunk.getID2(c.gx,c.gy,c.gz)];
    if (block1.id == id) {
      //can place on itself???
      int dir = Chunk.getDir(c.chunk.getBits(c.gx, c.gy, c.gz));
      float y = c.sy % 1.0f;
      if (y < 0) y = 1.0f - y;
      if (y >= 0.5f) {
        //place upper
        if ((dir & AB) != 0) return false;  //already have upper
      } else {
        //place lower
        if ((dir & BB) != 0) return false;  //already have lower
      }
      return true;
    }
    return block1.canReplace && block2.canReplace;
  }

  public boolean place(Client client, Coords c) {
    int dir = Chunk.getDir(c.chunk.getBits(c.gx, c.gy, c.gz));
    float y = c.sy % 1.0f;
    if (y < 0) y = 1.0f - y;
    if (y >= 0.5f) {
      //place upper
      if ((dir & AB) != 0) return false;  //already have upper
      dir |= AB;
    } else {
      //place lower
      if ((dir & BB) != 0) return false;  //already have lower
      dir |= BB;
    }
    int bits = Chunk.makeBits(dir, c.var);
    c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits);
    return true;
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    int dir = Chunk.getDir(c.bits);
    int x1 = 0, x2 = 16;
    int y1 = 8, y2 = 8;
    int z1 = 0, z2 = 16;
    if ((dir & AB) != 0) {
      y2 = 16;
    }
    if ((dir & BB) != 0) {
      y1 = 0;
    }
    if (dir == 0) {
      Static.log("BlockSlab:Error:dir=0");
    }
    list.add(new Box(x1,y1,z1, x2,y2,z2));
    return list;
  }

  public Item[] drop(Coords c, int var) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    int cnt = 0;
    if ((dir & AB) != 0) cnt++;
    if ((dir & BB) != 0) cnt++;
    return new Item[] {new Item(dropID, var, cnt)};
  }
}
