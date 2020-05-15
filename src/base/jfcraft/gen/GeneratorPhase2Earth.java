package jfcraft.gen;

/** Chunk generator phase 2 : structures
 *
 * Any structure can only span 8 chunks in any direction,
 *  for a total of 17 chunks span (that's 272 blocks).
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.biome.*;
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

    if (data.c1 % 500 == 0) {
      new River().build(chunk, data);
    }

    //add grass/dirt/sand/snow/etc.
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

        //river may have lowered elev
        while (elev > 1 && chunk.getID(x, elev, z) == 0) {
          elev--;
        }
        chunk.elev[p] = elev;

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
        if (elev < Static.SEALEVEL) {
          for(int y=elev+1;y<=Static.SEALEVEL;y++) {
            chunk.setBlock(x,y,z,Blocks.WATER,0);
          }
          if (temp < 32.0) {
            chunk.clearBlock(x, Static.SEALEVEL, z);
            chunk.setBlock(x, Static.SEALEVEL, z, Blocks.ICEBLOCK, 0);
          }
        } else {
          if (temp < 32.0) {
            chunk.setBlock(x, elev+1, z, Blocks.SNOW, 0);
          }
        }

        //soil/gravel 3d deposits
        for(int y=0;y<Static.SEALEVEL;y++) {
          float soil = Static.noises[Static.N_SOIL].noise_3d(wx, y, wz) * 100.0f;

          //add soil/gravel deposites
          if (soil <= -50) {
            //dirt
            if (chunk.getID(x,y,z) == Blocks.STONE) chunk.setBlock(x, y, z, Blocks.DIRT, 0);
          } else if (soil >= 50) {
            //gravel
            if (chunk.getID(x,y,z) == Blocks.STONE) chunk.setBlock(x, y, z, Blocks.GRAVEL, 0);
          }
        }

        //add grass
        for(int y=elev;y>=Static.SEALEVEL;y--) {
          char id = chunk.getID(x,y,z);
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

    if (data.c2 % 100 == 0) {
      new Cave().addCaves(chunk, data);
    }
    if (data.c3 % 1000 == 0) {
      new Ravine().addRavine(chunk, data);
    }
    if ((data.c1 ^ data.c2) % 1000 == 0) {
      new Cavern().addCavern(chunk, data);
    }

    if ((data.c1 ^ data.c3) % 10000 == 0) {
      if (chunk.biome[0] == Biomes.OCEAN) {
        addBlueprint("shipwreck");
      } else {
        addBlueprint("cabin");
      }
    }
  }

  private void addBlueprint(String name) {
    BluePrint cabin = chunk.world.getBluePrint(name);
    int elev = (int)chunk.elev[8 * 16 + 8] + 1;
    if (elev + cabin.Y > 255) return;
    if (chunk.getID(8, elev+1, 8) != 0) return;
    int ang = data.c1 % 4;
    switch (ang) {
      case 0: break;  //no change
      case 1: cabin.rotateY(R90); break;
      case 2: cabin.rotateY(R180); break;
      case 3: cabin.rotateY(R270); break;
    }
    cabin.writeChunk(chunk, 0, 0, 0, 0, elev, 0, cabin.X, cabin.Y, cabin.Z);
    //rotate back
    switch (ang) {
      case 0: break;  //no change
      case 1: cabin.rotateY(R270); break;
      case 2: cabin.rotateY(R180); break;
      case 3: cabin.rotateY(R90); break;
    }
  }
}
