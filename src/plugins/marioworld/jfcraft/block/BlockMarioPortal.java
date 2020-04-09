package jfcraft.block;

/**
 *
 * @author pquiring
 */

import jfcraft.block.*;
import jfcraft.dim.*;
import jfcraft.data.*;

public class BlockMarioPortal extends BlockPortal {
  public static char MARIO_PORTAL;
  public BlockMarioPortal(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public void getIDs() {
    MARIO_PORTAL = Static.server.world.getBlockID("MARIO_PORTAL");
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
