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

public class GeneratorPhase1End implements GeneratorPhase1Base {
  public World world;
  public Chunk chunk;
  private Random r = new Random();
  private char blocks[] = new char[16*256*16];
  private byte bits[] = new byte[16*256*16];
  private char blocks2[] = new char[16*256*16];
  private byte bits2[] = new byte[16*256*16];

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
    Noise noise_top = Static.noises[Static.N_END_1];
    Noise noise_bottom = Static.noises[Static.N_END_2];
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        int wx = chunk.cx * 16 + x;
        int wz = chunk.cz * 16 + z;
        chunk.elev[p] = noise_top.noise_2d(wx, wz) * 5.0f + 64.0f;
        chunk.depth[p] = noise_bottom.noise_2d(wx, wz) * 5.0f + 16.0f;
        chunk.rain[p] = 0;
        chunk.biome[p] = Chunk.END;
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

    fill();

    generate_default();

    copy();

    return chunk;
  }

  public void generate_default() {
    int p;
    if (chunk.cx > 6) return;
    if (chunk.cx < -6) return;
    if (chunk.cz > 6) return;
    if (chunk.cz < -6) return;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        p = z * 16 + x;
        int y2 = (int)chunk.elev[p];
        int y1 = y2 - (int)chunk.depth[p];
        p += y1 * 256;
        for(int y=y1;y<y2;y++) {
          blocks[p] = Blocks.END_STONE;
          p+=256;
        }
      }
    }
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
