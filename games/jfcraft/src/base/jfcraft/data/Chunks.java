package jfcraft.data;

/** Cache chunks
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.server.*;
import jfcraft.entity.*;

public class Chunks extends ClientServer {
  //cache of all chunks
  private HashMap<ChunkKey, Chunk> cache = new HashMap<ChunkKey, Chunk>();
  private Object lock = new Object();  //lock access to cache

  public Chunks(boolean isClient) {
    super(isClient);
  }

  //client/server side (may return null)
  public Chunk getChunk(int dim, int cx, int cz) {
    ChunkKey key = ChunkKey.alloc(dim, cx, cz);
    Chunk chunk;
    synchronized(lock) {
      chunk = cache.get(key);
    }
    key.free();
    return chunk;
  }

  public boolean dim_inited[] = new boolean[Dims.MAX_ID];

  //server side only : gets loaded chunk, or loads from disk, or generates it
  public synchronized Chunk getChunk2(int dim, int cx, int cz, boolean doPhase2, boolean doPhase3, boolean doLights) {
    Chunk chunk = getChunk(dim,cx,cz);
//    Static.log("getChunk2:" + cx + "," + cz + ":" + getAdj + ":" + cache.size());
    if (!dim_inited[dim]) {
      Static.dims.dims[dim].init();
      dim_inited[dim] = true;
    }
    if (chunk == null) {
      //load from disk
      chunk = loadChunk(dim, cx, cz);
      if (chunk == null) {
        //if not on disk then generate from scratch
        chunk = Static.dims.dims[dim].getGeneratorPhase1().generate(dim,cx,cz);
      } else {
        //test - delete all entities from disk
        if (Static.debugPurgeEntities && chunk.entities.size() > 0) {
          chunk.entities.clear();
          chunk.dirty = true;
        }
      }
      addChunk(chunk);
    }
    if (doPhase2 && chunk.needPhase2) {
      chunk.getAdjChunks(false, false, false, 8);
      Static.dims.dims[dim].getGeneratorPhase2().generate(chunk);
    }
    if (doPhase3 && chunk.needPhase3) {
      chunk.getAdjChunks(true, false, false, 8);
      Static.dims.dims[dim].getGeneratorPhase3().generate(chunk);
      chunk.buildShapes();
    }
    if (doLights && chunk.needLights) {
      chunk.getAdjChunks(true, true, false, 8);
      Static.dims.dims[dim].getLightingServer().light(chunk);
    }
    return chunk;
  }

  /** Loads surrounding chunks. */
  public void loadSurroundingChunks(int dim, int cx, int cz) {
    getChunk2(dim,cx+1,cz+1,true,true,true);
    getChunk2(dim,cx  ,cz+1,true,true,true);
    getChunk2(dim,cx+1,cz-1,true,true,true);
    getChunk2(dim,cx  ,cz-1,true,true,true);
    getChunk2(dim,cx-1,cz+1,true,true,true);
    getChunk2(dim,cx  ,cz+1,true,true,true);
    getChunk2(dim,cx-1,cz-1,true,true,true);
    getChunk2(dim,cx  ,cz-1,true,true,true);
  }

  public boolean hasChunk(int dim,int cx,int cz) {
    ChunkKey key = ChunkKey.alloc(dim, cx, cz);
    boolean contains;
    synchronized(lock) {
      contains = cache.containsKey(key);
    }
    key.free();
    return contains;
  }

  public synchronized void addChunk(Chunk chunk) {
    ChunkKey key = ChunkKey.alloc(chunk.dim, chunk.cx, chunk.cz);
//    Static.log("addChunk:" + cid);
    synchronized(lock) {
      Chunk old = cache.get(key);
      if (old != null) {
        removeChunk(old);
      }
    }
    if (isClient) chunk.createObjects();
    //add entities to cache and generate uid's if server
    EntityBase es[] = chunk.getEntities();
    for(int a=0;a<es.length;a++) {
      EntityBase e = es[a];
      e.init();
      if (isServer) {
        if (e.id == Entities.PLAYER) {
          //player saved to disk somehow???
          chunk.entities.remove(a);
          continue;
        }
        e.uid = world.generateUID();
        if (e.id == Entities.BOAT) {
          Static.log("S:Boat.uid=" + e.uid);
        }
        //ensure entity is in correct chunk
        int cx = Static.floor(e.pos.x / 16.0f);
        int cz = Static.floor(e.pos.z / 16.0f);
        if (chunk.cx != cx || chunk.cz != cz) {
          Chunk chunk2 = getChunk2(chunk.dim, cx, cz, false, false, false);
          Static.log("Warning:Entity moved to correct chunk:id=0x" + Integer.toString(e.id, 16)
            + ",x=" + e.pos.x + ",z=" + e.pos.z +",chunk.cx=" +chunk.cx + ",cz=" + chunk.cz);
          chunk.delEntity(e);
          chunk2.addEntity(e);
        }
      }
      world.addEntity(e);
    }
    //update links
    Chunk N = getChunk(chunk.dim, chunk.cx, chunk.cz - 1);
    if (N != null) {
      N.S = chunk;
      N.adjCount++;
      if (isClient && N.adjCount == 8) {
        if (N.needRelight)
          Static.client.chunkLighter.add(N, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(N);
      }
      chunk.N = N;
      chunk.adjCount++;
    }
    Chunk S = getChunk(chunk.dim, chunk.cx, chunk.cz + 1);
    if (S != null) {
      S.N = chunk;
      S.adjCount++;
      if (isClient && S.adjCount == 8) {
        if (S.needRelight)
          Static.client.chunkLighter.add(S, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(S);
      }
      chunk.S = S;
      chunk.adjCount++;
    }
    Chunk E = getChunk(chunk.dim, chunk.cx + 1, chunk.cz);
    if (E != null) {
      E.W = chunk;
      E.adjCount++;
      if (isClient && E.adjCount == 8) {
        if (E.needRelight)
          Static.client.chunkLighter.add(E, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(E);
      }
      chunk.E = E;
      chunk.adjCount++;
    }
    Chunk W = getChunk(chunk.dim, chunk.cx - 1, chunk.cz);
    if (W != null) {
      W.E = chunk;
      W.adjCount++;
      if (isClient && W.adjCount == 8) {
        if (W.needRelight)
          Static.client.chunkLighter.add(W, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(W);
      }
      chunk.W = W;
      chunk.adjCount++;
    }
    //check corners
    Chunk NE = getChunk(chunk.dim, chunk.cx + 1, chunk.cz - 1);
    if (NE != null) {
      NE.adjCount++;
      if (isClient && NE.adjCount == 8) {
        if (NE.needRelight)
          Static.client.chunkLighter.add(NE, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(NE);
      }
      chunk.adjCount++;
    }
    Chunk NW = getChunk(chunk.dim, chunk.cx - 1, chunk.cz - 1);
    if (NW != null) {
      NW.adjCount++;
      if (isClient && NW.adjCount == 8) {
        if (NW.needRelight)
          Static.client.chunkLighter.add(NW, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(NW);
      }
      chunk.adjCount++;
    }
    Chunk SE = getChunk(chunk.dim, chunk.cx + 1, chunk.cz + 1);
    if (SE != null) {
      SE.adjCount++;
      if (isClient && SE.adjCount == 8) {
        if (SE.needRelight)
          Static.client.chunkLighter.add(SE, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(SE);
      }
      chunk.adjCount++;
    }
    Chunk SW = getChunk(chunk.dim, chunk.cx - 1, chunk.cz + 1);
    if (SW != null) {
      SW.adjCount++;
      if (isClient && SW.adjCount == 8) {
        if (SW.needRelight)
          Static.client.chunkLighter.add(SW, 0,0,0, 15,255,15);
        else
          Static.client.chunkBuilder.add(SW);
      }
      chunk.adjCount++;
    }
    synchronized(lock) {
      cache.put(key.clone(), chunk);
    }
    if (isClient && chunk.adjCount == 8) {
      if (chunk.needRelight)
        Static.client.chunkLighter.add(chunk, 0,0,0, 15,255,15);
      else
        Static.client.chunkBuilder.add(chunk);
    }
    key.free();
  }
  public void removeChunk(int dim, int cx, int cz) {
    ChunkKey key = ChunkKey.alloc(dim, cx, cz);
    Chunk chunk;
    synchronized(lock) {
      chunk = cache.get(key);
    }
    key.free();
    removeChunk(chunk);
  }
  public synchronized void removeChunk(Chunk chunk) {
    ChunkKey key = ChunkKey.alloc(chunk.dim, chunk.cx, chunk.cz);
    synchronized(lock) {
      cache.remove(key);
    }
    key.free();
    //remove all links
    if (chunk.N != null) {chunk.N.S = null; chunk.N.adjCount--; }
    if (chunk.E != null) {chunk.E.W = null; chunk.E.adjCount--; }
    if (chunk.S != null) {chunk.S.N = null; chunk.S.adjCount--; }
    if (chunk.W != null) {chunk.W.E = null; chunk.W.adjCount--; }
    //check corners
    Chunk NE = getChunk(chunk.dim, chunk.cx + 1, chunk.cz - 1);
    if (NE != null) {
      NE.adjCount--;
    }
    Chunk NW = getChunk(chunk.dim, chunk.cx - 1, chunk.cz - 1);
    if (NW != null) {
      NW.adjCount--;
    }
    Chunk SE = getChunk(chunk.dim, chunk.cx + 1, chunk.cz + 1);
    if (SE != null) {
      SE.adjCount--;
    }
    Chunk SW = getChunk(chunk.dim, chunk.cx - 1, chunk.cz + 1);
    if (SW != null) {
      SW.adjCount--;
    }
    int cnt = chunk.entities.size();
    for(int a=0;a<cnt;a++) {
      EntityBase e = chunk.entities.get(a);
      world.delEntity(e.uid);
    }
  }
  public int getCount() {
    int cnt;
    synchronized(lock) {
      cnt = cache.size();
    }
    return cnt;
  }
  public Chunk[] getChunks() {
    Chunk chunks[];
    synchronized(lock) {
      chunks = cache.values().toArray(new Chunk[cache.size()]);
    }
    return chunks;
  }

  private Object fileLock = new Object();
  private HashMap<ChunkKey, ChunkStore> stores = new HashMap<ChunkKey, ChunkStore>();

  private static final int storeBits = 4;  //256 cubed blocks

  private Chunk loadChunk(int dim, int cx,int cz) {
    int ix = cx >> storeBits;
    int iz = cz >> storeBits;
    synchronized(fileLock) {
      ChunkKey key = ChunkKey.alloc(dim, ix, iz);
      ChunkStore store = stores.get(key);
      if (store == null) {
        store = new ChunkStore();
        String fileName = Server.folderName + "/" + dim + "/";
        new File(fileName).mkdir();
        fileName += "c" + ix + "," + iz + ".dat";
        if (new File(fileName).exists()) {
          //load store from disk
          store.open(fileName,ix,iz);
        } else {
          //create new store for this range of chunks
          store.create(fileName,ix,iz);
        }
        stores.put(key.clone(), store);
      }
      key.free();
      return store.loadChunk(cx, cz);
    }
  }

  private static SerialCoder coder = new SerialCoder();  //used to save chunk only

  public void saveChunk(Chunk chunk) {
    int ix = chunk.cx >> storeBits;
    int iz = chunk.cz >> storeBits;
    byte data[];
    synchronized(chunk.lock) {
      data = coder.encodeObject(chunk, true);
      chunk.dirty = false;
    }
    synchronized(fileLock) {
      ChunkKey key = ChunkKey.alloc(chunk.dim, ix, iz);
//      Static.log("saveChunk:" + chunk.dim + "," + chunk.cx + "," + chunk.cz);
      ChunkStore store = stores.get(key);
      if (store == null) {
        store = new ChunkStore();
        String fileName = Server.folderName + "/" + chunk.dim + "/";
        new File(fileName).mkdir();
        fileName += "c" + ix + "," + iz + ".dat";
        if (new File(fileName).exists()) {
          //load store from disk
          store.open(fileName,ix,iz);
        } else {
          //create new store for this range of chunks
          store.create(fileName,ix,iz);
        }
        stores.put(key.clone(), store);
      }
/*
      if (key.equals("0,0,0,0")) {  //an old test
        store.verify();
      }
*/
      key.free();
      store.saveChunk(data, chunk.cx, chunk.cz);
    }
  }

  public void closeStores() {
    try {
      synchronized(fileLock) {
        ChunkStore list[] = stores.values().toArray(new ChunkStore[0]);
        for(int a=0;a<list.length;a++) {
          list[a].raf.close();
        }
        stores.clear();
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  //server side only - purge inactive chunks
  public void purge(Player players[]) {
    Chunk list[] = getChunks();
    int range = Settings.current.loadRange * 2;
    for(int a=0;a<list.length;a++) {
      Chunk c = list[a];
      boolean inplay = false;
      for(int b=0;b<players.length;b++) {
        Player p = players[b];
        float dx = Math.abs(c.cx - (p.pos.x / 16.0f));
        float dz = Math.abs(c.cz - (p.pos.z / 16.0f));
        if (dx <= range && dz <= range) {
          inplay = true;
          break;
        }
      }
      if (inplay) continue;
      removeChunk(c);
      if (c.dirty) {
        saveChunk(c);
      }
    }
  }

  public void removeAll() {
    synchronized(lock) {
      cache.clear();
    }
  }
}
