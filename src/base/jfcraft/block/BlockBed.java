package jfcraft.block;

/** Bed
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import jfcraft.entity.WorldItem;
import jfcraft.item.Item;

public class BlockBed extends BlockBase {
  private static GLModel model_head, model_feet;
  private static int VAR_FEET = 0;
  private static int VAR_HEAD = 1;
  public BlockBed(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isDir = true;
    isDirXZ = true;
    isComplex = true;
    isSolid = false;
    canUse = true;
    cantGive = true;  //must give item
    resetBoxes(Type.BOTH);
    addBox(0, 0, 0, 15, 7, 15,Type.BOTH);
    model_head = Assets.getModel("bed_head").model;
    model_feet = Assets.getModel("bed_feet").model;
  }

  public void getIDs(World world) {
    super.getIDs(world);
    dropID = Items.BED_ITEM;
  }

  //"oak_planks" 0
  //"bed_feet_end" 1
  //"bed_head_end" 2
  //"bed_feet_side" 3
  //"bed_head_side" 4
  //"bed_feet_top" 5
  //"bed_head_top" 6
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    if (data.var[X] == 1) {
      //head end
      buildBuffers(model_head.getObject("BOTTOM"), buf, data, textures[0]);
      buildBuffers(model_head.getObject("SIDES"), buf, data, textures[4]);
      buildBuffers(model_head.getObject("END"), buf, data, textures[2]);
      buildBuffers(model_head.getObject("TOP"), buf, data, textures[6]);
    } else {
      //feet end
      buildBuffers(model_feet.getObject("BOTTOM"), buf, data, textures[0]);
      buildBuffers(model_feet.getObject("SIDES"), buf, data, textures[3]);
      buildBuffers(model_feet.getObject("END"), buf, data, textures[1]);
      buildBuffers(model_feet.getObject("TOP"), buf, data, textures[5]);
    }
  }

  public boolean place(Client client, Coords c) {
    Coords c1 = c.clone();
    c1.otherSide();
    Coords c2 = c1.clone();
    c2.adjacentBlock();
    c2.otherSide();
    int dir1 = c1.dir_xz;
    int dir2 = c2.dir_xz;
    int bits1 = Chunk.makeBits(dir1, VAR_FEET);
    int bits2 = Chunk.makeBits(dir2, VAR_HEAD);
    if (c2.chunk == null) {Static.log("BlockBed.place():Error:next chunk not found"); return false;}
    boolean placed = c1.chunk.setBlocksIfEmpty(c1, id, bits1, c2, id, bits2);
    if (!placed) return false;
    Static.server.broadcastSetBlock(c1.chunk.dim,c1.x,c1.y,c1.z,id,Chunk.makeBits(dir1, 0));
    Static.server.broadcastSetBlock(c2.chunk.dim,c2.x,c2.y,c2.z,id,Chunk.makeBits(dir2, 1));
    return true;
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    Coords c1 = c.clone();
    Chunk chunk1 = c1.chunk;
    Chunk chunk2 = chunk1;
    int bits = c1.chunk.getBits(c1.gx, c1.gy, c1.gz);
    c1.dir = Chunk.getDir(bits);
    Coords c2 = c1.clone();
    c2.adjacentBlock();
    chunk1.clearBlock(c1.gx, c1.gy, c1.gz);
    Static.server.broadcastClearBlock(chunk1.dim,c1.x,c1.y,c1.z);
    chunk2.clearBlock(c2.gx, c2.gy, c2.gz);
    Static.server.broadcastClearBlock(chunk2.dim,c2.x,c2.y,c2.z);
    if (doDrop) {
      Item items[] = drop(c, 0);
      for(int a=0;a<items.length;a++) {
        Item item = items[a];
        WorldItem.create(item, c.chunk.dim, c.x + 0.5f, c.y, c.z + 0.5f, c.chunk, -1);
      }
    }
  }

  public void useBlock(Client client, Coords c) {
    int time = Static.server.world.time;
    if (time > 5000 && time < 21000) {
      client.serverTransport.sendMsg("Can only sleep at night");
      return;
    }
    client.serverTransport.enterMenu(Client.BED);
    client.menu = Client.BED;
  }
}
