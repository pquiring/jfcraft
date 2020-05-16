package jfcraft.gen;

/**
 * TopSoil
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.data.Static.*;

public class TopSoil {
  public void build(Chunk chunk, BiomeData data) {
    //add grass/dirt/sand/snow/etc.
    char id;
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
        int newelev = elev;

        while (chunk.getBlock(x,newelev,z).id == 0 && newelev > 1) {
          newelev--;
        }

        if (newelev < Static.SEALEVEL && (biome == OCEAN || biome == SWAMP)) {
          for(int y=Static.SEALEVEL;y>1;y--) {
            if (chunk.getBlock(x, y, z).id != Blocks.AIR) break;
            chunk.setBlock(x,y,z,Blocks.WATER,0);
          }
          if (temp < 32.0) {
            chunk.clearBlock(x, Static.SEALEVEL, z);
            chunk.setBlock(x, Static.SEALEVEL, z, Blocks.ICEBLOCK, 0);
          }
        } else {
          if (temp < 32.0) {
            id = chunk.getBlock(x, newelev, z).id;
            if (id != Blocks.AIR && id != Blocks.WATER && id != Blocks.LAVA) {
              chunk.setBlock(x, newelev+1, z, Blocks.SNOW, 0);
            }
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
          if (chunk.getID(x, y, z) == Blocks.STONE) {
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
        for(int y=newelev;y>=Static.SEALEVEL;y--) {
          id = chunk.getID(x,y,z);
          if (id != Blocks.DIRT) break;
          chunk.setBlock(x, y, z, y == Static.SEALEVEL ? Blocks.GRASSBANK : Blocks.GRASS, 0);
          boolean n = chunk.getID(x  ,y,z-1) != 0;
          boolean e = chunk.getID(x+1,y,z  ) != 0;
          boolean s = chunk.getID(x  ,y,z+1) != 0;
          boolean w = chunk.getID(x-1,y,z  ) != 0;
          if (n && e && s && w) break;
        }

        p++;
      }
    }
  }
}
