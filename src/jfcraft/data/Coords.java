package jfcraft.data;

/** Coordinates used in various functions.
 *
 * Typically used to return info about a block.
 *
 * Not all fields are always set.  Depends on usage.
 *
 * @author pquiring
 *
 * Created : Mar 26, 2014
 */

import java.util.*;

import jfcraft.block.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;

public class Coords implements Cloneable {
  public int x,y,z;  //usage varies
  public int cx,cz;  //chunk x/z
  public int gx,gy,gz;  //grid (within chunk) x/y/z
  public XYZ ang = new XYZ();  //exact angle of player while placing block
  public float sx,sy,sz;  //selection in AIR just before selecting block/entity
  public int bits;
  public   int var;
  public   int dir;
  public   int dir_xz;
  public   int dir_y;  //block dir or player direction
  public   int face;
  public   int face_xz;
  public   int face_y;
  public int powerLevel;  //only valid in BlockBase.powerOn()
  public BlockBase block;
  public EntityBase entity;
  public Chunk chunk;
  public float length;
  public void copy(Coords in) {
    x = in.x;
    y = in.y;
    z = in.z;
    cx = in.cx;
    cz = in.cz;
    gx = in.gx;
    gy = in.gy;
    gz = in.gz;
    ang.x = in.ang.x;
    ang.y = in.ang.y;
    ang.z = in.ang.z;
    sx = in.sx;
    sy = in.sy;
    sz = in.sz;
    bits = in.bits;
    var = in.var;
    dir = in.dir;
    dir_xz = in.dir_xz;
    dir_y = in.dir_y;
    powerLevel = in.powerLevel;
    block = in.block;
    entity = in.entity;
    chunk = in.chunk;
    length = in.length;
  }
  /** Moves to adjacent coords based on 'side'. */
  public void adjacentBlock() {
    switch (dir) {
      case A:
        y++;
        gy++;
        break;
      case B:
        y--;
        gy--;
        break;
      case N:
        z--;
        gz--;
        if (gz == -1) {
          gz = 15;
          cz--;
          chunk = chunk.N;
        }
        break;
      case E:
        x++;
        gx++;
        if (gx == 16) {
          gx = 0;
          cx++;
          chunk = chunk.E;
        }
        break;
      case S:
        z++;
        gz++;
        if (gz == 16) {
          gz = 0;
          cz++;
          chunk = chunk.S;
        }
        break;
      case W:
        x--;
        gx--;
        if (gx == -1) {
          gx = 15;
          cx--;
          chunk = chunk.W;
        }
        break;
    }
  }
  public void setPos(float x, float y, float z) {
    this.x = (int)Static.floor(x);
    this.y = (int)Static.floor(y);
    this.z = (int)Static.floor(z);

    cx = Static.floor(x / 16.0f);
    cz = Static.floor(z / 16.0f);

    gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    gy = Static.floor(y);
    gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    chunk = null;
    entity = null;
  }
  public String toString() {
    return "Coords:" + x + "," + y + "," + z + ":" + cx + "," + cz + ":" + gx + "," + gy + "," + gz + ":" + dir;
  }
  /** Changes 'side' to other side. */
  public void otherSide() {
    switch (dir) {
      case A: dir = B; break;
      case B: dir = A; break;
      case N: dir = S; break;
      case E: dir = W; break;
      case S: dir = N; break;
      case W: dir = E; break;
    }
    switch (dir_xz) {
      case N: dir_xz = S; break;
      case E: dir_xz = W; break;
      case S: dir_xz = N; break;
      case W: dir_xz = E; break;
    }
    switch (dir_y) {
      case A: dir_y = B; break;
      case B: dir_y = A; break;
    }
  }
  /** Changes 'side' to left side (no effect if A or B). */
  public void leftSide() {
    switch (dir) {
      case N: dir = W; break;
      case E: dir = N; break;
      case S: dir = E; break;
      case W: dir = S; break;
    }
  }
  /** Changes 'side' to right side (no effect if A or B). */
  public void rightSide() {
    switch (dir) {
      case N: dir = E; break;
      case E: dir = S; break;
      case S: dir = W; break;
      case W: dir = N; break;
    }
  }
  //N = default
  public float getXAngleN() {
    switch (dir) {
      case N: return 0.0f;
      case S: return 180.0f;
      case A: return -90.0f;
      case B: return 90.0f;
    }
    return 0;
  }
  //A = default
  public float getXAngleA() {
    switch (dir) {
      case N: return 90.0f;
      case S: return -90.0f;
      case A: return 0.0f;
      case B: return 180.0f;
    }
    return 0;
  }
  //N = default
  public float getYAngle() {
    switch (dir_xz) {
      case N: return 0.0f;
      case E: return 90.0f;
      case S: return 180.0f;
      case W: return -90.0f;
    }
    return 0;
  }
  //A = default
  public float getZAngleA() {
    switch (dir) {
      case A: return 0.0f;
      case E: return 90.0f;
      case B: return 180.0f;
      case W: return -90.0f;
    }
    return 0;
  }
  public Coords clone() {
    try {
      return (Coords)super.clone();
    } catch (Exception e) {
      return null;
    }
  }

  private static ArrayList<Coords> pool = new ArrayList<Coords>();
  public static int cnt;  //200-300
  public static Coords alloc() {
    Coords c;
    synchronized(pool) {
      int size = pool.size();
      if (size == 0) {
        cnt++;
        return new Coords();
      }
      c = pool.remove(size-1);  //always take from end
    }
    return c;
  }
  private static void free(Coords c) {
    synchronized(pool) {
      pool.add(c);
    }
  }
  public void free() {
    free(this);
  }
}
