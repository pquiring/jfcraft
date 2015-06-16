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

public class Chunk extends ClientServer implements SerialClass, SerialCreator {
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
  public boolean readOnly;
  public boolean needPhase2, needPhase3;
  public boolean needLights;  //generator phases
  //flags
  public boolean dirty;  //need to write to disk
  public boolean needBuildBuffers, needCopyBuffers, needRelight;
  public boolean ready;
  public boolean inRange, isAllEmpty;

  //biome data
  public byte biome[] = new byte[16 * 16];
  public float temp[] = new float[16 * 16];
  public float rain[] = new float[16 * 16];
  public float elev[] = new float[16 * 16];  //elevation
  public float depth[] = new float[16 * 16];  //used in end world

  public ArrayList<EntityBase> entities = new ArrayList<EntityBase>();
  public ArrayList<Tick> ticks = new ArrayList<Tick>();  //server-side only
  public ArrayList<ExtraBase> extras = new ArrayList<ExtraBase>();

  //end of serializable data

  public static final int BLOCK_LIGHT_MASK = 0xf0;
  public static final int SUN_LIGHT_MASK = 0x0f;

  //size of each chunk
  public static final int X = 16;
  public static final int Y = 256;
  public static final int Z = 16;

  //biome types
  public static final byte TUNDRA = 0;  //snow plains
  public static final byte TAIGA = 1;   //snow forest
  public static final byte PLAINS = 2;  //dry plains (few trees)
  public static final byte DESERT = 3;  //dry sand
  public static final byte FOREST = 4;  //lots o trees
  public static final byte SWAMP = 5;   //swamp/marsh area
  public static final byte JUNGLE = 6;  //thick trees/plants
  public static final byte OCEAN = 7;   //the sea/rivers
  public static final byte NETHER = 8;  //nether
  public static final byte END = 9;     //end
  public static final byte MOUNTAIN = 64;  //mountains (modifier)

  public static String getBiomeName(byte type) {
    if ((type & MOUNTAIN) != 0) {
      return "MOUNTAIN";
    }
    switch (type) {
      case TUNDRA: return "TUNDRA";
      case TAIGA: return "TAIGA";
      case PLAINS: return "PLAINS";
      case DESERT: return "DESERT";
      case FOREST: return "FOREST";
      case SWAMP: return "SWAMP";
      case JUNGLE: return "JUNGLE";
      case OCEAN: return "OCEAN";
    }
    return null;
  }

  public Chunk N,E,S,W;  //links : north, east, south, west
  public Object lock = new Object();
  public RenderDest dest;
  public GLMatrix mat;
  public int adjCount;  //# of adj chunks to render (0-6)
  public ArrayList<ExtraCrack> cracks = new ArrayList<ExtraCrack>();

  //render dest buffers
  public static final int DEST_NORMAL = 0;  //stitched block
  public static final int DEST_ALPHA = 1;  //stitched block (ALPHA)
  public static final int DEST_TEXT = 2;  //ASCII text
  public static final int buffersCount = 3; //DEST_NORMAL + DEST_ALPHA + DEST_TEXT

  /** Old Chunk read from file/network. */
  public Chunk() {
    super(Static.isClient());
  }

  /** New Chunk created on server side only */
  public Chunk(int dim, int cx, int cz) {
    super(false);
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
    dest = new RenderDest(buffersCount);
    mat = new GLMatrix();
    mat.setIdentity();
    mat.setTranslate(cx * 16.0f, 0, cz * 16.0f);
    needBuildBuffers = true;
  }

  public void copyBuffers(GL gl) {
//    System.out.println("copyBuffers:" + cx + "," + cz);
    for(int a=0;a<buffersCount;a++) {
      if (!dest.exists(a)) continue;
      dest.getBuffers(a).copyBuffers(gl);
    }
    needCopyBuffers = false;
    ready = true;
  }

  public void render(RenderBuffers obj, GL gl) {
    obj.bindBuffers(gl);
    obj.render(gl);
  }

