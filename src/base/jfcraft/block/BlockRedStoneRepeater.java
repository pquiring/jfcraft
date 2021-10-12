package jfcraft.block;

/** Redstone Repeater
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockRedStoneRepeater extends BlockBase {
  private static Model model, torch;
  public BlockRedStoneRepeater(String id, String[] names, String[] images) {
    super(id, names, images);
    isRedstone = true;
    isDir = true;
    isDirXZ = true;
    isComplex = true;
    isSolid = false;
    canUse = true;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,1,16,Type.BOTH);
    model = Assets.getModel("repeater").model;
    torch = Assets.getModel("torch").model;
  }

  public void getIDs(World world) {
    super.getIDs(world);
    dropID = Items.REDSTONE_REPEATER_ITEM;
  }

  //NOTE : var = lever position
  // BIT_UPPER = locked (todo)

  //textures = repeater_off, repeater_on, stone, redstone_torch_off, redstone_torch_on
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    ExtraRedstone er;
    boolean active;
    if (data.chunk != null) {
      er = (ExtraRedstone)data.chunk.getExtra((int)data.x, (int)data.y, (int)data.z, Extras.REDSTONE);
      if (er == null) return;
      active = er.active;
    } else {
      Static.log("BlockRedStoneRepeater:Error:Block as item?");
      active = false;
    }
    buildBuffers(model.getObject("TOP"), buf, data, textures[active ? 1 : 0]);
    buildBuffers(model.getObject("SIDES"), buf, data, textures[2]);

    data.translate_pre = new float[] {0,-Static._1_16 * 4.0f,-Static._1_16 * 4.0f};
    buildBuffers(torch.getObject("TORCH"), buf, data, textures[active ? 4 : 3]);
    data.translate_pre = new float[] {0,-Static._1_16 * 4.0f,-Static._1_16 * 5.0f + Static._1_16 * (data.var[X] * 2 + 4)};
    buildBuffers(torch.getObject("TORCH"), buf, data, textures[active ? 4 : 3]);
    data.translate_pre = null;
  }

  public void useBlock(Client client, Coords c) {
    //change var
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockRedStoneRepeater.useBlock():Error:Can not find Extra data");
      return;
    }
    if (er.cnt > 0) return;  //too fast
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    var++;
    if (var == 4) var = 0;
    bits = Chunk.replaceVar(bits, var);
    c.chunk.setBits(c.gx, c.gy, c.gz, bits);
    Static.server.broadcastSetBlock(c.chunk.dim, c.x, c.y, c.z, id, bits);
    er.cnt = 10;
    c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
  }

  public int getPowerLevel(Coords c, Coords from) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockRedStoneRepeater.getPowerLevel():Error:Can not find Extra data");
      return 0;
    }
    if (!er.active) return 0;
    //check if only in front
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    Coords n = c.clone();
    n.dir = Chunk.getDir(bits);
    n.adjacentBlock();
    if (n.x != from.x || n.y != from.y || n.z != from.z) {
      return 0;
    }
    return 16;
  }

  private void lock(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.extra) return;
    er.extra = true;
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.log("repeater:locked");
  }

  private void unlock(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.extra) return;
    er.extra = false;
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.log("repeater:unlocked");
  }

  private boolean isLocked(Coords c) {
    Coords n;
    n = c.clone();
    n.leftSide();
    n.adjacentBlock();
    if (Static.server.world.getPowerLevel(c.chunk.dim, n.x, n.y, n.z,c) > 0) return true;
    n = c.clone();
    n.rightSide();
    n.adjacentBlock();
    if (Static.server.world.getPowerLevel(c.chunk.dim, n.x, n.y, n.z,c) > 0) return true;
    return false;
  }

  public void activate(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.active) return;
    er.active = true;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
  }

  public void deactivate(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.active) return;
    er.active = false;
    er.t1 = 0;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
  }

  public void powerOn(Client client, Coords c) {
    if (isLocked(c)) return;  //no change
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.powered) return;
    er.powered = true;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    //do not activate - see tick for delay
  }

  public void powerOff(Client client, Coords c) {
    if (isLocked(c)) return;  //no change
    super.powerOff(client, c);
  }

  public void checkPowered(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int powerLevel;
    int dim = c.chunk.dim;
    Coords n = c.clone();
    n.otherSide();
    n.adjacentBlock();
    World world = Static.server.world;
    powerLevel = world.getPowerLevel(dim,n.x,n.y,n.z,c);
    boolean locked = isLocked(c);
    if (er.active && !locked) {
      unlock(c);
    } else if (!er.active && locked) {
      lock(c);
    }
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
    if (er == null) {
      return;
    }
    if (er.cnt > 0) {
      er.cnt--;
    }
    int bits = chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    if (!er.powered) {
      if (er.cnt == 0) super.tick(chunk, tick);  //delete tick
      return;
    }
    er.t1++;
    if (er.t1 >= (var+1) * 2) {
      activate(null, c);
      if (er.cnt == 0) super.tick(chunk, tick);
    }
  }
  public boolean place(Client client, Coords c) {
    c.otherSide();
    return super.place(client, c);
  }
}
