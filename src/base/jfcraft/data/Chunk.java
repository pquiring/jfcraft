package jfcraft.data;

/** Chunk : holds blocks, items and entities for a 16x256x16 area.
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class Chunk /*extends ClientServer*/ implements SerialClass, SerialCreator {
  public int dim,cx,cz;
  //Blocks order : Y Z X
  //char is 16bits unsigned which allows full usage
  private char blocks[][] = new char[256][];  //type:16
  private byte bits[][] = new byte[256][];  //dir:4 var:4
  private char blocks2[][] = new char[256][];  //type:16
  private byte bits2[][] = new byte[256][];  //dir:4 var:4
  private byte lights[][] = new byte[256][];  //blk_light:4 sun_light:4
  //blocks2 is for WATER, LAVA, SNOW, etc. (extra plane)

  public long seed;
//  public boolean readOnly;
  public boolean needPhase2, needPhase3;
  public boolean needLights;  //generator phases
  //flags
  public boolean dirty;  //need to write to disk
  public boolean needRelight;
  public boolean ready;
  public boolean inRange, isAllEmpty;

  public int readOnly1, readOnly2;  //read only range (-1 to disable)

  //biome data
  public byte biome[] = new byte[16 * 16];
  public float temp[] = new float[16 * 16];
  public float rain[] = new float[16 * 16];
  public float elev[] = new float[16 * 16];  //elevation
  public float depth[] = new float[16 * 16];  //used in end world

  public ArrayList<EntityBase> entities = new ArrayList<EntityBase>();
  public ArrayList<Tick> ticks = new ArrayList<Tick>();  //server-side only
  public ArrayList<ExtraBase> extras = new ArrayList<ExtraBase>();

  public Object envData;  //environment data

  //end of serializable data

  public static final int BLOCK_LIGHT_MASK = 0xf0;
  public static final int SUN_LIGHT_MASK = 0x0f;

  //size of each chunk
  public static final int X = 16;
  public static final int Y = 256;
  public static final int Z = 16;

  public Chunk N,E,S,W;  //links : north, east, south, west
  private static class Lock {};
  public Lock lock = new Lock();
  public RenderDest dest;
  public GLMatrix mat;
  public int adjCount;  //# of adj chunks to render (0-6)
  public ArrayList<ExtraCrack> cracks = new ArrayList<ExtraCrack>();

  //render dest buffers
  public static final int DEST_NORMAL = 0;  //stitched block
  public static final int DEST_ALPHA = 1;  //stitched block (ALPHA)
  public static final int DEST_TEXT = 2;  //ASCII text
  public static final int DEST_COUNT = 3; //DEST_NORMAL + DEST_ALPHA + DEST_TEXT

  public World world;
  public Chunks chunks;

  /** Old Chunk read from file/network. */
  public Chunk(World world) {
    this.world = world;
    if (world != null) {
      chunks = world.chunks;
    }
  }

  /** New Chunk created on server side only */
  public Chunk(int dim, int cx, int cz) {
    this.chunks = chunks;
    this.world = Static.server.world;
    chunks = world.chunks;
    this.dim = dim;
    this.cx = cx;
    this.cz = cz;
    needPhase2 = true;
    needPhase3 = true;
    needLights = true;
    dirty = true;
  }

  /** Create client side objects. */
  public void createObjects() {
    dest = new RenderDest(DEST_COUNT);
    mat = new GLMatrix();
    mat.setIdentity();
    mat.setTranslate(cx * 16.0f, 0, cz * 16.0f);
  }

  public void copyBuffers() {
//    System.out.println("copyBuffers:" + cx + "," + cz);
    for(int a=0;a<DEST_COUNT;a++) {
      if (!dest.exists(a)) continue;
      dest.getBuffers(a).copyBuffers();
    }
    ready = true;
  }

  public void render(RenderBuffers obj) {
    obj.bindBuffers();
    obj.render();
  }

  /** Determines if lighting if different around a block. */
  private boolean doesLightingDiffer(int x,int y,int z) {
    int ll = -1, la = -1, lb = -1, ln = -1, le = -1, ls = -1, lw = -1; //light levels around block
    BlockBase base1, base2;
    if (y < 255) {
      base1 = Static.blocks.blocks[getID(x,y+1,z)];
      base2 = Static.blocks.blocks[getID2(x,y+1,z)];
      if (!base1.isOpaque && !base2.isOpaque) {
        la = getLights(x, y+1, z);
        if (ll != -1 && ll != la) {
          return true;
        }
        ll = la;
      }
    }
    if (y > 0) {
      base1 = Static.blocks.blocks[getID(x,y-1,z)];
      base2 = Static.blocks.blocks[getID2(x,y-1,z)];
      if (!base1.isOpaque && !base2.isOpaque) {
        lb = getLights(x, y-1, z);
        if (ll != -1 && ll != lb) {
          return true;
        }
        ll = lb;
      }
    }
    base1 = Static.blocks.blocks[getID(x,y,z-1)];
    base2 = Static.blocks.blocks[getID2(x,y,z-1)];
    if (!base1.isOpaque && !base2.isOpaque) {
      ln = getLights(x, y, z-1);
      if (ll != -1 && ll != ln) {
        return true;
      }
      ll = ln;
    }
    base1 = Static.blocks.blocks[getID(x+1,y,z)];
    base2 = Static.blocks.blocks[getID2(x+1,y,z)];
    if (!base1.isOpaque && !base2.isOpaque) {
      le = getLights(x+1, y, z);
      if (ll != -1 && ll != le) {
        return true;
      }
      ll = le;
    }
    base1 = Static.blocks.blocks[getID(x,y,z+1)];
    base2 = Static.blocks.blocks[getID2(x,y,z+1)];
    if (!base1.isOpaque && !base2.isOpaque) {
      ls = getLights(x, y, z+1);
      if (ll != -1 && ll != ls) {
        return true;
      }
      ll = ls;
    }
    base1 = Static.blocks.blocks[getID(x-1,y,z)];
    base2 = Static.blocks.blocks[getID2(x-1,y,z)];
    if (!base1.isOpaque && !base2.isOpaque) {
      lw = getLights(x-1, y, z);
      if (ll != -1 && ll != lw) {
        return true;
      }
      ll = lw;
    }
    if ((la & SUN_LIGHT_MASK) == 15 && lb != -1) {
      //sunlight might get blocked
      return true;
    }
    return false;
  }

  /** Determines what area of the lighting will be effected. */
  private int[] getLightCoordsSet(int x,int y,int z, BlockBase newBlock, BlockBase oldBlock) {
    //check how lighting will change
    int xyz[] = new int[6];
    //set min area
    if (newBlock.emitLight > 0 || oldBlock.emitLight > 0) {
      int el = newBlock.emitLight;
      if (oldBlock.emitLight > el) el = oldBlock.emitLight;
      xyz[0] = x - el;
      xyz[1] = y - el;
      xyz[2] = z - el;
      xyz[3] = x + el;
      xyz[4] = y + el;
      xyz[5] = z + el;
    } else {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
      xyz[3] = x;
      xyz[4] = y;
      xyz[5] = z;
    }
    if (doesLightingDiffer(x,y,z)) {
      xyz[0] = x-14;
      xyz[1] = 0;
      xyz[2] = z-14;
      xyz[3] = x+14;
      xyz[4] = y+14;
      xyz[5] = z+14;
    }
    return xyz;
  }

  /** Determines what area of the lighting will be effected. */
  private int[] getLightCoordsClear(int x,int y,int z, BlockBase oldBlock) {
    //check how lighting will change
    int xyz[] = new int[6];
    //set min area
    if (oldBlock.emitLight > 0) {
      int el = oldBlock.emitLight;
      xyz[0] = x - el;
      xyz[1] = y - el;
      xyz[2] = z - el;
      xyz[3] = x + el;
      xyz[4] = y + el;
      xyz[5] = z + el;
    } else {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
      xyz[3] = x;
      xyz[4] = y;
      xyz[5] = z;
    }
    if (doesLightingDiffer(x,y,z)) {
      xyz[0] = x-14;
      xyz[1] = 0;
      xyz[2] = z-14;
      xyz[3] = x+14;
      xyz[4] = y+14;
      xyz[5] = z+14;
    }
    return xyz;
  }

  public void setBlock(int x,int y,int z,char id, int _bits) {
    if (x > 15) {E.setBlock(x-16, y, z, id, _bits); return;}
    if (x < 0) {W.setBlock(x+16, y, z, id, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setBlock(x, y, z-16, id, _bits); return;}
    if (z < 0) {N.setBlock(x, y, z+16, id, _bits); return;}
    if (id == 0) {
      Static.log("optz:setBlock used as clearBlock");
      clearBlock(x,y,z);
      return;
    }
    int p = z * 16 + x;
    BlockBase newBlock = Static.blocks.blocks[id];
    char b[];
    char oldid;
    synchronized(lock) {
      if (newBlock.isBlocks2) {
        b = blocks2[y];
        if (b == null) {
          b = new char[16*16];
          blocks2[y] = b;
          bits2[y] = new byte[16*16];
        }
        oldid = b[p];
        b[p] = id;
        bits2[y][p] = (byte)_bits;
      } else {
        if (newBlock.isSolid) {
          if (blocks2[y] != null) {
            //remove water/lava/etc.
            blocks2[y][p] = 0;
            bits2[y][p] = 0;
          }
        }
        b = blocks[y];
        if (b == null) {
          b = new char[16*16];
          blocks[y] = b;
          bits[y] = new byte[16*16];
        }
        oldid = b[p];
        b[p] = id;
        bits[y][p] = (byte)_bits;
      }
      if (needLights) return;
      needRelight = true;
      dirty = true;
      if (isBorder()) return;
      int xyz[] = getLightCoordsSet(x,y,z, newBlock, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
    }
  }

  public boolean setBlocksIfEmpty(Coords c1, char id1, int _bits1, Coords c2, char id2, int _bits2) {
    synchronized(lock) {
      synchronized(c2.chunk.lock) {
        if (getID(c1.gx, c1.gy, c1.gz) != 0) return false;
        if (c2.chunk.getID(c2.gx, c2.gy, c2.gz) != 0) return false;
        setBlock(c1.gx, c1.gy, c1.gz, id1, _bits1);
        c2.chunk.setBlock(c2.gx, c2.gy, c2.gz, id2, _bits2);
      }
    }
    return true;
  }

  public boolean replaceBlock(int x,int y,int z,char id, int _bits, char oldid) {
    if (x > 15) {return E.replaceBlock(x-16, y, z, id, _bits, oldid);}
    if (x < 0) {return W.replaceBlock(x+16, y, z, id, _bits, oldid);}
    if (y > 255) return false;
    if (y < 0) return false;
    if (z > 15) {return S.replaceBlock(x, y, z-16, id, _bits, oldid);}
    if (z < 0) {return N.replaceBlock(x, y, z+16, id, _bits, oldid);}
    synchronized(lock) {
      if (getID(x, y, z) != oldid) return false;
      setBlock(x,y,z,id,_bits);
    }
    return false;
  }


  /**
   * Clears a block.
   *
   * @return old block id
   */
  public char clearBlock(int x,int y,int z) {
    if (x > 15) {return E.clearBlock(x-16, y, z);}
    if (x < 0) {return W.clearBlock(x+16, y, z);}
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) {return S.clearBlock(x, y, z-16);}
    if (z < 0) {return N.clearBlock(x, y, z+16);}
    int p = z * 16 + x;
    char b[] = blocks[y];
    if (b == null) return 0;
    char oldid;
    synchronized(lock) {
      oldid = b[p];
      b[p] = 0;
      bits[y][p] = 0;
      boolean empty = true;
      for(p=0;p<16*16;p++) {
        if (b[p] != 0) {empty = false; break;}
      }
      if (empty) {
        blocks[y] = null;
        bits[y] = null;
      }

      if (needLights) return oldid;
      needRelight = true;
      dirty = true;
      int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
      return oldid;
    }
  }

  /** Clears a block in the layer 2. */
  public char clearBlock2(int x,int y,int z) {
    if (x > 15) {return E.clearBlock2(x-16, y, z);}
    if (x < 0) {return W.clearBlock2(x+16, y, z);}
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) {return S.clearBlock2(x, y, z-16);}
    if (z < 0) {return N.clearBlock2(x, y, z+16);}
    int p = z * 16 + x;
    char b[] = blocks2[y];
    if (b == null) return 0;
    char oldid;
    synchronized(lock) {
      oldid = b[p];
      b[p] = 0;
      bits2[y][p] = 0;

      boolean empty = true;
      for(p=0;p<16*16;p++) {
        if (b[p] != 0) {empty = false; break;}
      }
      if (empty) {
        blocks2[y] = null;
        bits2[y] = null;
      }

      if (needLights) return oldid;
      needRelight = true;
      dirty = true;
      int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
      return oldid;
    }
  }

  /** Clear block if current id == id */
  public boolean clearBlockIf(int x,int y,int z, char id) {
    if (x > 15) {return E.clearBlockIf(x-16, y, z, id);}
    if (x < 0) {return W.clearBlockIf(x+16, y, z, id);}
    if (y > 255) return false;
    if (y < 0) return false;
    if (z > 15) {return S.clearBlockIf(x, y, z-16, id);}
    if (z < 0) {return N.clearBlockIf(x, y, z+16, id);}
    int p = z * 16 + x;
    char b[] = blocks[y];
    if (b == null) return false;
    char oldid;
    synchronized(lock) {
      oldid = b[p];
      if (oldid != id) return false;
      b[p] = 0;
      bits[y][p] = 0;
      boolean empty = true;
      for(p=0;p<16*16;p++) {
        if (b[p] != 0) {empty = false; break;}
      }
      if (empty) {
        blocks[y] = null;
        bits[y] = null;
      }

      if (needLights) return true;
      needRelight = true;
      dirty = true;
      int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
      return true;
    }
  }

  /** Clear block in layer 2 if current id == id */
  public boolean clearBlockIf2(int x,int y,int z, char id) {
    if (x > 15) {return E.clearBlockIf2(x-16, y, z, id);}
    if (x < 0) {return W.clearBlockIf2(x+16, y, z, id);}
    if (y > 255) return false;
    if (y < 0) return false;
    if (z > 15) {return S.clearBlockIf2(x, y, z-16, id);}
    if (z < 0) {return N.clearBlockIf2(x, y, z+16, id);}
    int p = z * 16 + x;
    char b[] = blocks2[y];
    if (b == null) return false;
    char oldid;
    synchronized(lock) {
      oldid = b[p];
      if (oldid != id) return false;
      b[p] = 0;
      bits2[y][p] = 0;
      boolean empty = true;
      for(p=0;p<16*16;p++) {
        if (b[p] != 0) {empty = false; break;}
      }
      if (empty) {
        blocks2[y] = null;
        bits2[y] = null;
      }

      if (needLights) return true;
      needRelight = true;
      dirty = true;
      int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
      return true;
    }
  }

  public boolean setBlockIfEmpty(int x,int y,int z,char id, int _bits) {
    if (x > 15) {return E.setBlockIfEmpty(x-16, y, z, id, _bits);}
    if (x < 0) {return W.setBlockIfEmpty(x+16, y, z, id, _bits);}
    if (y > 255) return false;
    if (y < 0) return false;
    if (z > 15) {return S.setBlockIfEmpty(x, y, z-16, id, _bits);}
    if (z < 0) {return N.setBlockIfEmpty(x, y, z+16, id, _bits);}
    int p = z * 16 + x;
    BlockBase newBlock = Static.blocks.blocks[id];
    char oldid;
    synchronized(lock) {
      if (newBlock.isBlocks2) {
        if (blocks[y] != null) {
          //check if there is solid block in layer 1
          BlockBase oldBlock = Static.blocks.blocks[blocks[y][p]];
          if (oldBlock.isSolid) {
            return false;
          }
        }
        if (blocks2[y] == null) {
          blocks2[y] = new char[16*16];
          bits2[y] = new byte[16*16];
        } else {
          if (blocks2[y][p] != 0) return false;
        }
        oldid = blocks2[y][p];
        blocks2[y][p] = id;
        bits2[y][p] = (byte)_bits;
      } else {
        if (blocks[y] == null) {
          blocks[y] = new char[16*16];
          bits[y] = new byte[16*16];
        } else {
          if (blocks[y][p] != 0) return false;
        }
        if (newBlock.isSolid) {
          if (blocks2[y] != null) {
            blocks2[y][p] = 0;
            bits2[y][p] = 0;
          }
        }
        oldid = blocks[y][p];
        blocks[y][p] = id;
        bits[y][p] = (byte)_bits;
      }
      if (needLights) return true;
      needRelight = true;
      dirty = true;
      int xyz[] = getLightCoordsSet(x,y,z, newBlock, Static.blocks.blocks[oldid]);
      if (world.isClient) {
        Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      } else {
        Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
      }
    }
    return true;
  }

  /** Sets a block ID (does not update lighting). */
  public void setID(int x,int y,int z, char id) {
    if (x > 15) {E.setID(x-16, y, z, id); return;}
    if (x < 0) {W.setID(x+16, y, z, id); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setID(x, y, z-16, id); return;}
    if (z < 0) {N.setID(x, y, z+16, id); return;}
    synchronized(lock) {
      if (blocks[y] == null) {
        blocks[y] = new char[16*16];
        bits[y] = new byte[16*16];
      }
      blocks[y][z * 16 + x] = id;
    }
  }

  /** Sets a block ID and bits (does not update lighting). */
  public void setIDBits(int x,int y,int z, char id, int _bits) {
    if (x > 15) {E.setIDBits(x-16, y, z, id, _bits); return;}
    if (x < 0) {W.setIDBits(x+16, y, z, id, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setIDBits(x, y, z-16, id, _bits); return;}
    if (z < 0) {N.setIDBits(x, y, z+16, id, _bits); return;}
    synchronized(lock) {
      if (blocks[y] == null) {
        blocks[y] = new char[16*16];
        bits[y] = new byte[16*16];
      }
      int p = z * 16 + x;
      blocks[y][p] = id;
      bits[y][p] = (byte)_bits;
    }
  }

  public char getID(int x,int y, int z) {
    if (x > 15) return E.getID(x-16, y, z);
    if (x < 0) return W.getID(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getID(x, y, z-16);
    if (z < 0) return N.getID(x, y, z+16);
    if (blocks[y] == null) return 0;
    return blocks[y][z * 16 + x];
  }

  public int getBits(int x,int y, int z) {
    if (x > 15) return E.getBits(x-16, y, z);
    if (x < 0) return W.getBits(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getBits(x, y, z-16);
    if (z < 0) return N.getBits(x, y, z+16);
    if (bits[y] == null) return 0;
    return bits[y][z * 16 + x] & 0xff;
  }

  public byte getBiome(int x, int z) {
    if (x > 15) return E.getBiome(x-16, z);
    if (x < 0) return W.getBiome(x+16, z);
    if (z > 15) return S.getBiome(x, z-16);
    if (z < 0) return N.getBiome(x, z+16);
    return biome[z * 16 + x];
  }

  public float getElev(int x, int z) {
    if (x > 15) return E.getElev(x-16, z);
    if (x < 0) return W.getElev(x+16, z);
    if (z > 15) return S.getElev(x, z-16);
    if (z < 0) return N.getElev(x, z+16);
    return elev[z * 16 + x];
  }

  /** Increment var to max.  Returns new value of -1 if not incremented. */
  public int incVar(int x, int y, int z, int max) {
    int var;
    synchronized(lock) {
      int bits = getBits(x,y,z);
      var = getVar(bits);
      if (var >= max) return -1;
      var++;
      bits &= 0xf0;
      bits |= var;
      setBits(x,y,z,bits);
    }
    return var;
  }

  public void setBits(int x,int y, int z,int _bits) {
    if (x > 15) {E.setBits(x-16, y, z, _bits); return;}
    if (x < 0) {W.setBits(x+16, y, z, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setBits(x, y, z-16, _bits); return;}
    if (z < 0) {N.setBits(x, y, z+16, _bits); return;}
    synchronized(lock) {
      if (blocks[y] == null) {
        blocks[y] = new char[16*16];
        bits[y] = new byte[16*16];
      }
      bits[y][z * 16 + x] = (byte)_bits;
    }
  }

  /** Sets a block ID (does not update lighting). */
  public void setID2(int x,int y,int z, char id) {
    if (x > 15) {E.setID2(x-16, y, z, id); return;}
    if (x < 0) {W.setID2(x+16, y, z, id); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setID2(x, y, z-16, id); return;}
    if (z < 0) {N.setID2(x, y, z+16, id); return;}
    synchronized(lock) {
      if (blocks2[y] == null) {
        blocks2[y] = new char[16*16];
        bits2[y] = new byte[16*16];
      }
      blocks2[y][z * 16 + x] = id;
    }
  }

  /** Sets a block ID and bits (does not update lighting). */
  public void setID2Bits(int x,int y,int z, char id, int _bits) {
    if (x > 15) {E.setID2Bits(x-16, y, z, id, _bits); return;}
    if (x < 0) {W.setID2Bits(x+16, y, z, id, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setID2Bits(x, y, z-16, id, _bits); return;}
    if (z < 0) {N.setID2Bits(x, y, z+16, id, _bits); return;}
    synchronized(lock) {
      if (blocks2[y] == null) {
        blocks2[y] = new char[16*16];
        bits2[y] = new byte[16*16];
      }
      int p = z * 16 + x;
      blocks2[y][p] = id;
      bits2[y][p] = (byte)_bits;
    }
  }

  public char getID2(int x,int y, int z) {
    if (x > 15) return E.getID2(x-16, y, z);
    if (x < 0) return W.getID2(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getID2(x, y, z-16);
    if (z < 0) return N.getID2(x, y, z+16);
    if (blocks2[y] == null) return 0;
    return blocks2[y][z * 16 + x];
  }

  public int getBits2(int x,int y, int z) {
    if (x > 15) return E.getBits2(x-16, y, z);
    if (x < 0) return W.getBits2(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getBits2(x, y, z-16);
    if (z < 0) return N.getBits2(x, y, z+16);
    if (bits2[y] == null) return 0;
    return bits2[y][z * 16 + x] & 0xff;
  }

  public void setBits2(int x,int y, int z,int _bits) {
    if (x > 15) {E.setBits2(x-16, y, z, _bits); return;}
    if (x < 0) {W.setBits2(x+16, y, z, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setBits2(x, y, z-16, _bits); return;}
    if (z < 0) {N.setBits2(x, y, z+16, _bits); return;}
    synchronized(lock) {
      if (blocks2[y] == null) {
        blocks2[y] = new char[16*16];
        bits2[y] = new byte[16*16];
      }
      bits2[y][z * 16 + x] = (byte)_bits;
    }
  }

  public byte getLights(int x,int y, int z) {
    if (x > 15) return E.getLights(x-16, y, z);
    if (x < 0) return W.getLights(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getLights(x, y, z-16);
    if (z < 0) return N.getLights(x, y, z+16);
    byte p[] = lights[y];
    if (p == null) return 0;
    return p[z * 16 + x];
  }

  public void setLights(int x,int y, int z, int v) {
    if (x > 15) {E.setLights(x-16, y, z, v); return;}
    if (x < 0) {W.setLights(x+16, y, z, v); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setLights(x, y, z-16, v); return;}
    if (z < 0) {N.setLights(x, y, z+16, v); return;}
    byte plane[] = lights[y];
    if (plane == null) {
      if (v == 0) return;
      plane = new byte[16*16];
      lights[y] = plane;
    }
    plane[z * 16 + x] = (byte)v;
  }

  public int getSunLight(int x,int y, int z) {
    if (x > 15) return E.getSunLight(x-16, y, z);
    if (x < 0) return W.getSunLight(x+16, y, z);
    if (y > 255) return 15;
    if (y < 0) return 0;
    if (z > 15) return S.getSunLight(x, y, z-16);
    if (z < 0) return N.getSunLight(x, y, z+16);
    byte p[] = lights[y];
    if (p == null) return 0;
    return p[z * 16 + x] & SUN_LIGHT_MASK;
  }

  public int getBlkLight(int x,int y, int z) {
    if (x > 15) return E.getBlkLight(x-16, y, z);
    if (x < 0) return W.getBlkLight(x+16, y, z);
    if (y > 255) return 0;
    if (y < 0) return 0;
    if (z > 15) return S.getBlkLight(x, y, z-16);
    if (z < 0) return N.getBlkLight(x, y, z+16);
    byte p[] = lights[y];
    if (p == null) return 0;
    return (p[z * 16 + x] & BLOCK_LIGHT_MASK) >> 4;
  }

  public void setPlane(int y, char ids[], byte _bits[]) {
    synchronized(lock) {
      blocks[y] = ids;
      bits[y] = _bits;
    }
  }

  public void setPlane2(int y, char ids[], byte _bits[]) {
    synchronized(lock) {
      blocks2[y] = ids;
      bits2[y] = _bits;
    }
  }

  /** Build buffers for this chunk.
   * Client side only.
   */
  public void buildBuffers() {
//    if (cx == -1 && cz == 0) Static.log("buildBuffers:" + cx + "," + cz);
    synchronized(lock) {
      char id, id2;
      int _bits;
      byte _ll;
      int crack;
      BlockBase block = null, adjBlock, block2 = null;
      RenderData data = new RenderData();
      data.chunk = this;
      //reset all objects
      for(int a=0;a<DEST_COUNT;a++) {
        if (!dest.exists(a)) continue;
        dest.getBuffers(a).reset();
      }
      for(int y=0;y<256;y++) {  //+ - 256
        if (blocks[y] == null && blocks2[y] == null) continue;
        data.y = y;
        for(int z=0;z<16;z++) {  //+ - 16
          data.z = z;
          for(int x=0;x<16;x++) {  //+ - 1
            data.x = x;
            data.temp = temp[z * 16 + x];
            data.rain = rain[z * 16 + x];
            id = getID(x,y,z);
            id2 = getID2(x,y,z);
            boolean hasBlock = id != 0;
            boolean hasBlock2 = id2 != 0;
            if (!hasBlock && !hasBlock2) {
              continue;
            }
            if (hasBlock) {
              _bits = getBits(x,y,z);
              data.bits = _bits;
              data.dir[Direction.X] = getDir(_bits);
              data.var[Direction.X] = getVar(_bits);
              block = Static.blocks.blocks[id];
              data.opaque[Direction.X] = block.isOpaque;
            }
            if (hasBlock2) {
              _bits = getBits2(x,y,z);
              data.dir2[Direction.X] = getDir(_bits);
              data.var2[Direction.X] = getVar(_bits);
              data.id2[Direction.X] = id2;
              block2 = Static.blocks.blocks[id2];
            }
            _ll = getLights(x,y,z);
            data.sl[Direction.X] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.X] = ((_ll & 0xf0) >> 4) / 15.0f;
            ExtraCrack c = getCrack(x,y,z);
            if (c != null) {
              crack = (int)(c.dmg / 10.0f);
              if (crack > 9) crack = 9;
            } else {
              crack = -1;
            }
            data.crack = crack;

            if (y > 0) {
              data.id[Direction.B] = getID(x,y-1,z);
              _bits = getBits(x,y-1,z);
              data.dir[Direction.B] = getDir(_bits);
              data.var[Direction.B] = getVar(_bits);
              adjBlock = Static.blocks.blocks[data.id[Direction.B]];
              data.opaque[Direction.B] = adjBlock.isOpaque;
              _ll = getLights(x,y-1,z);
              data.sl[Direction.B] = (_ll & 0x0f) / 15.0f;
              data.bl[Direction.B] = ((_ll & 0xf0) >> 4) / 15.0f;
              if (hasBlock2) {
                data.id2[Direction.B] = getID2(x,y-1,z);
                _bits = getBits2(x,y-1,z);
                data.dir2[Direction.B] = getDir(_bits);
                data.var2[Direction.B] = getVar(_bits);
              }
            } else {
              data.id[Direction.B] = 0;
              data.opaque[Direction.B] = false;
              data.sl[Direction.B] = 0;
              data.bl[Direction.B] = 0;
            }

            if (y < 255) {
              data.id[Direction.A] = getID(x,y+1,z);
              _bits = getBits(x,y+1,z);
              data.dir[Direction.A] = getDir(_bits);
              data.var[Direction.A] = getVar(_bits);
              adjBlock = Static.blocks.blocks[data.id[Direction.A]];
              data.opaque[Direction.A] = adjBlock.isOpaque;
              _ll = getLights(x,y+1,z);
              data.sl[Direction.A] = (_ll & 0x0f) / 15.0f;
              data.bl[Direction.A] = ((_ll & 0xf0) >> 4) / 15.0f;
              if (hasBlock2) {
                data.id2[Direction.A] = getID2(x,y+1,z);
                _bits = getBits2(x,y+1,z);
                data.dir2[Direction.A] = getDir(_bits);
                data.var2[Direction.A] = getVar(_bits);
              }
            } else {
              data.id[Direction.A] = 0;
              data.opaque[Direction.A] = false;
              data.sl[Direction.A] = 1;
              data.bl[Direction.A] = 0;
            }

            data.id[Direction.N] = getID(x,y,z-1);
            _bits = getBits(x,y,z-1);
            data.dir[Direction.N] = getDir(_bits);
            data.var[Direction.N] = getVar(_bits);
            adjBlock = Static.blocks.blocks[data.id[Direction.N]];
            data.opaque[Direction.N] = adjBlock.isOpaque;
            _ll = getLights(x,y,z-1);
            data.sl[Direction.N] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.N] = ((_ll & 0xf0) >> 4) / 15.0f;
            if (hasBlock2) {
              data.id2[Direction.N] = getID2(x,y,z-1);
              _bits = getBits2(x,y,z-1);
              data.dir2[Direction.N] = getDir(_bits);
              data.var2[Direction.N] = getVar(_bits);
            }

            data.id[Direction.S] = getID(x,y,z+1);
            _bits = getBits(x,y,z+1);
            data.dir[Direction.S] = getDir(_bits);
            data.var[Direction.S] = getVar(_bits);
            adjBlock = Static.blocks.blocks[data.id[Direction.S]];
            data.opaque[Direction.S] = adjBlock.isOpaque;
            _ll = getLights(x,y,z+1);
            data.sl[Direction.S] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.S] = ((_ll & 0xf0) >> 4) / 15.0f;
            if (hasBlock2) {
              data.id2[Direction.S] = getID2(x,y,z+1);
              _bits = getBits2(x,y,z+1);
              data.dir2[Direction.S] = getDir(_bits);
              data.var2[Direction.S] = getVar(_bits);
            }

            data.id[Direction.W] = getID(x-1,y,z);
            _bits = getBits(x-1,y,z);
            data.dir[Direction.W] = getDir(_bits);
            data.var[Direction.W] = getVar(_bits);
            adjBlock = Static.blocks.blocks[data.id[Direction.W]];
            data.opaque[Direction.W] = adjBlock.isOpaque;
            _ll = getLights(x-1,y,z);
            data.sl[Direction.W] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.W] = ((_ll & 0xf0) >> 4) / 15.0f;
            if (hasBlock2) {
              data.id2[Direction.W] = getID2(x-1,y,z);
              _bits = getBits2(x-1,y,z);
              data.dir2[Direction.W] = getDir(_bits);
              data.var2[Direction.W] = getVar(_bits);
            }

            data.id[Direction.E] = getID(x+1,y,z);
            _bits = getBits(x+1,y,z);
            data.dir[Direction.E] = getDir(_bits);
            data.var[Direction.E] = getVar(_bits);
            adjBlock = Static.blocks.blocks[data.id[Direction.E]];
            data.opaque[Direction.E] = adjBlock.isOpaque;
            _ll = getLights(x+1,y,z);
            data.sl[Direction.E] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.E] = ((_ll & 0xf0) >> 4) / 15.0f;
            if (hasBlock2) {
              data.id2[Direction.E] = getID2(x+1,y,z);
              _bits = getBits2(x+1,y,z);
              data.dir2[Direction.E] = getDir(_bits);
              data.var2[Direction.E] = getVar(_bits);
            }

            boolean adjLight = false;
            if (hasBlock) {
              data.adjLight = !block.isComplex;
              if (data.adjLight) {
                adjLight = true;
                getFarCorners(data,x,y,z);
              }
              block.buildBuffers(dest, data);
            }
            if (hasBlock2) {
              data.adjLight = !block2.isComplex;
              if (data.adjLight && !adjLight) {
                getFarCorners(data,x,y,z);
              }
              block2.buildBuffers(dest, data);
            }
          }
        }
      }
      isAllEmpty = dest.allEmpty();
    }
  }

  private void getFarCorners(RenderData data,int x,int y,int z) {
    int _ll;

    //A sides
    _ll = getLights(x,y+1,z-1);
    data.sl[Direction.AN] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.AN] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y+1,z);
    data.sl[Direction.AE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.AE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x,y+1,z+1);
    data.sl[Direction.AS] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.AS] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x-1,y+1,z);
    data.sl[Direction.AW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.AW] = ((_ll & 0xf0) >> 4) / 15.0f;

    //A corners
    _ll = getLights(x-1,y+1,z-1);
    data.sl[Direction.ANW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.ANW] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y+1,z-1);
    data.sl[Direction.ANE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.ANE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y+1,z+1);
    data.sl[Direction.ASE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.ASE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x-1,y+1,z+1);
    data.sl[Direction.ASW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.ASW] = ((_ll & 0xf0) >> 4) / 15.0f;

    //B sides
    _ll = getLights(x,y-1,z-1);
    data.sl[Direction.BN] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BN] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y-1,z);
    data.sl[Direction.BE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x,y-1,z+1);
    data.sl[Direction.BS] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BS] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x-1,y-1,z);
    data.sl[Direction.BW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BW] = ((_ll & 0xf0) >> 4) / 15.0f;

    //B corners
    _ll = getLights(x-1,y-1,z-1);
    data.sl[Direction.BNW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BNW] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y-1,z-1);
    data.sl[Direction.BNE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BNE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x+1,y-1,z+1);
    data.sl[Direction.BSE] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BSE] = ((_ll & 0xf0) >> 4) / 15.0f;

    _ll = getLights(x-1,y-1,z+1);
    data.sl[Direction.BSW] = (_ll & 0x0f) / 15.0f;
    data.bl[Direction.BSW] = ((_ll & 0xf0) >> 4) / 15.0f;
  }

  public boolean canRender() {
    return adjCount == 8;
  }

  public boolean canLights() {
    return adjCount == 8;
  }

  public boolean isBorder() {
    return adjCount != 8;
  }

  //server-side only : load all adjacent chunks within a range
  public void getAdjChunks(boolean phase2, boolean phase3, boolean lights, int range) {
    Chunks chunks = Static.server.world.chunks;
//    Static.log("getAdjChunks:"+dim+","+cx+","+cy+","+cz);
    for(int x=-range;x<=range;x++) {
      for(int z=-range;z<=range;z++) {
        if (x == 0 && z == 0) continue;
        chunks.getChunk2(dim, cx + x, cz + z, phase2, phase3, lights);
      }
    }
  }

  public void resetLights() {
    synchronized(lock) {
      for(int a=0;a<256;a++) {
        lights[a] = null;
      }
    }
  }

  public ExtraBase getExtra(int x,int y,int z,int id) {
    synchronized(lock) {
      int size = extras.size();
      for(int a=0;a<size;a++) {
        ExtraBase extra = extras.get(a);
        if (extra.x == x && extra.y == y && extra.z == z && extra.id == id) {
          return extra;
        }
      }
    }
    return null;
  }

  public void addExtra(ExtraBase extra) {
    if (extra.id == Extras.CRACK) {
      addCrack((ExtraCrack)extra);
      return;
    }
    synchronized(lock) {
      ExtraBase cur = getExtra(extra.x, extra.y, extra.z, extra.id);
      if (cur != null) {
        cur.update(extra);
      } else {
        extras.add(extra);
      }
    }
    dirty = true;
  }

  public void delExtra(int x,int y,int z, int type) {
    if (type == Extras.CRACK) {
      delCrack(x,y,z);
      return;
    }
    synchronized(lock) {
      int size = extras.size();
      for(int a=0;a<size;a++) {
        ExtraBase extra = extras.get(a);
        if (extra.x == x && extra.y == y && extra.z == z && extra.id == type) {
          extras.remove(a);
          dirty = true;
          return;
        }
      }
    }
  }
  public void delExtra(Coords c, int type) {
    synchronized(lock) {
      int size = extras.size();
      for(int a=0;a<size;a++) {
        ExtraBase extra = extras.get(a);
        if (extra.x == c.gx && extra.y == c.gy && extra.z == c.gz && extra.id == type) {
          extras.remove(a);
          return;
        }
      }
    }
  }

  private boolean hasTick(int x,int y,int z, boolean isBlocks2) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == isBlocks2) {
//          Static.log("hasTick:" + tick.x + "," + tick.y + "," + tick.z);
          return true;
        }
      }
    }
    return false;
  }
  public void addTick(Coords c, boolean isBlocks2) {
    synchronized(lock) {
      if (hasTick(c.gx, c.gy, c.gz, isBlocks2)) return;
      Tick tick = new Tick();
      tick.x = (short)c.gx;
      tick.y = (short)c.gy;
      tick.z = (short)c.gz;
      tick.isBlocks2 = isBlocks2;
//    Static.log("addTick:" + tick.x + "," + tick.y + "," + tick.z);
      ticks.add(tick);
    }
    dirty = true;
  }
  public void addTick(int x,int y,int z, boolean isBlocks2) {
    synchronized(lock) {
      if (hasTick(x, y, z, isBlocks2)) return;
      Tick tick = new Tick();
      tick.x = (short)x;
      tick.y = (short)y;
      tick.z = (short)z;
      tick.isBlocks2 = isBlocks2;
//    Static.log("addTick:" + tick.x + "," + tick.y + "," + tick.z);
      ticks.add(tick);
    }
    dirty = true;
  }
  public void delTick(int x,int y,int z, boolean isBlocks2) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == isBlocks2) {
//          Static.log("delTick:" + tick.x + "," + tick.y + "," + tick.z);
          ticks.remove(a);
          return;
        }
      }
    }
    dirty = true;
  }
  public void delTick(Tick tick) {
//    Static.log("delTick:" + tick.x + "," + tick.y + "," + tick.z);
    synchronized(lock) {
      ticks.remove(tick);
    }
    dirty = true;
  }
  public Tick getTick(int x,int y,int z, boolean isBlocks2) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == isBlocks2) {
          return tick;
        }
      }
    }
    return null;
  }
  public Tick[] getTicks() {
    synchronized(lock) {
      return ticks.toArray(new Tick[ticks.size()]);
    }
  }

  private static Random r = new Random();

  public void doTicks() {
    char id;
    BlockBase block;
    Tick list[] = getTicks();
    for(int a=0;a<list.length;a++) {
      Tick tick = list[a];
      if (!tick.isBlocks2)
        id = getID(tick.x,tick.y,tick.z);
      else
        id = getID2(tick.x,tick.y,tick.z);
      if (id != 0) {
        try {
          block = Static.blocks.blocks[id];
          block.tick(this, tick);
        } catch (Exception e) {
          Static.log(e);
        }
      }
    }
    //do 48 random ticks (3 per 16x16x16 area)
    if (!Static.debugDisableRandomTicks) {
      int x,y,z;
      int p = 0;
      for(int a=0;a<48;a++) {
        if (a > 0 && a % 3 == 0) p += 16;
        x = r.nextInt(16);
        y = r.nextInt(16) + p;
        z = r.nextInt(16);
        //TODO : can snow_cover be replaced here???
        id = getID(x,y,z);
        if (id != 0) {
          block = Static.blocks.blocks[id];
          block.rtick(this, x,y,z);
        }
        id = getID2(x,y,z);
        if (id != 0) {
          block = Static.blocks.blocks[id];
          block.rtick(this, x,y,z);
        }
      }
    }
    //do entity ticks
    EntityBase es[] = this.getEntities();
    for(int a=0;a<es.length;a++) {
      EntityBase e = es[a];
      if (e.offline) continue;
      e.tick();
    }
  }
  public void addEntity(EntityBase e) {
    synchronized(lock) {
      entities.add(e);
    }
    dirty = true;
  }
  public void delEntity(EntityBase e) {
    synchronized(lock) {
      entities.remove(e);
    }
    dirty = true;
  }
  /** Retrieves entity using globaly unique uid. */
  public EntityBase getEntity(int uid) {
    synchronized(lock) {
      int cnt = entities.size();
      for(int a=0;a<cnt;a++) {
        EntityBase e = entities.get(a);
        if (e.uid == uid) return e;
      }
    }
    return null;
  }
  /** Retrieves entity using temp cid. */
  public EntityBase getEntity2(int cid) {
    synchronized(lock) {
      int cnt = entities.size();
      for(int a=0;a<cnt;a++) {
        EntityBase e = entities.get(a);
        if (e.cid == cid) return e;
      }
    }
    return null;
  }
  public EntityBase[] getEntities() {
    synchronized(lock) {
      return entities.toArray(new EntityBase[0]);
    }
  }
  public EntityBase findBlockEntity(int id, Coords c) {
    synchronized(lock) {
      int cnt = entities.size();
      for(int a=0;a<cnt;a++) {
        EntityBase e = entities.get(a);
        if (!e.isBlock) continue;
        BlockEntity be = (BlockEntity)e;
        if (e.id == id && be.gx == c.gx && be.gy == c.gy && be.gz == c.gz) {
          return e;
        }
      }
    }
    return null;
  }
  private static Coords tmp = new Coords();
  /** Builds shapes (canRender() must return true)
    Server side only.
    Use after gen phase 2.
   */
  public void buildShapes() {
    int p = 0;
    char id;
    BlockBase block;
    for(int y=0;y<16;y++) {
      if (blocks[y] == null) continue;
      p=0;
      for(int z=0;z<16;z++) {
        for(int x=0;x<16;x++) {
          id = blocks[y][p];
          block = Static.blocks.blocks[id];
          if (!block.hasShape) continue;
          synchronized(tmp) {
            block.setShape(this, x,y,z, false, tmp);
          }
          p++;
        }
      }
    }
  }

  public static int makeBits(int dir, int var) {
    return (dir << 4 | var);
  }
  public static int getDir(int bits) {
    return (bits & 0x00f0) >> 4;
  }
  public static int getVar(int bits) {
    return bits & 0x000f;
  }
  public static int replaceDir(int bits, int dir) {
    return (bits & 0x0f) | (dir << 4);
  }
  public static int replaceVar(int bits, int var) {
    return (bits & 0xf0) | var;
  }
  public void addCrack(ExtraCrack crack) {
    synchronized(lock) {
      ExtraCrack cur = this.getCrack(crack.x, crack.y, crack.z);
      if (cur != null) {
        cur.update(crack);
      } else {
        cracks.add(crack);
      }
      if (world.isClient) {
        Static.client.chunkBuilder.add(this);
      }
      dirty = true;
    }
  }
  public void delCrack(int x,int y,int z) {
    synchronized(lock) {
      int cnt = cracks.size();
      for(int a=0;a<cnt;a++) {
        ExtraCrack c = cracks.get(a);
        if (c.x == x && c.y == y && c.z == z) {
          cracks.remove(a);
          if (world.isClient) {
            Static.client.chunkBuilder.add(this);
          }
          dirty = true;
          return;
        }
      }
    }
  }
  public ExtraCrack getCrack(int x,int y,int z) {
//    synchronized(lock) {
      int cnt = cracks.size();
      for(int a=0;a<cnt;a++) {
        ExtraCrack c = cracks.get(a);
        if (c.x == x && c.y == y && c.z == z) {
          return c;
        }
      }
//    }
    return null;
  }

  /** Get block for layer 1 or layer 2. */
  public BlockBase getBlock(int x,int y,int z) {
    char id = getID(x,y,z);
    if (id == 0) {
      id = getID2(x,y,z);
    }
    return Static.blocks.blocks[id];
  }

  public BlockBase getBlock1(int x,int y,int z) {
    char id = getID(x,y,z);
    return Static.blocks.blocks[id];
  }

  public BlockBase getBlock2(int x,int y,int z) {
    char id = getID2(x,y,z);
    return Static.blocks.blocks[id];
  }

  public boolean isEmpty(int x,int y,int z) {
    return (getID(x,y,z) == 0 && getID2(x,y,z) == 0);
  }

  public boolean canSpawnOn(int x,int y,int z) {
    if (getID2(x,y,z) != 0) return false;
    BlockBase base = getBlock(x, y, z);
    return base.canSpawnOn && !base.isComplex;
  }

  public byte[][] getLights() {
    return lights;
  }

  public void setLights(byte newLights[][]) {
    lights = newLights;
  }

  /** Removes planes that are empty. */
  public void reduce() {
    synchronized(lock) {
      for(int y=0;y<256;y++) {
        if (blocks[y] != null) {
          for(int p=0;p<256;p++) {
            if (blocks[y][p] != 0) break;
            if (p == 255) {
              blocks[y] = null;
              bits[y] = null;
            }
          }
        }
        if (blocks2[y] != null) {
          for(int p=0;p<256;p++) {
            if (blocks2[y][p] != 0) break;
            if (p == 255) {
              blocks2[y] = null;
              bits2[y] = null;
            }
          }
        }
      }
    }
  }

  private void removeExtra(int x1, int y1, int z1,  int x2, int y2, int z2) {
    int cnt = extras.size();
    for(int a=0;a<cnt;) {
      ExtraBase e = extras.get(a);
      if (e.x >= x1 && e.x <= x2) {
        if (e.y >= y1 && e.y <= y2) {
          if (e.z >= z1 && e.z <= z2) {
            extras.remove(a);
            cnt--;
            continue;
          }
        }
      }
      a++;
    }
  }

  private void removeEntities(int x1, int y1, int z1,  int x2, int y2, int z2) {
    synchronized(lock) {
      int cnt = entities.size();
      for(int a=0;a<cnt;) {
        EntityBase e = entities.get(a);
        if (e.id != Entities.PLAYER) {
          if (e.pos.x >= x1 && e.pos.x <= x2) {
            if (e.pos.y >= y1 && e.pos.y <= y2) {
              if (e.pos.z >= z1 && e.pos.z <= z2) {
                entities.remove(a);
                cnt--;
                continue;
              }
            }
          }
        }
        a++;
      }
    }
  }

  public void fill(int x1, int y1, int z1,  int dx, int dy, int dz, char id) {
    int x2 = x1 + dx - 1;
    int y2 = y1 + dy - 1;
    int z2 = z1 + dz - 1;
    synchronized(lock) {
      for(int y=y1;y<=y2;y++) {
        if (blocks[y] == null) {
          blocks[y] = new char[256];
        }
        if (bits[y] == null) {
          bits[y] = new byte[256];
        }
        for(int z=z1;z<=z2;z++) {
          int p = z * 16 + x1;
          for(int x=x1;x<=x2;x++) {
            blocks[y][p] = id;
            bits[y][p] = 0;
            if (blocks2[y] != null) {
              blocks2[y][p] = 0;
            }
            if (bits2[y] != null) {
              bits2[y][p] = 0;
            }
            p++;
          }
        }
      }
      removeExtra(x1, y1, z1, x2, y2, z2);
      removeEntities(x1, y1, z1, x2, y2, z2);
    }
    reduce();
  }

  public void fill2(int x1, int y1, int z1,  int dx, int dy, int dz, char id) {
    int x2 = x1 + dx - 1;
    int y2 = y1 + dy - 1;
    int z2 = z1 + dz - 1;
    synchronized(lock) {
      for(int y=y1;y<=y2;y++) {
        if (blocks2[y] == null) {
          blocks2[y] = new char[256];
        }
        if (bits2[y] == null) {
          bits2[y] = new byte[256];
        }
        for(int z=z1;z<=z2;z++) {
          int p = z * 16 + x1;
          for(int x=x1;x<=x2;x++) {
            blocks2[y][p] = id;
            bits2[y][p] = 0;
            if (blocks[y] != null) {
              blocks[y][p] = 0;
            }
            if (bits[y] != null) {
              bits[y][p] = 0;
            }
            p++;
          }
        }
      }
      removeExtra(x1, y1, z1, x2, y2, z2);
      removeEntities(x1, y1, z1, x2, y2, z2);
    }
    reduce();
  }

  public void B2E(int gx, int gy, int gz, float x, float y, float z, int uid) {
    char id = getID(gx, gy, gz);
    int bits = getBits(gx, gy, gz);
    if (id == 0) {
      Static.log("C:B2E=0:" + cx +"," + cz + ":" + gx + "," + gy + ","+ gz);
      return;
    }
    clearBlock(gx, gy, gz);
    MovingBlock mb = new MovingBlock();
    mb.init(Static.client.world);
    mb.dim = dim;
    mb.uid = uid;
    mb.pos.x = x;
    mb.pos.y = y;
    mb.pos.z = z;
    mb.blockid = id;
    mb.dir = Chunk.getDir(bits);
    mb.blockvar = Chunk.getVar(bits);
    world.addEntity(mb);
    addEntity(mb);
  }

  public byte[] encodeObject(SerialCoder coder) {
    synchronized(lock) {
      dirty = false;
      return coder.encodeObject(this, true);
    }
  }

  public String toString() {
    return "Chunk:" + cx + "," + cz;
  }

  private static final byte ver = 1;

  private static final int magic = 0x12345678;

  public boolean write(SerialBuffer buffer, boolean file) {
    synchronized(lock) {
      buffer.writeByte(ver);
      buffer.writeInt(dim);
      buffer.writeInt(cx);
      buffer.writeInt(cz);

      if (file) {
        buffer.writeLong(seed);
        int bits = 0;
//        if (readOnly) bits |= 0x1;
        if (needPhase2) bits |= 0x2;
        if (needPhase3) bits |= 0x4;
        if (needLights) bits |= 0x8;
        buffer.writeInt(bits);
        buffer.writeInt(readOnly1);
        buffer.writeInt(readOnly2);
      }


      //blocks
      int cnt1 = 0, cnt2 = 0;
      for(int a=0;a<256;a++) {
        if (blocks[a] == null) continue;
        cnt1++;
      }
      buffer.writeShort((short)cnt1);
      for(int a=0;a<256;a++) {
        if (blocks[a] == null) continue;
        cnt2++;
        buffer.writeByte((byte)a);
        buffer.writeChars(blocks[a]);
      }
      if (cnt1 != cnt2) {
        Static.log("Chunk.write() failed : data changed : need locks");
      }
      //bits
      int cnt3 = 0, cnt4 = 0;
      for(int a=0;a<256;a++) {
        if (bits[a] == null) continue;
        cnt3++;
      }
      buffer.writeShort((short)cnt3);
      for(int a=0;a<256;a++) {
        if (bits[a] == null) continue;
        cnt4++;
        buffer.writeByte((byte)a);
        buffer.writeBytes(bits[a]);
      }
      if (cnt3 != cnt4) {
        Static.log("Chunk.write() failed : data changed : need locks");
      }
      //blocks2
      int cnt5 = 0, cnt6 = 0;
      for(int a=0;a<256;a++) {
        if (blocks2[a] == null) continue;
        cnt5++;
      }
      buffer.writeShort((short)cnt5);
      for(int a=0;a<256;a++) {
        if (blocks2[a] == null) continue;
        cnt6++;
        buffer.writeByte((byte)a);
        buffer.writeChars(blocks2[a]);
      }
      if (cnt5 != cnt6) {
        Static.log("Chunk.write() failed : data changed : need locks");
      }
      //bits2
      int cnt7 = 0, cnt8 = 0;
      for(int a=0;a<256;a++) {
        if (bits2[a] == null) continue;
        cnt7++;
      }
      buffer.writeShort((short)cnt7);
      for(int a=0;a<256;a++) {
        if (bits2[a] == null) continue;
        cnt8++;
        buffer.writeByte((byte)a);
        buffer.writeBytes(bits2[a]);
      }
      if (cnt7 != cnt8) {
        Static.log("Chunk.write() failed : data changed : need locks");
      }
      //lights
      int cnt9 = 0, cnt10 = 0;
      for(int a=0;a<256;a++) {
        if (lights[a] == null) continue;
        cnt9++;
      }
      buffer.writeShort((short)cnt9);
      for(int a=0;a<256;a++) {
        if (lights[a] == null) continue;
        cnt10++;
        buffer.writeByte((byte)a);
        buffer.writeBytes(lights[a]);
      }
      if (cnt9 != cnt10) {
        Static.log("Chunk.write() failed : data changed : need locks");
      }

      //biome data
      buffer.writeBytes(biome);
      buffer.writeFloats(temp);
      buffer.writeFloats(rain);
      buffer.writeFloats(elev);
      buffer.writeFloats(depth);

      //entities
      int entity_size = entities.size();
      buffer.writeInt(entity_size);
      int cid = 1;
      for(int a=0;a<entity_size;a++) {
        entities.get(a).cid = cid++;
      }
      for(int a=0;a<entity_size;a++) {
        EntityBase entity = entities.get(a);
        entity.write(buffer, file);
      }
      int tick_size = 0;
      if (file) {
        //ticks (client does not need to know)
        tick_size = ticks.size();
        buffer.writeInt(tick_size);
        for(int a=0;a<tick_size;a++) {
          ticks.get(a).write(buffer, file);
        }
      }
      //extra data
      int extra_size = extras.size();
      buffer.writeInt(extra_size);
      for(int a=0;a<extra_size;a++) {
        extras.get(a).write(buffer, file);
      }

      //future stuff here

      buffer.writeInt(magic);
//      Static.log("chunk.write():" + cx + "," + cz + ":" + entity_size +","+ tick_size + "," + extra_size + ",pos=" + buffer.pos + ",file=" + file);
//      Static.log("write.cnts:" + cnt1 + "," + cnt3 + "," + cnt5 + "," + cnt7 + "," + cnt9);
    }
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    dim = buffer.readInt();
    cx = buffer.readInt();
    cz = buffer.readInt();

    if (file) {
      seed = buffer.readLong();
      int bits = buffer.readInt();
//      readOnly = ((bits & 0x01) != 0);
      needPhase2 = ((bits & 0x02) != 0);
      needPhase3 = ((bits & 0x04) != 0);
      needLights = ((bits & 0x08) != 0);
      if (ver > 0) {
        readOnly1 = buffer.readInt();
        readOnly2 = buffer.readInt();
      }
    }

    //blocks
    int idx;
    int cnt1 = buffer.readShort();
    for(int a=0;a<cnt1;a++) {
      idx = buffer.readByte() & 0xff;
      blocks[idx] = new char[16*16];
      buffer.readChars(blocks[idx]);
    }
    //bits
    int cnt3 = buffer.readShort();
    for(int a=0;a<cnt3;a++) {
      idx = buffer.readByte() & 0xff;
      bits[idx] = new byte[16*16];
      buffer.readBytes(bits[idx]);
    }
    //blocks2
    int cnt5 = buffer.readShort();
    for(int a=0;a<cnt5;a++) {
      idx = buffer.readByte() & 0xff;
      blocks2[idx] = new char[16*16];
      buffer.readChars(blocks2[idx]);
    }
    //bits2
    int cnt7 = buffer.readShort();
    for(int a=0;a<cnt7;a++) {
      idx = buffer.readByte() & 0xff;
      bits2[idx] = new byte[16*16];
      buffer.readBytes(bits2[idx]);
    }
    //lights
    int cnt9 = buffer.readShort();
    for(int a=0;a<cnt9;a++) {
      idx = buffer.readByte() & 0xff;
      lights[idx] = new byte[16*16];
      buffer.readBytes(lights[idx]);
    }

    //biome data
    buffer.readBytes(biome);
    buffer.readFloats(temp);
    buffer.readFloats(rain);
    buffer.readFloats(elev);
    buffer.readFloats(depth);

    //entities
    int entity_size;
    entity_size = buffer.readInt();
    for(int a=0;a<entity_size;a++) {
      EntityBase eb = (EntityBase)Static.entities.create(buffer);
      if (eb != null) {
        eb.read(buffer, file);
        entities.add(eb);
      }
    }
    entity_size = entities.size();
    for(int a=0;a<entity_size;a++) {
      EntityBase eb = entities.get(a);
      eb.setupLinks(this, file);
    }
    int tick_size = 0;
    if (file) {
      //ticks (client does not need to know)
      tick_size = buffer.readInt();
      for(int a=0;a<tick_size;a++) {
        Tick tick = new Tick();
        tick.read(buffer, file);
        ticks.add(tick);
      }
    }
    //extra data
    int extra_size = buffer.readInt();
    for(int a=0;a<extra_size;a++) {
      ExtraBase eb = (ExtraBase)Static.extras.create(buffer);
      if (eb != null) {
        eb.read(buffer, file);
        extras.add(eb);
      }
    }

    if (ver > 0) {
      //future stuff
    }

    int test = buffer.readInt();
//    Static.log("chunk.read():" + cx + "," + cz + ":" + entity_size +","+ tick_size + "," + extra_size + ",pos=" + buffer.pos + ",len=" + buffer.length + ",file=" + file);
//    Static.log("read.cnts:" + cnt1 + "," + cnt3 + "," + cnt5 + "," + cnt7 + "," + cnt9);
    if (test != magic) {
      Static.logTrace("Chunk corrupt !!!");
      return false;
    }
    return true;
  }

  public SerialClass create(SerialBuffer buffer) {
    return new Chunk(Static.server.world);
  }
}
