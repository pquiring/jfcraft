package jfcraft.biome;

/**
 * Biome Random Data
 */

import jfcraft.data.*;
import jfcraft.gen.*;
import static jfcraft.data.Static.*;

public class BiomeData {
  public int b1;  //per block
  public int b2;  //per block
  public int b3;  //per block

  public float bf1;  //per block
  public float bf2;  //per block
  public float bf3;  //per block

  public int c1;  //per chunk
  public int c2;  //per chunk
  public int c3;  //per chunk

  public float cf1;  //per chunk
  public float cf2;  //per chunk
  public float cf3;  //per chunk

  public float temp;  //0-100
  public float rain;  //0-100

  public int cx, cz;
  public int wx, wz;

  public void setChunk(Chunk chunk) {
    cx = chunk.cx;
    cz = chunk.cz;
    wx = chunk.cx * 16;
    wz = chunk.cz * 16;

    generateChunkValues();
  }

  public void setChunk(GeneratorChunk chunk) {
    cx = chunk.cx;
    cz = chunk.cz;
    wx = chunk.cx * 16;
    wz = chunk.cz * 16;

    generateChunkValues();
  }

  private void generateChunkValues() {
    c1 = Static.noiseInt(N_RANDOM1, Integer.MAX_VALUE, cx, cz);
    c2 = Static.noiseInt(N_RANDOM2, Integer.MAX_VALUE, cx, cz);
    c3 = Static.noiseInt(N_RANDOM3, Integer.MAX_VALUE, cx, cz);

    cf1 = Static.noiseFloat(N_RANDOM1, Integer.MAX_VALUE, cx, cz);
    cf2 = Static.noiseFloat(N_RANDOM2, Integer.MAX_VALUE, cx, cz);
    cf3 = Static.noiseFloat(N_RANDOM3, Integer.MAX_VALUE, cx, cz);
  }

  public void setBlock(int x, int z) {
    b1 = Static.noiseInt(N_RANDOM1, Integer.MAX_VALUE, wx+x, wz+z);
    b2 = Static.noiseInt(N_RANDOM2, Integer.MAX_VALUE, wx+x, wz+z);
    b3 = Static.noiseInt(N_RANDOM3, Integer.MAX_VALUE, wx+x, wz+z);

    bf1 = Static.noiseFloat(N_RANDOM1, Integer.MAX_VALUE, wx+x, wz+z);
    bf2 = Static.noiseFloat(N_RANDOM2, Integer.MAX_VALUE, wx+x, wz+z);
    bf3 = Static.noiseFloat(N_RANDOM3, Integer.MAX_VALUE, wx+x, wz+z);
  }
}
