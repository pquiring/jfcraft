package jfcraft.feature;

/**
 * TopSoil.
 *
 * Adds top layer of grass/dirt/clay/water/ice/snow/etc.
 *
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import jfcraft.gen.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.data.Static.*;

public class TopSoil {
  public void build(GeneratorChunk chunk, BiomeData data) {
    //add grass/dirt/sand/snow/etc.
    char id, id2, id3;
    int p = 0;
    for(int z=0;z<16;z++) {
      for(int x=0;x<16;x++) {
        data.setBlock(x, z);
        int wx = chunk.cx * 16 + x;
        int wz = chunk.cz * 16 + z;
        float dirt = 3.0f + Static.abs(data.bf1) * 3.0f;
        float temp = chunk.temp[p];
        float rain = chunk.rain[p];
        int biome = chunk.biome[p];
        int elev = Static.ceil(chunk.elev[p]);

        if (chunk.river[p] && elev < Static.SEALEVEL) {
          chunk.setBlock(x,elev,z,Blocks.CLAY,0);
        }
        for(int y=Static.SEALEVEL;y>1;y--) {
          if (chunk.getBlock(x, y, z) != Blocks.AIR || chunk.getBlock2(x, y, z) != Blocks.AIR) break;
          chunk.setBlock2(x,y,z,Blocks.WATER,0);
        }
        if (temp < 32.0) {
          if (chunk.getBlock2(x,Static.SEALEVEL,z) == Blocks.WATER) {
            chunk.clearBlock2(x, Static.SEALEVEL, z);
            chunk.setBlock(x, Static.SEALEVEL, z, Blocks.ICEBLOCK, 0);
          }
          id = chunk.getBlock(x, elev+1, z);
          id2 = chunk.getBlock2(x, elev+1, z);
          id3 = chunk.getBlock2(x, elev, z);
          if (id == Blocks.AIR && id2 != Blocks.WATER && id2 != Blocks.LAVA
            && id3 != Blocks.WATER && id3 != Blocks.LAVA)
          {
            chunk.setBlock(x, elev+1, z, Blocks.SNOW, 0);
          }
        }

        float sand = 0f;
        float clay = 0f;

        if (biome == OCEAN) {
          if (elev >= Static.SEALEVEL) {
            sand = 3.0f + Static.abs(data.bf2) * 3.0f;  //beach
          } else {
            float soil = Static.noises[Static.N_SOIL].noise_3d(wx, -Static.SEALEVEL, wz) * 100.0f;
            //add sand/clay deposites
            if (soil <= -50) {
              sand = 1.0f;
            } else if (soil >= 50) {
              clay = 1.0f;
            }
          }
        }

        switch (biome) {
          case DESERT:
            sand = 5.0f + Static.abs(data.bf3) * 2.0f;
            dirt = 0.0f;
            break;
          case PLAINS:
            break;
          case TAIGA:
            break;
          case FOREST:
            break;
          case SWAMP:
            break;
        }

        for(int y=elev;y>0;y--) {
          if (chunk.getBlock(x, y, z) == Blocks.STONE) {
            if (clay > 0.0f) {
              chunk.setBlock(x,y,z,Blocks.CLAY,0);
              clay -= 1.0f;
            } else if (sand > 0.0f) {
              chunk.setBlock(x,y,z,Blocks.SAND,0);
              sand -= 1.0f;
            } else if (dirt > 0.0f) {
              chunk.setBlock(x,y,z,Blocks.DIRT,0);
              dirt -= 1.0f;
            }
          }
        }

        //add grass
        for(int y=elev;y>=Static.SEALEVEL;y--) {
          id = chunk.getBlock(x,y,z);
          if (id != Blocks.DIRT) break;
          chunk.setBlock(x, y, z, y == Static.SEALEVEL ? Blocks.GRASSBANK : Blocks.GRASS, 0);
          boolean n = chunk.getBlock(x  ,y,z-1) != 0;
          boolean e = chunk.getBlock(x+1,y,z  ) != 0;
          boolean s = chunk.getBlock(x  ,y,z+1) != 0;
          boolean w = chunk.getBlock(x-1,y,z  ) != 0;
          if (n && e && s && w) break;
        }

        p++;
      }
    }
  }
}
