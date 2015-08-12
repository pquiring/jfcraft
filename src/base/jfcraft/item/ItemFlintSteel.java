package jfcraft.item;

/** Flint and Steel
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.Blocks;
import jfcraft.data.Chunk;
import jfcraft.data.Coords;
import jfcraft.data.Static;
import static jfcraft.data.Direction.*;

public class ItemFlintSteel extends ItemBase {
  public ItemFlintSteel(String name, String names[], String texture[]) {
    super(name, names, texture);
  }
  public void useItem(Client client, Coords c) {
    //set it on fire
    Coords f = c.clone();
    boolean onSide = false;
    if (f.chunk.getBlock(f.gx, f.gy, f.gz).isSolid) {
      f.otherSide();
      f.adjacentBlock();
      if (f.chunk.getBlock(f.gx, f.gy, f.gz).isSolid) return;  //can not place fire here
      f.otherSide();
      onSide = true;
    }
    //place fire @ f
    int dir = B;
    if (onSide || f.chunk.isEmpty(f.gx, f.gy-1, f.gz)) {
      dir = f.dir;
    }
    f.chunk.setBlock(f.gx, f.gy, f.gz, Blocks.FIRE, Chunk.makeBits(dir, 0));
    Static.server.broadcastSetBlock(f.chunk.dim, f.x, f.y, f.z, Blocks.FIRE, Chunk.makeBits(dir, 0));
  }
}
