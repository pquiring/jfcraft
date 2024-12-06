package jfcraft.gen;

/** Chunk Generator
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

import java.util.*;

import javaforce.*;

import jfcraft.block.*;
import jfcraft.data.*;
import static jfcraft.data.Biomes.*;

public class GeneratorPhase1Nether implements GeneratorPhase1Base {
  public World world;
  public GeneratorChunk chunk;
  private Random r = new Random();

  public void getIDs() {}

  public void reset() {}

  private void getSeed() {
    float _r1 = Static.noises[Static.N_RANDOM1].noise_2d(chunk.cx, chunk.cz);  //-1,1
    float _r2 = Static.noises[Static.N_RANDOM2].noise_2d(chunk.cx, chunk.cz);  //-1,1
    int _i1 = Float.floatToRawIntBits(_r1);
    int _i2 = Float.floatToRawIntBits(_r2);
    long seed = _i1;
    seed <<= 32;
    seed |= _i2;
    chunk.seed = seed;
    r.setSeed(seed);
  }

  public void generateBiomes() {
    int p = 0;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        chunk.elev[p] = 128;
        chunk.temp[p] = 100;
        chunk.rain[p] = 0;
        chunk.biome[p] = NETHER;
        p++;
      }
    }
  }

  public Chunk generate(int dim, int cx, int cz) {
    if (world == null) {
      world = Static.server.world;
    }

    chunk.clear();

    getSeed();

    generateBiomes();

    generate_default();

    return chunk.toChunk();
  }

  public void generate_default() {
    int p =0;
    Random r = new Random();
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    Noise noise = Static.noises[Static.N_NETHER];
    for(int y=0;y<128;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          if (y == 0 || y == 127) {
            chunk.setBlock(x,y,z, Blocks.BEDROCK, 0);
          } else {
            if (noise.noise_3d(wx + x, y, wz + z) > 0) {
              chunk.setBlock(x,y,z, Blocks.NETHER_RACK, 0);
            }
          }
          p++;
        }
      }
    }
    //now add clusters of minerals in ground
    int x,y,z,c;
    //quartz
    for(int a=0;a<32;a++) {
      x = r.nextInt(16);
      y = r.nextInt(100)+1;
      z = r.nextInt(16);
      c = r.nextInt(5) + 5;
      for(int b=0;b<c;b++) {
        p = x + y * 256 + z * 16;
        if (chunk.getBlock(x,y,z) == Blocks.NETHER_RACK) {
          chunk.setBlock(x,y,z, Blocks.NETHER_QUARTZ_ORE, 0);
        }
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
}
