package jfcraft.extra;

/**
 * Extra Item Container (base class for ExtraChest, Hopper, Furnace, etc.)
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.client.Client;
import jfcraft.item.*;
import jfcraft.server.Server;

public class ExtraContainer extends ExtraBase {
  public Item items[];

  private static final byte ver = 0;

  public ExtraContainer() {}

  public ExtraContainer(int cnt) {
    items = new Item[cnt];
    for(int a=0;a<cnt;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "container";
  }

  public void update(ExtraBase update) {
    ExtraContainer container = (ExtraContainer)update;
    this.items = container.items;
  }

  public void changed() {}

  public void convertIDs(char blockIDs[], char itemIDs[]) {
    for(int a=0;a<items.length;a++) {
      Item item = items[a];
      char id = item.id;
      if (Static.isBlock(id)) {
        id = blockIDs[id];
      } else {
        id = (char)(itemIDs[id - Items.FIRST_ID] + Items.FIRST_ID);
      }
      item.id = id;
    }
  }

  public void get(Server server, Client client, byte idx, byte count) {
    byte cc = client.container.items[idx].count;
    if (count > cc) {
      count = cc;
    }
    if (count == 0) {
      Static.log("nothing to pickup");
      return;
    }
    client.hand = new Item();
    client.hand.copy(client.container.items[idx], count);
    if (count == cc) {
      client.container.items[idx].clear();
    } else {
      client.container.items[idx].count = (byte)(cc - count);
    }
    server.broadcastSetContainerItem(idx, client.container);
    client.serverTransport.setHand(client.hand);
    if (client.chunk != null) {
      client.chunk.dirty = true;
    }
    changed();
  }

  public void put(Server server, Client client, byte idx, byte count) {
    if (count <= 0 || count > 64) {
      Static.log("invalid count");
      return;
    }
    if (client.hand == null) {
      Static.log(":but hand is empty");
      return;
    }
    if (count > client.hand.count) {
      Static.log(":count > hand.count");
      return;
    }
    ItemBase itembase = Static.items.items[client.hand.id];
    if (itembase.isDamaged) {
      if (client.container.items[idx].count != 0) Static.log(":not empty");
      client.container.items[idx] = client.hand;
      server.broadcastSetContainerItem(idx, client.container);
      client.hand = null;
    } else {
      int max = Static.items.items[client.hand.id].maxStack;
      int cc = client.container.items[idx].count;
      if (cc > 0) {
        if (!client.container.items[idx].equals(client.hand)) Static.log("items not same");
        if (cc + count > max) {
          count = (byte)(max - cc);
          if (count == 0) return;  //inv slot full (not an error)
        }
        client.container.items[idx].count += count;
      } else {
        if (count > max) {
          count = (byte)max;
        }
        client.container.items[idx].copy(client.hand, count);
      }
      server.broadcastSetContainerItem(idx, client.container);
      client.hand.count -= count;
      if (client.hand.count == 0) {
        client.hand = null;
      }
    }
    client.serverTransport.setHand(client.hand);
    if (client.chunk != null) {
      client.chunk.dirty = true;
    }
    changed();
  }

  public void exchange(Server server, Client client, byte idx) {
    if (client.hand == null) {
      Static.log(":but hand empty");
      return;
    }
    if (client.container.items[idx].count == 0) {
      Static.log("but item empty");
      return;
    }
    Item tmp = client.hand;
    client.hand = client.player.items[idx];
    client.container.items[idx] = tmp;
    server.broadcastSetContainerItem(idx, client.container);
    client.serverTransport.setHand(client.hand);
    if (client.chunk != null) {
      client.chunk.dirty = true;
    }
    changed();
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    int length = items.length;
    buffer.writeByte((byte)length);
    for(int a=0;a<length;a++) {
      items[a].write(buffer, file);
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    int length = buffer.readByte();
    items = new Item[length];
    for(int a=0;a<length;a++) {
      items[a] = new Item();
      items[a].read(buffer, file);
    }
    return true;
  }
}
