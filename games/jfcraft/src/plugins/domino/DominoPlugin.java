/** Creeper plugin
 *
 * @author pquiring
 */

import jfcraft.block.*;
import jfcraft.entity.*;
import jfcraft.plugin.*;
import jfcraft.data.*;

public class DominoPlugin extends PluginBase {

  public String getName() {
    return "Domino";
  }

  public String getVersion() {
    return "1.0";
  }

  public String getDesc() {
    return "Dominos";
  }

  public void registerPlugin() {
    registerBlock(new BlockDomino("DOMINO", new String[] {"Domino"}, new String[] {}));
    registerEntity(new Domino());
  }
}
