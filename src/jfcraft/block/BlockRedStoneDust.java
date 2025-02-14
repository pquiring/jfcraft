package jfcraft.block;

/** Redstone dust
 *
 * Depth bits are used for power level (0-15)
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.extra.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockRedStoneDust extends BlockBase {
  private static Model model;
  public BlockRedStoneDust(String id, String[] names, String[] images) {
    super(id, names, images);
    isRedstone = true;
    isRed = true;
    isComplex = true;
    isSolid = false;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,1,16,Type.SELECTION);
    model = Assets.getModel("facexz").model;
  }

  public void getIDs(World world) {
    super.getIDs(world);
    dropID = Items.RED_STONE_ITEM;
  }

  //textures = cross, line
  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("FACE"), buf, textures[0]);
  }

  public int getPowerLevel(Coords c, Coords from) {
    return Chunk.getVar(c.bits);
  }

  public void powerOn(Client client, Coords c) {
    if (c.powerLevel <= 1) {powerOff(client, c); return;}
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int currentLevel = Chunk.getVar(bits);
    if (er.active && (currentLevel == c.powerLevel-1)) return;
    er.active = true;
    bits = Chunk.replaceVar(bits, c.powerLevel-1);
    c.chunk.setBits(c.gx, c.gy, c.gz, bits);
    Static.server.broadcastSetBlock(c.chunk.dim, c.x, c.y, c.z, id, bits);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
  }

  public void powerOff(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    if (!er.active) return;
    er.active = false;
    bits = Chunk.replaceVar(bits, 0);
    c.chunk.setBits(c.gx, c.gy, c.gz, bits);
    Static.server.broadcastSetBlock(c.chunk.dim, c.x, c.y, c.z, id, bits);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
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

    pl = world.getPowerLevel(dim,x+1,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z-1,c); if (pl > powerLevel) powerLevel = pl;

    int currentLevel = world.getPowerLevel(dim, x, y, z, c);
    if (powerLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if ((powerLevel > 0 && !er.powered) || (currentLevel != powerLevel-1)) {
      c.powerLevel = powerLevel;
      powerOn(null, c);
    }
  }
}
