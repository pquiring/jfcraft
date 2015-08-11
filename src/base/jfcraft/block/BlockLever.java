package jfcraft.block;

/** Lever
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class BlockLever extends BlockBase {
  public BlockLever(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    resetBoxes(Type.BOTH);
    addBox(5, 0, 5, 11, 6, 11,Type.SELECTION);
    isRedstone = true;
    canUse = true;
    renderAsItem = true;
  }

  public SubTexture getTexture(RenderData data) {
    return textures[1];
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    Coords c = new Coords();
    c.setPos(data.x + data.chunk.cx * 16, data.y, data.z + data.chunk.cz * 16);
    Lever lever = (Lever)data.chunk.findBlockEntity(Entities.LEVER, c);
    if (lever == null) {
      Static.log("Lever:Error:Can't find object");
      return;
    }
    RenderData data2 = new RenderData();
    ExtraRedstone er = (ExtraRedstone)data.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er != null) {
      data2.active = er.active;
    } else {
      Static.log("Lever:Error:Can't find Extra data@" + c.gx +","+ c.gy +","+ c.gz);
    }
    data2.crack = data.crack;
    lever.buildBuffers(lever.getDest(), data2);
    lever.needCopyBuffers = true;
  }

  public boolean place(Client client, Coords c) {
    World world = Static.server.world;
    Lever lever = new Lever();
    lever.init(world);
    lever.dim = c.chunk.dim;
    lever.pos.x = ((float)c.x) + 0.5f;
    lever.pos.y = ((float)c.y) + 0.09375f;  //1.5 px
    lever.pos.z = ((float)c.z) + 0.5f;
    lever.gx = c.gx;
    lever.gy = c.gy;
    lever.gz = c.gz;
    lever.ang.x = c.getXAngleA();
    lever.ang.y = c.getYAngle();
    lever.ang.z = c.getZAngleA();
    lever.uid = world.generateUID();
    c.chunk.addEntity(lever);
    world.addEntity(lever);
    Static.server.broadcastEntitySpawn(lever);
    return super.place(client, c);
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    //find and remove entity
    EntityBase e = c.chunk.findBlockEntity(Entities.LEVER, c);
    if (e != null) {
      c.chunk.delEntity(e);
      Static.server.world.delEntity(e.uid);
      Static.server.broadcastEntityDespawn(e);
    }
  }

  public void useBlock(Client client, Coords c) {
    World world = Static.server.world;
    synchronized(c.chunk.lock) {
      ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
      if (er == null) {
        Static.log("Lever:Error:Can't find Extra data");
        return;
      }
      if (er.cnt > 0) return;  //too fast
      er.active = !er.active;
      er.cnt = 10;
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
      c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
    }
    //activate surrounding blocks
    world.powerChanged(c.chunk.dim, c.x,c.y,c.z);
    c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
  }
  public int getPowerLevel(Coords c, Coords from) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("Lever:Error:Can't find Extra data");
      return 0;
    }
    return er.active ? 16 : 0;
  }
  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er.cnt > 0) {
      er.cnt--;
    }
    if (er.cnt == 0) {
      chunk.delTick(tick);
    }
  }
}
