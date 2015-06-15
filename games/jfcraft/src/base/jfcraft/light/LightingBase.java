package jfcraft.light;

/** Lighting base class.
 *
 * @author pquiring
 */

import jfcraft.data.*;

public interface LightingBase {
  public void light(Chunk chunk);  //generator lighting
  public void update(Chunk chunk);  //update lighting
}
