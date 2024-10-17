package jfcraft.block;

/** Base class for all registered block types
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;
import static jfcraft.data.Types.*;

import static jfcraft.data.Direction.*;

public class BlockBase extends ItemBase implements BlockHitTest, RenderSource {
  /**
   * @param id = Block ID value
   * @param names = text names (with variations)
   * @param images = block images required to render this block
   */
  public BlockBase(String name, String names[], String images[]) {
    this.name = name;
    this.names = names;
    this.images = images;
    canPlace = true;  //all blocks can be placed
    isSolid = true;  //most blocks are solid
    canSelect = true;
    canSpawnOn = true;
    addBox(0,0,0, 16,16,16, Type.BOTH);
  }

  public static ArrayList<Box> boxListEmpty = new ArrayList<Box>();

  public void getIDs(World world) {
    super.getIDs(world);
    if (dropBlock == null) dropBlock = name;
    dropID = world.getBlockID(dropBlock);
    if (dropID == 0 && !dropBlock.toLowerCase().equals("air")) {
      Static.log("Error:dropID == 0 for " + dropBlock);
    }
    if (stepBlock != null) {
      stepID = world.getBlockID(stepBlock);
    }
  }

  public boolean isOpaque;  //completely opaque block (no transparent parts)
  public boolean isSolid;  //is solid full block
  public boolean isAlpha;  //has alpha blending
  public boolean isPerf;  //has transparent/opaque versions (performance)
  public boolean isGreenTop;  //green color on top (based on rain/temp)
  public boolean isGreenSide;  //green color on side (based on rain/temp)
  public boolean isGreenAllSides;  //green color on all sides (based on rain/temp)
  public boolean isRed;  //red color on top only (based on depth : redstone)
  public boolean isBlue;  //water
  public boolean isComplex;  //is complex object
  public boolean isPlant;  //need dirt
  public boolean isRedstone;  //is related to redstone
  public boolean isBlocks2;  //placed in the blocks2 plane (Water, Lava, etc.)
  public boolean isLiquid;  //water, lava, oil
  public boolean isSupported;  //needs support from block underneath
  public boolean hasShape;
  public boolean canReplace;  //can be replaced by placing a block
  public boolean canPlantOn;  //can plant flowers, trees, etc.
  public boolean canUse;
  public boolean canSmooth;  //has a step version
  public boolean canSelect;
  public boolean canSpawnOn;
  public boolean clampAlpha;
  public char stepID;  //step to smooth block type
  public String stepBlock;
  public char dropID;
  public String dropBlock;
  public boolean dropVar = true;
  public int dropCount = 1;
  public int emitLight = 0;  //0-14 (15 = sunLight)
  public int absorbLight = 0;
  public float hardness = 0f;
  public int preferedTool = TOOL_NONE;
  public int cls = 0;  //item class : ROCK I,II,III,IV, etc.

  private ArrayList<Box> boxes_entity = new ArrayList<Box>();
  private ArrayList<Box> boxes_selection = new ArrayList<Box>();
  //images2/textures2 is for opaque (fast grafix mode)
  public String[] images2 = new String[0];
  public SubTexture[] textures2;

  public SubTexture getTexture() {
    int cnt = textures.length / names.length;
    int idx = 0;
    if (isVar) {
      idx = (Static.data.var[X] & varMask) * cnt;
    }
    if ((idx + cnt - 1) >= textures.length) {
      System.out.println("textures missing for block:" + name + ":" + idx + ">" + textures.length);
    }
    switch (cnt) {
      case 1:
        return textures[idx];
      case 2:
        if (Static.data.side == A || Static.data.side == B)
          return textures[idx+0];
        else
          return textures[idx+1];
      case 3:
        if (Static.data.side == A) {
          return textures[idx+0];
        } else if (Static.data.side == B) {
          return textures[idx+2];
        } else {
          return textures[idx+1];
        }
      case 4:
        if (isDirXZ) {
          //block faces north
          if (Static.data.side == N) {
            return textures[idx+1];  //faces player
          } else if (Static.data.side == B) {
            return textures[idx+3];  //bottom
          } else if (Static.data.side == A) {
            return textures[idx+0];  //top
          } else {
            return textures[idx+2];  //sides
          }
        } else {
          //block faces UP!
          if (Static.data.side == A) {
            return textures[idx+1];  //faces player
          } else if (Static.data.side == S) {
            return textures[idx+3];  //bottom
          } else if (Static.data.side == N) {
            return textures[idx+0];  //top
          } else {
            return textures[idx+2];  //sides
          }
        }
    }
    return null;  //should not happen
  }

  public String getName(int var) {
    if (isVar) return names[var & varMask]; else return names[0];
  }
  public BlockBase setGreenTop() {
    isGreen = true;
    isGreenTop = true;
    return this;
  }
  public BlockBase setGreenTopSide() {
    isGreen = true;
    isGreenTop = true;
    isGreenSide = true;
    return this;
  }
  public BlockBase setGreenAllSides() {
    isGreen = true;
    isGreenAllSides = true;
    return this;
  }
  public BlockBase setBlue() {
    isBlue = true;
    return this;
  }
  public BlockBase setPerf() {
    isPerf = true;
    return this;
  }
  public BlockBase setPlant() {
    isPlant = true;
    return this;
  }

  public void addFace(RenderBuffers buf, SubTexture st) {
    Static.data.isDir = isDir;
    Static.data.isDirXZ = isDirXZ;
    Static.data.isGreen = isGreenAllSides
      || (isGreenTop && Static.data.dirSide == A)
      || (isGreenSide && Static.data.dirSide >= N && Static.data.dirSide <= W);
    Static.data.isRed = isRed;
    Static.data.isBlue = isBlue;
    buf.addFace(st);
  }

  private static Model quads;

  public void addQuad(RenderBuffers obj, int quad, SubTexture st) {
    if (quads == null) {
      quads = Assets.getModel("quads").model;
    }
    /*
      -z
     0|1
    --|--x+
     2|3
      y
     4|5
    -----x+
     6|7
      +z
    */
    buildBuffers(quads.getObject("Q" + quad), obj, st);
  }
  /** Clears all bounding boxes for block. */
  public BlockBase resetBoxes(BlockHitTest.Type type) {
    switch (type) {
      case ENTITY:
        boxes_entity.clear();
        break;
      case SELECTION:
        boxes_selection.clear();
        break;
      case BOTH:
        boxes_entity.clear();
        boxes_selection.clear();
        break;
    }
    return this;
  }
  /** Adds a bounding box to the block. */
  public BlockBase addBox(float x1, float y1, float z1, float x2, float y2, float z2, BlockHitTest.Type type) {
    Box box = new Box(x1,y1,z1, x2,y2,z2);
    switch (type) {
      case ENTITY:
        boxes_entity.add(box);
        break;
      case SELECTION:
        boxes_selection.add(box);
        break;
      case BOTH:
        boxes_entity.add(box);
        boxes_selection.add(box);
        break;
    }
    return this;
  }
  public BlockBase setLight(byte light) {
    emitLight = light;
    return this;
  }
  public BlockBase setSupportsPlant() {
    canPlantOn = true;
    return this;
  }
  public BlockBase setUseable() {
    canUse = true;
    return this;
  }
  public BlockBase setBake(String id) {
    super.setBake(id);
    return this;
  }
  public BlockBase setDrop(String id) {
    dropBlock = id;
    return this;
  }
  public BlockBase setDrop(String id, int cnt) {
    dropBlock = id;
    dropCount = cnt;
    return this;
  }
  public BlockBase setSupported() {
    isSupported = true;
    return this;
  }
  public BlockBase setBlocks2() {
    isBlocks2 = true;
    return this;
  }
  public BlockBase setCanReplace() {
    canReplace = true;
    return this;
  }
  /** Absorb block light in all directions and sun light that is not from above. */
  public final int absorbLight(int lvl) {
    if (isOpaque && !isPerf) return 0;
    if (lvl <= absorbLight) return 0;
    return (lvl - absorbLight);
  }
  public boolean canSupportBlock(Coords c) {
    return isSolid;
  }
  private static Coords supportingBlock = new Coords();
  /** Check if supported block is present.
   */
  public boolean checkSupported(Coords thisBlock) {
    //get supporting block
    synchronized(supportingBlock) {
      supportingBlock.copy(thisBlock);
      if (thisBlock.block.isDir || thisBlock.block.isDirFace) {
        supportingBlock.adjacentBlock();
      } else {
        supportingBlock.y--;
      }
      Static.server.world.getBlock(thisBlock.chunk.dim, supportingBlock.x, supportingBlock.y, supportingBlock.z, supportingBlock);
      if ((thisBlock.block.isPlant && !supportingBlock.block.canPlantOn) || (!supportingBlock.block.canSupportBlock(thisBlock))) {
        //supporting block gone
        return false;
      }
    }
    return true;
  }
  private static Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
