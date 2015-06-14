package jfcraft.light;

/** Lighting base class.
 *
 * @author pquiring
 */

import jfcraft.data.*;

public interface LightingBase {
  public void light(Chunk chunk);
  public void update(Chunk chunk, int x,int y,int z);
}
