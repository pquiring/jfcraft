package jfcraft.data;

/** Horse items.
 *
 * This extra is not saved to disk.
 * The horse will save the contents itself.
 * This is only used with the HorseMenu.
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.server.Server;

public class ExtraHorse extends ExtraContainer {
  public static final int SADDLE = 0;
  public static final int ARMOR = 1;

  public Horse horse;

  public ExtraHorse() {
    this.id = Extras.HORSE;
    items = new Item[2];
    for(int a=0;a<2;a++) {
      items[a] = new Item();
    }
  }

  public ExtraHorse(boolean withChest) {
    this.id = Extras.HORSE;
    int cnt = 2;
    if (withChest) cnt += 15;
    items = new Item[cnt];
    for(int a=0;a<cnt;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "horse";
  }

  private boolean isHorseArmor(Item item) {
    if (item.id == Items.HORSE_ARMOR_IRON) return true;
    if (item.id == Items.HORSE_ARMOR_GOLD) return true;
    if (item.id == Items.HORSE_ARMOR_DIAMOND) return true;
    return false;
  }

  public void changed() {
    horse.checkFlags();
  }

  public void get(Server server, Client client, byte idx, byte count) {
    if (idx == ExtraHorse.ARMOR) {
      if (items[ARMOR].id == Blocks.OBSIDIAN) return;
    }
    super.get(server, client, idx, count);
  }

  public void put(Server server, Client client, byte idx, byte count) {
    if (idx == ExtraHorse.SADDLE && client.hand.id != Items.SADDLE) return;
    if (idx == ExtraHorse.ARMOR) {
      if (!isHorseArmor(client.hand)) return;
      if (items[ARMOR].id == Blocks.OBSIDIAN) return;
    }
    super.put(server, client, idx, count);
  }

  public void exchange(Server server, Client client, byte idx) {
    if (idx == ExtraHorse.SADDLE && client.hand.id != Items.SADDLE) return;
    if (idx == ExtraHorse.ARMOR) {
      if (!isHorseArmor(client.hand)) return;
      if (items[ARMOR].id == Blocks.OBSIDIAN) return;
    }
    super.exchange(server, client, idx);
  }
}
