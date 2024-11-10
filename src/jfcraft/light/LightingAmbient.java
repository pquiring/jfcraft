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
  private void reset() {
    chunk.setLights(new byte[256][]);
    chunk.N = chunk.N;
    chunk.E = chunk.E;
    chunk.S = chunk.S;
    chunk.W = chunk.W;
    int lvl;
    for(int y=0;y<256;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          char id = chunk.getBlock(x,y,z);
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
      this.chunk = chunk;
      reset();
      tail = head = 0;
      addBlkInside();
      processQueue();
      chunk.setLights(chunk.getLights());
    }


    chunk.needLights = false;
    chunk.needRelight = false;
  }

  private byte[][] copyLights(byte in[][]) {
    byte lights[][] = new byte[256][];
    for(int a=0;a<256;a++) {
      if (in[a] == null) continue;
      lights[a] = new byte[16*16];
      System.arraycopy(in[a],0,lights[a],0,16*16);
    }
    return lights;
  }

  private void reset(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        for(int x=x1;x<=x2;x++) {
          char id = chunk.getBlock(x,y,z);
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

  public void update(Chunk chunk, int x1,int y1,int z1,int x2,int y2,int z2) {
    if (!chunk.canLights()) {
//      Static.log("Failed to update light:" + chunk.cx + "," + chunk.cz);
      return;
    }
    synchronized(lock) {
      this.chunk = chunk;
      tail = head = 0;
      reset(x1,y1,z1,x2,y2,z2);
      addBlkInside(x1,y1,z1,x2,y2,z2);
      addBlkEdges(x1,y1,z1,x2,y2,z2);
      processQueue();
    }

    chunk.needLights = false;
    chunk.needRelight = false;
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
          if (x < 31) {
            olvl = chunk.getBlkLight(x+1,y,z);
            if (olvl < lvl) {
              nlvl = getBlock(x+1, y, z).absorbLight(lvl);
              nlvl = getBlock2(x+1, y, z).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x+1,y,z, nlvl);
                if (nlvl > 1) add(BLK, x+1,y,z);
              }
            }
          }
          if (x > -15) {
            olvl = chunk.getBlkLight(x-1,y,z);
            if (olvl < lvl) {
              nlvl = getBlock(x-1, y, z).absorbLight(lvl);
              nlvl = getBlock2(x-1, y, z).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x-1,y,z, nlvl);
                if (nlvl > 1) add(BLK, x-1,y,z);
              }
            }
          }
          if (y < 255) {
            olvl = chunk.getBlkLight(x,y+1,z);
            if (olvl < lvl) {
              nlvl = getBlock(x, y+1, z).absorbLight(lvl);
              nlvl = getBlock2(x, y+1, z).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x,y+1,z, nlvl);
                if (nlvl > 1) add(BLK, x,y+1,z);
              }
            }
          }
          if (y > 0) {
            olvl = chunk.getBlkLight(x,y-1,z);
            if (olvl < lvl) {
              nlvl = getBlock(x, y-1, z).absorbLight(lvl);
              nlvl = getBlock2(x, y-1, z).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x,y-1,z, nlvl);
                if (nlvl > 1) add(BLK, x,y-1,z);
              }
            }
          }
          if (z < 31) {
            olvl = chunk.getBlkLight(x,y,z+1);
            if (olvl < lvl) {
              nlvl = getBlock(x, y, z+1).absorbLight(lvl);
              nlvl = getBlock2(x, y, z+1).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x,y,z+1, nlvl);
                if (nlvl > 1) add(BLK, x,y,z+1);
              }
            }
          }
          if (z > -15) {
            olvl = chunk.getBlkLight(x,y,z-1);
            if (olvl < lvl) {
              nlvl = getBlock(x, y, z-1).absorbLight(lvl);
              nlvl = getBlock2(x, y, z-1).absorbLight(nlvl);
              if (nlvl == lvl) nlvl--;
              if (nlvl > olvl) {
                setBlkLight(x,y,z-1, nlvl);
                if (nlvl > 1) add(BLK, x,y,z-1);
              }
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
    return Static.blocks.blocks[chunk.getBlock(x,y,z)];
  }

  private BlockBase getBlock2(int x, int y, int z) {
    return Static.blocks.blocks[chunk.getBlock2(x,y,z)];
  }

  private void setBlkLight(int x, int y, int z, int lvl) {
    chunk.setLights(x, y, z, (lvl << 4) | ambientLvl);
  }
}
