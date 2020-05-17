package jfcraft.gen;

/** Chunk generator phase 2 : structures
 *
 * Any structure can only span 8 chunks in any direction,
 *  for a total of 17 chunks span (that's 272 blocks).
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.biome.*;
import jfcraft.feature.*;
import jfcraft.block.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Biomes.*;

public class GeneratorPhase2Earth implements GeneratorPhase2Base {
  private Chunk chunk;
  private BiomeData data = new BiomeData();

  public void getIDs() {}

  public void reset() {
  }

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    chunk.needPhase2 = false;
    chunk.dirty = true;

    data.setChunk(chunk);

    if (data.c1 % 500 == 0) {
      new River().build(chunk, data);
    }

    if (data.c2 % 50 == 0) {
      new Cave().build(chunk, data);
    }

    if (data.c3 % 1000 == 0) {
      new Ravine().build(chunk, data);
    }

    if ((data.c1 ^ data.c2) % 1000 == 0) {
      new Cavern().addCavern(chunk, data);
    }
  }
}
