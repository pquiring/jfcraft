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
import static jfcraft.data.Chunk.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Biomes.*;

public class GeneratorPhase1Earth implements GeneratorPhase1Base {
  public World world;
  public Chunk chunk;
//  public BiomeData data = new BiomeData();

  private char blocks[] = new char[16*256*16];
  private byte bits[] = new byte[16*256*16];
  private char blocks2[] = new char[16*256*16];
  private byte bits2[] = new byte[16*256*16];

  public void reset() {}

  private void getSeed() {
    float _r1 = Static.noiseFloat(Static.N_RANDOM1, chunk.cx, chunk.cz);  //-1,1
    float _r2 = Static.noiseFloat(Static.N_RANDOM2, chunk.cx, chunk.cz);  //-1,1
    int _i1 = Float.floatToRawIntBits(_r1);
    int _i2 = Float.floatToRawIntBits(_r2);
    long seed = _i1;
    seed <<= 32;
    seed |= _i2;
    chunk.seed = seed;
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

        float plains = Static.abs(Static.noiseFloat(Static.N_ELEV1,wx, wz) * 3.0f);
        float swamps = 0;

        if (biome == SWAMP) {
          float scale = 1.0f * clamp(rain, 66.0f, 71.0f) * clamp(temp, 50.0f, 55.f);
          swamps = Static.abs(Static.noiseFloat(Static.N_ELEV4,wx, wz) * 3.0f) * scale;
        }

        float hills = Static.noiseFloat(Static.N_ELEV2,wx, wz) * 15.0f;

        float extreme = Static.noiseFloat(Static.N_ELEV3,wx, wz) * 75.0f;

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

        //fill in stone
        blocks[p] = Blocks.BEDROCK;
        int ielev = Static.ceil(elev);
        for(int y=1;y<=ielev;y++) {
          blocks[p + y * 256] = Blocks.STONE;
        }

        chunk.temp[p] = temp;
        chunk.rain[p] = rain;
        chunk.biome[p] = biome;
        chunk.elev[p] = elev;
        p++;
      }
    }
  }

  public Chunk generate(int dim, int cx, int cz) {
    if (world == null) {
      world = Static.server.world;
    }
    chunk = new Chunk(dim,cx,cz);
//    data.setChunk(chunk);

    getSeed();

    fill();

    generateBiomes();

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

  private void fill() {
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
