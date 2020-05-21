package jfcraft.gen;

/**
 * GeneratorChunk
 *
 * Used in phase 1 of chunk generation.
 *
 * Optimized for fast fills.
 *
 */

import java.util.Arrays;

import jfcraft.data.*;

public class GeneratorChunk {
  private char blocks[] = new char[16*256*16];
  private byte bits[] = new byte[16*256*16];
  private char blocks2[] = new char[16*256*16];
  private byte bits2[] = new byte[16*256*16];

  public byte biome[] = new byte[16 * 16];
  public float temp[] = new float[16 * 16];
  public float rain[] = new float[16 * 16];
  public float elev[] = new float[16 * 16];
  public float depth[] = new float[16 * 16];

  public boolean river[] = new boolean[16 * 16];

  public int dim,cx,cz;
  public long seed;

  public void clear() {
    Arrays.fill(blocks, (char)0);
    Arrays.fill(bits, (byte)0);
    Arrays.fill(blocks2, (char)0);
    Arrays.fill(bits2, (byte)0);
    Arrays.fill(biome, (byte)0);
    Arrays.fill(temp, 0f);
    Arrays.fill(rain, 0f);
    Arrays.fill(elev, 0f);
    Arrays.fill(depth, 0f);
    Arrays.fill(river, false);
  }

  public void setBlock(int x,int y,int z,char id, int _bits) {
    if (x < 0) return;
    if (x > 15) return;
    if (y < 0) return;
    if (y > 255) return;
    if (z < 0) return;
    if (z > 15) return;
    blocks[y * 256 + z * 16 + x] = id;
    bits[y * 256 + z * 16 + x] = (byte)_bits;
  }

  public void setBlock2(int x,int y,int z,char id, int _bits) {
    if (x < 0) return;
    if (x > 15) return;
    if (y < 0) return;
    if (y > 255) return;
    if (z < 0) return;
    if (z > 15) return;
    blocks2[y * 256 + z * 16 + x] = id;
    bits2[y * 256 + z * 16 + x] = (byte)_bits;
  }

  public void clearBlock(int x,int y,int z) {
    if (x < 0) return;
    if (x > 15) return;
    if (y < 0) return;
    if (y > 255) return;
    if (z < 0) return;
    if (z > 15) return;
    blocks[y * 256 + z * 16 + x] = 0;
    bits[y * 256 + z * 16 + x] = 0;
  }

  public void clearBlock2(int x,int y,int z) {
    if (x < 0) return;
    if (x > 15) return;
    if (y < 0) return;
    if (y > 255) return;
    if (z < 0) return;
    if (z > 15) return;
    blocks2[y * 256 + z * 16 + x] = 0;
    bits2[y * 256 + z * 16 + x] = 0;
  }

  public char getBlock(int x,int y,int z) {
    if (x < 0) return 0;
    if (x > 15) return 0;
    if (y < 0) return 0;
    if (y > 255) return 0;
    if (z < 0) return 0;
    if (z > 15) return 0;
    return blocks[y * 256 + z * 16 + x];
  }

  public char getBlock2(int x,int y,int z) {
    if (x < 0) return 0;
    if (x > 15) return 0;
    if (y < 0) return 0;
    if (y > 255) return 0;
    if (z < 0) return 0;
    if (z > 15) return 0;
    return blocks2[y * 256 + z * 16 + x];
  }

  public float getElev(int x,int z) {
    if (x < 0) return 0;
    if (x > 15) return 0;
    if (z < 0) return 0;
    if (z > 15) return 0;
    return elev[z * 16 + x];
  }

  public void setElev(int x,int z,float lvl) {
    if (x < 0) return;
    if (x > 15) return;
    if (z < 0) return;
    if (z > 15) return;
    elev[z * 16 + x] = lvl;
  }

  /** Converts GeneratorChunk to Chunk */
  public Chunk toChunk() {
    Chunk chunk = new Chunk(dim,cx,cz);
    chunk.seed = seed;
    System.arraycopy(biome, 0, chunk.biome, 0, biome.length);
    System.arraycopy(temp, 0, chunk.temp, 0, temp.length);
    System.arraycopy(rain, 0, chunk.rain, 0, rain.length);
    System.arraycopy(elev, 0, chunk.elev, 0, elev.length);
    System.arraycopy(depth, 0, chunk.depth, 0, depth.length);
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
    return chunk;
  }
}
