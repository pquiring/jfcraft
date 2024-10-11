package jfcraft.item;

/** Shield
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Types.*;
import jfcraft.packet.*;

public class ItemShield extends ItemBase {
  public ItemShield(String name, String names[], String texture[]) {
    super(name, names, texture);
    useRelease = true;
    isTool = true;
    tool = TOOL_SHIELD;
    material = MAT_WOOD;
    renderAsEntity = true;
    renderAsItem = true;
    renderAsArmor = true;
    entityID = 0;  //set in getIDs()
  }
  public void getIDs(World world) {
    super.getIDs(world);
    entityID = Entities.SHIELD;
  }
  public boolean useItem(Client client, Coords c) {
    if (client.player.blockCount < 4) {
      client.player.blockCount++;
      client.serverTransport.addUpdate(new PacketShield(Packets.SHIELD, client.player.blockCount));
    } else {
      client.player.blocking = true;
    }
    return true;
  }
  public void releaseItem(Client client) {
    if (client.player.blockCount != 0) {
      client.player.blockCount = 0;
      client.serverTransport.addUpdate(new PacketShield(Packets.SHIELD, client.player.blockCount));
    }
    client.player.blocking = false;
  }
}
