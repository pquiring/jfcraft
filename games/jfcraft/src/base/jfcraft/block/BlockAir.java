package jfcraft.block;

/** Block that represent empty space
 *
 * @author pquiring
 *
 * Created : Apr 12, 2014
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockAir extends BlockBase {
  public BlockAir(String name) {
    super(name, new String[] {"Air"}, new String[0]);
    canReplace = true;
    isOpaque = false;
    isSolid = false;
    isComplex = true;  //BlockLiquid.canFill() depends on this
    canSpawnOn = false;
    resetBoxes(Type.BOTH);
  }
  public void addFace(RenderBuffers obj, RenderData data, SubTexture st) {
    try {
      Static.log("BlockAir:addFace():id=" + (int)id);
    } catch (Exception e) {
      Static.log(e);
    }
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    try {
      Static.log("BlockAir:buildBuffers():id=" + (int)id);
    } catch (Exception e) {
      Static.log(e);
    }
  }
}
