package jfcraft.item;

/** Item Reference
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class ItemRef {
  public String name;  
  public byte count;
  
  public ItemRef(String name, int count) {
    this.name = name;
    this.count = (byte)count;
  }
  
  public Item toItem() {
    Item item = Static.getItem(name);
    if (item == null) return null;
    item.count = count;
    return item;
  }
  
  public static Item[] getItems(ItemRef[] refs) {
    if (refs == null) return null;
    int len = refs.length;
    Item[] items = new Item[len];
    for(int a=0;a<len;a++) {
      if (refs[a] != null) {
        items[a] = refs[a].toItem();
      }
    }
    return items;
  }

  public static Item[][] getItems(ItemRef[][] refs) {
    if (refs == null) return null;
    int len = refs.length;
    Item[][] items = new Item[len][];
    for(int a=0;a<len;a++) {
      if (refs[a] != null) {
        items[a] = getItems(refs[a]);
      }
    }
    return items;
  }
}
