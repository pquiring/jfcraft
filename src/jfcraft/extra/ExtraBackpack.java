package jfcraft.extra;

/** NPC Backpack items.
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.server.Server;

public class ExtraBackpack extends ExtraContainer {
  public static final int BACKPACK = 0;

  public EntityBase npc;

  public ExtraBackpack() {
    this.id = Extras.BACKPACK;
    items = new Item[1];
    items[0] = new Item();
  }

  public String getName() {
    return "backpack";
  }

  public void get(Server server, Client client, byte idx, byte count) {
    super.get(server, client, idx, count);
  }

  public void put(Server server, Client client, byte idx, byte count) {
    super.put(server, client, idx, count);
  }

  public void exchange(Server server, Client client, byte idx) {
    super.exchange(server, client, idx);
  }
}
