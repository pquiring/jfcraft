package jfcraft.block;

/** Opaque blocks (dirt, stone, etc.)
 * - a full block with no transparent parts
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

public class BlockOpaque extends BlockBase {
  public BlockOpaque(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = true;
    isAlpha = false;
    if (names != null && names.length == 1 && images != null && images.length == 4) {
      isDir = true;  //TODO : might not always be correct, may need a setDir()
    }
  }
}
