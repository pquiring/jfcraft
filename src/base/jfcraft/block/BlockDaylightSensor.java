package jfcraft.block;

/**
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class BlockDaylightSensor extends BlockBase {
  private static GLModel model;
  public BlockDaylightSensor(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    resetBoxes(Type.BOTH);
    addBox(0, 0, 0, 16, 5, 16,Type.BOTH);
    isRedstone = true;
    if (model == null) {
      model = Assets.getModel("daylight_sensor").model;
    }
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("TOP"), buf, data, textures[0]);
    buildBuffers(model.getObject("SIDES"), buf, data, textures[1]);
  }

  public int getPowerLevel(Coords c, Coords from) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("DaylightSensor:Error:Can't find Extra data");
      return 0;
    }
    return er.lvl;
  }
  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("DaylightSensor:Error:Can't find Extra data");
      return;
    }
    int lvl = 0;
    int time = Static.server.world.time;
    int sl = chunk.getSunLight(tick.x, tick.y+1, tick.z);
    time -= 6000;  //offset X
    if (time < 24000) time += 24000;
    double rad = time;
    rad /= 24000.0;
    rad *= Math.PI * 2.0;
    lvl = (int)(Math.sin(rad) * 15.0 + 2.0);  //2.0 = offset Y
    if (lvl < 0) lvl = 0;
    if (lvl > sl) lvl = sl;
    if (er.lvl != lvl) {
      er.lvl = lvl;
      chunk.needRelight = true;
      chunk.dirty = true;
      Static.server.world.powerChanged(chunk.dim, c.x, c.y, c.z);
    }
  }
}
