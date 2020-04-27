package jfcraft.data;

/**
 * Trees & Bushes
 */

import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;

public class Trees {
  public TreeBase trees[];

  public static int TREE_OAK = 0;
  public static int TREE_SPRUCE = 1;
  public static int TREE_BIRCH = 2;
  public static int TREE_DARK_OAK = 3;
  public static int TREE_SWAMP = 4;
  public static int TREE_SAVANA = 5;
  public static int TREE_JUNGLE = 6;

  public static int BUSH = 7;

  public Trees() {
    trees = new TreeBase[8];
    trees[0] = new TreeNormal().setVar(VAR_OAK);
    trees[1] = new TreeNormal().setVar(VAR_SPRUCE);
    trees[2] = new TreeNormal().setVar(VAR_BIRCH);
    trees[3] = new TreeQuad().setVar(VAR_DARK_OAK);
    trees[4] = new TreeNormal().setVar(VAR_OAK);
    trees[5] = new TreeNormal().setVar(VAR_ACACIA);
    trees[6] = new TreeQuad().setVar(VAR_JUNGLE);
    trees[7] = new TreeBush().setVar(VAR_OAK);
  }
}
