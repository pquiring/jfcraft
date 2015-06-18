package jfcraft.block;

/** Nether portal block.
 *
 * @author pquiring
 */

import jfcraft.data.Blocks;
import jfcraft.data.Dims;

public class BlockNetherPortal extends BlockPortal {
  public BlockNetherPortal(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public int getDimension() {
    return Dims.NETHER;
  }

  public char getFrameBlock() {
    return Blocks.OBSIDIAN;
  }
}
