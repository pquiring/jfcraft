package jfcraft.block;

/** Gates
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

public class BlockGate extends BlockBase {
  private static GLModel model_closed, model_open;

  public BlockGate(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isRedstone = true;
    isVar = true;
    isDir = true;
    isDirXZ = true;
    canUse = true;
    model_closed = Assets.getModel("gate").model;
    model_open = Assets.getModel("gate_open").model;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    boolean opened = false;
    if (data.chunk != null) {
      ExtraRedstone er = (ExtraRedstone)data.chunk.getExtra((int)data.x, (int)data.y, (int)data.z, Extras.REDSTONE);
      if (er == null) {
        Static.log("BlockGate.buildBuffers():Error:Can not find extra data");
        return;
      }
      opened = er.active;
    }
    SubTexture st = getTexture(data);
    GLObject obj;

    if (opened) {
      //opened
      obj = model_open.getObject("GATE");
    } else {
      //lower
      obj = model_closed.getObject("GATE");
    }

    buildBuffers(obj, buf, data, st);
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockGate.getBoxes():Error:Can not find Redstone data");
      return super.getBoxes(c, type);  //avoid NPE
    }
    ArrayList<Box> list = new ArrayList<Box>();
    if (type == Type.ENTITY && er.active) return list;  //gate open
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch (dir) {
      case N:
      case S:
        list.add(new Box( 0, 0, 7, 16,16, 9));
        break;
      case E:
      case W:
        list.add(new Box( 7, 0, 0,  9,16,16));
        break;
    }
    return list;
  }
  public boolean place(Client client, Coords c) {
    if (c.dir_xz < N || c.dir_xz > W) {
      Static.log("Gate:Can not place:xzdir invalid");
      return false;
    }
    synchronized(c.chunk) {
      if (c.chunk.getID(c.gx, c.gy, c.gz) != 0) return false;
      ExtraRedstone er = new ExtraRedstone(c.gx, c.gy, c.gz);
      c.chunk.addExtra(er);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
      int bits = Chunk.makeBits(c.dir_xz,c.var);
      c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits);
      Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits);
    }
    return true;
  }
  public void activate(Client client, Coords c) {
//    Static.log("gate activate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      return;
    }
    if (er.active) {
      return;  //already active
    }
    er.active = true;
    //switch dir if needed
    if (client != null) {
      int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
      int dir = Chunk.getDir(bits);
      int var = Chunk.getVar(bits);
      Coords p = new Coords();
      client.player.getDir(p);
      p.otherSide();
      int pdir = p.dir_xz;
      int newdir = dir;
      if (dir == N && pdir == S) {
        newdir = S;
      }
      else if (dir == S && pdir == N) {
        newdir = N;
      }
      else if (dir == E && pdir == W) {
        newdir = W;
      }
      else if (dir == W && pdir == E) {
        newdir = E;
      }
      //need to switch dir so door opens away from player
      if (newdir != dir) {
        bits = Chunk.makeBits(newdir, var);
        c.chunk.setBlock(c.gx, c.gy, c.gz, id, bits);
        Static.server.broadcastSetBlock(c.chunk.dim, c.x, c.y, c.z, id, bits);
      }
    }
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
  }
  public void deactivate(Client client, Coords c) {
//    Static.log("gate deactivate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active) {
      return;  //already deactive
    }
    er.active = false;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er,true);
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
      Static.log("Gate:Error:Can not find extra data");
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
    World world = Static.world();
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
