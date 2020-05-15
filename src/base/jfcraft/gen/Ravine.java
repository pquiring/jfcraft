package jfcraft.gen;

/**
 * Ravine
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;

public class Ravine {
  private Chunk chunk;
  private BiomeData data;

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

  public void addRavine(Chunk chunk, BiomeData data) {
    this.chunk = chunk;
    this.data = data;
//    Static.log("ravine@" + chunk.cx + "," + chunk.cz);
    Random r = new Random();
    r.setSeed(data.c1);
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

}
