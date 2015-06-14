package jfcraft.gen;

/** Base for all phase 3 generators.
 *
 * @author pquiring
 */

import jfcraft.data.Chunk;

public interface GeneratorPhase3Base {
  public void getIDs();
  public void generate(Chunk chunk);
}
