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
  private Chunk temp = new Chunk();
  private Chunk tn = new Chunk();
  private Chunk te = new Chunk();
  private Chunk ts = new Chunk();
  private Chunk tw = new Chunk();
  private Chunk tne = new Chunk();
  private Chunk tnw = new Chunk();
  private Chunk tse = new Chunk();
  private Chunk tsw = new Chunk();

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
    temp.setLights(new byte[256][]);
    temp.N = chunk.N;
    temp.E = chunk.E;
    temp.S = chunk.S;
    temp.W = chunk.W;
    int lvl;
    for(int y=0;y<256;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          char id = chunk.getID(x,y,z);
          lvl = Static.blocks.blocks[id].emitLight << 4;
          temp.setLights(x,y,z,lvl | ambientLvl);
        }
      }
    }
  }

  private void addBlkInside() {
    int lvl;
    for(int y=0;y<256;y++) {
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          lvl = temp.getBlkLight(x, y, z);
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
      chunk.setLights(temp.getLights());
    }


    chunk.needLights = false;
    chunk.needRelight = false;

    chunk.dirty = true;
    chunk.needBuildBuffers = true;
    chunk.N.dirty = true;
    chunk.N.needBuildBuffers = true;
    chunk.E.dirty = true;
    chunk.E.needBuildBuffers = true;
    chunk.S.dirty = true;
    chunk.S.needBuildBuffers = true;
    chunk.W.dirty = true;
    chunk.W.needBuildBuffers = true;
    chunk.N.E.dirty = true;
    chunk.N.E.needBuildBuffers = true;
    chunk.N.W.dirty = true;
    chunk.N.W.needBuildBuffers = true;
    chunk.S.E.dirty = true;
    chunk.S.E.needBuildBuffers = true;
    chunk.S.W.dirty = true;
    chunk.S.W.needBuildBuffers = true;
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
    temp.setLights(new byte[256][]);
    temp.N = chunk.N;
    temp.E = chunk.E;
    temp.S = chunk.S;
    temp.W = chunk.W;
    tn.setLights(copyLights(chunk.N.getLights()));
    te.setLights(copyLights(chunk.E.getLights()));
    ts.setLights(copyLights(chunk.S.getLights()));
    tw.setLights(copyLights(chunk.W.getLights()));
    tn.E = tne;
    tn.W = tnw;
    ts.E = tse;
    ts.W = tsw;
    te.N = tne;
    tw.N = tnw;
    te.S = tse;
    tw.S = tsw;
    tne.setLights(copyLights(chunk.N.E.getLights()));
    tnw.setLights(copyLights(chunk.N.W.getLights()));
    tse.setLights(copyLights(chunk.S.E.getLights()));
    tsw.setLights(copyLights(chunk.S.W.getLights()));

    int lvl;
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        for(int x=x1;x<=x2;x++) {
          char id = chunk.getID(x,y,z);
          lvl = Static.blocks.blocks[id].emitLight << 4;
          temp.setLights(x,y,z,lvl | ambientLvl);
        }
      }
    }
  }

  private void addBlkInside(int x1,int y1,int z1,int x2,int y2,int z2) {
    int lvl;
    for(int y=y1;y<=y2;y++) {
      for(int z=z1;z<=z2;z++) {
        for(int x=x1;x<=x2;x++) {
          lvl = temp.getBlkLight(x, y, z);
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
        lvl = temp.getBlkLight(x1-1,y,z);
        if (lvl > 0) {
          add(BLK, x1-1, y, z);
        }
        lvl = temp.getBlkLight(x2+1,y,z);
        if (lvl > 0) {
          add(BLK, x2+1, y, z);
        }
      }
    }
    //north / south planes
    for(int y=y1;y<=y2;y++) {
      for(int x=x1;x<=x2;x++) {
        lvl = temp.getBlkLight(x,y,z1-1);
        if (lvl > 0) {
          add(BLK, x, y, z1-1);
        }
        lvl = temp.getBlkLight(x,y,z2+1);
        if (lvl > 0) {
          add(BLK, x, y, z2+1);
        }
      }
    }
  }

  public void update(Chunk chunk) {
    if (!chunk.canLights()) {
//      Static.log("Failed to update light:" + chunk.cx + "," + chunk.cz);
      return;
    }
    int x1 = -14;
    int y1 = 0;
    int z1 = -14;
    int x2 = 15+14;
    int y2 = 255;
    int z2 = 15+14;
    synchronized(lock) {
      this.chunk = chunk;
      tail = head = 0;
      reset(x1,y1,z1,x2,y2,z2);
      addBlkInside(x1,y1,z1,x2,y2,z2);
      addBlkEdges(x1,y1,z1,x2,y2,z2);
      processQueue();
      chunk.setLights(temp.getLights());
      chunk.N.setLights(tn.getLights());
      chunk.E.setLights(te.getLights());
      chunk.S.setLights(ts.getLights());
      chunk.W.setLights(tw.getLights());
      chunk.N.E.setLights(tne.getLights());
      chunk.N.W.setLights(tnw.getLights());
      chunk.S.E.setLights(tse.getLights());
      chunk.S.W.setLights(tsw.getLights());
    }

    chunk.needLights = false;
    chunk.needRelight = false;

    chunk.dirty = true;
    chunk.needBuildBuffers = true;
    chunk.N.dirty = true;
    chunk.N.needBuildBuffers = true;
    chunk.E.dirty = true;
    chunk.E.needBuildBuffers = true;
    chunk.S.dirty = true;
    chunk.S.needBuildBuffers = true;
    chunk.W.dirty = true;
    chunk.W.needBuildBuffers = true;
    chunk.N.E.dirty = true;
    chunk.N.E.needBuildBuffers = true;
    chunk.N.W.dirty = true;
    chunk.N.W.needBuildBuffers = true;
    chunk.S.E.dirty = true;
    chunk.S.E.needBuildBuffers = true;
    chunk.S.W.dirty = true;
    chunk.S.W.needBuildBuffers = true;
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
          lvl = temp.getBlkLight(x,y,z);
          if (lvl <= 1) break;
          if (x < 31) {
            olvl = temp.getBlkLight(x+1,y,z);
            if (olvl < lvl) {
              nlvl = getBlock(x+1, y, z).absorbLight(lvl);
              nlvl = getBlock2(x+1, y, z).absorbLight(nlvl);
              if (nlvl > olvl) {
                setBlkLight(x+1,y,z, nlvl);
                if (nlvl > 1) add(BLK, x+1,y,z);
              }
            }
          }
          if (x > -15) {
            olvl = temp.getBlkLight(x-1,y,z);
            if (olvl < lvl) {
              nlvl = getBlock(x-1, y, z).absorbLight(lvl);
              nlvl = getBlock2(x-1, y, z).absorbLight(nlvl);
              if (nlvl > olvl) {
                setBlkLight(x-1,y,z, nlvl);
                if (nlvl > 1) add(BLK, x-1,y,z);
              }
            }
          }
          if (y < 255) {
            olvl = temp.getBlkLight(x,y+1,z);
            if (olvl < lvl) {
              nlvl = getBlock(x, y+1, z).absorbLight(lvl);
              nlvl = getBlock2(x, y+1, z).absorbLight(nlvl);
              if (nlvl > olvl) {
                setBlkLight(x,y+1,z, nlvl);
                if (nlvl > 1) add(BLK, x,y+1,z);
              }
            }
          }
          if (y > 0) {
            olvl = temp.getBlkLight(x,y-1,z);
            if (olvl < lvl) {
              nlvl = getBlock(x, y-1, z).absorbLight(lvl);
              nlvl = getBlock2(x, y-1, z).absorbLight(nlvl);
              if (nlvl > olvl) {
                setBlkLight(x,y-1,z, nlvl);
                if (nlvl > 1) add(BLK, x,y-1,z);
              }
            }
          }
          if (z < 31) {
            olvl = temp.getBlkLight(x,y,z+1);
            if (olvl < lvl) {
              nlvl = getBlock(x, y, z+1).absorbLight(lvl);
              nlvl = getBlock2(x, y, z+1).absorbLight(nlvl);
              if (nlvl > olvl) {
                setBlkLight(x,y,z+1, nlvl);
                if (nlvl > 1) add(BLK, x,y,z+1);
              }
            }
          }
          if (z > -15) {
            olvl = temp.getBlkLight(x,y,z-1);
            if (olvl < lvl) {
              nlvl = getBlock(x, y, z-1).absorbLight(lvl);
              nlvl = getBlock2(x, y, z-1).absorbLight(nlvl);
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
    return Static.blocks.blocks[chunk.getID(x,y,z)];
  }

  private BlockBase getBlock2(int x, int y, int z) {
    return Static.blocks.blocks[chunk.getID2(x,y,z)];
  }

  private void setBlkLight(int x, int y, int z, int lvl) {
    temp.setLights(x, y, z, (lvl << 4) | ambientLvl);
  }
}