  /** Determines if lighting if different around a block. */
  private boolean doesLightingDiffer(int x,int y,int z) {
    int ll = -1;
    BlockBase base1, base2;
    if (y < 255) {
      base1 = Static.blocks.blocks[getID(x,y+1,z)];
      base2 = Static.blocks.blocks[getID2(x,y+1,z)];
      if (!base1.isOpaque && !base2.isOpaque) {
        int la = getLights(x, y+1, z);
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
        int lb = getLights(x, y-1, z);
        if (ll != -1 && ll != lb) {
          return true;
        }
        ll = lb;
      }
    }
    base1 = Static.blocks.blocks[getID(x,y,z-1)];
    base2 = Static.blocks.blocks[getID2(x,y,z-1)];
    if (!base1.isOpaque && !base2.isOpaque) {
      int ln = getLights(x, y, z-1);
      if (ll != -1 && ll != ln) {
        return true;
      }
      ll = ln;
    }
    base1 = Static.blocks.blocks[getID(x+1,y,z)];
    base2 = Static.blocks.blocks[getID2(x+1,y,z)];
    if (!base1.isOpaque && !base2.isOpaque) {
      int le = getLights(x+1, y, z);
      if (ll != -1 && ll != le) {
        return true;
      }
      ll = le;
    }
    base1 = Static.blocks.blocks[getID(x,y,z+1)];
    base2 = Static.blocks.blocks[getID2(x,y,z+1)];
    if (!base1.isOpaque && !base2.isOpaque) {
      int ls = getLights(x, y, z+1);
      if (ll != -1 && ll != ls) {
        return true;
      }
      ll = ls;
    }
    base1 = Static.blocks.blocks[getID(x-1,y,z)];
    base2 = Static.blocks.blocks[getID2(x-1,y,z)];
    if (!base1.isOpaque && !base2.isOpaque) {
      int lw = getLights(x-1, y, z);
      if (ll != -1 && ll != lw) {
        return true;
      }
      ll = lw;
    }
    return false;
  }

  /** Determines what area of the lighting will be effected. */
  private int[] getLightCoordsSet(int x,int y,int z, BlockBase newBlock, BlockBase oldBlock) {
    //check how lighting will change
    int xyz[] = new int[6];
    if (doesLightingDiffer(x,y,z)) {
      xyz[0] = x-14;
      xyz[1] = 0;
      xyz[2] = z-14;
      xyz[3] = x+14;
      xyz[4] = y+14;
      xyz[5] = z+14;
    } else {
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
    }
    return xyz;
  }

  /** Determines what area of the lighting will be effected. */
  private int[] getLightCoordsClear(int x,int y,int z, BlockBase oldBlock) {
    //check how lighting will change
    int xyz[] = new int[6];
    if (doesLightingDiffer(x,y,z)) {
      xyz[0] = x-14;
      xyz[1] = 0;
      xyz[2] = z-14;
      xyz[3] = x+14;
      xyz[4] = y+14;
      xyz[5] = z+14;
    } else {
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
      needRelight = true;
      dirty = true;
      //TODO : calc precise coords
      if (!needLights) {
        int xyz[] = getLightCoordsSet(x,y,z, newBlock, Static.blocks.blocks[oldid]);
        if (isClient) {
          Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        } else {
          Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        }
      }
    }
  }

