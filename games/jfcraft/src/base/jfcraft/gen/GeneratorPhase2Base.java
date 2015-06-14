package jfcraft.gen;

/** Base for all phase 2 generators.
 *
 * @author pquiring
 */

import jfcraft.data.Chunk;

public interface GeneratorPhase2Base {
  public void getIDs();
  public void generate(Chunk chunk);
}
