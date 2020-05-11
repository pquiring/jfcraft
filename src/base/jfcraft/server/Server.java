package jfcraft.server;

/** Server for one world.
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.io.*;
import java.net.*;
import java.util.*;

import javaforce.*;

import jfcraft.audio.*;
import jfcraft.block.*;
import jfcraft.item.*;
import jfcraft.client.*;
import jfcraft.dim.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.packet.*;
import static jfcraft.data.Direction.*;

public class Server {
  public World world;

  public int dims[] = new int[Dims.MAX_ID];

  public ArrayList<Client> clients = new ArrayList<Client>();
  public Object clientsLock = new Object();  //lock to remove client
  public boolean active = true;

  private Timer tickTimer, lightTimer, saveTimer;
  public ChunkQueueLight chunkLighter;
  private static SerialCoder coder = new SerialCoder();  //used to save/load Player (sync)
  private ServerSocket ss;
  private VoIPServer voip_server;

  public static String folderName;

  public String errmsg;

  public Server() {
    Static.server = this;
  }

  /** Starts server and create new world. */
  public boolean createWorld(String worldName, WorldOptions options) {
    Static.log("Creating world:" + worldName);
    world = new World(true);
    world.init();
    world.chunks = new Chunks(world);
    world.name = worldName;
    world.type = "default";
    world.options = options;
    world.assignIDs();
    Static.dims.init();
    folderName = world.createFolderName(worldName);
    new File(folderName).mkdirs();
    if (new File(folderName + "/world.dat").exists()) {
      //world already exists???
      errmsg = "World already exists?";
      return false;
    }
    world.save(folderName + "/world.dat");
    if (!world.lock(folderName + "/lock.tmp")) {
      errmsg = "World is in use";
      return false;
    }
    startWorld();
    return true;
  }

  public boolean startWorld(String _folderName) {
    folderName = _folderName;
    int idx = folderName.lastIndexOf("/");
    Static.log("Starting world:" + folderName.substring(idx+1));
    world = World.load(folderName + "/world.dat", false);
    if (world == null) {
      errmsg = "Failed to load world?";
      return false;
    }
    if (!world.lock(folderName + "/lock.tmp")) {
      errmsg = "World is in use";
      return false;
    }
    world.init();
    world.chunks = new Chunks(world);
    world.assignIDs();
    Static.dims.init();
    startWorld();
    return true;
  }

  private void startWorld() {
    Static.dims.resetAll();  //reset all generators
    Static.initNoises(world);
    new File(folderName + "/players").mkdirs();
    if (!world.genSpawnAreaDone) {
      new Thread() {
        public void run() {
          genSpawnArea();
        }
      }.start();
    } else {
      startTimers();
    }
  }

  private void startTimers() {
    Static.log("Server.startTimers()");
    tickTimer = new Timer();
    tickTimer.scheduleAtFixedRate(new TimerTask() {
      private boolean initThread = true;
      public void run() {
        if (initThread) {
          initTimer("Server ticks", true);
          initThread = false;
        }
        try {
          long start = System.currentTimeMillis();
          doTick();
          long stop = System.currentTimeMillis();
          long diff = (stop - start);
          if (Static.debugProfile && diff > 50) {
            Static.log("server tick:" + diff + "ms : nChunks=" + nChunks + ",nThings=" + nThings + ",p=" + (p2-p1) + "," + (p3-p2) + "," + (p4-p3) + "," + (p5-p4) + "," + (p6-p5));
          }
          Static.tick = (int)diff;
        } catch (Throwable t) {
          Static.log(t);
        }
      }
    }, 50, 50);
    chunkLighter = new ChunkQueueLight(null, false);
    lightTimer = new Timer();
    lightTimer.scheduleAtFixedRate(new TimerTask() {
      private boolean initThread = true;
      public void run() {
        if (initThread) {
          initTimer("Server lighting timer", true);
          initThread = false;
        }
        try {
          chunkLighter.signal();
          chunkLighter.process();
        } catch (Throwable t) {
          Static.log(t);
        }
      }
    }, 50, 50);
    saveTimer = new Timer();
    saveTimer.schedule(new TimerTask() {
      private boolean initThread = true;
      public void run() {
        if (initThread) {
          initTimer("Server chunk saver", true);
          initThread = false;
        }
        doSave();
        doPurge();
      }
    }, 5000, 5000);
    chunkWorker = new ChunkWorker();
    chunkWorker.start();
    teleportWorker = new TeleportWorker();
    teleportWorker.start();
    if (Static.iface != null) Static.iface.ready();
  }

  private void stopTimers() {
    Static.log("Server.stopTimers()");
    if (tickTimer != null) {
      tickTimer.cancel();
      tickTimer = null;
    }
    if (chunkLighter != null) {
      chunkLighter = null;
    }
    if (lightTimer != null) {
      lightTimer.cancel();
      lightTimer = null;
    }
    if (saveTimer != null) {
      saveTimer.cancel();
      saveTimer = null;
    }
  }

  private void genSpawnArea() {
    initThread("Server generate spawn area", true);
    Static.log("Generating spawn area...");
    long p1 = System.currentTimeMillis();
    world.chunks.getChunk2(0, 0, 0, true, true, true);
    world.genSpawnAreaDone = true;
    long p2 = System.currentTimeMillis();
    long diff = p2-p1;
    float sec = diff;
    sec /= 1000.0f;
    Static.log("Generated spawn area in " + sec + " seconds");
    startTimers();
    broadcastGenSpawnAreaDone(100);
    Static.log("Thread ended:" + Thread.currentThread().getName());
  }

  public void close() {
    Static.log("Server shutdown ... 1");
    stopNetworking();
    Static.log("Server shutdown ... 2");
    //close all client connections
    int cnt = clients.size();
    for(int a=0;a<cnt;a++) {
      clients.get(a).serverTransport.logout();
      clients.get(a).serverTransport.close();
    }
    Static.log("Server shutdown ... 3");
    stopTimers();
    Static.log("Server shutdown ... 4");
    //stop Workers
    chunkWorker.close();
    chunkWorker = null;
    teleportWorker.close();
    teleportWorker = null;
    Static.log("Server shutdown ... 5");
    //finalize save to disk
    doSave();
    Static.log("Server shutdown ... 6");
    world.chunks.closeStores();  //close all files
    world.unlock();
    Static.log("Server shutdown ... 7");
    Static.server = null;
    Static.log("Server shutdown complete");
  }

  public void initThread(String name, boolean stdout) {
    Static.initServerThread(name, stdout, false);
  }

  public void initTimer(String name, boolean stdout) {
    Static.initServerThread(name, stdout, true);
  }

  public void addClient(ServerTransport serverTransport, Client client) {
    //assume already logged in for now
//    client.chunks = chunks;  //might need later (broadcast?)
    Static.log("addClient:" + client);
    synchronized(clientsLock) {
      clients.add(client);
    }
    client.world = world;
    serverTransport.start();
  }

  public void removeClient(Client client) {
    Static.log("removeClient:" + client.player != null ? client.player.name : "???");
    if (Static.iface != null) {
      Static.iface.clientDropped(client.name);
    }
    synchronized(clientsLock) {
      clients.remove(client);
      if (client.player != null && !client.player.offline) {
        dims[client.player.dim]--;
      }
    }
    if (client.player != null) {
      Chunk chunk = client.player.getChunk();
      if (chunk != null) {
        chunk.delEntity(client.player);
      }
      broadcastEntityDespawn(client.player);
    }
    savePlayer(client.player);
  }

  public String getClients() {
    StringBuilder sb = new StringBuilder();
    synchronized(clientsLock) {
      for(int a=0;a<clients.size();a++) {
        sb.append(clients.get(a).name);
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  /** Starts listening for clients on TCP port */
  public void startNetworking() {
    Static.log("Starting Game Server on TCP port " + Settings.current.tcpPort);
    if (ss != null) return;
    active = true;
    try {
      ss = new ServerSocket(Settings.current.tcpPort);
      new Thread() {
        /** Listens for clients in separate thread. */
        public void run() {
          try {
            while (active) {
              Socket s = ss.accept();
              NetworkServerTransport transport = new NetworkServerTransport();
              Client client = new Client(transport);
              client.isLocal = false;
              transport.init(Server.this, s, client);
              addClient(transport, client);
            }
          } catch (Exception e) {
            Static.log(e);
          }
        }
      }.start();
    } catch (Exception e) {
      Static.log(e);
    }
    if (Settings.current.server_voip) {
      startVoIPServer();
    }
  }

  public void stopNetworking() {
    if (ss == null) return;
    active = false;
    try {
      ss.close();
      ss = null;
    } catch (Exception e) {
      Static.log(e);
    }
    if (Settings.current.server_voip) {
      stopVoIPServer();
    }
  }

  private void startVoIPServer() {
    voip_server = new VoIPServer();
    voip_server.start();
  }

  private void stopVoIPServer() {
    if (voip_server != null) {
      voip_server.stop();
      voip_server = null;
    }
  }

  public void broadcastGenSpawnAreaDone(int percent) {
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        client.serverTransport.genSpawnAreaDone(percent);
      }
    }
  }

  public void broadcastTime() {
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        client.serverTransport.setTime(world.time);
      }
    }
  }

  public void broadcastChunk(Chunk chunk) {
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != chunk.dim) continue;
        if (!client.hasChunk(chunk.cx, chunk.cz)) continue;
        client.serverTransport.sendChunk(chunk);
      }
    }
  }

  public void broadcastSetBlock(int dim,float x, float y, float z,char id,int bits) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Packet update = new PacketSetBlock(Packets.SETBLOCK, cx,cz,gx,gy,gz,id << 16 | bits);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
    world.addTick(dim,x, y, z);
    world.addTick(dim,x+1, y, z);
    world.addTick(dim,x-1, y, z);
    world.addTick(dim,x, y+1, z);
    world.addTick(dim,x, y-1, z);
    world.addTick(dim,x, y, z+1);
    world.addTick(dim,x, y, z-1);
  }

  public void broadcastClearBlock(int dim,float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Packet update = new PacketClearBlock(Packets.CLEARBLOCK, cx,cz,gx,gy,gz, true);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
    world.addTick(dim,x, y, z);
    world.addTick(dim,x+1, y, z);
    world.addTick(dim,x-1, y, z);
    world.addTick(dim,x, y+1, z);
    world.addTick(dim,x, y-1, z);
    world.addTick(dim,x, y, z+1);
    world.addTick(dim,x, y, z-1);
  }

  public void broadcastClearBlock2(int dim,float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    Packet update = new PacketClearBlock2(Packets.CLEARBLOCK2, cx,cz,gx,gy,gz, true);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
    world.addTick(dim,x, y, z);
    world.addTick(dim,x+1, y, z);
    world.addTick(dim,x-1, y, z);
    world.addTick(dim,x, y+1, z);
    world.addTick(dim,x, y-1, z);
    world.addTick(dim,x, y, z+1);
    world.addTick(dim,x, y, z-1);
  }

  public void broadcastExtra(int dim,float x, float y, float z,ExtraBase extra, boolean addTicks) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update;
    update = new PacketSetExtra(Packets.SETEXTRA, cx,cz,extra);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
    if (addTicks) {
      world.addTick(dim,x, y, z);
      world.addTick(dim,x+1, y, z);
      world.addTick(dim,x-1, y, z);
      if (y < 255) world.addTick(dim,x, y+1, z);
      if (y > 0) world.addTick(dim,x, y-1, z);
      world.addTick(dim,x, y, z+1);
      world.addTick(dim,x, y, z-1);
    }
  }

  public void broadcastDelExtra(int dim,float x, float y, float z,byte type, boolean addTicks) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update;
    update = new PacketDelExtra(Packets.DELEXTRA,x,y,z,type);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
    if (addTicks) {
      world.addTick(dim,x, y, z);
      world.addTick(dim,x+1, y, z);
      world.addTick(dim,x-1, y, z);
      if (y < 255) world.addTick(dim,x, y+1, z);
      if (y > 0) world.addTick(dim,x, y-1, z);
      world.addTick(dim,x, y, z+1);
      world.addTick(dim,x, y, z-1);
    }
  }

  public void broadcastAddCrack(int dim,float x, float y, float z,float level) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    int gx = Static.floor(x % 16.0f);
    if (x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(y);
    int gz = Static.floor(z % 16.0f);
    if (z < 0 && gz != 0) gz = 16 + gz;
    ExtraCrack crack = new ExtraCrack();
    crack.x = (short)gx;
    crack.y = (short)gy;
    crack.z = (short)gz;
    crack.dmg = level;
    Packet update = new PacketSetExtra(Packets.SETEXTRA, cx,cz,crack);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastDelExtra(int dim,float x, float y, float z, byte type) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update = new PacketDelExtra(Packets.DELEXTRA, x,y,z, type);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntityFlags(EntityBase entity) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    int gx = Static.floor(entity.pos.x % 16.0f);
    if (entity.pos.x < 0 && gx != 0) gx = 16 + gx;
    int gz = Static.floor(entity.pos.z % 16.0f);
    if (entity.pos.z < 0 && gz != 0) gz = 16 + gz;
    Packet update = new PacketSetFlags(Packets.SETFLAGS, entity.uid, entity.flags);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastSound(int dim,float x, float y, float z,int idx,int freq) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update = new PacketSound(Packets.SOUND, x,y,z,idx,freq);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntitySpawn(EntityBase entity) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    Packet update = new PacketSpawn(Packets.SPAWN, entity);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player == entity) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntityDespawn(EntityBase entity) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    Packet update = new PacketDespawn(Packets.DESPAWN, entity.uid);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player == entity) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntityMove(EntityBase e, boolean self) {
    int cx = Static.floor(e.pos.x / 16.0f);
    int cz = Static.floor(e.pos.z / 16.0f);
    Packet update = new PacketMove(Packets.MOVE,
      e.pos.x, e.pos.y, e.pos.z, e.ang.x, e.ang.y, e.ang.z,
      e.uid, e.mode
    );
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player == e && !self) continue;
        if (client.player.dim != e.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntityHealth(CreatureBase entity) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    Packet update = new PacketHealth(Packets.HEALTH, entity.uid, entity.health);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastEntityArmor(HumaniodBase entity, byte idx) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    Packet update = new PacketEntityArmor(Packets.ENTITY_ARMOR, entity.uid, entity.armors[idx], idx);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player == entity) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastWorldItemSetCount(WorldItem entity) {
    int cx = Static.floor(entity.pos.x / 16.0f);
    int cz = Static.floor(entity.pos.z / 16.0f);
    Packet update = new PacketWorldItemSetCount(Packets.WORLDITEM_SET_COUNT, entity.uid, entity.item.count);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != entity.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastRiding(VehicleBase v, CreatureBase o, boolean mount) {
    int cx = Static.floor(v.pos.x / 16.0f);
    int cz = Static.floor(v.pos.z / 16.0f);
    Packet update = new PacketRiding(Packets.RIDING, v.uid, o.uid, mount);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != v.dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastContainerChange(ExtraContainer container, int cx, int cz) {
    Packet update = new PacketSetExtra(Packets.SETEXTRA, cx,cz,container);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.container != container) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastSetContainerItem(byte idx, ExtraContainer container) {
    Packet update = new PacketSetContainerItem(Packets.SETCONTAINERITEM, idx, container.items[idx]);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.container != container) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastB2E(int dim, int x,int y,int z, int uid) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update = new PacketB2E(Packets.B2E, x, y, z, uid);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastE2B(int dim, float x, float y, float z,int uid) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update = new PacketE2B(Packets.E2B, uid);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void broadcastMoveBlock(int dim, int uid, float x, float y, float z) {
    int cx = Static.floor(x / 16.0f);
    int cz = Static.floor(z / 16.0f);
    Packet update = new PacketMoveBlock(Packets.MOVEBLOCK, x,y,z, uid);
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null) continue;
        if (client.player.dim != dim) continue;
//        if (!client.hasChunk(cx, cz)) continue;
        int dx = Static.floor(client.player.pos.x / 16.0f) - cx;
        int dz = Static.floor(client.player.pos.z / 16.0f) - cz;
        if (dx > Static.maxLoadRange || dx < -Static.maxLoadRange) continue;
        if (dz > Static.maxLoadRange || dz < -Static.maxLoadRange) continue;
        client.serverTransport.addUpdate(update);
      }
    }
  }

  public void updateCrafted(Client client) {
    Item crafted = null;
    if (client.menu == Client.INVENTORY) crafted = Static.recipes.make2x2(client.craft);
    if (client.menu == Client.CRAFTTABLE) crafted = Static.recipes.make3x3(client.craft);
    client.serverTransport.setCraftedItem(crafted);
  }

  private Random r = new Random();

  public int nextInt(int range) {
    return r.nextInt(range);
  }

  private int nChunks, nThings;
  private long p1,p2,p3,p4,p5,p6;

  private int bedticks;

  private void doTick() {
    world.time++;
    if (world.time == 24000) world.time = 0; //midnight
    //do spawning
    boolean doSpawn = Static.spawn && world.time % 2000 == 0;
    //do chunk ticks
    Chunk list[] = world.chunks.getChunks();
    nChunks = list.length;
    for(int a=0;a<list.length;a++) {
      Chunk chunk = list[a];
      if (!chunk.canRender()) continue;
      chunk.doTicks();
      //spawn entities every 2 hours
      if (doSpawn) {
        DimBase db = Static.dims.dims[chunk.dim];
        if (db == null) continue;
        db.spawnMonsters(chunk);
      }
    }
    //check if bedtime
    synchronized(clientsLock) {
      int cnt = clients.size();
      int out = 0;  //other dimension or offline
      int bed = 0;
      for(int a=0;a<cnt;a++) {
        Client client = clients.get(a);
        if (client.player == null || client.player.dim != 0 || client.player.offline) {
          out++;
        }
        else if (client.menu == Client.BED) {
          bed++;
        }
      }
      if (bed > 0 && cnt > 0 && cnt == bed+out) {
        bedticks++;
        if (bedticks == 5 * 20) {
          //everyone is in bed
          world.time = 6000;  //set time to morning
          broadcastTime();
          for(int a=0;a<cnt;a++) {
            Client client = clients.get(a);
            client.serverTransport.leaveMenu();
            client.menu = Client.GAME;
          }
          bedticks = 0;
        } else {
          for(int a=0;a<cnt;a++) {
            Client client = clients.get(a);
            client.serverTransport.setBedTime(bedticks);
          }
        }
      } else {
        bedticks = 0;
        if (world.time == 0) {
          //broadcast midnight to keep clients in sync
          broadcastTime();
        }
      }
    }
  }

  private synchronized void doSave() {
    Chunk chunkList[] = world.chunks.getChunks();
    for(int a=0;a<chunkList.length;a++) {
      if (chunkList[a].dirty) {
        world.chunks.saveChunk(chunkList[a]);
      }
    }
    int cnt = clients.size();
    for(int a=0;a<cnt;a++) {
      Player player = clients.get(a).player;
      if (player != null) savePlayer(player);
    }
    world.save(folderName + "/world.dat");
  }

  private void doPurge() {
    world.chunks.purge(getPlayers());
  }

  public Player createPlayer(String name) {
    Player player = new Player();
    player.ang.y = 180.0f;
    player.name = name;
    player.health = 20;
    player.hunger = 20;
    player.init(world);
    return player;
  }

  public Player loadPlayer(String name) {
    try {
      synchronized(coder) {
        FileInputStream fis = new FileInputStream(folderName + "/players/" + name + ".dat");
        byte data[] = JF.readAll(fis);
        fis.close();
        Player player = (Player)coder.decodeObject(data, Static.entities, true);
        player.init(world);
        return player;
      }
    } catch (FileNotFoundException e) {
      //do nothing
    } catch (Exception e) {
      Static.log(e);
    }
    return null;
  }

  public void savePlayer(Player player) {
    if (player == null) return;
//    Static.log("savePlayer:" + player.name);
    try {
      synchronized(coder) {
        byte data[] = coder.encodeObject(player, true);
        FileOutputStream fos = new FileOutputStream(folderName + "/players/" + player.name + ".dat");
        fos.write(data);
        fos.close();
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void spawnPlayer(EntityBase player) {
    //TODO : random position a little
    player.dim = 0;
    player.pos.x = world.spawn.x;
    player.pos.y = world.spawn.y;
    player.pos.z = world.spawn.z;
    //TODO : if in water keep moving north until we hit land
    //  or create a small island
    int cx, cz;
    do {
      cx = Static.floor(player.pos.x / 16.0f);
      cz = Static.floor(player.pos.z / 16.0f);
      world.chunks.getChunk2(player.dim, cx, cz, true, true, false);
      if (player.inBlock(0, 0, 0, false, 0, EntityBase.AVOID_NONE) == 0) break;
      player.pos.y += 1.0f;
      if (player.pos.y > 250) break;  //OHOH
    } while (true);
    do {
      cx = Static.floor(player.pos.x / 16.0f);
      cz = Static.floor(player.pos.z / 16.0f);
      world.chunks.getChunk2(player.dim, cx, cz, true, true, false);
      if (player.onGround(0, 0, 0, (char)0)) break;
      player.pos.y -= 0.5f;
      if (player.pos.y < 0) {
        //TODO : need a different chunk
        player.pos.y = 0;
        break;
      }
    } while (true);
  }

  public EntityBase findEntity(float x,float y,float z,float range,int id) {
    EntityBase list[] = world.getEntities();
    float d;
    for(int a=0;a<list.length;a++) {
      EntityBase e = list[a];
      if (e.offline) continue;
      if (e.id != id) continue;
      d = Static.abs(e.pos.x - x);
      if (d > range) continue;
      d = Static.abs(e.pos.y - y);
      if (d > range) continue;
      d = Static.abs(e.pos.z - z);
      if (d > range) continue;
      return e;
    }
    return null;
  }

  public ChunkWorker chunkWorker;

  public static class ChunkRequest {
    public int dim;
    public int cx, cz;
    public int x,y,z;  //update
    public ServerTransport transport;
    public Chunk chunk;
  }

  public static class ChunkWorker extends Thread {
    public Object lock = new Object();
    public boolean active = true;
    public ArrayList<ChunkRequest> queue = new ArrayList<ChunkRequest>();
    public void run() {
      World world = Static.server.world;
      Static.server.initThread("Server Chunk Generator", true);
      ChunkRequest request;
      while (active) {
        synchronized(lock) {
          if (queue.isEmpty()) {
            try {lock.wait();} catch (Exception e) {}
            continue;
          }
          request = queue.remove(0);
        }
        try {
          Chunk chunk = world.chunks.getChunk2(request.dim, request.cx, request.cz, true, true, true);
          request.transport.sendChunk(chunk);
        } catch (Exception e) {
          Static.log(e);
        }
      }
      Static.log("Thread ended:" + Thread.currentThread().getName());
    }
    public void close() {
      active = false;
      synchronized(lock) {
        lock.notify();
      }
    }
    public void add(int dim, int cx, int cz, ServerTransport transport) {
      ChunkRequest req = new ChunkRequest();
      req.dim = dim;
      req.cx = cx;
      req.cz = cz;
      req.transport = transport;
      synchronized(lock) {
        queue.add(req);
        lock.notify();
      }
    }
  }

  public String cleanString(String msg) {
    //TODO
    return msg;
  }

  private Item findItem(String name, int cnt) {
    if (name.startsWith("-")) return null;
    name = name.replaceAll("_", " ");
    for(int id=0;id<Static.items.items.length;id++) {
      ItemBase base = Static.items.items[id];
      if (base == null) continue;
      if (base.cantGive) continue;
      if (base.isVar) {
        String names[] = base.getNames();
        for(int var=0;var<names.length;var++) {
          if (names[var].equalsIgnoreCase(name)) {
            return new Item((char)id, var, cnt);
          }
        }
      } else {
        if (base.getName(0).equalsIgnoreCase(name)) {
          if (base.isDamaged) {
            return new Item((char)id, 1.0f);
          } else {
            return new Item((char)id, 0, cnt);
          }
        }
      }
      if (base.name.equalsIgnoreCase(name)) {
        if (base.isDamaged) {
          return new Item((char)id, 1.0f);
        } else {
          return new Item((char)id, 0, cnt);
        }
      }
    }
    return null;
  }

  private EntityBase findEntity(String name) {
    name = name.replaceAll("_", " ");
    for(int id=0;id<Static.entities.entities.length;id++) {
      EntityBase base = Static.entities.entities[id];
      if (base == null) continue;
      if (base.getName().equalsIgnoreCase(name)) {
        return base;
      }
    }
    return null;
  }

  public void broadcastMsg(String msg) {
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client client2 = clients.get(a);
        client2.serverTransport.sendMsg(msg);
      }
    }
  }

  public void doCommand(Client client, String cmd) {
    String p[] = cmd.split(" ");
    if (p[0].equals("/give")) {
      // /give who what [count]
      if (p.length < 3) {
        client.serverTransport.sendMsg("/give who what [count]");
        return;
      }
      int cnt = 1;
      if (p.length >= 4) {
        cnt = JF.atoi(p[3]);
        if (cnt < 1 || cnt > 64) cnt = 1;
      }
      Item item = findItem(p[2], cnt);
      if (item == null) {
        client.serverTransport.sendMsg("Error:Item not found:" + p[2]);
      } else {
        Static.log("give:" + (int)item.id + ":" + cnt);
        client.addItem(item, true);
      }
    }
    else if (p[0].equals("/diamonds")) {
      doCommand(client, "/give @p diamond_sword");
      doCommand(client, "/give @p diamond_pickaxe");
      doCommand(client, "/give @p diamond_axe");
      doCommand(client, "/give @p diamond_shovel");
      doCommand(client, "/give @p diamond_hoe");
      doCommand(client, "/give @p diamond_chest");
      doCommand(client, "/give @p diamond_pants");
      doCommand(client, "/give @p diamond_boots");
      doCommand(client, "/give @p diamond_helmet");
      doCommand(client, "/give @p shield");
    }
    else if (p[0].equals("/delete")) {
      // /delete all entities in player's chunk (for debugging)
      Chunk chunk = client.player.getChunk();
      while(chunk.entities.size() > 0) {
        EntityBase e = (EntityBase)chunk.entities.get(0);
        chunk.delEntity(e);
        world.delEntity(e.uid);
        broadcastEntityDespawn(e);
      }
    }
    else if (p[0].equals("/tp")) {
      // /tp x y z
      if (p.length != 4) return;
      float x = JF.atof(p[1]);
      float y = JF.atof(p[2]);
      float z = JF.atof(p[3]);
      client.player.pos.x = x;
      client.player.pos.y = y;
      client.player.pos.z = z;
      broadcastEntityMove(client.player, true);
    }
    else if (p[0].equals("/time")) {
      //time #
      int time = JF.atoi(p[1]);
      if (time < 0 || time > 23999) return;
      world.time = time;
      broadcastTime();
    }
    else if (p[0].equals("/healme")) {
      client.player.health = 20;
      client.player.hunger = 20;
      client.player.saturation = 20;
      client.serverTransport.sendHealth(client.player);
      client.serverTransport.sendHunger(client.player);
      client.serverTransport.sendMsg("Health/hunger restored");
    }
    else if (p[0].equals("/clear")) {
      int cnt = 0;
      for(int a=0;a<client.player.items.length;a++) {
        cnt += client.player.items[a].count;
        client.player.items[a].clear();
        client.serverTransport.setInvItem((byte)a, client.player.items[a]);
      }
      client.serverTransport.sendMsg("Removed " + cnt + " items");
    }
    else if (p[0].equals("/fill")) {
      // /fill x1 y1 z1 x2 y2 z2 id [...]
      if (p.length < 8) {
        client.serverTransport.sendMsg("/fill x1 y1 z1 x2 y2 z2 id [...]");
        return;
      }
      int x1 = Integer.valueOf(p[1]);
      int y1 = Integer.valueOf(p[2]);
      int z1 = Integer.valueOf(p[3]);
      int x2 = Integer.valueOf(p[4]);
      int y2 = Integer.valueOf(p[5]);
      int z2 = Integer.valueOf(p[6]);
      Item item = findItem(p[7], 1);
      if (item == null) {
        client.serverTransport.sendMsg("Error:Item not found:" + p[7]);
        return;
      }
      if (Static.isItem(item.id)) {
        client.serverTransport.sendMsg("Error:Can not fill with item");
        return;
      }
      BlockBase block = Static.blocks.blocks[item.id];
      if (block.isRedstone) {
        client.serverTransport.sendMsg("Error:Can not fill with complex blocks");
        return;
      }
      if (x2 < x1) {
        int t = x1;
        x1 = x2;
        x2 = t;
      }
      if (y2 < y1) {
        int t = y1;
        y1 = y2;
        y2 = t;
      }
      if (z2 < z1) {
        int t = z1;
        z1 = z2;
        z2 = t;
      }
      int w = x2 - x1 + 1;
      int h = y2 - y1 + 1;
      int d = z2 - z1 + 1;
      //chunk coords
      int cx1 = (int)Math.floor(x1 / 16f);
      int cz1 = (int)Math.floor(z1 / 16f);
      int cx2 = (int)Math.floor(x2 / 16f);
      int cz2 = (int)Math.floor(z2 / 16f);
      //chunks width count
      int cw = cx2 - cx1 + 1;
      //chunks depth count
      int cd = cz2 - cz1 + 1;
      //dimension
      int dim = client.player.dim;
      //dest
      int dx = 0;
      int dz = 0;
      //this chunk
      int tw = 16;
      int th = h;
      int td = 16;

      int gx1 = x1 % 16;
      if (x1 < 0 && gx1 != 0) gx1 = 16 + gx1;
      int gy1 = y1;
      int gz1 = z1 % 16;
      if (z1 < 0 && gz1 != 0) gz1 = 16 + gz1;

      int gx2 = x2 % 16;
      if (x2 < 0 && gx2 != 0) gx2 = 16 + gx2;
      int gy2 = y2;
      int gz2 = z2 % 16;
      if (z2 < 0 && gz2 != 0) gz2 = 16 + gz2;

//      Static.log("fill:" + x1 + "," + y1 + "," + z1 + ":" + x2 + "," + y2 + "," + z2);
//      Static.log("fill:" + gx1 + "," + gy1 + "," + gz1 + ":" + gx2 + "," + gy2 + "," + gz2);
//      Static.log("fill:" + cw + "," + cd);
      ChunkQueueLight queue = new ChunkQueueLight(null, false);
      for(int z=0;z<cd;z++) {
        for(int x=0;x<cw;x++) {
          Chunk chunk = world.chunks.getChunk(dim, cx1 + x, cz1 + z);
          if (chunk == null) {
            client.serverTransport.sendMsg("Fill failed : area not loaded");
            return;
          }
          dx = 0;
          dz = 0;
          tw = 16;
          th = h;
          td = 16;
          //calc edge cases
          if (x == 0) {
            dx = gx1;
            tw -= gx1;
          }
          if (x == cw-1) {
            tw -= 15 - gx2;
          }
          if (z == 0) {
            dz = gz1;
            td -= gz1;
          }
          if (z == cd-1) {
            td -= 15 - gz2;
          }
          if (block.isBlocks2)
            chunk.fill2(dx, y1, dz,  tw, th, td, item.id);
          else
            chunk.fill(dx, y1, dz,  tw, th, td, item.id);
          chunk.resetLights();
          queue.add(chunk, 0, 0, 0, 15, 255, 15);
          if (x == 0) {
            chunk.W.resetLights();
            queue.add(chunk.W, 0, 0, 0, 15, 255, 15);
          }
          if (x == cw - 1) {
            chunk.E.resetLights();
            queue.add(chunk.E, 0, 0, 0, 15, 255, 15);
          }
          if (z == 0) {
            chunk.N.resetLights();
            queue.add(chunk.N, 0, 0, 0, 15, 255, 15);
          }
          if (z == cd-1) {
            chunk.S.resetLights();
            queue.add(chunk.S, 0, 0, 0, 15, 255, 15);
          }
        }
      }
      Chunk chunks[] = queue.getQueue();
      queue.signal();
      queue.setMax(-1);
      queue.process();  //long process
      for(int a=0;a<chunks.length;a++) {
        broadcastChunk(chunks[a]);
      }
      client.serverTransport.sendMsg("Fill complete");
    }
    else if (p[0].equals("/spawn")) {
      if (p.length < 2) {
        client.serverTransport.sendMsg("/spawn name");
        return;
      }
      EntityBase base = findEntity(p[1]);
      if (base == null) {
        client.serverTransport.sendMsg("Entity not registered");
        return;
      }
      Chunk chunk = client.player.getChunk();
      if (chunk == null) return; //chunk not found ???
      EntityBase e = base.spawn(chunk);
      if (e == null) return;  //could not spawn ???
      e.uid = Static.server.world.generateUID();
      chunk.addEntity(e);
      Static.server.world.addEntity(e);
      Static.server.broadcastEntitySpawn(e);
      Static.log("spawn " + e.getName() + " @=" + e.pos.x + "," + e.pos.y + "," + e.pos.z + ":uid=" + e.uid);
    }
    else if (p[0].equals("/tame")) {
      EntityBase entity = client.player.vehicle;
      if (entity == null) {
        Static.log("Not on a horse (1)");
        return;
      }
      if (!(entity instanceof Horse)) {
        Static.log("Not on a horse (2)");
        return;
      }
      Horse horse = (Horse)entity;
      if (horse.isTamed()) {
        Static.log("Already tamed");
        return;
      }
      horse.tameCounter = Horse.tameCounterMax;
      Static.log("Horse is now tamed");
    }
    else if (p[0].equals("/export")) {
      // /export x1 y1 z1 x2 y2 z2 filename
      if (p.length != 8) {
        client.serverTransport.sendMsg("/export x1 y1 z1 x2 y2 z2 filename");
        return;
      }
      int x1 = Integer.valueOf(p[1]);
      int y1 = Integer.valueOf(p[2]);
      int z1 = Integer.valueOf(p[3]);
      int x2 = Integer.valueOf(p[4]);
      int y2 = Integer.valueOf(p[5]);
      int z2 = Integer.valueOf(p[6]);
      String filename = p[7];
      int t;
      if (y1 < 0) y1 = 0;
      if (y2 < 0) y2 = 0;
      if (y1 > 255) y1 = 255;
      if (y2 > 255) y2 = 255;
      if (x2 < x1) {t = x1; x1 = x2; x2 = t;}
      if (y2 < y1) {t = y1; y1 = y2; y2 = t;}
      if (z2 < z1) {t = z1; z1 = z2; z2 = t;}
      int w = x2 - x1 + 1;
      int h = y2 - y1 + 1;
      int d = z2 - z1 + 1;
      if (w > 256 || d > 256) {
        client.serverTransport.sendMsg("Export failed : area too large (max 256x256x256)");
        return;
      }
      BluePrint blueprint = new BluePrint();
      blueprint.readInit(w, h, d, world);
      //chunk coords
      int cx1 = (int)Math.floor(x1 / 16f);
      int cz1 = (int)Math.floor(z1 / 16f);
      int cx2 = (int)Math.floor(x2 / 16f);
      int cz2 = (int)Math.floor(z2 / 16f);
      //chunks width count
      int cw = cx2 - cx1 + 1;
      //chunks depth count
      int cd = cz2 - cz1 + 1;
      //dimension
      int dim = client.player.dim;
      //source
      int sx = 0;
      int sz = 0;
      //dest
      int dx = 0;
      int dz = 0;
      //this chunk
      int tw = 16;
      int th = h;
      int td = 16;

      int gx1 = x1 % 16;
      if (x1 < 0 && gx1 != 0) gx1 = 16 + gx1;
//      int gy1 = y1;
      int gz1 = z1 % 16;
      if (z1 < 0 && gz1 != 0) gz1 = 16 + gz1;

      int gx2 = x2 % 16;
      if (x2 < 0 && gx2 != 0) gx2 = 16 + gx2;
//      int gy2 = y2;
      int gz2 = z2 % 16;
      if (z2 < 0 && gz2 != 0) gz2 = 16 + gz2;

      for(int z=0;z<cd;z++) {
        dx = 0;
        for(int x=0;x<cw;x++) {
          Chunk chunk = world.chunks.getChunk(dim, cx1 + x, cz1 + z);
          if (chunk == null) {
            client.serverTransport.sendMsg("Export failed : area not loaded");
            return;
          }
          sx = 0;
          sz = 0;
          tw = 16;
          th = h;
          td = 16;
          //calc edge cases
          if (x == 0) {
            sx = gx1;
            tw -= gx1;
          }
          if (x == cw-1) {
            tw -= 15 - gx2;
          }
          if (z == 0) {
            sz = gz1;
            td -= gz1;
          }
          if (z == cd-1) {
            td -= 15 - gz2;
          }
          blueprint.readChunk(chunk,  sx, y1, sz,  dx, 0, dz,  tw, th, td);
          dx += tw;
        }
        dz += td;
      }
      new File(Static.getBasePath() + "/blueprints").mkdir();
      blueprint.save(Static.getBasePath() + "/blueprints/" + filename + ".blueprint");
      client.serverTransport.sendMsg("Export complete:" + filename);
    }
    else if (p[0].equals("/import")) {
      // /import x1 y1 z1 filename [orientations]
      //   orientations = [mirror] [rotate]
      //   mirror = mx mz
      //   rotate = r90 r180 r270
      if (p.length < 5) {
        client.serverTransport.sendMsg("/import x1 y1 z1 filename [mx | mz] [r90 | r180 | r270]");
        return;
      }
      int x1 = Integer.valueOf(p[1]);
      int y1 = Integer.valueOf(p[2]);
      int z1 = Integer.valueOf(p[3]);
      String filename = p[4];
      BluePrint blueprint = BluePrint.read(Static.getBasePath() + "/blueprints/" + filename + ".blueprint");
      if (blueprint == null) {
        client.serverTransport.sendMsg("BluePrint not found");
        return;
      }
      if (!blueprint.convertIDs(world)) {
        client.serverTransport.sendMsg("BluePrint import failed, missing ID:" + blueprint.missingID);
        return;
      }
      Static.log("blueprint:" + blueprint.X + "," + blueprint.Y + "," + blueprint.Z);
      for(int a=5;a<p.length;a++) {
        String or = p[a];
        if (or.equals("mx")) {
          blueprint.mirrorX();
        }
        else if (or.equals("mz")) {
          blueprint.mirrorZ();
        }
        else if (or.equals("r90")) {
          blueprint.rotateY(R90);
        }
        else if (or.equals("r180")) {
          blueprint.rotateY(R180);
        }
        else if (or.equals("r270")) {
          blueprint.rotateY(R270);
        }
      }
      int x2 = x1 + blueprint.X - 1;
      int y2 = y1 + blueprint.Y - 1;
      int z2 = z1 + blueprint.Z - 1;
      int w = x2 - x1 + 1;
      int h = y2 - y1 + 1;
      int d = z2 - z1 + 1;
      //chunk coords
      int cx1 = (int)Math.floor(x1 / 16f);
      int cz1 = (int)Math.floor(z1 / 16f);
      int cx2 = (int)Math.floor(x2 / 16f);
      int cz2 = (int)Math.floor(z2 / 16f);
      //chunks width count
      int cw = cx2 - cx1 + 1;
      //chunks depth count
      int cd = cz2 - cz1 + 1;
      //dimension
      int dim = client.player.dim;
      //source
      int sx = 0;
      int sz = 0;
      //dest
      int dx = 0;
      int dz = 0;
      //this chunk
      int tw = 16;
      int th = h;
      int td = 16;

      int gx1 = x1 % 16;
      if (x1 < 0 && gx1 != 0) gx1 = 16 + gx1;
      int gy1 = y1;
      int gz1 = z1 % 16;
      if (z1 < 0 && gz1 != 0) gz1 = 16 + gz1;

      int gx2 = x2 % 16;
      if (x2 < 0 && gx2 != 0) gx2 = 16 + gx2;
      int gy2 = y2;
      int gz2 = z2 % 16;
      if (z2 < 0 && gz2 != 0) gz2 = 16 + gz2;

      Static.log("import:" + x1 + "," + y1 + "," + z1 + ":" + x2 + "," + y2 + "," + z2);
      Static.log("import:" + gx1 + "," + gy1 + "," + gz1 + ":" + gx2 + "," + gy2 + "," + gz2);
      Static.log("import:" + cw + "," + cd);
      ChunkQueueLight queue = new ChunkQueueLight(null, false);
      for(int z=0;z<cd;z++) {
        sx = 0;
        for(int x=0;x<cw;x++) {
          Chunk chunk = world.chunks.getChunk(dim, cx1 + x, cz1 + z);
          if (chunk == null) {
            client.serverTransport.sendMsg("Import failed : area not loaded");
            return;
          }
          dx = 0;
          dz = 0;
          tw = 16;
          th = h;
          td = 16;
          //calc edge cases
          if (x == 0) {
            dx = gx1;
            tw -= gx1;
          }
          if (x == cw-1) {
            tw -= 15 - gx2;
          }
          if (z == 0) {
            dz = gz1;
            td -= gz1;
          }
          if (z == cd-1) {
            td -= 15 - gz2;
          }
          blueprint.writeChunk(chunk,  sx, 0, sz,  dx, y1, dz,  tw, th, td);
          chunk.resetLights();
          queue.add(chunk, 0, 0, 0, 15, 255, 15);
          if (x == 0) {
            chunk.W.resetLights();
            queue.add(chunk.W, 0, 0, 0, 15, 255, 15);
          }
          if (x == cw-1) {
            chunk.E.resetLights();
            queue.add(chunk.E, 0, 0, 0, 15, 255, 15);
          }
          if (z == 0) {
            chunk.N.resetLights();
            queue.add(chunk.N, 0, 0, 0, 15, 255, 15);
          }
          if (z == cd-1) {
            chunk.S.resetLights();
            queue.add(chunk.S, 0, 0, 0, 15, 255, 15);
          }
          sx += tw;
        }
        sz += td;
      }
      Chunk chunks[] = queue.getQueue();
      queue.signal();
      queue.setMax(-1);
      queue.process();  //long process
      for(int a=0;a<chunks.length;a++) {
        broadcastChunk(chunks[a]);
      }
      client.serverTransport.sendMsg("Import complete:" + filename);
    }
    else if (p[0].equals("/relight")) {
      //fix lighting in chunk that player is currently in
      int cx = Static.floor(client.player.pos.x / 16.0f);
      int cz = Static.floor(client.player.pos.z / 16.0f);
      Chunk chunk = world.chunks.getChunk(client.player.dim, cx, cz);
      ChunkQueueLight queue = new ChunkQueueLight(null, false);
      queue.add(chunk, 0, 0, 0, 15, 255, 15);
      queue.signal();
      queue.setMax(-1);
      queue.process();
      broadcastChunk(chunk);
      client.serverTransport.sendMsg("Chunk " + cx + "," + cz + " relight");
    } else {
      client.serverTransport.sendMsg("Unknown command");
    }
  }

  /** Checks if block is clear of all entities. */
  public boolean blockClear(Coords c) {
    EntityBase e[] = world.getEntities();
    float hx = c.x + 0.5f;
    float hy = c.y + 0.5f;
    float hz = c.z + 0.5f;
    for(int a=0;a<e.length;a++) {
      if (e[a].hitBox(hx, hy, hz, 0.5f,0.5f,0.5f)) return false;
    }
    return true;
  }

  public Client getClient(Player p) {
    synchronized(clientsLock) {
      int cnt = clients.size();
      for(int a=0;a<cnt;a++) {
        Client c = clients.get(a);
        if (c.player == p) return c;
      }
    }
    return null;
  }

  public Player[] getPlayers() {
    synchronized(clientsLock) {
      int cnt = clients.size();
      Player players[] = new Player[cnt];
      for(int a=0;a<cnt;a++) {
        players[a] = clients.get(a).player;
      }
      return players;
    }
  }

  public void teleport(EntityBase e, Coords c,int newdim) {
    //send entity to new dimension
    if (e.offline) {
      //this is normal (entity could touch multiple portal blocks)
      return;
    }
    if (e instanceof Player) {
      Player player = (Player)e;
      Client client = Static.server.getClient(player);
      client.player.offline = true;
      synchronized(clientsLock) {
        dims[client.player.dim]--;
      }
      client.serverTransport.teleport1();
    }
    c.chunk.delEntity(e);
    world.delEntity(e.uid);
    broadcastEntityDespawn(e);
    e.offline = true;
    //entity is now in limbo
    teleportWorker.add(TELEPORT, e, c, newdim);
  }

  public TeleportWorker teleportWorker;
  public static final int TELEPORT = 0;

  public static class TeleportRequest {
    public EntityBase e;
    public Coords c;
    public int newdim;
    public int type;
  }

  /** Teleporting can be a lengthy process so it is done in a special thread. */
  public static class TeleportWorker extends Thread {
    public Object lock = new Object();
    public boolean active = true;
    public ArrayList<TeleportRequest> queue = new ArrayList<TeleportRequest>();
    public void run() {
      Static.server.initThread("Server Teleport Worker", true);
      TeleportRequest req;
      while (active) {
        synchronized(lock) {
          if (queue.isEmpty()) {
            try {lock.wait();} catch (Exception e) {}
            continue;
          }
          req = queue.remove(0);
        }
        try {
          switch (req.type) {
            case TELEPORT: {
              teleport(req.e, req.c, req.newdim);
              break;
            }
          }
        } catch (Exception e) {
          Static.log(e);
        }
      }
      Static.log("Thread ended:" + Thread.currentThread().getName());
    }
    public void close() {
      active = false;
      synchronized(lock) {
        lock.notify();
      }
    }
    public void add(int type, EntityBase e, Coords c, int newdim) {
      Static.log("teleport:" + c);
      TeleportRequest req = new TeleportRequest();
      req.type = type;
      req.e = e;
      req.c = c;
      req.newdim = newdim;
      synchronized(lock) {
        queue.add(req);
        lock.notify();
      }
    }

    public void teleport(EntityBase e, Coords c, int newdim) {
      //move entity into portal
      e.pos.x = c.x + 0.5f;
      e.pos.z = c.z + 0.5f;
      //move entity to new dimension
      if (e.dim == 0)
        e.dim = newdim;
      else
        e.dim = 0;
      e.teleportTimer = 20;  //do not teleport again
      //find other portal, create if needed
      char id = c.chunk.getID(c.gx, c.gy, c.gz);
      BlockBase block = Static.blocks.blocks[id];
      block.teleport(e,c);
      if (e.id == Entities.PLAYER) {
        Player player = (Player)e;
        Client client = Static.server.getClient(player);
        client.serverTransport.teleport2(e);
      } else {
        e.offline = false;
      }
      Static.server.world.addEntity(e);
      Static.server.broadcastEntitySpawn(e);
    }
  }

  public void makeEndFountainPortal(Chunk chunk) {
    int y = (int)chunk.elev[7 * 16 + 7];
    y += 5;
    for(int x=5;x<=9;x++) {
      for(int z=5;z<=9;z++) {
        if (x == 5 && (z == 5 || z == 9)) continue;
        if (x == 9 && (z == 5 || z == 9)) continue;
        chunk.setBlock(x,y,z,Blocks.BEDROCK,0);
        broadcastSetBlock(1, x,y,z,Blocks.BEDROCK,0);
      }
    }
    y++;
    for(int x=4;x<=10;x++) {
      for(int z=4;z<=10;z++) {
        if (
          (x==5 && (z==5 || z==9)) ||
          (x==9 && (z==5 || z==9)) ||
          (x==4 && (z>=6 || z<=8)) ||
          (x==10 && (z>=6 || z<=8)) ||
          (z==4 && (x>=6 || x<=8)) ||
          (z==10 && (x>=6 || x<=8))
        )
        {
          chunk.setBlock(x,y,z,Blocks.BEDROCK,0);
          broadcastSetBlock(1, x,y,z,Blocks.BEDROCK,0);
          continue;
        }
        if (x == 7 && z == 7) continue;
        if (
          (x==6 && (z>=6 || z<=8)) ||
          (x==8 && (z>=6 || z<=8)) ||
          (x==7 && z==6) ||
          (x==7 && z==8)
        )
        {
          chunk.setBlock(x,y,z,Blocks.END_PORTAL,0);
          broadcastSetBlock(1,x,y,z,Blocks.END_PORTAL,0);
        }
      }
    }
    for(int a=0;a<3;a++) {
      y++;
      chunk.setBlock(7,y,7,Blocks.BEDROCK,0);
      broadcastSetBlock(1, 7,y,7,Blocks.BEDROCK,0);
    }
    EndPortal ep = new EndPortal();
    ep.setDiameter(5.0f);
    ep.init(world);
    ep.uid = world.generateUID();
    ep.dim = 1;
    ep.pos.x = 7.5f;
    ep.pos.z = 7.5f;
    ep.pos.y = y - 2.0f;
    ep.gx = 7;
    ep.gy = 7;
    ep.gz = y - 2;
    chunk.addEntity(ep);
    world.addEntity(ep);
    broadcastEntitySpawn(ep);
  }
}
