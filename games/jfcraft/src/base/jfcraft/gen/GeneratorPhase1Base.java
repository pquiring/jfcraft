package jfcraft.gen;

/** Base for all phase 1 generators.
 *
 * @author pquiring
 */

import jfcraft.data.Chunk;

public interface GeneratorPhase1Base {
  public void getIDs();
  public Chunk generate(int dim, int cx, int cz);
}
