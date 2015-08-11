/** Creeper plugin
 *
 * @author pquiring
 */

import jfcraft.entity.*;
import jfcraft.plugin.*;
import jfcraft.data.*;

public class CreeperPlugin extends PluginBase {

  public String getName() {
    return "Creeper";
  }

  public String getVersion() {
    return "1.0";
  }

  public String getDesc() {
    return "Creeper : Most hated monster";
  }

  public void registerPlugin() {
    registerEntity(new Creeper());
  }
}
