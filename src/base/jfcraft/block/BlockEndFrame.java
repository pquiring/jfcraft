package jfcraft.block;

/** End Frame Block
 *
 * Note : Each frame MUST have a dir facing inward.
 * The dir does not effect the look of the block though.
 *
 * @author pquiring
 */

import javaforce.gl.GLModel;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockEndFrame extends BlockBase {
  private static GLModel model;
  public BlockEndFrame(String id, String names[], String images[]) {
    super(id, names, images);
    isDir = true;
    isDirXZ = true;
    isComplex = true;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,13,16,Type.BOTH);
    model = Assets.getModel("end_frame").model;
  }
  private static final int VAR_ENDER_EYE = 1;
  //textures = "endframe_top", "endframe_side", "end_stone", "endframe_eye"
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("TOP"), buf, data, textures[0]);
    buildBuffers(model.getObject("SIDES"), buf, data, textures[1]);
    buildBuffers(model.getObject("BOTTOM"), buf, data, textures[2]);
    if (data.var[X] == VAR_ENDER_EYE) {
      //add ender pearl on top
      buildBuffers(model.getObject("EYE"), buf, data, textures[3]);
    }
  }
  public boolean useTool(Client client, Coords c) {
    Static.log("useTool on End Frame");
    Item item = client.player.items[client.player.activeSlot];
    if (item.id != Items.ENDER_EYE) return super.useTool(client, c);
    Static.log("useTool on End Frame:" + (int)item.id);
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    int var = Chunk.getVar(bits);
    if (var != 0) return super.useTool(client, c);
    var = VAR_ENDER_EYE;
    bits = Chunk.makeBits(dir, var);
    c.chunk.setBits(c.gx, c.gy, c.gz, bits);
    Static.server.broadcastSetBlock(c.chunk.dim, c.x, c.y, c.z, id, bits);
    //use item in player inventory
    item.count--;
    if (item.count == 0) {
      item.clear();
    }
    client.serverTransport.setInvItem((byte)client.player.activeSlot, item);
    //create portal if all 12 are done
    int cnt = 0;
    for(int dx = -5;dx<=5;dx++) {
      for(int dz = -5;dz<=5;dz++) {
        if (c.chunk.getBlock(c.gx + dx, c.gy, c.gz + dz) == id) {
          if (Chunk.getVar(c.chunk.getBits(c.gx + dx, c.gy, c.gz + dz)) == VAR_ENDER_EYE) {
            cnt++;
          } else {
            Static.log("no eye@" + (c.gx + dx) + "," + c.gy + "," + (c.gz + dz));
            return true;  //no eye of ender in this one
          }
        }
      }
    }
    if (cnt != 12) {
      Static.log("eye count=" + cnt);
      return true;
    }
    //create ender portal
    int fx = 0, fz = 0;
    float cx = 0, cz = 0;  //center
    for(int dx = -5;dx<=5;dx++) {
      for(int dz = -5;dz<=5;dz++) {
        if (c.chunk.getBlock(c.gx + dx, c.gy, c.gz + dz) == id) {
          bits = c.chunk.getBits(c.gx + dx, c.gy, c.gz + dz);
          dir = Chunk.getDir(bits);
          switch (dir) {
            case N: fx =  0; fz = -1; cz = c.z + dz - 2; break;
            case E: fx =  1; fz =  0; break;
            case S: fx =  0; fz =  1; break;
            case W: fx = -1; fz =  0; cx = c.x + dx - 2; break;
            default:
              Static.log("Error:BlockEndFrame:Found EndFrame without dir");
              continue;
          }
          for(int a=1;a<=3;a++) {
            if (c.chunk.getBlock(c.gx + dx + fx * a, c.gy, c.gz + dz + fz * a) == Blocks.END_PORTAL) break;
            c.chunk.setBlock(c.gx + dx + fx * a, c.gy, c.gz + dz + fz * a, Blocks.END_PORTAL, 0);
            Static.server.broadcastSetBlock(c.chunk.dim, c.x + dx + fx * a, c.y, c.z + dz + fz * a, Blocks.END_PORTAL, 0);
          }
        }
      }
    }
    //create end portal entity
    Static.log("Create EndPortal@" + cx + "," + c.y + "," + cz);
    EndPortal ep = new EndPortal();
    ep.setDiameter(3.0f);
    ep.init(Static.server.world);
    ep.uid = Static.server.world.generateUID();
    ep.pos.x = cx + 0.5f;
    ep.pos.y = c.y;
    ep.pos.z = cz + 0.5f;
    ep.gx = c.gx;
    ep.gy = c.gy;
    ep.gz = c.gz;
    c.chunk.addEntity(ep);
    Static.server.world.addEntity(ep);
    Static.server.broadcastEntitySpawn(ep);
    return true;
  }
}
