package jfcraft.data;

/** Recipe registry
 *
 * @author pquiring
 *
 * Created : May 5, 2014
 */

import java.util.*;

import jfcraft.item.*;
import jfcraft.recipe.*;

public class Recipes {
  public ArrayList<Recipe> receipes = new ArrayList<Recipe>();

  public void registerRecipe(Recipe r) {
    receipes.add(r);
  }

  public void registerDefault() {
    registerRecipe(new RecipeBread());
    registerRecipe(new RecipePlanks());
    registerRecipe(new RecipeCraftTable());
    registerRecipe(new RecipeFurnace());
    registerRecipe(new RecipeStick());
    registerRecipe(new RecipeTorch());
    registerRecipe(new RecipePickAxe());
    registerRecipe(new RecipeAxe());
    registerRecipe(new RecipeShovel());
    registerRecipe(new RecipeSword());
    registerRecipe(new RecipeHoe());
    registerRecipe(new RecipeFlintAndSteel());
    registerRecipe(new RecipeBow());
    registerRecipe(new RecipeArrow());
    registerRecipe(new RecipeBowl());
    registerRecipe(new RecipeMushroomStew());
    registerRecipe(new RecipeHelmet());
    registerRecipe(new RecipeBoots());
    registerRecipe(new RecipeChestArmor());
    registerRecipe(new RecipePants());
    registerRecipe(new RecipePainting());
    registerRecipe(new RecipeSign());
    registerRecipe(new RecipeDoor());
    registerRecipe(new RecipeBucket());
    registerRecipe(new RecipeMinecart());
    registerRecipe(new RecipeSaddle());
    registerRecipe(new RecipeBoat());
    registerRecipe(new RecipeBook());
    registerRecipe(new RecipeCompass());
    registerRecipe(new RecipeFishingRod());
    registerRecipe(new RecipeCake());
    registerRecipe(new RecipeBed());
    registerRecipe(new RecipeCookie());
    registerRecipe(new RecipeBoneMeal());
    registerRecipe(new RecipeShears());
    registerRecipe(new RecipeBrewingStand());
    registerRecipe(new RecipeCauldron());
    registerRecipe(new RecipeItemFrame());
    registerRecipe(new RecipePot());
    registerRecipe(new RecipePumpkinPie());
    registerRecipe(new RecipePumpkinSeeds());
    registerRecipe(new RecipePumpkinLit());
    registerRecipe(new RecipeLead());
    registerRecipe(new RecipeSugar());
    registerRecipe(new RecipePaper());
    registerRecipe(new RecipeChest());
    registerRecipe(new RecipePiston());
    registerRecipe(new RecipePistonSticky());
    registerRecipe(new RecipeStairs());
    registerRecipe(new RecipeBrickBlock());
    registerRecipe(new RecipeTNT());
    registerRecipe(new RecipeBookshelf());
    registerRecipe(new RecipeLadder());
    registerRecipe(new RecipeRail());
    registerRecipe(new RecipeLever());
    registerRecipe(new RecipePressurePlate());
    registerRecipe(new RecipeRedstoneTorch());
    registerRecipe(new RecipeButton());
    registerRecipe(new RecipeFence());
    registerRecipe(new RecipeGate());
    registerRecipe(new RecipeBars());
    registerRecipe(new RecipeGlassPane());
    registerRecipe(new RecipeWall());
    registerRecipe(new RecipeHopper());
    registerRecipe(new RecipeCarpet());
    registerRecipe(new RecipePoweredRail());
    registerRecipe(new RecipeDetectorRail());
    registerRecipe(new RecipeDispenser());
    registerRecipe(new RecipeDropper());
    registerRecipe(new RecipeMinecartChest());
    registerRecipe(new RecipeMinecartFurnace());
    registerRecipe(new RecipeMinecartHopper());
    registerRecipe(new RecipeMinecartTNT());
    registerRecipe(new RecipeEnderChest());
    registerRecipe(new RecipeTripHook());
    registerRecipe(new RecipeBottle());
    registerRecipe(new RecipeEnderEye());
    registerRecipe(new RecipeBlock3x3());
    registerRecipe(new RecipeBlock2x2());
    registerRecipe(new RecipeExpand());
    registerRecipe(new RecipeSolarPanel());
    registerRecipe(new RecipeRedstoneComparator());
    registerRecipe(new RecipeRedstoneRepeater());
  }

