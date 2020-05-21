package jfcraft.gen;

/** Chunk generator phase 2 : structures (blueprints)
 *
 * Any structure can only span to adjacent chunks in any direction.
 * Max blueprint size = 16x256x16
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

    if ((chunk.cx !=0 && chunk.cz !=0) && (data.c1 ^ data.c3) % 10033 == 0) {
      if (chunk.biome[0] == Biomes.OCEAN) {
        addBlueprint("shipwreck", 32, Static.SEALEVEL - 8);
      } else {
        addBlueprint("cabin", Static.SEALEVEL, 255);
      }
    }

  }

  private void addBlueprint(String name, int elevMin, int elevMax) {
    BluePrint print = chunk.world.getBluePrint(name);
    int elev = (int)chunk.elev[8 * 16 + 8] + 1;
    if (elev + print.Y > elevMax) return;
    if (elev < elevMin) return;
    if (chunk.getID(8, elev+1, 8) != 0) return;
    int ang = data.c1 % 4;
    switch (ang) {
      case 0: break;  //no change
      case 1: print.rotateY(R90); break;
      case 2: print.rotateY(R180); break;
      case 3: print.rotateY(R270); break;
    }
    print.writeChunk(chunk, 0, 0, 0, 0, elev, 0, print.X, print.Y, print.Z);
    //rotate back
    switch (ang) {
      case 0: break;  //no change
      case 1: print.rotateY(R270); break;
      case 2: print.rotateY(R180); break;
      case 3: print.rotateY(R90); break;
    }
  }
}
