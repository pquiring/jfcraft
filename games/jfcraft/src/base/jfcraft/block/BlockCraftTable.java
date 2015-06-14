package jfcraft.block;

/** Crafting table
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.Chunk;
import jfcraft.data.Coords;

public class BlockCraftTable extends BlockOpaque {
  public BlockCraftTable(String id, String names[], String images[]) {
    super(id, names, images);
    isDir = true;
    isDirXZ = true;
    canUse = true;
  }

  public void useBlock(Client client, Coords c) {
    client.serverTransport.enterMenu(Client.CRAFTTABLE);
    client.menu = Client.CRAFTTABLE;
  }
}
