package jfcraft.recipe;

/** Makes different dye combos.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeDyes extends Recipe {
  public RecipeDyes() {
    super(-1,-1);
  }

  //                             BkRdGnBrBuPuCyLgGrPkLmYwLbMaOrWh
  private static int purple[] = {0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0};
  private static int   cyan[] = {0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0};
  private static int    lg1[] = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2};
  private static int    lg2[] = {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1};
  private static int   gray[] = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
  private static int   pink[] = {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
  private static int   lime[] = {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1};
  private static int     lb[] = {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1};
  private static int    ma1[] = {0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0};
  private static int    ma2[] = {0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,1};
  private static int    ma3[] = {0,1,0,0,1,0,0,0,0,1,0,0,0,0,0,0};
  private static int orange[] = {0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0};

  private boolean match(int vars[], int color[]) {
    for(int a=0;a<16;a++) {
      if (vars[a] != color[a]) return false;
    }
    return true;
  }

  public Item make(Item items[]) {
    if (items.length == 1) {
      //check flowers
      char id = items[0].id;
      if (id == Blocks.CACTUS) return new Item(Items.COLOR, Items.VAR_GREEN);
      if (id == Blocks.DANDELION) return new Item(Items.COLOR, Items.VAR_YELLOW);
      if (id == Blocks.FLOWER) {
        byte var = items[0].var;
//        if (var == Blocks.VAR_DANDELION) return new Item(Items.COLOR, Items.VAR_YELLOW);
//        if (var == Blocks.VAR_SUNFLOWER) return new Item(Items.COLOR, Items.VAR_YELLOW);
        if (var == Blocks.VAR_ALLIUM) return new Item(Items.COLOR, Items.VAR_MAGENTA);
//        if (var == Blocks.VAR_LILAC) return new Item(Items.COLOR, Items.VAR_MAGENTA);
        if (var == Blocks.VAR_BLUE_ORCHID) return new Item(Items.COLOR, Items.VAR_LIGHT_BLUE);
        if (var == Blocks.VAR_POPPY) return new Item(Items.COLOR, Items.VAR_RED);
        if (var == Blocks.VAR_TULIP_RED) return new Item(Items.COLOR, Items.VAR_RED);
        if (var == Blocks.VAR_TULIP_WHITE) return new Item(Items.COLOR, Items.VAR_LIGHT_GRAY);
        if (var == Blocks.VAR_OXEYE_DAISY) return new Item(Items.COLOR, Items.VAR_LIGHT_GRAY);
        if (var == Blocks.VAR_AZURE_BLUET) return new Item(Items.COLOR, Items.VAR_LIGHT_BLUE);
        if (var == Blocks.VAR_TULIP_ORANGE) return new Item(Items.COLOR, Items.VAR_ORANGE);
        if (var == Blocks.VAR_TULIP_PINK) return new Item(Items.COLOR, Items.VAR_PINK);
//        if (var == Blocks.VAR_PENOY) return new Item(Items.COLOR, Items.VAR_PINK);
      }
    }
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Blocks.AIR) continue;
      if (id != Items.COLOR) return null;
    }
    int vars[] = new int[16];
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Blocks.AIR) continue;
      vars[items[a].var]++;
    }
    if (match(vars, purple)) return new Item(Items.COLOR, Items.VAR_PURPLE);
    if (match(vars, cyan)) return new Item(Items.COLOR, Items.VAR_CYAN);
    if (match(vars, lg1)) return new Item(Items.COLOR, Items.VAR_LIGHT_GRAY);
    if (match(vars, lg2)) return new Item(Items.COLOR, Items.VAR_LIGHT_GRAY);
    if (match(vars, gray)) return new Item(Items.COLOR, Items.VAR_GRAY);
    if (match(vars, pink)) return new Item(Items.COLOR, Items.VAR_PINK);
    if (match(vars, lime)) return new Item(Items.COLOR, Items.VAR_LIME);
    if (match(vars, lb)) return new Item(Items.COLOR, Items.VAR_LIGHT_BLUE);
    if (match(vars, ma1)) return new Item(Items.COLOR, Items.VAR_MAGENTA);
    if (match(vars, ma2)) return new Item(Items.COLOR, Items.VAR_MAGENTA);
    if (match(vars, ma3)) return new Item(Items.COLOR, Items.VAR_MAGENTA);
    if (match(vars, orange)) return new Item(Items.COLOR, Items.VAR_ORANGE);
    return null;
  }
}
