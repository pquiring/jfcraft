package jfcraft.item;

/** Shield
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Types.*;

public class ItemShield extends ItemBase {
  public ItemShield(String name, String names[], String texture[]) {
    super(name, names, texture);
    useRelease = true;
    isArmor = true;
    armor = ARMOR_SHIELD;
    isTool = true;
    tool = TOOL_SHIELD;
    material = MAT_WOOD;
  }
  public boolean useItem(Client client, Coords c) {
    if (client.player.blockCount < 4) {
      client.player.blockCount++;
    } else {
      client.player.blocking = true;
      System.out.println("Shields up");
    }
    return true;
  }
  public void releaseItem(Client client) {
    client.player.blockCount = 0;
    client.player.blocking = false;
    System.out.println("Shields down");
  }
}
