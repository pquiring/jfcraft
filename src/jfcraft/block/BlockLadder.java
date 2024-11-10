package jfcraft.block;

/**
 * Ladders and vines.
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class BlockLadder extends BlockFace {
  public BlockLadder(String id, String names[], String images[]) {
    super(id, names, images);
    isDirFace = true;
    isSupported = true;
  }
  public boolean place(Client client, Coords c) {
    if (c.dir == A || c.dir == B) return false;  //can only place on walls
    return super.place(client, c);
  }
}
