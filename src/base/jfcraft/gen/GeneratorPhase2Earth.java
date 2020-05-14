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

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;

public class GeneratorPhase2Earth implements GeneratorPhase2Base {
  private Chunk chunk;
  private int cx16, cz16;
  private Random r = new Random();

  public void getIDs() {}

  public void reset() {
  }

  public void generate(Chunk chunk) {
    this.chunk = chunk;
    cx16 = chunk.cx * 16;
    cz16 = chunk.cz * 16;

    chunk.needPhase2 = false;
    chunk.dirty = true;

    //extend grass down if dirt is visible
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = x + z * 16;
        int biome = chunk.biome[p];
        if (biome == Biomes.DESERT || biome == Biomes.OCEAN) continue;
        int elev = (int)Math.ceil(chunk.elev[p]);
        out:
        for(int y=elev-1;y>Static.SEALEVEL;y--) {
          boolean n = chunk.getID(x  ,y,z-1) != 0;
          boolean e = chunk.getID(x+1,y,z  ) != 0;
          boolean s = chunk.getID(x  ,y,z+1) != 0;
          boolean w = chunk.getID(x-1,y,z  ) != 0;
          if (n && e && s && w) break out;
          chunk.setBlock(x, y, z, Blocks.GRASS, 0);
        }
      }
    }

    r.setSeed(chunk.seed);

    if (r.nextInt(20) == 0) {
      addCaves();
    }
    if (r.nextInt(1000) == 0) {
      addRavine();
    }
    if (r.nextInt(10) == 0 || (chunk.cx == 0 && chunk.cz == 0)) {
      addRoom();
    }
    if (r.nextInt(10000) == 0 || (chunk.cx == 0 && chunk.cz == -3)) {
      if (chunk.biome[0] == Biomes.OCEAN) {
        addShipwreck();
      } else {
        addCabin();
      }
    }
  }
  private void setBlock(int x, int y, int z, char id, int bits) {
    if (id == 0) {
      clearBlock(x,y,z);
      return;
    }
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    c.setBlock(x, y, z, id, bits);
  }
  private void clearBlock(int x, int y, int z) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    c.clearBlock(x, y, z);
    if (y < 10) {
      c.setBlock(x, y, z, Blocks.LAVA, 0);
    }
  }
  private BlockBase getBlock(int x, int y, int z) {
    if (y < 0) return null;
    if (y > 255) return null;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    return Static.blocks.blocks[c.getID(x,y,z)];
  }

  private void addCaves() {
    //generate caves
//    Static.log("cave@" + chunk.cx + "," + chunk.cz);
    //start a cave on this chunk, can span max 8 chunks from here in any direction
    float elev = r.nextInt((int)chunk.elev[8*16+8]);
    float dir = r.nextFloat() * 180f;
    doCave(elev, dir, false);
    dir += 180;
    if (dir > 180) dir -= 360;
    doCave(elev, dir, false);
  }

  private void doCave(float elev, float xzdir, boolean fork) {
    float x = 8;
    float y = elev;
    float z = 8;
    int len = 64 + r.nextInt(256);
    GLMatrix mat = new GLMatrix();
    GLVector3 vecx = new GLVector3();
    GLVector3 vecy = new GLVector3();
    GLVector3 vecz = new GLVector3();
    float width = 3f;
    float height = 3f;
    float ydir = 0f;
    int plen = 0;
    float dxzdir = 0, dydir = 0;
    do {
      if (plen == 0) {
        dxzdir = r.nextFloat() * 2f;
        dydir = r.nextFloat() * 1f;
        plen = 16 + r.nextInt(32);
      } else {
        plen--;
      }
      if (!fork && r.nextInt(150) == 0) {
        //fork
        float d = r.nextFloat() * 45f;
        if (d < 0) d -= 45f; else d += 45f;
        doCave(y, xzdir + d, true);
      }
      mat.setIdentity();
      mat.addRotate(xzdir, 0, 1, 0);
      mat.addRotate3(ydir, 1, 0, 0);
      vecx.set(1, 0, 0);
      mat.mult(vecx);
      vecy.set(0, 1, 0);
      mat.mult(vecy);
      vecz.set(0, 0, 1);
      mat.mult(vecz);
      float xx = vecx.v[0];
      float xy = vecx.v[1];
      float xz = vecx.v[2];
      float yx = vecy.v[0];
      float yy = vecy.v[1];
      float yz = vecy.v[2];
      float zx = vecz.v[0];
      float zy = vecz.v[1];
      float zz = vecz.v[2];

      if (Static.debugCaves) {
        clearBlock((int)x,(int)y,(int)z);
      } else {
        for(float a=0;a<=height;a++) {
          //create the curves of the cave walls
          /*
             .|.
            . | .
           .  |  .
            ..|..
          */
          float r = (float)Math.sin(Math.toRadians(a / height * 194f)) + 0.25f;
          float w = width * r;
          float px = x - xx * w / 2f;
          float py = y - xy * w / 2f;
          float pz = z - xz * w / 2f;
          px += yx * a;
          py += yy * a;
          pz += yz * a;
          for(float b = 0;b <= w; b++) {
            clearBlock((int)px,(int)py,(int)pz);
            px += xx;
            py += xy;
            pz += xz;
          }
        }
      }

      //move at 50% to make sure we get everything
      x += zx * 0.50f;
      y += zy * 0.50f;
      z += zz * 0.50f;
      if (y < 10) y = 10;
      if (y > 55) y = 55;

      xzdir += dxzdir;
      if (xzdir > 180) {
        xzdir -= 360;
      }
      if (xzdir < -180) {
        xzdir += 360;
      }
      ydir += dydir;
      if (ydir < -3) ydir = -3;
      if (ydir > 3) ydir = 3;

      width += r.nextFloat() * 0.3f;
      if (width > 5) width = 5;
      if (width < 2) width = 2;
      height += r.nextFloat() * 0.3f;
      if (height > 5) height = 5;
      if (height < 2) height = 2;

      len--;

      //can only go upto 8 chunks beyond start point (should just tapper off)
      if (x + width > 128) break;
      if (x - width < -128) break;
      if (z + width > 128) break;
      if (z - width < -128) break;
    } while (len > 0);
  }

  private void addRavine() {
//    Static.log("ravine@" + chunk.cx + "," + chunk.cz);
    float elev = r.nextInt((int)chunk.elev[8*16+8]);
    float dir = r.nextFloat() * 180f;
    float height = 30 + r.nextInt(20);
    float width = 10 + r.nextInt(5);
    float len = 40 + r.nextInt(25);
    doRavine(elev, dir, height, width, len);
    dir += 180;
    doRavine(elev, dir, height, width, len);
  }

  private void doRavine(float elev, float xzdir, float height, float width, float len) {
    float x = 8;
    float y = elev;
    float z = 8;
    GLMatrix mat = new GLMatrix();
    GLVector3 vecx = new GLVector3();
    GLVector3 vecy = new GLVector3();
    GLVector3 vecz = new GLVector3();
    float dxzdir = 0;
    do {
      mat.setIdentity();
      mat.addRotate(xzdir, 0, 1, 0);
      vecx.set(1, 0, 0);
      mat.mult(vecx);
      vecy.set(0, 1, 0);
      mat.mult(vecy);
      vecz.set(0, 0, 1);
      mat.mult(vecz);
      float xx = vecx.v[0];
      float xy = vecx.v[1];
      float xz = vecx.v[2];
      float yx = vecy.v[0];
      float yy = vecy.v[1];
      float yz = vecy.v[2];
      float zx = vecz.v[0];
      float zy = vecz.v[1];
      float zz = vecz.v[2];

      for(float a=0;a<=height;a++) {
        //create the curves of the ravine walls (to create ledges near top)
        /*
           .|.
         .  |  .
          . | .
          . | .
          . | .
        */
        float p = a * 100f / height;
        float r = 0;
        if (p < 75) {
          //i:0-75 => o:80-100
          //f(x) = ((omax - omin) * (x - imin)) / (imax - imin)
          r = ((100f-80f) * (p)) / (75f) + 80f;
          if (r < 80f || r >100f) {
            Static.log("bad r=" + r + ",p=" + p);
          }
        } else {
          //i:75-100 => o:100-0
          //f(x) = ((omax - omin) * (x - imin)) / (imax - imin)
          r = ((-100f) * (p - 75f)) / (25f) + 100f;
        }
        float w = width * r / 100f;
        if (len < 5f) {
          //tapper the end
          w /= (5f - len);
        }
        float px = x - xx * w / 2f;
        float py = y - xy * w / 2f;
        float pz = z - xz * w / 2f;
        px += yx * a;
        py += yy * a;
        pz += yz * a;
        for(float b = 0;b <= w; b++) {
          clearBlock((int)px,(int)py,(int)pz);
          px += xx;
          py += xy;
          pz += xz;
        }
      }

      x += zx * 0.50f;
      y += zy * 0.50f;
      z += zz * 0.50f;

      len--;
    } while (len > 0);
  }
  private void addCabin() {
    BluePrint cabin = chunk.world.getBluePrint("cabin");
    int elev = (int)chunk.elev[8 * 16 + 8] + 1;
    if (elev + cabin.Y > 255) return;
    if (chunk.getID(8, elev+1, 8) != 0) return;
    int ang = r.nextInt(4);
    switch (ang) {
      case 0: break;  //no change
      case 1: cabin.rotateY(R90); break;
      case 2: cabin.rotateY(R180); break;
      case 3: cabin.rotateY(R270); break;
    }
    cabin.writeChunk(chunk, 0, 0, 0, 0, elev, 0, cabin.X, cabin.Y, cabin.Z);
    //TODO : extend foundation
    //rotate back if needed
    switch (ang) {
      case 0: break;  //no change
      case 1: cabin.rotateY(R270); break;
      case 2: cabin.rotateY(R180); break;
      case 3: cabin.rotateY(R90); break;
    }
  }
  private void addShipwreck() {
    BluePrint ship = chunk.world.getBluePrint("shipwreck");
    int elev = (int)chunk.elev[8 * 16 + 8] + 1;
    if (elev + ship.Y > 255) return;
    if (chunk.getID(8, elev+1, 8) != 0) return;
    int ang = r.nextInt(4);
    ang = 3;  //test
    switch (ang) {
      case 0: break;  //no change
      case 1: ship.rotateY(R90); break;
      case 2: ship.rotateY(R180); break;
      case 3: ship.rotateY(R270); break;
    }
    ship.writeChunk(chunk, 0, 0, 0, 0, elev, 0, ship.X, ship.Y, ship.Z);
    //rotate back if needed
    switch (ang) {
      case 0: break;  //no change
      case 1: ship.rotateY(R270); break;
      case 2: ship.rotateY(R180); break;
      case 3: ship.rotateY(R90); break;
    }
  }

  private void lineNS(float x, float y, float z, float dz, char id) {
    int cnt = 0;
    while (cnt < 120 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      if (id != 0) {
        clearBlock((int)x,(int)y,(int)z);
      }
      setBlock((int)x,(int)y,(int)z,id,0);
      z += dz;
      cnt++;
    }
  }

  private void lineEW(float x, float y, float z, float dx, char id) {
    int cnt = 0;
    while (cnt < 120 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      if (id != 0) {
        clearBlock((int)x,(int)y,(int)z);
      }
      setBlock((int)x,(int)y,(int)z,id,0);
      lineNS(x,y,z,1.0f,id);
      lineNS(x,y,z,-1.0f,id);
      x += dx;
      cnt++;
    }
  }

  private float threshold = 0.5f;

  public void addRoom() {
    int x = Static.abs(r.nextInt(16));
    int z = Static.abs(r.nextInt(16));
    int y = Static.abs(r.nextInt(50)) + 5;
    while (y > 5 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) < threshold) {
      y--;
    }
    while (y < 55 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) < threshold) {
      y++;
    }
    if (y == 55) {
      return;
    }
    //move to the top
    while (y < 60.0f && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y+1,cz16 + z) > threshold) {
      y++;
    }
//    Static.log("room@" + x + "," + y + "," + z + ":" + chunk.cx + "," + chunk.cz);
    int gap = r.nextInt(3) + 3;
    char id = Blocks.AIR;
    char liquid;
    if (y > 40.0f) {
      liquid = Blocks.WATER;
    } else {
      liquid = Blocks.LAVA;
    }
    while (y > 2.0f && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      //set level
      lineEW(x,y,z,1.0f,id);
      lineEW(x,y,z,-1.0f,id);
      //move down
      y--;
      if (gap > 0) {
        gap--;
        if (gap == 0) {
          id = liquid;
        }
      }
    }
  }
}
