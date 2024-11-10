package jfcraft.block;

/** Block Solid with Variations
 * ie: planks, wood, etc.
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

public class BlockOpaqueVar extends BlockOpaque {
  public BlockOpaqueVar(String id, String names[], String images[]) {
    super(id, names, images);
    isVar = true;
  }
}
