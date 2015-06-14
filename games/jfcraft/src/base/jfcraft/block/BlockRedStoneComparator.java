package jfcraft.block;

/** Redstone Comparator
 *
 * NOTE : Container measuring is not implemented yet.
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockRedStoneComparator extends BlockBase {
  private static GLModel model, torch;
  public BlockRedStoneComparator(String id, String[] names, String[] images) {
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

  public void getIDs() {
    super.getIDs();
    dropID = Items.REDSTONE_COMPARATOR_ITEM;
  }

  // BIT_UPPER = subtract mode (front torch on)

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
      Static.log("BlockRedStoneComparator:Error:Block as item?");
      active = false;
    }
    buildBuffers(model.getObject("TOP"), buf, data, textures[active ? 1 : 0]);
    buildBuffers(model.getObject("SIDES"), buf, data, textures[2]);

    data.translate_pre = new float[] {0,-Static._1_16 * 4.0f,-Static._1_16 * 4.0f};
    buildBuffers(torch.getObject("TORCH"), buf, data, textures[active ? 4 : 3]);
    data.translate_pre = new float[] {-Static._1_16 * 4.0f,-Static._1_16 * 4.0f,+Static._1_16 * 4.0f};
    buildBuffers(torch.getObject("TORCH"), buf, data, textures[active ? 4 : 3]);
    data.translate_pre = new float[] {+Static._1_16 * 4.0f,-Static._1_16 * 4.0f,+Static._1_16 * 4.0f};
    buildBuffers(torch.getObject("TORCH"), buf, data, textures[active ? 4 : 3]);
    data.translate_pre = null;
  }

  public void useBlock(Client client, Coords c) {
    //toggle active
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    er.active = !er.active;
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    er.cnt = 10;
    c.chunk.addTick(c.gx, c.gy, c.gz, isBlocks2);
  }

  private int getPowerLevel(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return 0;
    int inputLevel = getInputPowerLevel(c);
    int sideLevel = getGreatestSidePowerLevel(c);
    if (sideLevel == 0) {
      return inputLevel;
    }
    if (er.active) {
      //subtract mode
      int ret = inputLevel - sideLevel;
      if (ret < 0) ret = 0;
      return ret;
    } else {
      //compare mode
      if (sideLevel > inputLevel) {
        return 0;
      } else {
        return inputLevel;
      }
    }
  }

  public int getPowerLevel(Coords c, Coords from) {
    //check if only in front
    Coords n = c.clone();
    n.adjacentBlock();
    if (n.x != from.x || n.y != from.y || n.z != from.z) {
      return 0;
    }
    return getPowerLevel(c);
  }

  private int getInputPowerLevel(Coords c) {
    Coords n;
    n = c.clone();
    n.otherSide();
    n.adjacentBlock();
    return Static.server.world.getPowerLevel(c.chunk.dim, n.x, n.y, n.z,c);
  }

  private int getGreatestSidePowerLevel(Coords c) {
    int ret = 0, pl;
    Coords n;
    n = c.clone();
    n.leftSide();
    n.adjacentBlock();
    pl = Static.server.world.getPowerLevel(c.chunk.dim, n.x, n.y, n.z,c);
    if (pl > ret) ret = pl;
    n = c.clone();
    n.rightSide();
    n.adjacentBlock();
    pl = Static.server.world.getPowerLevel(c.chunk.dim, n.x, n.y, n.z,c);
    if (pl > ret) ret = pl;
    return ret;
  }

  public void checkPowered(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int inputLevel = 0;
    int dim = c.chunk.dim;
    Coords n = c.clone();
    n.otherSide();
    n.adjacentBlock();
    World world = Static.world();
    inputLevel = world.getPowerLevel(dim,n.x,n.y,n.z,c);
    int outputLevel = getPowerLevel(c);
    if (outputLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if (inputLevel > 0 && !er.powered && outputLevel > 0) {
      c.powerLevel = inputLevel;
      powerOn(null, c);
    }
  }
  public boolean place(Client client, Coords c) {
    c.otherSide();
    return super.place(client, c);
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
