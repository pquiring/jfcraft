package jfcraft.data;

/** World data
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.io.*;
import java.nio.channels.*;
import java.util.*;

import javaforce.*;

import jfcraft.block.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.dim.*;
import jfcraft.entity.*;
import jfcraft.plugin.PluginLoader;

public class World implements SerialClass, SerialCreator {
  public String name;  //may differ from folderName
  public String type;  //default, flat, custom...
  public long seed;
  public XYZ spawn = new XYZ(8.5f, 65f, 8.5f);
  public int time = 6000;  //time of day (in ticks) (24000 = full day)
  public int day;
  public boolean genSpawnAreaDone;

  public boolean isClient, isServer;

  public World(boolean isServer) {
    this.isServer = isServer;
    this.isClient = !isServer;
  }

  //id mapping
  public ArrayList<String> blockMap = new ArrayList<String>();  //0-32767
  public ArrayList<String> itemMap = new ArrayList<String>();  //32768-65535
  public ArrayList<String> entityMap = new ArrayList<String>();
  public ArrayList<String> dimMap = new ArrayList<String>();
  public ArrayList<String> extraMap = new ArrayList<String>();
  //screen sound ??? -> not saved in world
  public HashMap<String, BluePrint> blueprints = new HashMap<String, BluePrint>();

  public Chunks chunks;

  public static SerialCoder coder = new SerialCoder();  //used to save/load World (sync)

  public static String createFolderName(String worldName) {
    //TODO : return a unique folder name for world name
    return Static.getWorldsPath() + worldName;
  }

  public synchronized void save(String fileName) {
    try {
      synchronized(coder) {
        byte data[] = coder.encodeObject(this, true);
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(data);
        fos.close();
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public boolean incompatible;
  public static boolean listingOnly;

  public static World load(String fileName, boolean listingOnly) {
    try {
      synchronized(coder) {
        World.listingOnly = listingOnly;
        FileInputStream fis = new FileInputStream(fileName);
        byte data[] = JF.readAll(fis);
        fis.close();
        World world = (World)coder.decodeObject(data, new World(true), true);
        if (world != null && world.incompatible && !listingOnly) {
          JFAWT.showError("Error : Can not load world", "World is saved in incompatible version");
          return null;
        }
        return world;
      }
    } catch (Exception e) {
      Static.log(e);
    }
    return null;
  }

  public void setBlock(int dim,float x,float y,float z,char id, int bits) {
    if (id == 0) {
      clearBlock(dim,x,y,z);
      return;
    }
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim,cx,cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    chunk.setBlock(gx, gy, gz, id, bits);
    Static.server.broadcastSetBlock(chunk.dim, x,y,z,id,bits);
  }

  public void clearBlock(int dim,float x,float y,float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim,cx,cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    chunk.clearBlock(gx, gy, gz);
    Static.server.broadcastClearBlock(chunk.dim, x,y,z);
  }

  public void destroyBlock(int dim,float x,float y,float z, boolean doDrop) {
    Coords c = new Coords();
    c.setPos(x, y, z);
    Chunk chunk = chunks.getChunk(dim,c.cx,c.cz);
    BlockBase bb = chunk.getBlock(c.gx, c.gy, c.gz);
    c.chunk = chunk;
    bb.destroy(null, c, doDrop);
  }

  public void addTick(int dim,float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim,cx,cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    chunk.addTick(gx,gy,gz, true);
    chunk.addTick(gx,gy,gz, false);
  }

  public boolean isPowered(int dim,float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim,cx,cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    ExtraRedstone er = (ExtraRedstone)chunk.getExtra(gx, gy, gz, Extras.REDSTONE);
    if (er == null) return false;
    return er.powered;
  }

  public int getPowerLevel(int dim,float x, float y, float z, Coords from) {
    synchronized(tmp) {
      getBlock(dim,x,y,z,tmp);
      if (tmp.block == null) {
        return 0;
      }
      int lvl = tmp.block.getPowerLevel(tmp, from);
      return lvl;
    }
  }

  public void powerChanged(int dim,float x, float y, float z) {
    checkPowered(dim,x,y+1,z);
    checkPowered(dim,x,y-1,z);

    checkPowered(dim,x+1,y,z);
    checkPowered(dim,x-1,y,z);
    checkPowered(dim,x,y,z+1);
    checkPowered(dim,x,y,z-1);
    checkPowered(dim,x+1,y,z+1);
    checkPowered(dim,x+1,y,z-1);
    checkPowered(dim,x-1,y,z+1);
    checkPowered(dim,x-1,y,z-1);
  }

  private static Coords tmp = new Coords();

  public void checkPowered(int dim,float x, float y, float z) {
    synchronized(tmp) {
      getBlock(dim, x, y, z, tmp);
      if (tmp.block != null) {
        tmp.block.checkPowered(tmp);
      }
    }
  }

  public boolean hasBlock(int dim, float x, float y, float z, char id) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) return false;
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    return chunk.getID(gx,gy,gz) == id;
  }
  public boolean hasExtra(int dim, float x, float y, float z, char id) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) return false;
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    return chunk.getID2(gx,gy,gz) == id;
  }
  public boolean isEmpty(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    return chunk.isEmpty(gx,gy,gz);
  }

  public boolean canSpawnOn(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    return chunk.canSpawnOn(gx,gy,gz);
  }

  public Coords getBlock(int dim, float x, float y, float z, Coords c) {
    char id;
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
      //should not happen (but could at and of loaded world)
      //make it appear to be a wall of stone
      c.block = Static.blocks.blocks[Blocks.STONE];
      return c;
    }
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      id = chunk.getID(gx,gy,gz);
      if (id == 0) {
        id = chunk.getID2(gx,gy,gz);
        if (id == 0) {
          //return AIR
          c.block = Static.blocks.blocks[0];
          return c;
        }
      }
      c.block = Static.blocks.blocks[id];
      c.bits = chunk.getBits(gx,gy,gz);
      c.var = Chunk.getVar(c.bits);
      c.dir = Chunk.getDir(c.bits);
      c.chunk = chunk;
      c.x = Static.floor(x);
      c.y = Static.floor(y);
      c.z = Static.floor(z);
      c.cx = cx;
      c.cz = cz;
      c.gx = gx;
      c.gy = gy;
      c.gz = gz;
      return c;
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }

  public char getID(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
//      Static.log("World.getIDAt():Chunk not found:"+dim+","+cx+","+cz);
      return Blocks.STONE;
    }
    int gx = Static.floor(x % 16.0F);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0F);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      return chunk.getID(gx,gy,gz);
    } catch (Exception e) {
      Static.log(e);
      return Blocks.STONE;
    }
  }

  public int getBits(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
      return 0;
    }
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      return chunk.getBits(gx,gy,gz);
    } catch (Exception e) {
      return 0;
    }
  }

  public char getID2(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
//      Static.log("World.getIDAt():Chunk not found:"+dim+","+cx+","+cz);
      return Blocks.STONE;
    }
    int gx = Static.floor(x % 16.0F);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0F);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      return chunk.getID2(gx,gy,gz);
    } catch (Exception e) {
      Static.log(e);
      return Blocks.STONE;
    }
  }

  public int getBlockLight(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
      return 0;
    }
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      return chunk.getBlkLight(gx,gy,gz);
    } catch (Exception e) {
      return 0;
    }
  }

  public int getSunLight(int dim, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Chunk chunk = chunks.getChunk(dim, cx, cz);
    if (chunk == null) {
      return 0;
    }
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) {
      gx = 16 + gx;
    }
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) {
      gz = 16 + gz;
    }
    try {
      return chunk.getSunLight(gx,gy,gz);
    } catch (Exception e) {
      Static.log(e);
      return 0;
    }
  }

  //cache of all entities within chunks
  private HashMap<Integer, EntityBase> entities;

  public void init() {
    entities = new HashMap<Integer, EntityBase>();
    uid = 0x1000;
  }

  public void addEntity(EntityBase e) {
    synchronized(entities) {
      entities.put(e.uid, e);
    }
  }

  public EntityBase getEntity(int uid) {
    synchronized(entities) {
      return entities.get(uid);
    }
  }

  public boolean hasEntity(int uid) {
    synchronized(entities) {
      return entities.containsKey(uid);
    }
  }

  public void delEntity(int uid) {
    synchronized(entities) {
      entities.remove(uid);
    }
  }

  public EntityBase[] getEntities() {
    synchronized(entities) {
      return entities.values().toArray(new EntityBase[entities.size()]);
    }
  }

  private int uid;
  /** Generate unique ID for entity. */
  public synchronized int generateUID() {
    return uid++;
  }

  public char getBlockID(String name) {
    char id = getBlockID(name, false);
    if (id != 0xffff) return id;
    return getItemID(name, false);
  }

  //blocks are numbered 0 -> 32767
  private char getBlockID(String name, boolean create) {
    name = name.toLowerCase();
    int cid = -1;
    int cnt = blockMap.size();
    for(int a=0;a<cnt;a++) {
      if (blockMap.get(a).equals(name)) {
        cid = a;
        break;
      }
    }
    if (cid != -1) {
      return (char)cid;
    }
    if (!create) return 0xffff;
    //assign new id
    if (getItemID(name) != 0xffff) {
      Static.log("Error:Block and Item with same name:" + name);
    }
    char nid = (char)blockMap.size();
//    Static.log("block:" + name + "=" + (int)nid);
    if (nid == 32767) {
      Static.log("Too many blocks loaded");
      JFAWT.showError("Error", "Too many blocks loaded, remove some plugins");
      System.exit(0);
    }
    blockMap.add(name);
    return nid;
  }

  public char getItemID(String name) {
    char id = getItemID(name, false);
    if (id != 0xffff) return id;
    return getBlockID(name, false);
  }

  //items are numbered 32768 -> 65535
  private char getItemID(String name, boolean create) {
    name = name.toLowerCase();
    int cid = -1;
    int cnt = itemMap.size();
    for(int a=0;a<cnt;a++) {
      if (itemMap.get(a).equals(name)) {
        cid = a;
        break;
      }
    }
    if (cid != -1) {
      return (char)(Items.FIRST_ID + cid);
    }
    if (!create) return 0xffff;
    //assign new id
    if (getBlockID(name) != 0xffff) {
      Static.log("Error:Item and Block with same name:" + name);
    }
    char nid = (char)itemMap.size();
//    Static.log("item:" + name + "=" + (int)nid);
    if (nid == 32767) {
      Static.log("Too many items loaded");
      JFAWT.showError("Error", "Too many items loaded, remove some plugins");
      System.exit(0);
    }
    itemMap.add(name);
    return (char)(Items.FIRST_ID + nid);
  }

  public int getEntityID(String name) {
    return getEntityID(name, false);
  }

  private int getEntityID(String name, boolean create) {
    name = name.toLowerCase();
    int cid = -1;
    int cnt = entityMap.size();
    for(int a=0;a<cnt;a++) {
      if (entityMap.get(a).equals(name)) {
        cid = a;
        break;
      }
    }
    if (cid != -1) {
//      Static.log("old entity:" + name + "=" + (int)cid);
      return cid;
    }
    if (!create) return -1;
    //assign new id
    int nid = entityMap.size();
//    Static.log("new entity:" + name + "=" + (int)nid);
    entityMap.add(name);
    return nid;
  }

  public int getDimID(String name) {
    return getDimID(name, false);
  }

  public int getDimID(String name, boolean create) {
    name = name.toLowerCase();
    int cid = -1;
    int cnt = dimMap.size();
    for(int a=0;a<cnt;a++) {
      if (dimMap.get(a).equals(name)) {
        cid = a;
        break;
      }
    }
    if (cid != -1) {
      return cid;
    }
    if (!create) return -1;
    //assign new id
    int nid = dimMap.size();
    dimMap.add(name);
    return nid;
  }

  public byte getExtraID(String name) {
    return getExtraID(name, false);
  }

  public byte getExtraID(String name, boolean create) {
    name = name.toLowerCase();
    byte cid = -1;
    int cnt = extraMap.size();
    for(byte a=0;a<cnt;a++) {
      if (extraMap.get(a).equals(name)) {
        cid = a;
        break;
      }
    }
    if (cid != -1) {
      return cid;
    }
    if (!create) return -1;
    //assign new id
    byte nid = (byte)extraMap.size();
    extraMap.add(name);
    return nid;
  }

  public BluePrint getBluePrint(String name) {
    BluePrint blueprint = blueprints.get(name);
    if (blueprint != null) return blueprint;
    blueprint = Assets.getBluePrint(name).blueprint;
    blueprint.convertIDs(this);
    blueprints.put(name, blueprint);
    return blueprint;
  }

  private RandomAccessFile lockraf; // The file we'll lock
  private FileChannel lockChannel; // The channel to the file
  private FileLock lockLock; // The lock object we hold
  private File lockFile;

  public boolean lock(String fileName) {
    try {
      lockFile = new File(fileName);
      lockraf = new RandomAccessFile(lockFile, "rw");
      lockChannel = lockraf.getChannel();
      lockLock = lockChannel.tryLock();
      if (lockLock == null) return false;
    } catch (Exception e) {
      Static.log(e);
      return false;
    }
    return true;
  }

  public void unlock() {
    if (lockLock == null) return;
    try {
      lockLock.release();
      lockraf.close();
      lockFile.delete();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  /** Assigns IDs to blocks,items,etc.
   * If a new plug-in is installed, it will get new IDs assigned to avoid
   * id conflicts.
   */
  public void assignIDs() {
    Static.log("World.assignIDs");
    //blocks
    int bcnt = Static.blocks.blockCount;
    BlockBase blocks[] = Static.blocks.regBlocks;
    for(int idx=0;idx<bcnt;idx++) {
      blocks[idx].id = getBlockID(blocks[idx].getName(), true);
    }
    //items
    int icnt = Static.items.itemCount;
    ItemBase items[] = Static.items.regItems;
    for(int idx=0;idx<icnt;idx++) {
      items[idx].id = getItemID(items[idx].getName(), true);
    }
    //entity
    int ecnt = Static.entities.entityCount;
    EntityBase entities[] = Static.entities.regEntities;
    for(int idx=0;idx<ecnt;idx++) {
      entities[idx].id = getEntityID(entities[idx].getName(), true);
    }
    //dim
    int dcnt = Static.dims.dimCount;
    DimBase dims[] = Static.dims.regDims;
    for(int idx=0;idx<dcnt;idx++) {
      dims[idx].id = getDimID(dims[idx].getName(), true);
    }
    //extra
    int xcnt = Static.extras.extraCount;
    ExtraBase extras[] = Static.extras.regExtras;
    for(int idx=0;idx<xcnt;idx++) {
      extras[idx].id = getExtraID(extras[idx].getName(), true);
    }
    //now allow everyone to find IDs as needed
    Static.blocks.getIDs(this);
    Static.items.getIDs(this);
    Static.entities.getIDs(this);
    Static.dims.getIDs(this);
    Static.extras.getIDs(this);
    for(int idx=0;idx<bcnt;idx++) {
      blocks[idx].getIDs(this);
    }
    for(int idx=0;idx<icnt;idx++) {
      items[idx].getIDs(this);
    }
    for(int idx=0;idx<ecnt;idx++) {
      entities[idx].getIDs(this);
    }
    for(int idx=0;idx<dcnt;idx++) {
      dims[idx].getIDs(this);
    }
    Static.items.orderItems();  //must do this before blocks
    Static.blocks.orderBlocks();  //these are also copied into items
    Static.entities.orderEntities();
    Static.dims.orderDims();
    Static.extras.orderExtras();
  }

  private static final int magic = 0x5743464a;
  //this version MUST be incremented if any other ver is incremented
  public static final int min_ver = 3;  //min version supported
  public static final int ver = 3;  //latest version supported
  //this version is also used in blueprints

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeInt(magic);
    buffer.writeInt(ver);
    byte nameBytes[] = name.getBytes();
    buffer.writeInt(nameBytes.length);
    buffer.writeBytes(nameBytes);
    byte typeBytes[] = type.getBytes();
    buffer.writeInt(typeBytes.length);
    buffer.writeBytes(typeBytes);
    buffer.writeLong(seed);
    buffer.writeFloat(spawn.x);
    buffer.writeFloat(spawn.y);
    buffer.writeFloat(spawn.z);
    buffer.writeInt(time);
    buffer.writeInt(day);
    int bits = 0;
    if (genSpawnAreaDone) bits |= 0x1;
    buffer.writeInt(bits);
    //write ID assignments
    //blocks
    int blockSize = blockMap.size();
    buffer.writeInt(blockSize);
    for(int a=0;a<blockSize;a++) {
      String name = blockMap.get(a);
      buffer.writeByte((byte)name.length());
      buffer.writeBytes(name.getBytes());
    }
    //items
    int itemSize = itemMap.size();
    buffer.writeInt(itemSize);
    for(int a=0;a<itemSize;a++) {
      String name = itemMap.get(a);
      buffer.writeByte((byte)name.length());
      buffer.writeBytes(name.getBytes());
    }
    //entities
    int entitySize = entityMap.size();
    buffer.writeInt(entitySize);
    for(int a=0;a<entitySize;a++) {
      String name = entityMap.get(a);
      buffer.writeByte((byte)name.length());
      buffer.writeBytes(name.getBytes());
    }
    //dims
    int dimSize = dimMap.size();
    buffer.writeInt(dimSize);
    for(int a=0;a<dimSize;a++) {
      String name = dimMap.get(a);
      buffer.writeByte((byte)name.length());
      buffer.writeBytes(name.getBytes());
    }
    //extras
    int extraSize = extraMap.size();
    buffer.writeInt(extraSize);
    for(int a=0;a<extraSize;a++) {
      String name = extraMap.get(a);
      buffer.writeByte((byte)name.length());
      buffer.writeBytes(name.getBytes());
    }

    String plugins = PluginLoader.getPluginsString();
    int size = plugins.length();
    buffer.writeInt(size);
    buffer.writeBytes(plugins.getBytes());

    buffer.writeInt(magic);
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    int _magic = buffer.readInt();
    if (_magic != magic) {
      Static.log("World.magic invalid");
      return false;
    }
    int _ver = buffer.readInt();
    int nameLength = buffer.readInt();
    byte nameBytes[] = new byte[nameLength];
    buffer.readBytes(nameBytes);
    name = new String(nameBytes);

    if (_ver > ver) {
      Static.log("World ver newer than supported, can not load");
      incompatible = true;
      return true;
    }
    if (_ver < min_ver) {
      Static.log("World ver older than supported, can not load");
      incompatible = true;
      return true;
    }

    int typeLength = buffer.readInt();
    byte typeBytes[] = new byte[typeLength];
    buffer.readBytes(typeBytes);
    type = new String(typeBytes);

    seed = buffer.readLong();
    spawn.x = buffer.readFloat();
    spawn.y = buffer.readFloat();
    spawn.z = buffer.readFloat();
    time = buffer.readInt();
    day = buffer.readInt();

    int bits = buffer.readInt();
    genSpawnAreaDone = (bits & 0x01) != 0;

    //read ID assignments
    //blocks
    int blockCnt = buffer.readInt();
    if (blockCnt > 32767) return false;
    for(int a=0;a<blockCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      blockMap.add(new String(name));
    }
    //items
    int itemCnt = buffer.readInt();
    if (itemCnt > 32767) return false;
    for(int a=0;a<itemCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      itemMap.add(new String(name));
    }
    //entities
    int entityCnt = buffer.readInt();
    if (entityCnt > 65535) return false;
    for(int a=0;a<entityCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      entityMap.add(new String(name));
    }
    //dims
    int dimCnt = buffer.readInt();
    if (dimCnt > 128) return false;
    for(int a=0;a<dimCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      dimMap.add(new String(name));
    }
    //extras
    int extraCnt = buffer.readInt();
    if (extraCnt > 128) return false;
    for(int a=0;a<extraCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      extraMap.add(new String(name));
    }

    //plugins
    int size = buffer.readInt();
    if (size > 8192) return false;
    byte name[] = new byte[size];
    buffer.readBytes(name);
    String oldPlugins = new String(name);
    String curPlugins = PluginLoader.getPluginsString();
    if (!oldPlugins.equals(curPlugins)) {
      //ensure curPlugins contains all oldPlugins
      String cp[] = curPlugins.split(",");
      String op[] = oldPlugins.split(",");
      StringBuilder sb = new StringBuilder();
      int cnt = 0;
      for(int o=0;o<op.length;o++) {
        if (op[o].length() == 0) continue;
        boolean ok = false;
        for(int c=0;c<cp.length;c++) {
          if (cp[c].equals(op[o])) {
            ok = true;
            break;
          }
        }
        if (!ok) {
          if (sb.length() > 0) sb.append(",");
          sb.append(op[o]);
          cnt++;
        }
      }
      if (cnt > 0) {
        Static.log("World.load():Plugins missing:" + sb.toString());
        if (!listingOnly) {
          JFAWT.showError("Error : Can not load world", "Plugins missing:\n" + sb.toString());
          return false;
        }
      }
    }

    int test = buffer.readInt();
    if (test != magic) {
      Static.log("Corrupt world!");
      return false;
    }

    Static.log("Loaded world:" + this.name + "," + type + "," + seed);
    return true;
  }

  public SerialClass create(SerialBuffer buffer) {
    return new World(true);
  }
}
