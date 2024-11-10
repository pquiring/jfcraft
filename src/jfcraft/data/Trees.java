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
  public static int TREE_TAIGA = 7;
  public static int CACTUS = 8;
  public static int BUSH = 9;
  public static int KELP = 10;

  public Trees() {
    trees = new TreeBase[11];
    trees[0] = new TreeNormal().setVar(VAR_OAK);
    trees[1] = new TreeNormal().setVar(VAR_SPRUCE);
    trees[2] = new TreeNormal().setVar(VAR_BIRCH);
    trees[3] = new TreeQuad().setVar(VAR_DARK_OAK);
    trees[4] = new TreeNormal().setVar(VAR_OAK).setVines(true);
    trees[5] = new TreeNormal().setVar(VAR_ACACIA);
    trees[6] = new TreeQuad().setVar(VAR_JUNGLE);
    trees[7] = new TreeNormal().setVar(VAR_OAK).setSnow(true);
    trees[8] = new TreeCactus();
    trees[9] = new TreeBush().setVar(VAR_OAK);
    trees[10] = new TreeKelp();
  }
}
