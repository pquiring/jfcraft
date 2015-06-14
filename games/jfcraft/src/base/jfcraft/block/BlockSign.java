package jfcraft.block;

/** Sign
 *
 * @author pquiring
 *
 * Created : Jun 9, 2015
 */

import java.util.ArrayList;
import javaforce.gl.*;
import static jfcraft.block.BlockBase.boxListEmpty;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import jfcraft.entity.WorldItem;
import jfcraft.item.Item;

public class BlockSign extends BlockBase {
  private static GLModel model;
  private static ArrayList<Box> lb = new ArrayList<Box>();
  private static ArrayList<Box> ln = new ArrayList<Box>();
  private static ArrayList<Box> le = new ArrayList<Box>();
  private static ArrayList<Box> ls = new ArrayList<Box>();
  private static ArrayList<Box> lw = new ArrayList<Box>();
  public BlockSign(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDirFace = true;
    isDirXZ = true;  //only if dir != B (needed so Faces.rotate() rotates properly)
    dropName = "SIGN_ITEM";
    cantGive = true;  //must give item
    isSupported = true;
    resetBoxes(Type.ENTITY);
    if (model == null) {
      model = Assets.getModel("sign").model;
      lb.add(new Box( 4, 0, 4, 12,16,12));
      ln.add(new Box( 0, 3, 0, 16,14, 2));
      le.add(new Box(14, 3, 0, 16,14,16));
      ls.add(new Box( 0, 3,14, 16,14,16));
      lw.add(new Box( 0, 3, 0,  2,14,16));
    }
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    if (data.chunk == null) {
      Static.log("BlockSign.buildBuffer() as item???");
      return;
    }
    ExtraSign er = (ExtraSign)data.chunk.getExtra((int)data.x, (int)data.y, (int)data.z, Extras.SIGN);
    if (er == null) {
      Static.log("BlockSign.buildBuffer() : can not find extra data");
      er = new ExtraSign();
    }
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    int dir = data.dir[X];
    if (dir == B) {
      data.yrotate = true;
      data.rotate = er.dir;
      buildBuffers(model.getObject("SIGN"), buf, data, textures[0]);
      buildBuffers(model.getObject("POST"), buf, data, textures[0]);
      data.translate_pre = new float[] {0,0.25f,Static._1_16 + Static._1_32};
      addText(dest, er.txt, data);
    } else {
      float y = -0.25f;
      switch(dir) {
        case N:
          data.translate_pst = new float[] {0,y,-Static._1_16 * 7f};
          break;
        case E:
          data.translate_pst = new float[] {Static._1_16 * 7f,y,0};
          break;
        case S:
          data.translate_pst = new float[] {0,y,Static._1_16 * 7f};
          break;
        case W:
          data.translate_pst = new float[] {-Static._1_16 * 7f,y,0};
          break;
      }
      buildBuffers(model.getObject("SIGN"), buf, data, textures[0]);
      data.translate_pst[1] = 0;
      switch(dir) {
        case N:
          data.translate_pst[2] += Static._1_16 + Static._1_32;
          break;
        case E:
          data.translate_pst[0] -= Static._1_16 + Static._1_32;
          break;
        case S:
          data.translate_pst[2] -= Static._1_16 + Static._1_32;
          break;
        case W:
          data.translate_pst[0] += Static._1_16 + Static._1_32;
          break;
      }
      addText(dest, er.txt, data);
    }
    data.resetRotate();
  }

  public boolean place(Client client, Coords c) {
    if (!canPlace(c)) return false;
    int dir = 0;
    dir = c.dir;
    if (dir == A) return false;
    int var = 0;
    int bits = Chunk.makeBits(dir, var);
    ExtraSign extra = new ExtraSign(c.gx,c.gy,c.gz);
    if (dir == B) {
      extra.dir = client.player.ang.y;
    }
    c.chunk.addExtra(extra);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, extra, false);
    c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits);
    client.sign = extra;
    client.chunk = c.chunk;
    client.menu = Client.SIGN;
    client.serverTransport.enterMenu(client.menu);
    return true;
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    int bits = c.chunk.getBits(c.gx,c.gy,c.gz);
    Item items[] = null;
    if (doDrop) {
      items = drop(c, isVar ? Chunk.getVar(bits) : 0);
    }
    if (c.block.isBlocks2) {
      c.chunk.clearBlock2(c.gx,c.gy,c.gz);
      Static.server.broadcastClearBlock2(c.chunk.dim,c.x,c.y,c.z);
    } else {
      c.chunk.clearBlock(c.gx,c.gy,c.gz);
      Static.server.broadcastClearBlock(c.chunk.dim,c.x,c.y,c.z);
    }
    if (doDrop) {
      for(int a=0;a<items.length;a++) {
        Item item = items[a];
        if (item.id == 0) continue;
        WorldItem.create(item, c.chunk.dim, c.x + 0.5f, c.y, c.z + 0.5f, c.chunk, -1);
      }
    }
    if (c.block != null && c.block.isRedstone) {
      c.chunk.delExtra(c, Extras.REDSTONE);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, null, true);
      Static.server.world.powerChanged(c.chunk.dim,c.x,c.y,c.z);
    }
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    if (type == Type.ENTITY) return boxListEmpty;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch(dir) {
      case B: return lb;
      case N: return ln;
      case E: return le;
      case S: return ls;
      case W: return lw;
    }
    return null;
  }
}
