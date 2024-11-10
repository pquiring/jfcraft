package jfcraft.block;

/** Button
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.ArrayList;
import javaforce.*;
import javaforce.gl.*;
import jfcraft.client.Client;

import jfcraft.data.*;
import static jfcraft.data.Direction.*;
import jfcraft.opengl.*;

public class BlockButton extends BlockBase {
  private static Model model;

  public BlockButton(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isRedstone = true;
    isDirFace = true;
    canUse = true;
    isSupported = true;
    model = Assets.getModel("button").model;
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("BUTTON"), buf, textures[0]);
  }

  public void useBlock(Client client, Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockButton.useBlock():Error:Can not find Extra data");
      return;
    }
    if (er.active) {
      Static.log("BlockButton.useBlock():already active");
      return;
    }
    World world = Static.server.world;
//      Static.log("act button@" + c);
    er.active = true;
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
    //activate surrounding blocks
    world.powerChanged(c.chunk.dim, c.x,c.y,c.z);
  }

  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
//    Static.log("button:tick:" + tick.t1);
    //check supported
    tick.toWorldCoords(chunk, c);
    Static.server.world.getBlock(chunk.dim, c.x, c.y, c.z, c);
    if (!checkSupported(c)) {
      destroy(null, c, true);
      chunk.delTick(tick);
      return;
    }
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockButton.tick():Error:Can not find Extra data");
      return;
    }
    if (!er.active) {
      //tick for no reason
//      Static.log("BlockButton.tick() but not active");
      chunk.delTick(tick);
      return;
    }
    if (er.t1 < 30) {
      er.t1++;
    }
    if (er.t1 == 30) {
//      Static.log("deact button@" + c);
      er.active = false;
      chunk.delTick(tick);
      Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    }
  }
  public int getPowerLevel(Coords c, Coords from) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockButton.getPowerLevel():Error:Can not find Extra data");
      return 0;
    }
    return er.active ? 16 : 0;
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    if (type == Type.ENTITY) return list;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch (dir) {
      case A: list.add(new Box( 5,14, 6, 10+1,15+1, 9+1)); break;
      case B: list.add(new Box( 5, 0, 6, 10+1, 1+1, 9+1)); break;
      case N: list.add(new Box( 5, 6, 0, 10+1, 9+1, 1+1)); break;
      case E: list.add(new Box(14, 6, 5, 15+1, 9+1,10+1)); break;
      case S: list.add(new Box( 5, 6,14, 10+1, 9+1,15+1)); break;
      case W: list.add(new Box( 0, 6, 5,  1+1, 9+1,10+1)); break;
    }
    return list;
  }
}
