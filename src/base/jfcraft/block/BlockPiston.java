package jfcraft.block;

/** Piston
 *
 * @author pquiring
 */

import javaforce.*;

import static jfcraft.audio.Sounds.*;
import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockPiston extends BlockBase {
  private boolean sticky;
  public BlockPiston(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isComplex = true;
    isSolid = false;
    canReplace = false;
    isDir = true;
    isRedstone = true;
    renderAsEntity = true;
  }
  public void getIDs(World world) {
    super.getIDs(world);
    if (!sticky)
      entityID = Entities.PISTON;
    else
      entityID = Entities.PISTON_STICKY;
  }
  //I could use BlockBase.buildBuffers() instead of using an Entity
  //  since it uses stitched textures
  //  but since piston moves entity is better
  public void buildBuffers(RenderDest dest, RenderData data) {
    Coords c = new Coords();
    c.setPos(data.x + data.chunk.cx * 16, data.y, data.z + data.chunk.cz * 16);
    Piston piston = (Piston)data.chunk.findBlockEntity(entityID, c);
    if (piston == null) {
      Static.log("BlockPiston.buildBuffers():Can not find entity");
      return;
    }
    RenderData data2 = new RenderData();
    data2.crack = data.crack;
    data2.var[X] = data.var[X];
    piston.buildBuffers(piston.getDest(), data2);
    piston.needCopyBuffers = true;
  }
  public boolean place(Client client, Coords c) {
    Static.log("piston place:" + c.dir + "," + c.dir_xz + "," + c.dir_y);
    World world = Static.server.world;
    Piston piston = new Piston();
    if (sticky) piston.setSticky();
    piston.init(world);
    piston.dim = c.chunk.dim;
    piston.pos.x = ((float)c.x) + 0.5f;
    piston.pos.y = ((float)c.y) + 0.5f;
    piston.pos.z = ((float)c.z) + 0.5f;
    piston.gx = c.gx;
    piston.gy = c.gy;
    piston.gz = c.gz;
    piston.ang.x = c.getXAngleA();
    piston.ang.z = c.getZAngleA();
    piston.uid = world.generateUID();
    c.chunk.addEntity(piston);
    world.addEntity(piston);
    Static.server.broadcastEntitySpawn(piston);
    return super.place(client, c);
  }
  public BlockBase setSticky() {
    sticky = true;
    return this;
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    //delete barrier in case piston was extended
    Coords n = c.clone();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    n.dir = Chunk.getDir(bits);
    n.adjacentBlock();
    char id1 = n.chunk.getBlock(n.gx, n.gy, n.gz);
    if (id1 == Blocks.BARRIER) {
      n.chunk.clearBlock(n.gx, n.gy, n.gz);
      Static.server.broadcastClearBlock(n.chunk.dim, n.x, n.y, n.z);
    }
    //find and remove entity
    EntityBase e = c.chunk.findBlockEntity(entityID, c);
    if (e != null) {
      c.chunk.delEntity(e);
      Static.server.world.delEntity(e.uid);
      Static.server.broadcastEntityDespawn(e);
    }
    super.destroy(client, c, doDrop);
  }
  public void activate(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.active || er.busy) {
      return;  //already active or busy
    }
    //convert upto 12 blocks into MovingBlock
    World world = Static.server.world;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    c.dir = Chunk.getDir(bits);
    {
      //check if all blocks can be pushed
      Coords n = c.clone();
      for(int a=0;a<13;a++) {
        n.adjacentBlock();
        char id1 = world.getID(n.chunk.dim, n.x, n.y, n.z);
        char id2 = world.getID2(n.chunk.dim, n.x, n.y, n.z);
        if (id2 != 0) {
          Static.log("S:Piston can not extend, object in the way");
          return;
        }
        if (id1 == Blocks.AIR) break;
        if (id1 == Blocks.BARRIER) {
          Static.log("S:Piston can not extend, barrier in the way");
          return;
        }
        if (a == 12) {
          Static.log("S:Piston can not extend, too many blocks");
          return;
        }
        if (!Static.blocks.blocks[id1].isOpaque) {
          Static.log("S:Piston can not extend, block is not opaque");
          return;
        }
      }
    }
    er.active = true;
    c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits);  //change bits
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, SOUND_PISTON, 1);
    {
      //convert all blocks to push
      Coords n = c.clone();
      for(int a=0;a<12;a++) {
        n.adjacentBlock();
        char id1 = world.getID(c.chunk.dim, n.x, n.y, n.z);
        int var = Chunk.getVar(world.getBits(c.chunk.dim, n.x, n.y, n.z));
        if (id1 == Blocks.AIR) break;  //all done

        MovingBlock mb = new MovingBlock();
        mb.init(world);
        mb.dim = n.chunk.dim;
        mb.uid = Static.server.world.generateUID();
        mb.pos.x = n.x;
        mb.pos.y = n.y;
        mb.pos.z = n.z;
        mb.blockid = id1;
        mb.blockvar = var;
        mb.type = MovingBlock.PUSH;
        mb.dir = n.dir;
        n.chunk.addEntity(mb);
        Static.server.world.addEntity(mb);
        n.chunk.clearBlock(n.gx, n.gy, n.gz);
        Static.server.broadcastB2E(n.chunk.dim, n.x, n.y, n.z, mb.uid);
      }
    }
    {
      //place a barrier to prevent something from falling into piston
      Coords n = c.clone();
      n.adjacentBlock();
      n.chunk.setBlock(n.gx, n.gy, n.gz, Blocks.BARRIER, 0);
      Static.server.broadcastSetBlock(n.chunk.dim, n.x, n.y, n.z, Blocks.BARRIER, 0);
    }
    extend(c);
  }
  public void deactivate(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active || er.busy) {
      return;  //already deactive or busy
    }
    er.active = false;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    c.dir = Chunk.getDir(bits);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, SOUND_PISTON, 1);
    //delete barrier
    {
      Coords n = c.clone();
      n.adjacentBlock();
      char id1 = n.chunk.getBlock(n.gx, n.gy, n.gz);
      if (id1 == Blocks.BARRIER) {
        n.chunk.clearBlock(n.gx, n.gy, n.gz);
        Static.server.broadcastClearBlock(n.chunk.dim, n.x, n.y, n.z);
      }
    }
    //retract (if sticky then pull 1 block back)
    retract(c);
    if (sticky) {
      Coords n = c.clone();
      n.adjacentBlock();
      n.adjacentBlock();
      char id1 = n.chunk.getBlock(n.gx, n.gy, n.gz);
      char id2 = n.chunk.getBlock2(n.gx, n.gy, n.gz);
      if (id1 == Blocks.AIR || id2 != Blocks.AIR) {
        Static.log("BlockPiston:Sticky:Nothing to pull back");
        return;
      }

      n.otherSide();
      MovingBlock mb = new MovingBlock();
      mb.init(Static.server.world);
      mb.dim = n.chunk.dim;
      mb.uid = Static.server.world.generateUID();
      mb.pos.x = n.x;
      mb.pos.y = n.y;
      mb.pos.z = n.z;
      mb.blockid = id1;
      mb.type = MovingBlock.PUSH;
      mb.dir = n.dir;
      n.chunk.addEntity(mb);
      Static.server.world.addEntity(mb);
      n.chunk.clearBlock(n.gx, n.gy, n.gz);
      Static.server.broadcastB2E(n.chunk.dim, n.x, n.y, n.z, mb.uid);
    }
  }

  public void checkPowered(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int powerLevel = 0;
    int pl;
    int dim = c.chunk.dim;
    int x = c.x;
    int y = c.y;
    int z = c.z;
    World world = Static.server.world;
    pl = world.getPowerLevel(dim,x,y+1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x+1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z-1,c); if (pl > powerLevel) powerLevel = pl;
    if (powerLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if (powerLevel > 0 && !er.powered) {
      c.powerLevel = powerLevel;
      powerOn(null, c);
    }
  }

  private void extend(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("Error:ExtraRedstone not found for piston");
      return;
    }
    if (er.extra) {
      Static.log("Error:Piston already extended");
      return;
    }
    er.extra = true;
    er.busy = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
  }

  private void retract(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.extra) {
      return;  //already retracted
    }
    er.extra = false;
    er.busy = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
  }

  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("Piston.tick() can not find Extra data");
      return;
    }
    int bits = chunk.getBits(tick.x, tick.y, tick.z);
    int var = Chunk.getVar(bits);
    int dir = Chunk.getDir(bits);
    if (er.active) {
      if (var < 8) {
        var++;
        bits = Chunk.replaceVar(bits, var);
        chunk.setBlock(tick.x, tick.y, tick.z, id, bits);
        Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z, id, bits);
      } else {
        //clear tick
        chunk.delTick(tick);
        er.busy = false;
      }
    } else {
      if (var > 0) {
        var--;
        bits = Chunk.replaceVar(bits, var);
        chunk.setBlock(tick.x, tick.y, tick.z, id, bits);
        Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z, id, bits);
      } else {
        //clear tick
        chunk.delTick(tick);
        er.busy = false;
      }
    }
  }
}
