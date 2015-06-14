package jfcraft.light;

/** Lighting - fast chunk lighting implementation.
 *
 * Fills lighting like a paint program would implement the fill tool.
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import jfcraft.block.*;
import jfcraft.data.*;

public class LightingEarth implements LightingBase {
  private int ta[], xa[], ya[], za[];
  private int head, tail;

  private static final int size = 65536;

  private Chunk chunk;

  //types
  private static final int SUN_FULL = 1;  //downward full sun
  private static final int SUN_CAST = 2;  //sun light in all directions
  private static final int BLK = 3;       //block light in all directions

  //masks
  private static final int SUN_LIGHT_MASK = 0x0f;
  private static final int BLK_LIGHT_MASK = 0xf0;

  private Object lock = new Object();

  public LightingEarth() {
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
          if (y == 255 && id == 0) {
            lvl |= SUN_LIGHT_MASK;
          }
          chunk.setLights(x,y,z,lvl);
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

  private void addSunEdges() {
    //do sun light casting from adj chunks (only after sun light full is complete)
    //check N,E,S,W planes for sun light differences
    int ilvl, olvl;

    //east/west planes
    for(int y=0;y<255;y++) {
      for(int z=0;z<16;z++) {
        ilvl = chunk.getSunLight(15,y,z);
        olvl = chunk.E.getSunLight(0,y,z);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, 16, y, z);
        }

        ilvl = chunk.getSunLight(0,y,z);
        olvl = chunk.W.getSunLight(15,y,z);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, -1, y, z);
        }
      }
    }

    //north/south planes
    for(int y=0;y<255;y++) {
      for(int x=0;x<16;x++) {
        ilvl = chunk.getSunLight(x,y,15);
        olvl = chunk.S.getSunLight(x,y,0);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x, y, 16);
        }

        ilvl = chunk.getSunLight(x,y,0);
        olvl = chunk.N.getSunLight(x,y,15);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x, y, -1);
        }
      }
    }
  }

  private void addSunTop() {
    int lvl;
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        lvl = chunk.getSunLight(x, 255, z);
        if (lvl == 15)
          add(SUN_FULL, x, 255, z);
        else if (lvl > 1)
          add(SUN_CAST, x, 255, z);
      }
    }
  }

  /** Performs lighting on a chunk.  Chunk.canLight() must return true
   * before calling this function to ensure adjacent chunks are available.
   *
   * @param chunk - the chunk to light
   */
  public void light(Chunk chunk) {
    if (!chunk.canLights()) {
      Static.log("Failed to light chunk:" + chunk.cx + "," + chunk.cz);
      return;
    }
    synchronized(lock) {
      tail = head = 0;
      this.chunk = chunk;
      reset(chunk);
      addSunTop();
      addSunEdges();
      addBlkInside();
      //addBlkEdges();  //not needed in generation
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
          if (y == 255 && id == 0) {
            lvl |= SUN_LIGHT_MASK;
          }
          chunk.setLights(x,y,z,lvl);
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
        if (lvl > 1) {
          add(BLK, x1-1, y, z);
        }
        lvl = chunk.getBlkLight(x2+1,y,z);
        if (lvl > 1) {
          add(BLK, x2+1, y, z);
        }
      }
    }
    //north / south planes
    for(int y=y1;y<=y2;y++) {
      for(int x=x1;x<=x2;x++) {
        lvl = chunk.getBlkLight(x,y,z1-1);
        if (lvl > 1) {
          add(BLK, x, y, z1-1);
        }
        lvl = chunk.getBlkLight(x,y,z2+1);
        if (lvl > 1) {
          add(BLK, x, y, z2+1);
        }
      }
    }
  }

  private void addSunTop(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    int y = y2;
    if (y < 255) y++;
    for(int x=x1;x<=x2;x++) {
      for(int z=z1;z<=z2;z++) {
        lvl = chunk.getSunLight(x, y, z);
        if (lvl == 15)
          add(SUN_FULL, x, y, z);
        else if (lvl > 1)
          add(SUN_CAST, x, y, z);
      }
    }
  }

  private void addSunEdges(int x1,int y1,int z1,int x2,int y2,int z2) {
    //do sun light casting from adj chunks (only after sun light full is complete)
    //check N,E,S,W planes for sun light differences
    int ilvl, olvl;

    //east/west planes
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        ilvl = chunk.getSunLight(x1,y,z);
        olvl = chunk.getSunLight(x1-1,y,z);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x1-1, y, z);
        }

        ilvl = chunk.getSunLight(x2,y,z);
        olvl = chunk.getSunLight(x2+1,y,z);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x2+1, y, z);
        }
      }
    }

    //north/south planes
    for(int y=y1;y<=y2;y++) {
      for(int x=x1;x<=x2;x++) {
        ilvl = chunk.getSunLight(x,y,z1);
        olvl = chunk.getSunLight(x,y,z1-1);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x, y, z1-1);
        }

        ilvl = chunk.getSunLight(x,y,z2);
        olvl = chunk.getSunLight(x,y,z2+1);
        if (olvl-1 > ilvl) {
          add(SUN_CAST, x, y, z2+1);
        }
      }
    }
  }

  public void update(Chunk chunk, int x, int y, int z) {
    if (!chunk.canLights()) {
//      Static.log("Failed to update light:" + chunk.cx + "," + chunk.cz);
      return;
    }
    int x1 = x-14;
    int y1 = 0;  //sun light could change all the way down
    int z1 = z-14;
    int x2 = x+14;
    int y2 = y+14;
    if (y2 > 255) y2 = 255;
    int z2 = z+14;
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
      addSunTop(x1,y1,z1,x2,y2,z2);
      addSunEdges(x1,y1,z1,x2,y2,z2);
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
        case SUN_FULL:
          if (chunk.getSunLight(x,y-1,z) < 15) {
            lvl = getBlock(x, y-1, z).absorbSunLight(15);
            lvl = getBlock2(x, y-1, z).absorbSunLight(lvl);
            if (lvl > 0) {
              if (setSunLight(x,y-1,z, lvl)) {
                add(lvl == 15 ? SUN_FULL : SUN_CAST, x,y-1,z);
              }
            }
          }
          if (chunk.getSunLight(x+1,y,z) < 14) {
            lvl = getBlock(x+1, y, z).absorbLight(15);
            lvl = getBlock2(x+1, y, z).absorbLight(lvl);
            if (lvl > 0) {
              if (setSunLight(x+1,y,z, lvl)) {
                add(SUN_CAST, x+1,y,z);
              }
            }
          }
          if (chunk.getSunLight(x-1,y,z) < 14) {
            lvl = getBlock(x-1, y, z).absorbLight(15);
            lvl = getBlock2(x-1, y, z).absorbLight(lvl);
            if (lvl > 0) {
              if (setSunLight(x-1,y,z, lvl)) {
                add(SUN_CAST, x-1,y,z);
              }
            }
          }
          if (chunk.getSunLight(x,y,z+1) < 14) {
            lvl = getBlock(x, y, z+1).absorbLight(15);
            lvl = getBlock2(x, y, z+1).absorbLight(lvl);
            if (lvl > 0) {
              if (setSunLight(x,y,z+1, lvl)) {
                add(SUN_CAST, x,y,z+1); }
              }
          }
          if (chunk.getSunLight(x,y,z-1) < 14) {
            lvl = getBlock(x, y, z-1).absorbLight(15);
            lvl = getBlock2(x, y, z-1).absorbLight(lvl);
            if (lvl > 0) {
              if (setSunLight(x,y,z-1, lvl)) {
                add(SUN_CAST, x,y,z-1);
              }
            }
          }
          break;
        case SUN_CAST:
          lvl = chunk.getSunLight(x,y,z);
          olvl = chunk.getSunLight(x+1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x+1, y, z).absorbLight(lvl);
            nlvl = getBlock2(x+1, y, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x+1,y,z, nlvl)) {
                add(SUN_CAST, x+1,y,z);
              }
            }
          }
          olvl = chunk.getSunLight(x-1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x-1, y, z).absorbLight(lvl);
            nlvl = getBlock2(x-1, y, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x-1,y,z, nlvl)) {
                add(SUN_CAST, x-1,y,z);
              }
            }
          }
          olvl = chunk.getSunLight(x,y+1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y+1, z).absorbLight(lvl);
            nlvl = getBlock2(x, y+1, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x,y+1,z, nlvl)) {
                add(SUN_CAST, x,y+1,z);
              }
            }
          }
          olvl = chunk.getSunLight(x,y-1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y-1, z).absorbLight(lvl);
            nlvl = getBlock2(x, y-1, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x,y-1,z, nlvl)) {
                add(SUN_CAST, x,y-1,z);
              }
            }
          }
          olvl = chunk.getSunLight(x,y,z+1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z+1).absorbLight(lvl);
            nlvl = getBlock2(x, y, z+1).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x,y,z+1, nlvl)) {
                add(SUN_CAST, x,y,z+1);
              }
            }
          }
          olvl = chunk.getSunLight(x,y,z-1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z-1).absorbLight(lvl);
            nlvl = getBlock2(x, y, z-1).absorbLight(nlvl);
            if (nlvl > olvl) {
              if (setSunLight(x,y,z-1, nlvl)) {
                add(SUN_CAST, x,y,z-1);
              }
            }
          }
          break;
        case BLK:
          lvl = chunk.getBlkLight(x,y,z);
          if (lvl <= 1) break;
          olvl = chunk.getBlkLight(x+1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x+1, y, z).absorbLight(lvl);
            nlvl = getBlock2(x+1, y, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              setBlkLight(x+1,y,z, nlvl);
              if (nlvl > 1) add(BLK, x+1,y,z);
            }
          }
          olvl = chunk.getBlkLight(x-1,y,z);
          if (olvl < lvl) {
            nlvl = getBlock(x-1, y, z).absorbLight(lvl);
            nlvl = getBlock2(x-1, y, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              setBlkLight(x-1,y,z, nlvl);
              if (nlvl > 1) add(BLK, x-1,y,z);
            }
          }
          olvl = chunk.getBlkLight(x,y+1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y+1, z).absorbLight(lvl);
            nlvl = getBlock2(x, y+1, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              setBlkLight(x,y+1,z, nlvl);
              if (nlvl > 1) add(BLK, x,y+1,z);
            }
          }
          olvl = chunk.getBlkLight(x,y-1,z);
          if (olvl < lvl) {
            nlvl = getBlock(x, y-1, z).absorbLight(lvl);
            nlvl = getBlock2(x, y-1, z).absorbLight(nlvl);
            if (nlvl > olvl) {
              setBlkLight(x,y-1,z, nlvl);
              if (nlvl > 1) add(BLK, x,y-1,z);
            }
          }
          olvl = chunk.getBlkLight(x,y,z+1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z+1).absorbLight(lvl);
            nlvl = getBlock2(x, y, z+1).absorbLight(nlvl);
            if (nlvl > olvl) {
              setBlkLight(x,y,z+1, nlvl);
              if (nlvl > 1) add(BLK, x,y,z+1);
            }
          }
          olvl = chunk.getBlkLight(x,y,z-1);
          if (olvl < lvl) {
            nlvl = getBlock(x, y, z-1).absorbLight(lvl);
            nlvl = getBlock2(x, y, z-1).absorbLight(nlvl);
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
  private BlockBase getBlock2(int x, int y, int z) {
    return Static.blocks.blocks[chunk.getID2(x,y,z)];
  }
  private boolean setSunLight(int x, int y, int z, int lvl) {
    if (y < 0) return false;
    if (y > 255) return false;
    Chunk c = chunk;
    while (x < 0) {c = c.W; x += 16;}
    while (x > 15) {c = c.E; x -= 16;}
    while (z < 0) {c = c.N; z += 16;}
    while (z > 15) {c = c.S; z -= 16;}
    byte o = c.getLights(x, y, z);
    o &= BLK_LIGHT_MASK;
    o |= lvl;
    c.setLights(x, y, z, o);
    return true;
  }
  private void setBlkLight(int x, int y, int z, int lvl) {
    if (y < 0) return;
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {c = c.W; x += 16;}
    while (x > 15) {c = c.E; x -= 16;}
    while (z < 0) {c = c.N; z += 16;}
    while (z > 15) {c = c.S; z -= 16;}
    byte o = c.getLights(x, y, z);
    o &= SUN_LIGHT_MASK;
    o |= lvl << 4;
    c.setLights(x, y, z, o);
  }
}