  public Item make3x3(Item items[]) {
    for(int a=0;a<9;a++) {
      if (items[a].count == 0) items[a].id = 0;
    }
    Item item;
    if ((items[0].id == 0) && items[1].id == 0 && items[2].id == 0) {
      //top row empty
      if ((items[3].id == 0) && items[4].id == 0 && items[5].id == 0) {
        //middle row empty
        return make3x1(new Item[] {items[6], items[7], items[8]});
      }
      if ((items[6].id == 0) && items[7].id == 0 && items[8].id == 0) {
        //bottom row empty
        return make3x1(new Item[] {items[3], items[4], items[5]});
      }
      if (items[3].id == 0 && items[6].id == 0) {
        return make2x2(new Item[] {items[4], items[5], items[7], items[8]});
      }
      if (items[5].id == 0 && items[8].id == 0) {
        return make2x2(new Item[] {items[3], items[4], items[6], items[7]});
      }
    }
    if ((items[6].id == 0) && items[7].id == 0 && items[8].id == 0) {
      //bottom row empty
      if ((items[3].id == 0) && items[4].id == 0 && items[5].id == 0) {
        //middle row empty
        return make3x1(new Item[] {items[0], items[1], items[2]});
      }
      if ((items[0].id == 0) && items[1].id == 0 && items[2].id == 0) {
        //top row empty
        return make3x1(new Item[] {items[3], items[4], items[5]});
      }
      if (items[0].id == 0 && items[3].id == 0) {
        return make2x2(new Item[] {items[1], items[2], items[4], items[5]});
      }
      if (items[2].id == 0 && items[5].id == 0) {
        return make2x2(new Item[] {items[0], items[1], items[3], items[4]});
      }
    }
    if ((items[0].id == 0) && items[3].id == 0 && items[6].id == 0) {
      //left col empty
      if ((items[1].id == 0) && items[4].id == 0 && items[7].id == 0) {
        //center col empty
        return make1x3(new Item[] {items[2], items[5], items[8]});
      }
      if ((items[2].id == 0) && items[5].id == 0 && items[8].id == 0) {
        //right col empty
        return make1x3(new Item[] {items[1], items[4], items[7]});
      }
      return make2x3(new Item[] {items[1], items[2], items[4], items[5], items[7], items[8]});
    }
    if ((items[2].id == 0) && items[5].id == 0 && items[8].id == 0) {
      //right col empty
      if ((items[1].id == 0) && items[4].id == 0 && items[7].id == 0) {
        //center col empty
        return make1x3(new Item[] {items[0], items[3], items[6]});
      }
      return make2x3(new Item[] {items[0], items[1], items[3], items[4], items[6], items[7]});
    }
    if ((items[0].id == 0) && items[1].id == 0 && items[2].id == 0) {
      return make3x2(new Item[] {items[3], items[4], items[5], items[6], items[7], items[8]});
    }
    if ((items[6].id == 0) && items[7].id == 0 && items[8].id == 0) {
      return make3x2(new Item[] {items[0], items[1], items[2], items[3], items[4], items[5]});
    }

    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 3 && r.height == 3) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make3x2(Item items[]) {
    Item item;
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 3 && r.height == 2) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make2x3(Item items[]) {
    Item item;
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 2 && r.height == 3) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make3x1(Item items[]) {
    Item item;
    if (items[0].id == 0) {
      return make2x1(new Item[] {items[1], items[2]});
    }
    if (items[2].id == 0) {
      return make2x1(new Item[] {items[0], items[1]});
    }
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 3 && r.height == 1) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make1x3(Item items[]) {
    Item item;
    if (items[0].id == 0) {
      return make1x2(new Item[] {items[1], items[2]});
    }
    if (items[2].id == 0) {
      return make1x2(new Item[] {items[0], items[1]});
    }
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 1 && r.height == 3) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make2x2(Item items[]) {
    for(int a=0;a<4;a++) {
      if (items[a].count == 0) items[a].id = 0;
    }
    Item item;
    if (items[0].id == 0 && items[2].id == 0) {
      return make1x2(new Item[] {items[1], items[3]});
    }
    if (items[1].id == 0 && items[3].id == 0) {
      return make1x2(new Item[] {items[0], items[2]});
    }
    if (items[0].id == 0 && items[1].id == 0) {
      return make2x1(new Item[] {items[2], items[3]});
    }
    if (items[2].id == 0 && items[3].id == 0) {
      return make2x1(new Item[] {items[0], items[1]});
    }
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 2 && r.height == 2) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make2x1(Item items[]) {
    Item item;
    if (items[0].id == 0) {
      return make1x1(new Item[] {items[1]});
    }
    if (items[1].id == 0) {
      return make1x1(new Item[] {items[0]});
    }
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 2 && r.height == 1) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make1x2(Item items[]) {
    Item item;
    if (items[0].id == 0) {
      return make1x1(new Item[] {items[1]});
    }
    if (items[1].id == 0) {
      return make1x1(new Item[] {items[0]});
    }
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 1 && r.height == 2) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
  public Item make1x1(Item items[]) {
    Item item;
    int size = receipes.size();
    for(int a=0;a<size;a++) {
      Recipe r = receipes.get(a);
      if (r.width == -1 && r.height == -1) {
        item = r.make(items);
        if (item != null) return item;
      }
      else if (r.width == 1 && r.height == 1) {
        item = r.make(items);
        if (item != null) return item;
      }
    }
    return null;
  }
}
