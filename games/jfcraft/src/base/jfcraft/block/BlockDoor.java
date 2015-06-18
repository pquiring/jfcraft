package jfcraft.block;

/** Doors
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.ArrayList;

import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockDoor extends BlockBase {
  private static GLModel model_upper, model_lower;

  public BlockDoor(String id, String names[], String images[]) {
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
    model_upper = Assets.getModel("door_upper").model;
    model_lower = Assets.getModel("door_lower").model;
  }

  public static final int VAR_UPPER = 0x8;  //placed in var

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    ExtraRedstone er = (ExtraRedstone)data.chunk.getExtra((int)data.x, (int)data.y, (int)data.z, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockDoor.buildBuffers():Error:Can not find extra data");
      return;
    }
    SubTexture st;
    GLObject obj;

    if ((data.var[X] & VAR_UPPER) == VAR_UPPER) {
      //upper
      st = textures[0];  //TODO : data.var[X]];
      obj = model_upper.getObject("DOOR_UPPER");
    } else {
      //lower
      st = textures[1];  //TODO : 6 + data.var[X]];
      obj = model_lower.getObject("DOOR_LOWER");
    }

    int dir = data.dir[X];
    if (er.active) {
      switch (dir) {
        case N: data.dir[X] = W; break;
        case E: data.dir[X] = S; break;
        case S: data.dir[X] = E; break;
        case W: data.dir[X] = N; break;
      }
    }
    switch (data.dir[X]) {
      case E: data.rotate = -90; data.translate_pst = new float[] {0.8125f,0,0}; break;
      case W: data.rotate = -90; data.translate_pst = new float[] {-0.8125f,0,0}; break;
    }
    buildBuffers(obj, buf, data, st);
    data.rotate = 90;
    data.translate_pst = null;
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockDoor.getBoxes():Error:Can not find Redstone data");
      return super.getBoxes(c, type);  //avoid NPE
    }
    ArrayList<Box> list = new ArrayList<Box>();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    if (er.active) {
      switch (dir) {
        case N: dir = W; break;
        case E: dir = S; break;
        case S: dir = E; break;
        case W: dir = N; break;
      }
    }
    switch (dir) {
      case N: list.add(new Box( 0, 0, 0, 16,16, 3)); break;
      case E: list.add(new Box(14, 0, 0, 16,16,16)); break;
      case S: list.add(new Box( 0, 0,14, 16,16,16)); break;
      case W: list.add(new Box( 0, 0, 0,  3,16,16)); break;
    }
    return list;
  }
  public boolean place(Client client, Coords c) {
    Coords c2 = c.clone();
    c2.y++;
    c2.gy++;
    if (c.dir_xz < N || c.dir_xz > W) {
      Static.log("Door:Can not place:xzdir invalid");
      return false;
    }
    synchronized(c.chunk) {
      if (c.chunk.getID(c.gx, c.gy, c.gz) != 0) return false;
      if (c.chunk.getID(c2.gx, c2.gy, c2.gz) != 0) return false;
      ExtraRedstone er1 = new ExtraRedstone(c.gx, c.gy, c.gz);
      c.chunk.addExtra(er1);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er1, true);
      ExtraRedstone er2 = new ExtraRedstone(c2.gx, c2.gy, c2.gz);
      c2.chunk.addExtra(er2);
      Static.server.broadcastExtra(c2.chunk.dim, c2.x, c2.y, c2.z, er2, true);
      int bits1 = Chunk.makeBits(c.dir_xz,c.var);
      c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits1);
      Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits1);
      int bits2 = Chunk.makeBits(c.dir_xz,c.var | VAR_UPPER);
      c2.chunk.setBlock(c2.gx,c2.gy,c2.gz,id,bits2);
      Static.server.broadcastSetBlock(c2.chunk.dim,c2.x,c2.y,c2.z,id,bits2);
    }
    return true;
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    super.destroy(client, c, doDrop);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      c.y--;
      c.gy--;
    } else {
      c.y++;
      c.gy++;
    }
    c.chunk.clearBlock(c.gx,c.gy,c.gz);
    Static.server.broadcastClearBlock(c.chunk.dim,c.x,c.y,c.z);
    c.chunk.delExtra(c, Extras.REDSTONE);
    Static.server.broadcastDelExtra(c.chunk.dim, c.x, c.y, c.z, Extras.REDSTONE, true);
  }
  public void activate(Client client, Coords c) {
//    Static.log("door activate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      return;
    }
    if (er.active) {
      return;  //already active
    }
    er.active = true;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      bits &= 0xff - VAR_UPPER;
      c.y--;
      c.gy--;
    } else {
      bits |= VAR_UPPER;
      c.y++;
      c.gy++;
    }
    er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.active) {
      return;  //already active
    }
    er.active = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
  }
  public void deactivate(Client client, Coords c) {
//    Static.log("door deactivate:" + c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active) {
      return;  //already deactive
    }
    er.active = false;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      bits &= 0xff - VAR_UPPER;
      c.y--;
      c.gy--;
    } else {
      bits |= VAR_UPPER;
      c.y++;
      c.gy++;
    }
    er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active) {
      return;  //already deactive
    }
    er.active = false;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
  }

  public void powerOn(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.powered) {
      return;  //already powered
    }
    er.powered = true;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      bits &= 0xff - VAR_UPPER;
      c.y--;
      c.gy--;
    } else {
      bits |= VAR_UPPER;
      c.y++;
      c.gy++;
    }
    er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.powered) {
      return;  //already powered
    }
    er.powered = true;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    activate(client, c);
  }
  public void powerOff(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.powered) {
      return;  //already powered
    }
    er.powered = false;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    Static.server.broadcastSound(c.chunk.dim, c.x, c.y, c.z, Sounds.SOUND_DOOR, 1);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      bits &= 0xff - VAR_UPPER;
      c.y--;
      c.gy--;
    } else {
      bits |= VAR_UPPER;
      c.y++;
      c.gy++;
    }
    er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.powered) {
      return;  //already powered
    }
    er.powered = false;
    Static.server.broadcastExtra(c.chunk.dim,c.x,c.y,c.z,er, true);
    deactivate(client, c);
  }

  public void useBlock(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("Door:Error:Can not find extra data");
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
