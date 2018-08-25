/** Mario World plugin
 *
 * @author pquiring
 */

import jfcraft.block.*;
import jfcraft.entity.*;
import jfcraft.plugin.*;
import jfcraft.recipe.*;
import jfcraft.dim.*;

public class MarioWorldPlugin extends PluginBase {

  public String getName() {
    return "Mario World";
  }

  public String getVersion() {
    return "1.0";
  }

  public String getDesc() {
    return "Mario World";
  }

  public void registerPlugin() {
    registerBlock(new BlockScrew("SCREW", new String[] {"Screw"}, new String[] {"screw"}));
    registerBlock(new BlockCoinBlock("COIN_BLOCK", new String[] {"Coin Block"}, new String[] {"coinblock"}));
    registerBlock(new BlockMarioPortal("MARIO_PORTAL", new String[] {"Mario Portal"}, new String[] {"nether_portal"}));
    registerRecipe(new RecipeCoinBlock());
    registerEntity(new Turtle());
    registerDimension(new DimMarioWorld());
  }
}
