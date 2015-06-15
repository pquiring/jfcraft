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
import static jfcraft.data.Chunk.*;
import static jfcraft.data.Direction.*;

public class GeneratorPhase1Earth implements GeneratorPhase1Base {
  public World world;
  public Chunk chunk;
  private Random r = new Random();
  private char blocks[] = new char[16*256*16];
  private byte bits[] = new byte[16*256*16];
  private char blocks2[] = new char[16*256*16];
  private byte bits2[] = new byte[16*256*16];

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

  public void getIDs() {}

  private void generateBiomes() {
    int p = 0;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        int wx = chunk.cx * 16 + x;
        int wz = chunk.cz * 16 + z;
        float temp = Static.noises[Static.N_TEMP].noise_2d(wx, wz) * 60.0f + 50.0f;
        float rain = Static.noises[Static.N_RAIN].noise_2d(wx, wz) * 60.0f + 50.0f;

        //determine biome type (Biome....) - could be overriden by oceans
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
              biome = FOREST;
            }
          } else if (rain < 33.0) {
            biome = temp < 80.0 ? PLAINS : DESERT;
          } else {
            biome = FOREST;
          }
        }

        float plains = Static.noises[Static.N_ELEV1].noise_2d(wx, wz) * 10.0f + 10.0f;  //0-20
        float plains_scale = Static.noises[Static.N_ELEV2].noise_2d(wx, wz);

        //mountains
        float mountains = Static.noises[Static.N_ELEV3].noise_2d(wx, wz) * 30.0f + 30.0f;  //0-60
        float mountains_scale = Static.noises[Static.N_ELEV4].noise_2d(wx, wz) * 2.0f;
        if (mountains_scale > 1.0) mountains_scale = 1.0f;

        //oceans
        float oceans = Static.noises[Static.N_ELEV5].noise_2d(wx, wz) * 10.0f + 11.0f;  //1-21
        float oceans_scale = Static.noises[Static.N_ELEV6].noise_2d(wx, wz) * 2.0f;
        if (oceans_scale > 1.0) oceans_scale = 1.0f;
        mountains_scale -= oceans_scale;
        plains_scale -= oceans_scale;

        if (biome == SWAMP) {
          //swamp area = lower plains_scale
          plains_scale /= (temp - 49);  //temp=50-80
        }

        chunk.elev[p] = (64 + (plains * plains_scale));
        if (oceans_scale > 0.0f) {
          chunk.elev[p] -= (oceans * oceans_scale);
          biome = OCEAN;
        }
        else if (mountains_scale > 0.0f) {
          chunk.elev[p] += (mountains * mountains_scale);
          biome = MOUNTAIN;
        }

        chunk.temp[p] = temp;
        chunk.rain[p] = rain;
        chunk.biome[p] = biome;
        p++;
      }
    }
  }

  public Chunk generate(int dim, int cx, int cz) {
    if (world == null) {
      world = Static.server.world;
    }
    chunk = new Chunk(dim,cx,cz);

    getSeed();

    generateBiomes();

    reset();

    if (world.type.equals("default")) {
      generate_default();
    } else {
      generate_custom();
    }

    copy();

    return chunk;
  }

  public void generate_custom() {
    //TODO
  }

  public void generate_default() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = x + z * 16;
        int wx = chunk.cx * 16 + x;
        int wz = chunk.cz * 16 + z;

        float temp = chunk.temp[p];
        float rain = chunk.rain[p];
        int elev = (int)Math.ceil(chunk.elev[p]);
        int bt = chunk.biome[p];
        boolean mountain = (bt & Chunk.MOUNTAIN) != 0;
        bt &= 0x7;

        float sand = 0.0f;
        float dirt = 5.0f + (r.nextFloat() - 0.5f) * 6.0f;
        float clay = 0.0f;
        float grass = 1.0f;

        if (bt == Chunk.OCEAN) {
          if (chunk.elev[p] > 64) {
            sand = 3.0f + (r.nextFloat() - 0.5f) * 4.0f;  //beach
          } else {
            float soil = Static.noises[Static.N_SOIL].noise_3d(wx, -64, wz) * 100.0f;
            //add sand/clay deposites
            if (soil <= -50) {
              sand = 1.0f;
            } else if (soil >= 50) {
              clay = 1.0f;
            }
          }
        }

        switch (bt) {
          case Chunk.DESERT:
            grass = 0.0f;
            sand = 5.0f + (r.nextFloat() - 0.5f) * 4.0f;
            dirt = 0.0f;
            break;
          case Chunk.PLAINS:
            if (r.nextInt() % 3 == 0) {
              blocks[p + elev * 256] = Blocks.TALLGRASS;
            }
            break;
          case Chunk.TAIGA:
            break;
          case Chunk.FOREST:
            break;
          case Chunk.SWAMP:
            if (elev < 64 && r.nextInt() % 2 == 0) {
              blocks[p + 65 * 256] = Blocks.LILLYPAD;
              bits[p + 65 * 256] = (byte)Chunk.makeBits(B, 0);
            }
            break;
          case Chunk.OCEAN:
            grass = 0.0f;
            break;
        }

        if (elev < 64) grass = 0;

        blocks[p] = Blocks.BEDROCK;
        for(int y=elev;y>0;y--) {
          if (clay > 0.0f) {
            blocks[p + y * 256] = Blocks.CLAY;
            clay -= 1.0f;
          } else if (sand > 0.0f) {
            blocks[p + y * 256] = Blocks.SAND;
            sand -= 1.0f;
          } else if (grass > 0.0f) {
            blocks[p + y * 256] = Blocks.GRASS;
            grass -= 1.0f;
          } else if (dirt > 0.0f) {
            blocks[p + y * 256] = Blocks.DIRT;
            dirt -= 1.0f;
          } else {
            blocks[p + y * 256] = Blocks.STONE;
          }
        }
        if (elev < 64) {
          for(int y=elev+1;y<64;y++) {
            blocks2[p + y * 256] = Blocks.WATER;
          }
          if (temp < 32.0) {
            blocks[p + 64 * 256] = Blocks.ICEBLOCK;
            bits[p + 64 * 256] = 0;
          } else {
            blocks2[p + 64 * 256] = Blocks.WATER;
          }
        } else {
          if (temp < 32.0) {
            blocks2[p + (elev+1) * 256] = Blocks.SNOW;
          }
        }

        //soil/gravel
        for(int y=0;y<64;y++) {
          float soil = Static.noises[Static.N_SOIL].noise_3d(wx, y, wz) * 100.0f;

          //add soil/gravel deposites
          if (soil <= -50) {
            //dirt
            if (blocks[p + y * 256] == Blocks.STONE) blocks[p + y * 256] = Blocks.DIRT;
          } else if (soil >= 50) {
            //gravel
            if (blocks[p + y * 256] == Blocks.STONE) blocks[p + y * 256] = Blocks.GRAVEL;
          }
        }
      }  //for z
    }  //for x
    //now add clusters of minerals in ground
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
    int x,y,z,c,p;
    //coal
    for(int a=0;a<32;a++) {
      x = r.nextInt(16);
      y = r.nextInt(100)+1;
      z = r.nextInt(16);
      c = r.nextInt(5) + 5;
      for(int b=0;b<c;b++) {
        p = x + y * 256 + z * 16;
        if (blocks[p] == Blocks.STONE) blocks[p] = Blocks.COALORE;
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
        p = x + y * 256 + z * 16;
        if (blocks[p] == Blocks.STONE) blocks[p] = Blocks.IRONORE;
        x += r.nextInt(3)-1;
        if (x < 0) x = 0;
        if (x > 15) x = 15;
        y += r.nextInt(3)-1;
        if (y < 1) y = 1;
        if (y > 64) y = 64;
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
        p = x + y * 256 + z * 16;
        if (blocks[p] == Blocks.STONE) blocks[p] = Blocks.GOLDORE;
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
        p = x + y * 256 + z * 16;
        if (blocks[p] == Blocks.STONE) blocks[p] = Blocks.DIAMOND_ORE;
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
        p = x + y * 256 + z * 16;
        if ((blocks[p] == Blocks.STONE) && (chunk.elev[x + z * 16] > 30)) {
          blocks[p] = Blocks.EMERALD_ORE;
        }
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
        p = x + y * 256 + z * 16;
        if (blocks[p] == Blocks.STONE) blocks[p] = Blocks.REDSTONE_ORE;
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
  }

  private void setBlock(int x,int y,int z,char id, int _bits) {
    int p = y * 256 + z * 16 + x;
    if (blocks[p] != 0) return;
    blocks[p] = id;
    bits[p] = (byte)_bits;
  }

  private void reset() {
    Arrays.fill(blocks, (char)0);
    Arrays.fill(bits, (byte)0);
    Arrays.fill(blocks2, (char)0);
    Arrays.fill(bits2, (byte)0);
  }

  private void copy() {
    int p, cnt;
    boolean empty;
    for(int y=0;y<256;y++) {
      empty = true;
      for(p=y*256,cnt=0;cnt<16*16;cnt++,p++) {
        if (blocks[p] != 0) {
          empty = false;
          break;
        }
      }
      if (!empty) {
        chunk.setPlane(y,
          Arrays.copyOfRange(blocks, y*256, y*256+256),
          Arrays.copyOfRange(bits, y*256, y*256+256));
      }
      empty = true;
      for(p=y*256,cnt=0;cnt<16*16;cnt++,p++) {
        if (blocks2[p] != 0) {
          empty = false;
          break;
        }
      }
      if (!empty) {
        chunk.setPlane2(y,
          Arrays.copyOfRange(blocks2, y*256, y*256+256),
          Arrays.copyOfRange(bits2, y*256, y*256+256));
      }
    }
  }
}
