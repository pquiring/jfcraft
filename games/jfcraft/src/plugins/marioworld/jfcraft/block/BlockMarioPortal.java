package jfcraft.block;

/**
 *
 * @author pquiring
 */

import jfcraft.block.*;
import jfcraft.dim.*;

public class BlockMarioPortal extends BlockPortal {
  public static char MARIO_PORTAL;
  public BlockMarioPortal(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public Class getIDClass() {
    return BlockMarioPortal.class;
  }

  public int getDimension() {
    return DimMarioWorld.MARIO_WORLD;
  }

  public char getFrameBlock() {
    return BlockCoinBlock.COIN_BLOCK;
  }

  public char getPortalBlock() {
    return MARIO_PORTAL;
  }
}
