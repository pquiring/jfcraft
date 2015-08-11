package jfcraft.block;

/** Barrier
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockBarrier extends BlockBase {
  public BlockBarrier(String name, String names[], String images[]) {
    super(name, names, images);
    isSolid = false;
    setDrop("air");
  }

  public void buildBuffers(RenderDest dest, RenderData data) {}
  public void destroy(Client client, Coords c, boolean doDrop) {}
}
