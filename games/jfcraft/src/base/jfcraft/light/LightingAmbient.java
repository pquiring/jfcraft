package jfcraft.light;

/** Lighting - Ambient sunlight (used in nether and end dims).
 *
 * Fills lighting like a paint program would implement the fill tool.
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import jfcraft.block.*;
import jfcraft.data.*;

public class LightingAmbient implements LightingBase {
  private int ta[], xa[], ya[], za[];
  private int head, tail;

  private static final int size = 65536;

  private Chunk chunk;

  //types
  private static final int BLK = 1;       //block light in all directions

  //masks
  public static final int SUN_LIGHT_MASK = 0x0f;
  public static final int BLK_LIGHT_MASK = 0xf0;

  private int ambientLvl;

  private Object lock = new Object();

  /** @param ambientLightLevel = 0 thru 15 */
  public LightingAmbient(int ambientLightLevel) {
    this.ambientLvl = ambientLightLevel << 4;
    ta = new int[size];
    xa = new int[size];
    ya = new int[size];
    za = new int[size];
  }

  //fills in all block light levels
  private void reset(Chunk chunk) {
    int lvl;
    for(int y=0;y<256;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          char id = chunk.getID(x,y,z);
          lvl = Static.blocks.blocks[id].emitLight << 4;
          chunk.setLights(x,y,z,lvl | ambientLvl);
        }
      }
    }
  }

  private void addBlkInside() {
    int lvl;
    for(int y=0;y<256;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          lvl = chunk.getBlkLight(x, y, z);
          if (lvl > 1) {
            add(BLK, x, y, z);
          }
        }
      }
    }
  }

  /** Performs lighting on a chunk.  Chunk.canLight() must return true
   * before calling this function to ensure adjacent chunks are available.
   *
   * @param chunk - the chunk to light
   * @param update - if set also updates adjacent chunks
   *   (usually only false when generating chunk for the first time)
   */
  public void light(Chunk chunk) {
    if (!chunk.canLights()) {
      Static.log("Failed to light chunk:" + chunk.cx + "," + chunk.cz);
      return;
    }
    synchronized(lock) {
      reset(chunk);
      tail = head = 0;
      this.chunk = chunk;
      addBlkInside();
      processQueue();
    }

    chunk.needLights = false;
    chunk.dirty = true;
    chunk.N.dirty = true;
    chunk.E.dirty = true;
    chunk.S.dirty = true;
    chunk.W.dirty = true;
    chunk.N.E.dirty = true;
    chunk.N.W.dirty = true;
    chunk.S.E.dirty = true;
    chunk.S.W.dirty = true;
  }

  private void reset(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        for(int x=x1;x<=x2;x++) {
          char id = chunk.getID(x,y,z);
          lvl = Static.blocks.blocks[id].emitLight << 4;
          chunk.setLights(x,y,z,lvl | ambientLvl);
        }
      }
    }
  }

  private void addBlkInside(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        for(int x=x1;x<=x2;x++) {
          lvl = chunk.getBlkLight(x, y, z);
          if (lvl > 1) {
            add(BLK, x, y, z);
          }
        }
      }
    }
  }

  private void addBlkEdges(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    //east / west planes
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        lvl = chunk.getBlkLight(x1-1,y,z);
        if (lvl > 0) {
          add(BLK, x1-1, y, z);
        }
        lvl = chunk.getBlkLight(x2+1,y,z);
        if (lvl > 0) {
          add(BLK, x2+1, y, z);
        }
      }
    }
    //north / south planes
    for(int y=y1;y<=y2;y++) {
      for(int x=x1;x<=x2;x++) {
        lvl = chunk.getBlkLight(x,y,z1-1);
        if (lvl > 0) {
          add(BLK, x, y, z1-1);
        }
        lvl = chunk.getBlkLight(x,y,z2+1);
        if (lvl > 0) {
          add(BLK, x, y, z2+1);
        }
      }
    }
  }

  public void update(Chunk chunk, int x, int y, int z) {
    if (!chunk.canLights()) {
//      Static.log("Failed to update light:" + chunk.cx + "," + chunk.cz);
      return;
    }
    int x1 = x-15;
    int y1 = y-15;  //sunlight doesn't matter
    if (y1 < 0) y1 = 0;
    int z1 = z-15;
    int x2 = x+15;
    int y2 = y+15;
    if (y2 > 255) y2 = 255;
    int z2 = z+15;
    synchronized(lock) {
    synchronized(chunk.lock) {
    synchronized(chunk.N.lock) {
    synchronized(chunk.E.lock) {
    synchronized(chunk.S.lock) {
    synchronized(chunk.W.lock) {
    synchronized(chunk.N.E.lock) {
    synchronized(chunk.N.W.lock) {
    synchronized(chunk.S.E.lock) {
    synchronized(chunk.S.W.lock) {
      this.chunk = chunk;
      tail = head = 0;
      reset(x1,y1,z1,x2,y2,z2);
      addBlkInside(x1,y1,z1,x2,y2,z2);
      addBlkEdges(x1,y1,z1,x2,y2,z2);
      processQueue();
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    chunk.needLights = false;
    chunk.dirty = true;
    if (z < 14) chunk.N.dirty = true;
    if (x > 1) chunk.E.dirty = true;
    if (z > 1) chunk.S.dirty = true;
    if (x < 14) chunk.W.dirty = true;
    if ((z < 14) && (x > 1)) chunk.N.E.dirty = true;
    if ((z < 14) && (x < 14)) chunk.N.W.dirty = true;
    if ((z > 1) && (x > 1)) chunk.S.E.dirty = true;
    if ((z > 1) && (x < 14)) chunk.S.W.dirty = true;
  }

  private void processQueue() {
    int lvl, olvl, nlvl;
    while (tail != head) {
      int t = ta[tail];
      int x = xa[tail];
      int y = ya[tail];
      int z = za[tail];
      tail++;
      if (tail == size) tail = 0;
      switch (t) {
        case BLK:
          lvl = chunk.getBlkLight(x,y,z);
          if (lvl <= 1) break;
          olvl = chunk.getBlkLight(x+1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x+1, y, z).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x+1,y,z, nlvl);
              if (nlvl > 1) add(BLK, x+1,y,z);
            }
          }
          olvl = chunk.getBlkLight(x-1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x-1, y, z).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x-1,y,z, nlvl);
              if (nlvl > 1) add(BLK, x-1,y,z);
            }
          }
          olvl = chunk.getBlkLight(x,y+1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y+1, z).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x,y+1,z, nlvl);
              if (nlvl > 1) add(BLK, x,y+1,z);
            }
          }
          olvl = chunk.getBlkLight(x,y-1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y-1, z).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x,y-1,z, nlvl);
              if (nlvl > 1) add(BLK, x,y-1,z);
            }
          }
          olvl = chunk.getBlkLight(x,y,z+1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z+1).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x,y,z+1, nlvl);
              if (nlvl > 1) add(BLK, x,y,z+1);
            }
          }
          olvl = chunk.getBlkLight(x,y,z-1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z-1).absorbLight((byte)lvl);
            if (nlvl > olvl) {
              setBlkLight(x,y,z-1, nlvl);
              if (nlvl > 1) add(BLK, x,y,z-1);
            }
          }
          break;
      }
    }
  }

  private void add(int t, int x, int y, int z) {
    if (x > 31) return;
    if (x < -16) return;
    if (y > 255) return;
    if (y < 0) return;
    if (z > 31) return;
    if (z < -16) return;
    ta[head] = t;
    xa[head] = x;
    ya[head] = y;
    za[head] = z;
    head++;
    if (head == size) head = 0;
  }

  private BlockBase getBlock(int x, int y, int z) {
    return Static.blocks.blocks[chunk.getID(x,y,z)];
  }
  private void setBlkLight(int x, int y, int z, int lvl) {
    chunk.setLights(x, y, z, (lvl << 4) | ambientLvl);
  }
}
