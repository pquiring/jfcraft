package jfcraft.block;

/** Slab
 *
 * https://minecraft.fandom.com/wiki/Slab
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
  private static Model model;
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
  private static final int UPPER = 0x1;
  private static final int LOWER = 0x2;

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    int dir = Static.data.dir[X];
    if (dir == 0) {
      dir = LOWER;  //inventory
    }
    int var_ab = Static.data.var[X];
    int var_sides = var_ab;
    if (names.length == 1 && images.length == 2) {
      var_ab = 0;
      var_sides = 1;
    }
    if ((dir & UPPER) != 0) {
      buildBuffers(model.getObject("UPPER_AB"), buf, textures[var_ab]);
      buildBuffers(model.getObject("UPPER_SIDES"), buf, textures[var_sides]);
    }
    if ((dir & LOWER) != 0) {
      buildBuffers(model.getObject("LOWER_AB"), buf, textures[var_ab]);
      buildBuffers(model.getObject("LOWER_SIDES"), buf, textures[var_sides]);
    }
  }

  public boolean canPlace(Coords c) {
    BlockBase block1 = Static.blocks.blocks[c.chunk.getBlock(c.gx,c.gy,c.gz)];
    BlockBase block2 = Static.blocks.blocks[c.chunk.getBlock2(c.gx,c.gy,c.gz)];
    if (block1.id == id) {
      //can place on itself???
      int dir = Chunk.getDir(c.chunk.getBits(c.gx, c.gy, c.gz));
      float y = c.sy % 1.0f;
      if (y < 0) y = 1.0f - y;
      if (y >= 0.5f) {
        //place upper
        if ((dir & UPPER) != 0) return false;  //already have upper
      } else {
        //place lower
        if ((dir & LOWER) != 0) return false;  //already have lower
      }
      return true;
    }
    return block1.canReplace && block2.canReplace;
  }

  public boolean place(Client client, Coords c) {
    BlockBase block1 = Static.blocks.blocks[c.chunk.getBlock(c.gx,c.gy,c.gz)];
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    int var = Chunk.getVar(bits);
    if (block1.id == id) {
      if (var != c.var) return false;
    }
    float y = c.sy % 1.0f;
    if (y < 0) y = 1.0f - y;
    if (y >= 0.5f) {
      //place upper
      if ((dir & UPPER) != 0) return false;  //already have upper
      dir |= UPPER;
    } else {
      //place lower
      if ((dir & LOWER) != 0) return false;  //already have lower
      dir |= LOWER;
    }
    bits = Chunk.makeBits(dir, c.var);
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
    if (dir == 0) {
      Static.log("BlockSlab:Error:dir=0");
      dir = UPPER | LOWER;
    }
    if ((dir & UPPER) != 0) {
      y2 = 16;
    }
    if ((dir & LOWER) != 0) {
      y1 = 0;
    }
    list.add(new Box(x1,y1,z1, x2,y2,z2));
    return list;
  }

  public Item[] drop(Coords c, int var) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    int cnt = 0;
    if ((dir & UPPER) != 0) cnt++;
    if ((dir & LOWER) != 0) cnt++;
    if (cnt == 0) {
      Static.log("BlockSlab:Error:dir=0");
      cnt = 1;
    }
    return new Item[] {new Item(dropID, var, cnt)};
  }
}
