package jfcraft.gen;

/** Chunk Generator
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

import java.util.*;

import jfcraft.data.*;
import jfcraft.biome.*;
import jfcraft.feature.*;
import static jfcraft.data.Chunk.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.data.Static.*;

public class GeneratorPhase1Earth implements GeneratorPhase1Base {
  private GeneratorChunk chunk = new GeneratorChunk();
  private BiomeData data = new BiomeData();

  public void reset() {}

  public Chunk generate(int dim, int cx, int cz) {
    chunk.clear();
    chunk.seed = getSeed();
    chunk.dim = dim;
    chunk.cx = cx;
    chunk.cz = cz;

    data.setChunk(chunk);

    generateBiomes();

    fillStone();

    if (!hasOcean() && !Static.server.world.options.doFlatWorld) {
      float features = Static.noiseFloat(Static.N_ELEV3, cx*16, cz*16) * 100.0f;

      if (features > 50f) {
        //new Fortress().build(chunk, data);  //TODO
      } else if (features < -50f) {
        new MineShaft().build(chunk, data);
      } else {
        new Cave().build(chunk, data);
      }
    }

    new TopSoil().build(chunk, data);

    addDeposits();

    return chunk.toChunk();
  }

  private long getSeed() {
    float _r1 = Static.noiseFloat(Static.N_RANDOM1, chunk.cx, chunk.cz);  //-1,1
    float _r2 = Static.noiseFloat(Static.N_RANDOM2, chunk.cx, chunk.cz);  //-1,1
    int _i1 = Float.floatToRawIntBits(_r1);
    int _i2 = Float.floatToRawIntBits(_r2);
    long seed = _i1;
    seed <<= 32;
    seed |= _i2;
    return seed;
  }

  public void getIDs() {}

  //clamp 0.0f - 1.0f
  private float clamp(float val, float min, float max) {
    if (val <= min) return 0;
    if (val >= max) return 1;
    return (val - min) / (max - min);
  }

  private void generateBiomes() {
    int p = 0;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        int wx = chunk.cx * 16 + x;
        int wz = chunk.cz * 16 + z;
        float temp = Static.noiseFloat(Static.N_TEMP,wx, wz) * 50.0f + 50.0f;  //0 - 100
        float rain = Static.noiseFloat(Static.N_RAIN,wx, wz) * 50.0f + 50.0f;  //0 - 100
        float elev;

        //determine biome type (Biome....)
        byte biome = -1;
        if (temp < 16.0) {
          biome = TUNDRA;
        } else if (temp < 32.0) {
          biome = TAIGA;
        } else {
          if (rain > 66.0) {
            if (temp > 80.0) {
              biome = JUNGLE;
            } else if (temp > 50.0) {
              biome = SWAMP;
            } else {
              biome = DARK_FOREST;
            }
          } else if (rain < 33.0) {
            biome = temp < 80.0 ? PLAINS : DESERT;
          } else {
            biome = FOREST;
          }
        }

        /** Plains map defined the base elevation. */
        float plains = Static.abs(Static.noiseFloat(Static.N_ELEV1,wx, wz) * 3.0f);

        float swamps = 0;

        if (biome == SWAMP) {
          /** If biome == SWAMP than the elevation is lowered to create swamps. */
          float scale = 1.0f * clamp(rain, 66.0f, 71.0f) * clamp(temp, 50.0f, 55.f);
          swamps = Static.abs(Static.noiseFloat(Static.N_ELEV4,wx, wz) * 3.0f) * scale;
        }

        /** Hills map.  Creates small hills in elevation.
         * Range : -15 to +15
         * Hills : +10 to +15
         * No change : -15 to 10
         */
        float hills = Static.noiseFloat(Static.N_ELEV2,wx, wz) * 15.0f;

        /** Extreme range defines mountains and oceans.
         * Range : -75 to +75
         * Mountains : +25 to +75
         * No change : -25 to +25
         * Oceans : -75 to -25
         */
        float extreme = Static.noiseFloat(Static.N_ELEV3, wx, wz) * 75.0f;

        /** When rivers map intersects with extreme map it creates a river.
         *  The river map does NOT extend into ranges that cause mountains or oceans.
         *  Range : -20 to 20
         */
        float rivers = Static.noiseFloat(Static.N_ELEV6, wx, wz) * 20.0f;

        elev = (Static.SEALEVEL + plains - swamps);
        if (extreme <= -25.0f) {
          extreme += 25.0f;
          elev += extreme;
          biome = OCEAN;
        }
        else if (extreme >= 25.0f) {
          extreme -= 25.0f;
          elev += extreme;
        }

        if (hills > 5.0f) {
          hills -= 5.0f;
          elev += hills;
        }

        float riverElev = Static.abs(extreme - rivers);
        if (riverElev <= 5f) {
          riverElev = 5f - riverElev;  //inverse level
          elev -= riverElev * 0.7f;
          if (riverElev > 2) {
            //add clay later
            chunk.river[p] = true;
          }
        }

        if (Static.server.world.options.doFlatWorld) {
          elev = Static.SEALEVEL + 1;
        }

        chunk.temp[p] = temp;
        chunk.rain[p] = rain;
        chunk.biome[p] = biome;
        chunk.elev[p] = elev;
        p++;
      }
    }
  }

  private void fillStone() {
    int p = 0;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        float elev = chunk.elev[p];
        //fill in stone
        chunk.setBlock(x,0,z, Blocks.BEDROCK, 0);
        int ielev = Static.ceil(elev);
        for(int y=1;y<=ielev;y++) {
          chunk.setBlock(x,y,z, Blocks.STONE, 0);
        }
        p++;
      }
    }
  }

  private void addDeposits() {
    /*
    mineral  amt/chunk  levels
    -------  ---------  ------
    coal     142.6      any
    iron     77         1-64
    gold     8.2        1-32
    diamond  3.1        1-16
    emerald  3-8        1-32    (mountain biome only)
    redstone 24.8       1-16
    */
    Random r = new Random();
    r.setSeed(chunk.seed);
    int x,y,z,c;
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    //3d deposits
    for(z=0;z<16;z++) {
      for(x=0;x<16;x++) {
        for(y=0;y<Static.SEALEVEL;y++) {
          float soil = Static.noises[Static.N_SOIL].noise_3d(wx + x, y, wz + z) * 100.0f;

          //add soil/gravel deposites
          if (soil <= -50) {
            //dirt
            if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.DIRT, 0);
          } else if (soil >= 50) {
            //gravel
            if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.GRAVEL, 0);
          }

          float cavern = Static.noises[Static.N_CAVERNS].noise_3d(wx, y, wz) * 100.0f;

//this is too much
          if (cavern <= -90) {
            //empty cavern
//            if (chunk.getBlockType(x,y,z) == Blocks.STONE) chunk.clearBlock(x,y,z);
          } else if (cavern > 90) {
            //water/lava cavern
            //TODO : could put water above lava ???
//            if (chunk.getBlockType(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z,y < 10 ? Blocks.LAVA : Blocks.WATER,0);
          }
        }
      }
    }

    //coal
    for(int a=0;a<32;a++) {
      x = r.nextInt(16);
      y = r.nextInt(100)+1;
      z = r.nextInt(16);
      c = r.nextInt(5) + 5;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.COAL_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 255) y = 255;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //iron
    for(int a=0;a<16;a++) {
      x = r.nextInt(16);
      y = r.nextInt(64)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 4;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.IRON_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > Static.SEALEVEL) y = Static.SEALEVEL;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //copper
    for(int a=0;a<16;a++) {
      x = r.nextInt(16);
      y = r.nextInt(64)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 4;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.COPPER_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > Static.SEALEVEL) y = Static.SEALEVEL;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //gold
    for(int a=0;a<2;a++) {
      x = r.nextInt(16);
      y = r.nextInt(32)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 2;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.GOLD_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 32) y = 32;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //diamond
    for(int a=0;a<1;a++) {
      x = r.nextInt(16);
      y = r.nextInt(16)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 1;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.DIAMOND_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 16) y = 16;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //emerald
    for(int a=0;a<1;a++) {
      x = r.nextInt(16);
      y = r.nextInt(32)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 1;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.EMERALD_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 32) y = 32;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //redstone
    for(int a=0;a<4;a++) {
      x = r.nextInt(16);
      y = r.nextInt(16)+1;
      z = r.nextInt(16);
      c = r.nextInt(2) + 1;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.REDSTONE_ORE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 16) y = 16;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }

    //deepslate
    for(int a=0;a<32;a++) {
      x = r.nextInt(16);
      y = r.nextInt(100)+1;
      z = r.nextInt(16);
      c = r.nextInt(5) + 5;
      for(int b=0;b<c;b++) {
        if (chunk.getBlock(x,y,z) == Blocks.STONE) chunk.setBlock(x,y,z, Blocks.DEEPSLATE, 0);
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 255) y = 255;
        z += r.nextInt(3)-1;
        if (z < 0) z = 0;
        if (z > 15) z = 15;
      }
    }
  }

  public boolean hasOcean() {
    for(int p=0;p<16*16;p++) {
      if (chunk.biome[p] == Biomes.OCEAN) return true;
    }
    return false;
  }
}
