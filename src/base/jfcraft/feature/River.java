package jfcraft.feature;

/**
 * River
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.biome.*;
import static jfcraft.data.Direction.*;

public class River extends Eraser {
  private int x1, x2;
  private int z1, z2;

  private float dx[];  //depth x (1d)
  private float dz[];  //depth z (1d)
  private float profile[];  //depth profile (2d)
  private int profileSize;
  private Random r = new Random();

  private float d1, d2;  //directions
  private float dir;  //current direction
  private float min, max;  //direction min/max

  private static final int length = 110;  //128 is real max (but have to subtract 1/2 size of shape + trimAbove())
  private int count;  //current length
  private boolean taper;  //taper off end
  private float factor;  //factor to taper off end
  private GLMatrix mat = new GLMatrix();
  private GLVector3 vec = new GLVector3();

  private boolean setupRiver() {
    r.setSeed(data.c1);

    if (chunk.getBiome(8, 8) == Biomes.OCEAN) return false;

//    Static.log("River @ " + wx + "," + wz);

    //calc river width
    profileSize = r.nextInt(9) + 5;  //5-13
    if ((profileSize & 1) == 0) profileSize++;  //dim must be odd
    int half = profileSize / 2;
    setSize(profileSize);
    setPos(8,Static.SEALEVEL,8);
    setCenter(half, profileSize-1, half);  //y offset is at top of shape

    setFill(Blocks.WATER, Static.SEALEVEL);
    setClearAbove(true);

    //build pyramid x,z profiles
    float fx=0;
    float fz=0;
    dx = new float[profileSize];
    dz = new float[profileSize];
    for(int p=0;p<half;p++) {
      fx += r.nextFloat() * 1.5f;
      fz += r.nextFloat() * 1.5f;
      dx[p] = fx;
      dz[p] = fz;
      dx[profileSize-p-1] = fx;
      dz[profileSize-p-1] = fx;
    }
    dx[half] = fx;
    dz[half] = fz;

    //now create an upside down 3d pyramid
    profile = new float[profileSize * profileSize];
    int p = 0;
    for(int z=0;z<profileSize;z++) {
      for(int x=0;x<profileSize;x++) {
        profile[p] = Static.min(dx[x], dz[z]);
        p++;
      }
    }

    //calc directions
    d1 = 90f + r.nextFloat() * 360f;
    d2 = d1 + 180f;

    dir = d1;
    min = dir - 90f;
    max = dir + 90f;

    count = 0;
    taper = false;
    factor = 1.0f;

    return true;
  }

  private void flipDirection() {
    dir = d2;
    min = dir - 90f;
    max = dir + 90f;
    int half = profileSize / 2;
    setSize(profileSize);
    setPos(8,Static.SEALEVEL,8);
    setCenter(half, profileSize-1, half);  //y offset is at top of shape
    count = 0;
    taper = false;
    factor = 1.0f;
  }

  /** Clamps angle 0-360 degrees. */
  private float clamp(float ang) {
    if (ang < 0f) return ang += 360f;
    if (ang >= 360f) return ang -= 360f;
    return ang;
  }

  private void buildShape() {
    resetShape();
    if (factor < 1.0f) {
      int newSize = (int)(profileSize * factor);
      if (newSize < 3) newSize = 3;
      if ((newSize & 1) == 0) newSize++;
      int half = newSize / 2;
      setSize(newSize);
      setCenter(half, newSize-2, half);
    }
    int size = getSize();
    int off = (profileSize - size) / 2;
    size -= off * 2;
    for(int z=0;z<size;z++) {
      for(int x=0;x<size;x++) {
        int depth = (int)((profile[(z + off) * profileSize + (x + off)] + (r.nextFloat() * 0.3f)) * factor);
        if (depth > size) depth = size;
        int y = size-1;
        while (depth > 0) {
          setShape(x,y,z);
          y--;
          depth--;
        }
      }
    }
  }

  private static final float maxelev = Static.SEALEVEL + 5.0f;

  private float nw,ne,sw,se;

  /** Get elevations at corners of pyramid. */
  private void getElevs() {
    int x = getX();
    int z = getZ();
    int half = getSize() / 2;
    x1 = x - half;
    x2 = x + half;
    z1 = z - half;
    z2 = z + half;
    nw = chunk.getElev(x1,z1);
    ne = chunk.getElev(x2,z1);
    se = chunk.getElev(x2,z2);
    sw = chunk.getElev(x1,z2);
  }

  private boolean avoidMountains() {
    //basically move 1 block away from high elevations
    //return false if not possible (ends river)

    getElevs();

    float cdir = clamp(dir);

    boolean _nw = nw > maxelev;
    boolean _ne = ne > maxelev;
    boolean _se = se > maxelev;
    boolean _sw = sw > maxelev;

    if (_nw && _ne) {
      //move s
      addZ(1.0f);
      if (cdir < 180f) {
        cdir += 10f;
      } else {
        cdir -= 10f;
      }
    } else if (_ne && _se) {
      //move w
      addX(-1.0f);
      if (cdir > 270f || cdir < 90f) {
        dir -= 10f;
      } else {
        dir += 10f;
      }
    } else if (_se && _sw) {
      //move n
      addZ(-1.0f);
      if (cdir > 180f) {
        dir -= 10f;
      } else {
        dir += 10f;
      }
    } else if (_sw && _nw) {
      //move e
      addX(1.0f);
      if (cdir > 270f || cdir < 90f) {
        dir += 10f;
      } else {
        dir -= 10f;
      }
    } else if (_nw) {
      if (cdir > 315f) {
        //move e
        addX(1.0f);
        dir += 10f;
      } else {
        //move s
        addZ(1.0f);
        dir -= 10f;
      }
    } else if (_ne) {
      if (cdir < 45f) {
        //move w
        addX(-1.0f);
        dir -= 10f;
      } else {
        //move s
        addZ(1.0f);
        dir += 10f;
      }
    } else if (_sw) {
      if (cdir < 225f) {
        //move e
        addX(1.0f);
        dir -= 10f;
      } else {
        //move n
        addZ(-1.0f);
        dir += 10f;
      }
    } else if (_se) {
      if (cdir > 135f) {
        //move w
        addX(-1.0f);
        dir += 10f;
      } else {
        //move n
        addZ(-1.0f);
        dir -= 10f;
      }
    }

    return _nw || _ne || _se || _sw;
  }

   private byte bnw,bne,bsw,bse;  //biomes

  /** Get biomes at corners of pyramid. */
  private void getBiomes() {
    int x = getX();
    int z = getZ();
    int half = getSize() / 2;
    x1 = x - half;
    x2 = x + half;
    z1 = z - half;
    z2 = z + half;
    bnw = chunk.getBiome(x1,z1);
    bne = chunk.getBiome(x2,z1);
    bse = chunk.getBiome(x2,z2);
    bsw = chunk.getBiome(x1,z2);
  }

  /** Steers river towards ocean if detected on corners.
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

  /** Carve out slope (one ring at a time).  Could trim up to 10 blocks away. */
  private void clearAbove(int ix, int iz) {
    int cnt;
    int half = getSize() / 2;
    int nx1 = ix - half - 1;
    int nx2 = ix + half + 1;
    int nz = iz - half - 1;
    int sx1 = ix - half - 1;
    int sx2 = ix + half + 1;
    int sz = iz + half + 1;
    int ez1 = iz - half - 1;
    int ez2 = iz + half + 1;
    int ex = ix + half + 1;
    int wz1 = iz - half - 1;
    int wz2 = iz + half + 1;
    int wx = ix - half - 1;
    char id;
    int ys = Static.SEALEVEL + 1;
    do {
      cnt = 0;
      //N side
      for(int nx = nx1; nx <= nx2; nx++) {
        int y = ys;
        do {
          id = chunk.getID(nx,y,nz);
          if (id != 0) {
            chunk.clearBlock(nx, y, nz);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //E side
      for(int ez = ez1; ez <= ez2; ez++) {
        int y = ys;
        do {
          id = chunk.getID(ex,y,ez);
          if (id != 0) {
            chunk.clearBlock(ex, y, ez);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //S side
      for(int sx = sx1; sx <= sx2; sx++) {
        int y = ys;
        do {
          id = chunk.getID(sx,y,sz);
          if (id != 0) {
            chunk.clearBlock(sx, y, sz);
            cnt++;
          }
          y++;
        } while (id != 0);
      }
      //W side
      for(int wz = wz1; wz <= wz2; wz++) {
        int y = ys;
        do {
          id = chunk.getID(wx,y,wz);
          if (id != 0) {
            chunk.clearBlock(wx, y, wz);
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

  /** Setup Eraser, call setSize() and fill in shape. */
  public boolean setup() {
    return setupRiver();
  }

  /** Move center position by one block. */
  public void move() {
    mat.setIdentity();
    mat.addRotate(clamp(dir), 0, 1, 0);
    vec.set(0, 0, -1);  //0 deg = north
    mat.mult(vec);
    addX(vec.v[0]);
    addZ(vec.v[2]);
    dir += (r.nextFloat() - 0.5f) * 15f;
    //min/max prevents river from turning back on itself
    if (dir < min) dir = min;
    if (dir > max) dir = max;
    count++;
  }

  /** Check if we are done. */
  public boolean endPath() {
    if (count > length - 16) taper = true;  //taper end of river
    if (chunk.getBiome(getX(), getZ()) == Biomes.OCEAN) {
      //stop once we hit ocean in center of pyramid
      return true;
    }
    while (avoidMountains()) {
      count++;
      if (count >= length) break;
    }
    moveTowardOcean();
    if (taper) factor -= Static._1_16;
    return ((count > length) || (factor <= 0f));
  }

  /** Flip position to erase other half of path. */
  public boolean flip() {
    flipDirection();
    return true;
  }

  /** Called before erasing a point. */
  public void preErase() {
    buildShape();
  }

  /** Called after erasing a point. */
  public void postErase() {
    clearAbove(getX(), getZ());
  }

}
