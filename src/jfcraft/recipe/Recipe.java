package jfcraft.recipe;

/** Recipe base class.
 *
 * If width/height == -1 the recipe takes any size.
 * In this case the number of items input may vary.
 * The dimensions should be one of the following:
 *   3x3
 *   3x2
 *   3x1
 *   2x3
 *   2x2
 *   2x1
 *   1x3
 *   1x2
 *   1x1
 *
 * @author pquiring
 *
 * Created : May 5, 2014
 */

import jfcraft.item.*;

public abstract class Recipe {
  public int width, height;
  public Recipe(int width, int height) {
    this.width = width;
    this.height = height;
  }
  /** Returns an item if the recipe can make it from input items.
   @param items - list of items that is width x height (or all items if width/height == -1)
   */
  public abstract Item make(Item items[]);
}
