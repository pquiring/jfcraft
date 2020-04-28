package jfcraft.block;

/** Block for wheat.
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.item.Item;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockWheat extends BlockBase {
  private static GLModel model;
  public BlockWheat(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isVar = true;
    resetBoxes(Type.BOTH);
    addBox(0, 0, 0, 16, 4, 16, Type.SELECTION);
    model = Assets.getModel("wheat").model;
    adjustTextureSize(model.getObject("WHEAT"));
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("WHEAT"), buf, data, textures[data.var[X] & varMask]);
  }
  public void rtick(Chunk chunk, int gx, int gy,int gz) {
    int x = chunk.cx * 16 + gx;
    int y = gy;
    int z = chunk.cz * 16 + gz;
    int var = chunk.incVar(gx,gy,gz,7);
    if (var == -1) return;
    Static.server.broadcastSetBlock(chunk.dim,x,y,z,id,Chunk.makeBits(0,var));
  }
  private static Random rnd = new Random();
  public Item[] drop(Coords c, int var) {
    if (var == 7) {
      //full wheat
      return new Item[] {
        new Item(Items.SEEDS, (byte)0, (byte)(rnd.nextInt(2) + 1)),
        new Item(Items.WHEAT_ITEM),
      };
    } else {
      //drop just seeds
      return new Item[] {new Item(Items.SEEDS)};
    }
  }
}
