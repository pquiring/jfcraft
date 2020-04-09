package jfcraft.block;

/** Star block (portal to Mario World)
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.data.*;

public class BlockCoinBlock extends BlockOpaque {
  public static char COIN_BLOCK;
  public BlockCoinBlock(String name, String names[], String images[]) {
    super(name, names, images);
  }
  public void getIDs() {
    COIN_BLOCK = Static.server.world.getBlockID("COIN_BLOCK");
  }
  public boolean useTool(Client client, Coords c) {
    Item item = client.player.items[client.player.activeSlot];
    if (item.id == Items.FLINT_STEEL) {
      if (Portal.makePortal(c, id, BlockMarioPortal.MARIO_PORTAL)) return true;
      Static.log("makePortal:failed");
    }
    return super.useTool(client, c);
  }
}
