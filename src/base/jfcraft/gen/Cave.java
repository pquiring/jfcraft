package jfcraft.gen;

/**
 * Cave
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Static.*;

public class Cave {
  private Chunk chunk;
  private BiomeData data;

  private void setBlock(int x, int y, int z, char id, int bits) {
    if (id == 0) {
      clearBlock(x,y,z);
      return;
    }
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    chunk.setBlock(x, y, z, id, bits);
  }
  private void clearBlock(int x, int y, int z) {
    if (y < 1) return;  //do not clear bedrock
    chunk.clearBlock(x, y, z);
    chunk.clearBlock2(x, y, z);
    if (y < 10) {
      chunk.setBlock(x, y, z, Blocks.LAVA, 0);
    }
  }
  public void addCaves(Chunk chunk, BiomeData data) {
    this.chunk = chunk;
    this.data = data;
    //generate caves
//    Static.log("cave@" + chunk.cx + "," + chunk.cz);
    //start a cave on this chunk, can span max 8 chunks from here in any direction
    float elev = data.c1 % chunk.elev[8*16+8];
    float dir = data.cf1 * 180f;
    doCave(elev, dir, false, data.c1);
    dir += 180;
    if (dir > 180) dir -= 360;
    doCave(elev, dir, false, data.c2);
  }

  private void doCave(float elev, float xzdir, boolean fork, int seed) {
    float x = 8;
    float y = elev;
    float z = 8;
    Random r = new Random();
    r.setSeed(seed);
    int len = 64 + data.b1 % 256;
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
        doCave(y, xzdir + d, true, seed + 1);
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
          float rad = (float)Math.sin(Math.toRadians(a / height * 194f)) + 0.25f;
          float w = width * rad;
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
}
