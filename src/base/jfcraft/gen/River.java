package jfcraft.gen;

/**
 * River
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class River {
  private int W;  //width (odd)
  private int W2;  //width / 2 (round down)

  private int x1, x2;
  private int z1, z2;

  private float dx[];  //depth x (1d)
  private float dz[];  //depth z (1d)
  private float di[];  //depth init (2d)
  private float dc[];  //depth current (2d)
  private Chunk c;
  private Random r;

  private float fx, fz;  //center position
  private float dir;
  private float min, max;

  public void build(Chunk c, Random r) {
    this.c = c;
    this.r = r;

    if (c.getBiome(8, 8) == Biomes.OCEAN) return;

    //calc river width
    W = r.nextInt(9) + 5;  //5-13
    if ((W & 1) == 0) W++;  //W must be odd
    W2 = W / 2;

    //build pyramid x,z profiles
    fx=0;
    fz=0;
    dx = new float[W];
    dz = new float[W];
    for(int p=0;p<W2;p++) {
      fx += r.nextFloat() * 0.6f;
      fz += r.nextFloat() * 0.6f;
      dx[p] = fx;
      dz[p] = fz;
      dx[W-p-1] = fx;
      dz[W-p-1] = fx;
    }
    dx[W2] = fx;
    dz[W2] = fz;

    //now create an upside down 3d pyramid
    int p = 0;
    di = new float[W*W];
    dc = new float[W*W];
    for(int z=0;z<W;z++) {
      for(int x=0;x<W;x++) {
        di[p] = Static.min(dx[x], dz[z]);
        p++;
      }
    }

    float d1 = 90f + r.nextFloat() * 360f;
    float d2 = d1 + 180f;

    dir = d1;
    min = dir - 90f;
    max = dir + 90f;
    if (!avoidMountains()) return;
    Static.log("River@" + (c.cx * 16) + "," + (c.cz * 16) + ":" + d1 + "," + d2);
    carveLine();

    //carve out second half of river
    dir = d2;
    min = dir - 90f;
    max = dir + 90f;
    carveLine();
  }

  /** Clamps angle 0-360 degrees. */
  private float clamp(float ang) {
    if (ang < 0f) return ang += 360f;
    if (ang >= 360f) return ang -= 360f;
    return ang;
  }

  private static final int length = 100;

  private void carveLine() {
    fx = 8f;
    fz = 8f;
    GLMatrix mat = new GLMatrix();
    GLVector3 vec = new GLVector3();
    boolean taper = false;
    float factor = 1.0f;
    boolean nearOcean = false;
    for(int a=0;a<length;a++) {
      if (a > length - 16) taper = true;  //taper end of river
      if (c.getBiome((int)fx, (int)fz) == Biomes.OCEAN) break;  //stop once we hit ocean in center of pyramid
      if (!taper && !avoidMountains()) taper = true;  //taper once we hit mountain
      nearOcean = moveTowardOcean();
      if (taper) factor -= Static._1_16;
      if (factor <= 0f) break;
      copyValues();
      carvePoint((int)fx, (int)fz, factor);
      if (!nearOcean) {
        //min/max prevents river from turning back on itself
        if (dir < min) dir = min;
        if (dir > max) dir = max;
      }
      mat.setIdentity();
      mat.addRotate(clamp(dir), 0, 1, 0);
      vec.set(0, 0, -1);  //0 deg = north
      mat.mult(vec);
      fx += vec.v[0];
      fz += vec.v[2];
      if (!nearOcean) {
        dir += (r.nextFloat() - 0.5f) * 5f;
      }
    }
  }

  private void copyValues() {
    int cnt = W * W;
    for(int i=0;i<cnt;i++) {
      dc[i] = di[i] + r.nextFloat() * 0.2f;
    }
  }

  private static final float maxelev = Static.SEALEVEL + 5.0f;

  private float nw,ne,sw,se;

  /** Get elevations at corners of pyramid. */
  private void getElevs() {
    int x = (int)fx;
    int z = (int)fz;
    //TODO : use factor
    x1 = x - W2;
    x2 = x + W2;
    z1 = z - W2;
    z2 = z + W2;
    nw = c.getElev(x1,z1);
    ne = c.getElev(x2,z1);
    se = c.getElev(x2,z2);
    sw = c.getElev(x1,z2);
  }

  private boolean avoidMountains() {
    //basically move 1 block away from high elevations
    //return false if not possible (ends river)

    getElevs();

    float cdir = clamp(dir);

    if (nw > maxelev) {
      if (cdir > 315f) {
        //move e
        fx += 1.0f;
        dir += 10f;
      } else {
        //move s
        fz += 1.0f;
        dir -= 10f;
      }
    } else if (ne > maxelev) {
      if (cdir < 45f) {
        //move w
        fx -= 1.0f;
        dir -= 10f;
      } else {
        //move s
        fz += 1.0f;
        dir += 10f;
      }
    } else if (sw > maxelev) {
      if (cdir < 225f) {
        //move e
        fx += 1.0f;
        dir -= 10f;
      } else {
        //move n
        fz -= 1.0f;
        dir += 10f;
      }
    } else if (se > maxelev) {
      if (cdir > 135f) {
        //move w
        fx -= 1.0f;
        dir += 10f;
      } else {
        //move n
        fz -= 1.0f;
        dir -= 10f;
      }
    }

    getElevs();

    return nw < maxelev && ne < maxelev && se < maxelev && sw < maxelev;
  }

   private byte bnw,bne,bsw,bse;  //biomes

  /** Get biomes at corners of pyramid. */
  private void getBiomes() {
    int x = (int)fx;
    int z = (int)fz;
    //TODO : use factor
    x1 = x - W2;
    x2 = x + W2;
    z1 = z - W2;
    z2 = z + W2;
    bnw = c.getBiome(x1,z1);
    bne = c.getBiome(x2,z1);
    bse = c.getBiome(x2,z2);
    bsw = c.getBiome(x1,z2);
  }

  /** Moves river towards ocean if detected on cornders.
   * @return true if ocean detected.
   */
  private boolean moveTowardOcean() {
    getBiomes();
    float cdir = clamp(dir);
    if (bnw == Biomes.OCEAN) {
      //change angle towards 270f
      if (cdir > 315f) dir -= 10f; else dir += 10f;
      return true;
    }
    if (bne == Biomes.OCEAN) {
      //change angle towards 45f
      if (cdir > 45f) dir -= 10f; else dir += 10f;
      return true;
    }
    if (bse == Biomes.OCEAN) {
      //change angle towards 135f
      if (cdir > 135f) dir -= 10f; else dir += 10f;
      return true;
    }
    if (bsw == Biomes.OCEAN) {
      //change angle towards 225f
      if (cdir > 225f) dir -= 10f; else dir += 10f;
      return true;
    }
    return false;
  }

  private void carvePoint(int ix, int iz, float factor) {
    int px, pz;
    //carve out pyramid
    int w = (int)(W * factor);
    if ((w & 1) == 0) w++;
    int w2 = w / 2;
    int shrink = W2 - w2;
    px = shrink;
    pz = shrink;
    for(int z=-w2;z<=w2;z++) {
      for(int x=-w2;x<=w2;x++) {
        int y = Static.SEALEVEL - (int)(dc[pz * W + px] * factor);
        while (y <= Static.SEALEVEL || c.getID(ix+x, y, iz+z) != 0) {
          c.clearBlock(ix+x, y, iz+z);
          if (y <= Static.SEALEVEL) {
            c.setBlock(ix+x, y, iz+z, Blocks.WATER, 0);
          }
          y++;
        }
        px++;
      }
      px = shrink;
      pz++;
    }

    //carve out slope (one ring at a time)
    int cnt;
    int nx1 = ix - w2 - 1;
    int nx2 = ix + w2 + 1;
    int nz = iz - w2 - 1;
    int sx1 = ix - w2 - 1;
    int sx2 = ix + w2 + 1;
    int sz = iz + w2 + 1;
    int ez1 = iz - w2 - 1;
    int ez2 = iz + w2 + 1;
    int ex = ix + w2 + 1;
    int wz1 = iz - w2 - 1;
    int wz2 = iz + w2 + 1;
    int wx = ix - w2 - 1;
    char id;
    int ys = Static.SEALEVEL + 1;
    do {
      cnt = 0;
      //N side
      for(int nx = nx1; nx <= nx2; nx++) {
        int y = ys;
        do {
          id = c.getID(nx,y,nz);
          if (id != 0) {
            c.clearBlock(nx, y, nz);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //E side
      for(int ez = ez1; ez <= ez2; ez++) {
        int y = ys;
        do {
          id = c.getID(ex,y,ez);
          if (id != 0) {
            c.clearBlock(ex, y, ez);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //S side
      for(int sx = sx1; sx <= sx2; sx++) {
        int y = ys;
        do {
          id = c.getID(sx,y,sz);
          if (id != 0) {
            c.clearBlock(sx, y, sz);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //W side
      for(int wz = wz1; wz <= wz2; wz++) {
        int y = ys;
        do {
          id = c.getID(wx,y,wz);
          if (id != 0) {
            c.clearBlock(wx, y, wz);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      if (cnt > 0) {
        //move ring outward
        nx1--;
        nx2++;
        nz--;
        ez1--;
        ez2++;
        ex++;
        sx1--;
        sx2++;
        sz++;
        wz1--;
        wz2++;
        wx--;
        ys++;  //slope up
      }
    } while (cnt > 0);
  }
}
