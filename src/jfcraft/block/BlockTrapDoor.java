package jfcraft.block;

/** Trap Door
 *
 * @author pquiring
 *
 */

import java.util.ArrayList;

import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockTrapDoor extends BlockBase {
  private static Model model;
  public BlockTrapDoor(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isRedstone = true;
    isDir = true;  //not isDirXZ but that is used in place()
    canUse = true;
    model = Assets.getModel("trapdoor").model;
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    boolean opened = false;
    if (Static.data.chunk != null) {
      ExtraRedstone er = (ExtraRedstone)Static.data.chunk.getExtra((int)Static.data.x, (int)Static.data.y, (int)Static.data.z, Extras.REDSTONE);
      if (er == null) {
        er = new ExtraRedstone();
      }
      opened = er.active;
    }

    if (opened) {
      Static.data.dir[X] = Static.data.var[X];
    }

    buildBuffers(model.getObject("TRAPDOOR"), buf, textures[0]);
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockTrapDoor.getBoxes():Error:Can not find Redstone data");
      return super.getBoxes(c, type);  //avoid NPE
    }
    ArrayList<Box> list = new ArrayList<Box>();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    if (!er.active) {
      //closed
      int dir = Chunk.getDir(bits);
      switch (dir) {
        case A:
          list.add(new Box( 0,12, 0, 16,16,16));
          break;
        case B:
          list.add(new Box( 0, 0, 0, 16, 4,16));
          break;
      }
    } else {
      //open
      int var = Chunk.getVar(bits);
      switch (var) {
        case N:
          list.add(new Box( 0, 0, 0, 16,16, 4));
          break;
        case S:
          list.add(new Box( 0, 0,12, 16,16,16));
          break;
        case E:
          list.add(new Box(12, 0, 0, 16,16,16));
          break;
        case W:
          list.add(new Box( 0, 0, 0,  4,16,16));
          break;
      }
    }
    return list;
  }

  public boolean place(Client client, Coords c) {
    if (c.dir_xz < N || c.dir_xz > W) {
      Static.log("BlockTrapDoor:Can not place:xzdir invalid");
      return false;
    }
    c.otherSide();
    int var = c.dir_xz;  //var = open dir
    int dir;  //dir = closed dir
    float y = c.sy % 1.0f;
    if (y < 0) y = 1.0f - y;
    if (y >= 0.5f) {
      //place upper
      dir = A;
    } else {
      //place lower
      dir = B;
    }
    int bits = Chunk.makeBits(dir,var);
    if (!c.chunk.setBlockIfEmpty(c.gx,c.gy,c.gz,id,bits)) return false;
    ExtraRedstone er = new ExtraRedstone(c.gx, c.gy, c.gz);
    c.chunk.addExtra(er);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits);
    return true;
  }
  public void activate(Client client, Coords c) {
//    Static.log("trapdoor activate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      return;
    }
    if (er.active) {
      return;  //already active
    }
    er.active = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
  }

  public void deactivate(Client client, Coords c) {
//    Static.log("trapdoor deactivate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active) {
      return;  //already deactive
    }
    er.active = false;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
  }

  public void powerOn(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.powered) {
      return;  //already powered
    }
    er.powered = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    activate(client, c);
  }

  public void powerOff(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.powered) {
      return;  //already powered
    }
    er.powered = false;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    deactivate(client, c);
  }

  public void useBlock(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockTrapDoor:Error:Can not find extra data");
      return;
    }
    if (er.cnt > 0) return;  //too fast
    if (er.active) {
      deactivate(client, c);
    } else {
      activate(client, c);
    }
    er.cnt = 10;
    c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
  }

  public void checkPowered(Coords c) {
//    if (c.upper) return;
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
    pl = world.getPowerLevel(dim,x+1,y,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x+1,y,z-1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y,z-1,c); if (pl > powerLevel) powerLevel = pl;
    if (powerLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if (powerLevel > 0 && !er.powered) {
      c.powerLevel = powerLevel;
      powerOn(null, c);
    }
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
