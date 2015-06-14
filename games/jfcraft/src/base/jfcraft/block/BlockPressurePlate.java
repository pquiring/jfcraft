package jfcraft.block;

/** Pressure Plate
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.entity.*;

public class BlockPressurePlate extends BlockCarpet {
  public BlockPressurePlate(String id, String names[], String images[]) {
    super(id, names, images);
    isRedstone = true;
  }

  public int getPowerLevel(Coords c, Coords from) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockPressurePlate.getPowerLevel():Error:Can not find Extra data");
      return 0;
    }
    return er.active ? 16 : 0;
  }

  public void etick(EntityBase e, Coords c) {
//    Static.log("etick:" + e);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockPressurePlate.etick():Error:Can not find Extra data");
      return;
    }
    if (er.active) {
      Tick tick = c.chunk.getTick(c.gx, c.gy, c.gz, false);
      er.t1 = 0;  //reset timer
    } else {
      er.active = true;
      c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
      Static.world().powerChanged(c.chunk.dim, c.x,c.y,c.z);
    }
  }

  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    World world = Static.world();
    tick.toWorldCoords(chunk, c);
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockPressurePlate.tick():Error:Can not find Extra data");
      super.tick(chunk, tick);
      return;
    }
//    int bits = chunk.getBits(c.gx, c.gy, c.gz);
    if (er.t1 < 10) er.t1++;  //delay time on
    if (er.t2 < 20) er.t2++;  //min time on
    if (er.t1 < 10 || er.t2 < 20) {
      //still powered
      return;
    }
    //lost power
    er.active = false;
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    world.powerChanged(c.chunk.dim, c.x,c.y,c.z);
    super.tick(chunk, tick); //this will delete tick (no longer needed)
  }
}
