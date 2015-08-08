package jfcraft.block;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;

/** Block Obsidian
 *
 * @author pquiring
 */

public class BlockObsidian extends BlockOpaque {
  public BlockObsidian(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public boolean useTool(Client client, Coords c) {
    Item item = client.player.items[client.player.activeSlot];
    if (item.id == Items.FLINT_STEEL) {
      if (Portal.makePortal(c, id, Blocks.NETHER_PORTAL)) return true;
      Static.log("makePortal:failed");
    }
    return super.useTool(client, c);
  }
}
