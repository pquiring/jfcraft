package jfcraft.block;

import jfcraft.client.Client;
import jfcraft.data.Coords;
import jfcraft.data.Items;
import jfcraft.data.Static;
import jfcraft.item.Item;
import jfcraft.data.Portal;

/** Block Obsidian
 *
 * @author pquiring
 */

public class BlockObsidian extends BlockOpaque {
  public BlockObsidian(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public boolean useTool(Client client, Coords c) {
    Item item = client.player.items[client.activeSlot];
    if (item.id == Items.FLINT_STEEL) {
      if (Portal.makePortal(c, id)) return true;
      Static.log("makePortal:failed");
    }
    return super.useTool(client, c);
  }
}