//    Static.log("tick:blockbase:" + tick);
    if (hasShape) setShape(chunk, tick.x, tick.y, tick.z, true, c);
    if (isSupported) {
      tick.toWorldCoords(chunk, c);
      Static.server.world.getBlock(chunk.dim, c.x, c.y, c.z, c);
      if (!checkSupported(c)) {
        c.block = this;
        destroy(null, c, true);
      }
    }
    chunk.delTick(tick);
  }
  /** Random tick */
  public void rtick(Chunk chunk, int gx,int gy,int gz) {}
  /** Entity tick */
  public void etick(EntityBase e, Coords c) {}
  /** Check shape of block (fence, glass pane, bars, etc.) */
  public void setShape(Chunk chunk, int gx, int gy, int gz, boolean live, Coords c) {}
  private static Coords tmp = new Coords();
  /** Place block @ coords. */
  public boolean place(Client client, Coords c) {
    int dir = 0;
    if (isDir) {
      if (reverseDir) c.otherSide();
      if (isDirXZ) {
        dir = c.dir_xz;
      } else {
        dir = c.dir;
      }
    } else if (isDirFace) {
      dir = c.dir;  //will not be from player.getDir()
    }
    int var = 0;
    if (isVar) {
      var = c.var;
    }
    int bits = Chunk.makeBits(dir, var);
    if (isRedstone) {
      ExtraRedstone er = new ExtraRedstone(c.gx, c.gy, c.gz);
      c.chunk.addExtra(er);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    }
    c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits);
    if (hasShape) {
      synchronized(tmp) {
        setShape(c.chunk, c.gx, c.gy, c.gz, false, tmp);
      }
      bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    }
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits);
    return true;
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    int bits = c.chunk.getBits(c.gx,c.gy,c.gz);
    Item items[] = null;
    if (client != null && cls != CLS_NONE) {
      ItemBase tool = Static.items.items[client.player.items[client.player.activeSlot].id];
      if (tool.material < cls) {
        doDrop = false;
      }
    }
    if (doDrop) {
      items = drop(c, isVar ? Chunk.getVar(bits) : 0);
    }
    BlockBase block = c.chunk.getBlockType(c.gx, c.gy, c.gz);
    if (block.isBlocks2) {
      c.chunk.clearBlock2(c.gx,c.gy,c.gz);
      Static.server.broadcastClearBlock2(c.chunk.dim,c.x,c.y,c.z);
    } else {
      c.chunk.clearBlock(c.gx,c.gy,c.gz);
      Static.server.broadcastClearBlock(c.chunk.dim,c.x,c.y,c.z);
    }
    if (doDrop) {
      for(int a=0;a<items.length;a++) {
        Item item = items[a];
        if (item.id == 0) continue;
        WorldItem.create(item, c.chunk.dim, c.x + 0.5f, c.y, c.z + 0.5f, c.chunk, -1);
      }
    }
    if (block.isRedstone) {
      c.chunk.delExtra(c, Extras.REDSTONE);
      Static.server.broadcastDelExtra(c.chunk.dim, c.x, c.y, c.z, Extras.REDSTONE, true);
      Static.server.world.powerChanged(c.chunk.dim,c.x,c.y,c.z);
    }
  }
  public void activate(Client client, Coords c) {}
  public void deactivate(Client client, Coords c) {}
  public void powerOn(Client client, Coords c) {
    if (!isRedstone) return;
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (er.powered) return;
    er.powered = true;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    activate(client, c);
  }
  public void powerOff(Client client, Coords c) {
    if (!isRedstone) return;
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    if (!er.powered) return;
    er.powered = false;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    deactivate(client, c);
  }
  public void checkPowered(Coords c) {}
  public void useBlock(Client client, Coords c) {}
  public boolean useTool(Client client, Coords c) {
    return false;
  }
  public int getPowerLevel(Coords c, Coords from) {return 0;}
  public Item bake() {
    if (!canBake) return null;
    return new Item(bakeID);
  }
  public Item[] drop(Coords c, int var) {
    if (dropID == 0) return new Item[0];
    return new Item[] {new Item(dropID, isVar && dropVar ? var : 0, dropCount)};
  }
  public BlockBase setFuel(int heat) {
    super.setFuel(heat);
    return this;
  }
  public BlockBase setMaterial(int type) {
    super.setMaterial(type);
    return this;
  }
  public BlockBase setVar() {
    isVar = true;
    return this;
  }
  public BlockBase setDropVar(boolean state) {
    dropVar = state;
    return this;
  }
  public BlockBase setDir() {
    isDir = true;
    return this;
  }
  public BlockBase setDirFace() {
    isDirFace = true;
    return this;
  }
  public BlockBase setDirXZ() {
    isDir = true;
    isDirXZ = true;
    return this;
  }
  public BlockBase setSmooth(String id) {
    this.canSmooth = true;
    this.stepBlock = id;
    return this;
  }
  public BlockBase setShowAsItem() {
    renderAsItem = true;
    return this;
  }

  /** Returns all bounding boxes.  Override if block changes with direction. */
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    switch (type) {
      case ENTITY: return boxes_entity;
      case SELECTION: return boxes_selection;
    }
    return null;
  }

  public boolean hitPoint(float hx, float hy, float hz,Coords c, Type type) {
    ArrayList<Box> boxes = getBoxes(c, type);
    int cnt = boxes.size();
    for(int a=0;a<cnt;a++) {
      Box box = boxes.get(a);
      float x1 = box.x1 + c.x;
      float y1 = box.y1 + c.y;
      float z1 = box.z1 + c.z;
      float x2 = box.x2 + c.x;
      float y2 = box.y2 + c.y;
      float z2 = box.z2 + c.z;
      if (
           hx >= x1 && hx <= x2
        && hy >= y1 && hy < y2
        && hz >= z1 && hz < z2
      ) return true;
    }
    return false;
  }
  public boolean hitBox(float hx, float hy, float hz, float hwidth2, float hheight2, float hdepth2,Coords c, Type type) {
    ArrayList<Box> boxes = getBoxes(c, type);
    int cnt = boxes.size();
    for(int a=0;a<cnt;a++) {
      Box box = boxes.get(a);
      float x = (box.x1 + box.x2) / 2.0f + c.x;
      float y = (box.y1 + box.y2) / 2.0f + c.y;
      float z = (box.z1 + box.z2) / 2.0f + c.z;
      float width2 = Math.abs(box.x1 - box.x2) / 2.0f;
      float height2 = Math.abs(box.y1 - box.y2) / 2.0f;
      float depth2 = Math.abs(box.z1 - box.z2) / 2.0f;

      //AABB (Axis Aligned Bounding Box)

      if (Math.abs( (hx) - (x) ) < (width2 + hwidth2) ) {
        if (Math.abs( (hy) - (y) ) < (height2 + hheight2) ) {
          if (Math.abs( (hz) - (z) ) < (depth2 + hdepth2) ) {
            return true;
          }
        }
      }

    }
    return false;
  }

  public BlockBase setHardness(float hardness, int preferedTool, int cls) {
    this.hardness = hardness;
    this.preferedTool = preferedTool;
    this.cls = cls;
    return this;
  }

  /** Returns amount of dmg done.
   * see http://minecraft.gamepedia.com/Breaking
   */
  public float dmg(Item item) {
    ItemBase tool = Static.items.items[item.id];

    if (hardness == -1f) return 0f;  //can not destroy (bedrock, etc.)

    float time = hardness * 1.5f;

    boolean slow = false;
    switch (cls) {
      case CLS_WOOD: if (tool.material < MAT_WOOD) slow = true; break;
      case CLS_STONE: if (tool.material < MAT_STONE) slow = true; break;
      case CLS_IRON: if (tool.material < MAT_IRON) slow = true; break;
      case CLS_DIAMOND: if (tool.material < MAT_DIAMOND) slow = true; break;
    }
    if (slow) time *= 3.33;

    if (tool.tool == preferedTool) {
      switch (tool.material) {
        case MAT_WOOD: time /= 2f; break;
        case MAT_STONE: time /= 4f; break;
        case MAT_IRON: time /= 6f; break;
        case MAT_DIAMOND: time /= 8f; break;
        case MAT_GOLD: time /= 12f; break;
      }
    }

    float ticks = time * 20.0f;

    float dmg = 100.0f / ticks;

    return dmg;
  }

  public void teleport(EntityBase e, Coords c) {}

  public int rotateSide(int side, int dir) {
    switch (dir) {
      case A: return side;
      case B:  //rotate top to bottom
        switch (side) {
          case A: return B;
          case B: return A;
//          case N: return N;
          case E: return W;
//          case S: return S;
          case W: return E;
        }
        break;
      case N:  //rotate A to N
        switch (side) {
          case A: return N;
          case B: return S;
          case N: return A;
          case E: return W;  //rotated
          case S: return B;
          case W: return E;  //rotated
        }
        break;
      case E:  //rotate A to E
        switch (side) {
          case A: return E;
          case B: return W;
          case N: return A;  //rotated
          case E: return N;
          case S: return B;  //rotated
          case W: return S;
        }
        break;
      case S:  //rotate A to S
        switch (side) {
          case A: return S;
          case B: return N;
          case N: return A;
          case E: return E;  //rotated
          case S: return B;
          case W: return W;  //rotated
        }
        break;
      case W:  //rotate A to W
        switch (side) {
          case A: return W;
          case B: return E;
          case N: return A;  //rotated
          case E: return S;
          case S: return B;  //rotated
          case W: return N;
        }
        break;
    }
    return side; //no change
  }

  public int rotateSideXZ(int side, int dir) {
    switch (dir) {
      case A: return side;  //should not be used (could be same as N)
      case B: return side;  //should not be used
      case N: return side;  //no change
      case E:  //rotate N to E
        switch (side) {
          case N: return E;
          case E: return S;
          case S: return W;
          case W: return N;
        }
        break;
      case S:  //rotate N to S
        switch (side) {
          case N: return S;
          case E: return W;
          case S: return N;
          case W: return E;
        }
        break;
      case W:  //rotate N to W
        switch (side) {
          case N: return W;
          case E: return N;
          case S: return E;
          case W: return S;
        }
        break;
    }
    return side; //no change
  }

  public void buildBuffers(RenderDest dest) {
    if (renderAsEntity) {
      EntityBase entity = Static.entities.entities[entityID];
      entity.pos.x = 0;
      entity.pos.y = 0;
      entity.pos.z = 0;
      entity.ang.y = 180;
      entity.setScale(1.0f);
    } else if (renderAsItem) {
      //voxel ???
      buffersIdx = 0;
    } else {
      buffersIdx = textures[isVar ? Static.data.var[X] : 0].buffersIdx;

      RenderBuffers buf = dest.getBuffers(buffersIdx);
      for(int a=0;a<6;a++) {
        int side = a;
        if (isDir) {
          if (!isDirXZ) {
            side = rotateSide(side, Static.data.dir[X]);
          } else {
            side = rotateSideXZ(side, Static.data.dir[X]);
          }
        }
        Static.data.dirSide = side;
        if (Static.data.opaque[side]) continue;
        Static.data.side = a;
        Static.data.isDir = isDir;
        Static.data.isDirXZ = isDirXZ;
        addFace(buf, getTexture());
      }
    }
  }

  private static Faces faces = new Faces();

  //copies Object3 to RenderBuffers (rotates/translates into position)
  public void buildBuffers(Object3 obj, RenderBuffers buf, SubTexture st) {
    float xyz[] = obj.vpl.toArray();
    int idx[] = obj.vil.toArray();
    Static.data.isDir = isDir || isDirFace;
    Static.data.isDirXZ = isDirXZ;
    Static.data.isGreen = isGreen;
    Static.data.isRed = isRed;
    Static.data.isBlue = isBlue;
    faces.xyz = xyz;
    faces.rotate();
    int off = buf.getVertexCount();
    for(int a=0;a<idx.length;a++) {
      idx[a] += off;
    }
    buf.addVertex(xyz);
    buf.addPoly(idx);
    int uvmaps = obj.maps.size();
    switch (uvmaps) {
      case 0: {
        Static.log("Error:No UVMaps:" + this);
        break;
      }
      case 1: {
        float uv1[] = obj.getUVMap("normal").uvl.toArray();
        buf.adjustTexture(uv1, st);
        if (Static.data.crack == -1) {
          buf.addTextureCoords(uv1);
        } else {
          float uv2[] = obj.getUVMap("normal").uvl.toArray();
          buf.adjustCrack(uv2, Static.data.crack);
          buf.addTextureCoords(uv1, uv2);
        }
        break;
      }
      case 2: {
        float uv1[] = obj.getUVMap("normal").uvl.toArray();
        buf.adjustTexture(uv1, st);
        if (Static.data.crack == -1) {
          buf.addTextureCoords(uv1);
        } else {
          float uv2[] = obj.getUVMap("crack").uvl.toArray();
          buf.adjustCrack(uv2, Static.data.crack);
          buf.addTextureCoords(uv1, uv2);
        }
        break;
      }
    }
    int vc = xyz.length/3;
    for(int a=0;a<vc;a++) {
      buf.addColor();
      buf.addSunLight(Static.data.sl[X]);
      buf.addBlockLight(Static.data.bl[X]);
    }
  }

  /** Adjusts texture coords slightly inward to avoid adjacent textures from bleeding inward.
   * Only for full texture UV maps.
   */
  public void adjustTextureSize(Object3 obj) {
    int mcnt = obj.maps.size();
    for(int m=0;m<mcnt;m++) {
      UVMap map = obj.maps.get(m);
      int uvcnt = map.uvl.size();
      float uv[] = map.uvl.getBuffer();
      for(int a=0;a<uvcnt;a++) {
        if (uv[a] < 0.5f) {
          uv[a] = 0.001f;
        } else {
          uv[a] = 0.999f;
        }
      }
    }
  }

  public boolean canPlace(Coords c) {
    if (isSupported) {
      c.block = this;
      if (!checkSupported(c)) {
        return false;
      }
    }
    return super.canPlace(c);
  }

  public void addText(RenderDest dest, String[] lns) {
    RenderBuffers buf = dest.getBuffers(Chunk.DEST_TEXT);
    int chars = 0;
    for(int a=0;a<lns.length;a++) {
      chars += lns[a].length();
    }
    float xyz[] = new float[chars * 4 * 3];
    float uv[] = new float[chars * 4 * 2];
    int idx[] = new int[chars * 4];
    int xyzpos = 0;
    int uvpos = 0;
    int idxpos = 0;
    float y = lns.length * Static._1_16;
    for(int a=0;a<lns.length;a++) {
      String ln = lns[a];
      int sl = ln.length();
      float x1 = -(sl / 32f);
      float y1 = y;
      float z1 = 0;
      float x2 = x1 + Static._1_16;
      float y2 = y1 - Static._1_8;
//      float z2 = z1;
      char chs[] = ln.toCharArray();
      for(int c=0;c<sl;c++) {
        char ch = chs[c];
        float u1 = (ch % 16) / 16.0f;
        float v1 = Static.floor(ch / 16) / 16.0f;
        float u2 = u1 + Static._1_16;
        float v2 = v1 + Static._1_16;
        //add 4 pts
        int off = xyzpos / 3;

        xyz[xyzpos++] = x1;
        xyz[xyzpos++] = y1;
        xyz[xyzpos++] = z1;
        uv[uvpos++] = u1;
        uv[uvpos++] = v1;

        xyz[xyzpos++] = x2;
        xyz[xyzpos++] = y1;
        xyz[xyzpos++] = z1;
        uv[uvpos++] = u2;
        uv[uvpos++] = v1;

        xyz[xyzpos++] = x2;
        xyz[xyzpos++] = y2;
        xyz[xyzpos++] = z1;
        uv[uvpos++] = u2;
        uv[uvpos++] = v2;

        xyz[xyzpos++] = x1;
        xyz[xyzpos++] = y2;
        xyz[xyzpos++] = z1;
        uv[uvpos++] = u1;
        uv[uvpos++] = v2;

        idx[idxpos++] = off + 3;
        idx[idxpos++] = off + 2;
        idx[idxpos++] = off + 1;
        idx[idxpos++] = off + 0;

        x1 += Static._1_16;
        x2 += Static._1_16;
      }
      y -= Static._1_8;
    }
    faces.xyz = xyz;
    faces.rotate();
    int off = buf.getVertexCount();
    for(int a=0;a<idx.length;a++) {
      idx[a] += off;
    }
    buf.addVertex(xyz);
    buf.addTextureCoords(uv);
    buf.addPoly(idx);
    int vc = xyz.length/3;
    for(int a=0;a<vc;a++) {
      buf.addColor(Static.black);
      buf.addSunLight(0);
      buf.addBlockLight(0);
    }
  }

  public byte rotateBits(byte bits, int rotation) {
    if (hasShape) {
      byte dir = (byte)((bits & 0xf0) >> 4);
      byte var = (byte)(bits & 0x0f);
      dir = Direction.rotateShape(dir, rotation);
      return (byte)((dir << 4) | var);
    }
    if (isDir || isDirFace || isDirXZ) {
      byte dir = (byte)((bits & 0xf0) >> 4);
      byte var = (byte)(bits & 0x0f);
      dir = Direction.rotate(dir, rotation);
      return (byte)((dir << 4) | var);
    }
    return bits;
  }

  public SubTexture getDestroyTexture(int var) {
    if (textures.length == 0) return null;
    if (isVar)
      return textures[var & varMask];
    else
      return textures[0];
  }

  public void reloadAll() {}
}