  public void clearBlock(int x,int y,int z) {
    if (x > 15) {E.clearBlock(x-16, y, z); return;}
    if (x < 0) {W.clearBlock(x+16, y, z); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.clearBlock(x, y, z-16); return;}
    if (z < 0) {N.clearBlock(x, y, z+16); return;}
    int p = z * 16 + x;
    char b[] = blocks[y];
    if (b == null) return;
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
      needRelight = true;
      dirty = true;
      if (!needLights) {
        int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
        if (isClient) {
          Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        } else {
          Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        }
      }
    }
  }

  public void clearBlock2(int x,int y,int z) {
    if (x > 15) {E.clearBlock2(x-16, y, z); return;}
    if (x < 0) {W.clearBlock2(x+16, y, z); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.clearBlock2(x, y, z-16); return;}
    if (z < 0) {N.clearBlock2(x, y, z+16); return;}
    int p = z * 16 + x;
    char b[] = blocks2[y];
    if (b == null) return;
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

      needRelight = true;
      dirty = true;
      if (!needLights) {
        int xyz[] = getLightCoordsClear(x,y,z, Static.blocks.blocks[oldid]);
        if (isClient) {
          Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        } else {
          Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        }
      }
    }
  }

  public void setBlockIfEmpty(int x,int y,int z,char id, int _bits) {
    if (x > 15) {E.setBlockIfEmpty(x-16, y, z, id, _bits); return;}
    if (x < 0) {W.setBlockIfEmpty(x+16, y, z, id, _bits); return;}
    if (y > 255) return;
    if (y < 0) return;
    if (z > 15) {S.setBlockIfEmpty(x, y, z-16, id, _bits); return;}
    if (z < 0) {N.setBlockIfEmpty(x, y, z+16, id, _bits); return;}
    int p = z * 16 + x;
    BlockBase newBlock = Static.blocks.blocks[id];
    char b[];
    char oldid;
    synchronized(lock) {
      if (newBlock.isBlocks2) {
        if (blocks2[y] == null) {
          blocks2[y] = new char[16*16];
          bits2[y] = new byte[16*16];
        } else {
          if (blocks2[y][p] != 0) return;
        }
        oldid = blocks2[y][p];
        blocks2[y][p] = id;
        bits2[y][p] = (byte)_bits;
      } else {
        if (blocks[y] == null) {
          blocks[y] = new char[16*16];
          bits[y] = new byte[16*16];
        } else {
          if (blocks[y][p] != 0) return;
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
      needRelight = true;
      dirty = true;
      if (!needLights) {
        int xyz[] = getLightCoordsSet(x,y,z, newBlock, Static.blocks.blocks[oldid]);
        if (isClient) {
          Static.client.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        } else {
          Static.server.chunkLighter.add(this, xyz[0], xyz[1], xyz[2], xyz[3], xyz[4], xyz[5]);
        }
      }
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
    synchronized(lock) {
      byte plane[] = lights[y];
      if (plane == null) {
        if (v == 0) return;
        plane = new byte[16*16];
        lights[y] = plane;
      }
      plane[z * 16 + x] = (byte)v;
    }
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
      needBuildBuffers = false;
      char id, xid;
      int _bits;
      byte _ll;
      int crack;
      BlockBase block = null, adjBlock, xblock = null;
      RenderData data = new RenderData();
      data.chunk = this;
      //reset all objects
      for(int a=0;a<buffersCount;a++) {
        if (!dest.exists(a)) continue;
        dest.getBuffers(a).reset();
      }
      for(int y=0;y<256;y++) {  //+ - 256
        data.y = y;
        for(int z=0;z<16;z++) {  //+ - 16
          data.z = z;
          for(int x=0;x<16;x++) {  //+ - 1
            data.x = x;
//            data.temp = biome.temp[z * 16 + x];
//            data.rain = biome.rain[z * 16 + x];
            ExtraCrack c = getCrack(x,y,z);
            if (c != null) {
              crack = (int)(c.dmg / 10.0f);
              if (crack > 9) crack = 9;
            } else {
              crack = -1;
            }
            data.crack = crack;
            id = getID(x,y,z);
            xid = getID2(x,y,z);
            boolean hasBlock = id != 0;
            boolean hasExtra = xid != 0;
            if (!hasBlock && !hasExtra) {
              continue;
            }
            if (hasBlock) {
              _bits = getBits(x,y,z);
              data.dir[Direction.X] = getDir(_bits);
              data.var[Direction.X] = getVar(_bits);
              block = Static.blocks.blocks[id];
              data.opaque[Direction.X] = block.isOpaque;
            }
            if (hasExtra) {
              _bits = getBits2(x,y,z);
              data.dir2[Direction.X] = getDir(_bits);
              data.var2[Direction.X] = getVar(_bits);
              data.id2[Direction.X] = xid;
              xblock = Static.blocks.blocks[xid];
            }
            _ll = getLights(x,y,z);
            data.sl[Direction.X] = (_ll & 0x0f) / 15.0f;
            data.bl[Direction.X] = ((_ll & 0xf0) >> 4) / 15.0f;

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
              if (hasExtra) {
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
              if (hasExtra) {
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
            if (hasExtra) {
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
            if (hasExtra) {
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
            if (hasExtra) {
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
            if (hasExtra) {
              data.id2[Direction.E] = getID2(x+1,y,z);
              _bits = getBits2(x+1,y,z);
              data.dir2[Direction.E] = getDir(_bits);
              data.var2[Direction.E] = getVar(_bits);
            }

            if (hasBlock) {
              data.adjLight = !block.isComplex;
              block.buildBuffers(dest, data);
            }
            if (hasExtra) {
              data.adjLight = !xblock.isComplex;
              xblock.buildBuffers(dest, data);
            }
          }
        }
      }
      isAllEmpty = dest.allEmpty();
      needCopyBuffers = true;
    }
  }

  public boolean canRender() {
    return adjCount == 8;
  }

  public boolean canLights() {
    return adjCount == 8;
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

  private boolean hasTick(int x,int y,int z, boolean extra) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == extra) {
//          Static.log("hasTick:" + tick.x + "," + tick.y + "," + tick.z);
          return true;
        }
      }
    }
    return false;
  }
  public void addTick(Coords c, boolean extra) {
    synchronized(lock) {
      if (hasTick(c.gx, c.gy, c.gz, extra)) return;
      Tick tick = new Tick();
      tick.x = (short)c.gx;
      tick.y = (short)c.gy;
      tick.z = (short)c.gz;
      tick.isBlocks2 = extra;
//    Static.log("addTick:" + tick.x + "," + tick.y + "," + tick.z);
      ticks.add(tick);
    }
    dirty = true;
  }
  public void addTick(int x,int y,int z, boolean extra) {
    synchronized(lock) {
      if (hasTick(x, y, z, extra)) return;
      Tick tick = new Tick();
      tick.x = (short)x;
      tick.y = (short)y;
      tick.z = (short)z;
      tick.isBlocks2 = extra;
//    Static.log("addTick:" + tick.x + "," + tick.y + "," + tick.z);
      ticks.add(tick);
    }
    dirty = true;
  }
  public void delTick(int x,int y,int z, boolean extra) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == extra) {
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
  public void delTick(Coords c) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == c.gx && tick.y == c.gy && tick.z == c.gz) {
//          Static.log("delTick:" + tick.x + "," + tick.y + "," + tick.z);
          ticks.remove(a);
          return;
        }
      }
    }
    dirty = true;
  }
  public Tick getTick(int x,int y,int z, boolean extra) {
    synchronized(lock) {
      int size = ticks.size();
      for(int a=0;a<size;a++) {
        Tick tick = ticks.get(a);
        if (tick.x == x && tick.y == y && tick.z == z && tick.isBlocks2 == extra) {
          return tick;
        }
      }
    }
    return null;
  }

  private static Random r = new Random();

  public void doTicks() {
    char id;
    BlockBase block;
    long p1 = System.nanoTime() / 1000000;
    synchronized(lock) {
      long p2 = System.nanoTime() / 1000000;
      long diff = p2 - p1;
      if (Static.debugProfile && diff > 1) {
        Static.log("Chunk tick lock:" + diff);
      }
      int cnt = ticks.size();
      if (cnt == 0) return;
      Tick list[] = ticks.toArray(new Tick[cnt]);
      for(int a=0;a<cnt;a++) {
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
      //do 48 random ticks (suppose to be 3 per 16x16x16 area)
      //does : plants grow or die, fire burns out, ice melts, leaves decay, farmland becomes hydrated, and so on
      if (Static.debugDisableRandomTicks) return;
      int x,y,z;
      for(int a=0;a<48;a++) {
        x = r.nextInt(16);
        y = r.nextInt(256);
        z = r.nextInt(16);
        //TODO : can snow_cover be placed here???
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
          block.setShape(this, x,y,z, false);
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
      needBuildBuffers = true;
      if (isClient) {
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
          needBuildBuffers = true;
          if (isClient) {
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

  public String toString() {
    return "Chunk:" + cx + "," + cz;
  }

  private static final byte ver = 0;

  private static final int magic = 0x12345678;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    synchronized(lock) {
      buffer.writeByte(ver);
      buffer.writeInt(dim);
      buffer.writeInt(cx);
      buffer.writeInt(cz);

      if (file) {
        buffer.writeLong(seed);
        int bits = 0;
        if (readOnly) bits |= 0x1;
        if (needPhase2) bits |= 0x2;
        if (needPhase3) bits |= 0x4;
        if (needLights) bits |= 0x8;
        buffer.writeInt(bits);
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
      for(int a=0;a<entity_size;a++) {
        entities.get(a).write(buffer, file);
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

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    dim = buffer.readInt();
    cx = buffer.readInt();
    cz = buffer.readInt();

    if (file) {
      seed = buffer.readLong();
      int bits = buffer.readInt();
      readOnly = ((bits & 0x01) != 0);
      needPhase2 = ((bits & 0x02) != 0);
      needPhase3 = ((bits & 0x04) != 0);
      needLights = ((bits & 0x08) != 0);
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

  @Override
  public SerialClass create(SerialBuffer buffer) {
    return new Chunk();
  }
}
