package jfcraft.client;

/** Holds game data for one client (player).
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;
import java.awt.event.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import jfcraft.server.*;
import jfcraft.packet.*;

public class Client {
  public World world;  //client copy

  public VoIPClient sip;

  public String name, pass;
  public boolean auth = false;
  public boolean isLocal = false;
  public Player player;
  public XYZ ang = new XYZ();  //mouse angle (copied to player in tick)
  public ServerTransport serverTransport;
  public ClientTransport clientTransport;
  public Coords selection = new Coords();  //selection under mouse cursor
  public Coords s1 = new Coords(), s2 = new Coords();  //server side
  public ArrayList<String> chat = new ArrayList<String>();
  public int chatTime;
  public int itemTextTime;
  public int bedtime = 0;  //fade screen
  public Object lock = new Object();  //lock for inventory/hand
  public boolean loadedSpawnArea;
  public int spawnAreaChunksTodo;
  public int spawnAreaChunksDone;
  public boolean teleport;
  public int bowPower;
  public int foodCounter;
  public int placeCounter;
  public int underwaterCounter;
  public int cheat;

  public int action[] = new int[2];
  public static int ACTION_IDLE = 0;
  public static int ACTION_PLACE = 1;
  public static int ACTION_USE_BLOCK = 2;
  public static int ACTION_ATTACK = 3;
  public static int ACTION_USE_ITEM = 4;
  public static int ACTION_USE_ENTITY = 5;
  public static int ACTION_USE_TOOL = 6;

  //item in hand angle/pos
  public XYZ handAngle = new XYZ(), handPos = new XYZ();

  //menu types (static)
  public static byte GAME = 0;
  public static byte INVENTORY = 1;
  public static byte CRAFTTABLE = 2;
  public static byte FURNACE = 3;
  public static byte BED = 4;
  public static byte CHEST = 5;
  public static byte HOPPER = 7;
  public static byte CHAT = 8;
  public static byte CONFIRM = 9;
  public static byte CREATEWORLD = 10;
  public static byte DEAD = 11;
  public static byte LOADING = 12;
  public static byte LOADINGCHUNKS = 13;
  public static byte LOGIN = 14;
  public static byte MAIN = 15;
  public static byte MESSAGE = 16;
  public static byte MULTI = 17;
  public static byte SINGLE = 18;
  public static byte PAUSE = 19;
  public static byte WAIT = 20;
  public static byte DROPPER = 21;
  public static byte DISPENSER = 22;
  public static byte SIGN = 23;

  public byte menu = GAME;  //current menu

  public byte activeSlot;
  public Chunk chunk;
  public ExtraContainer container;
  public ExtraSign sign;
  public boolean openToLan;
  public boolean active;  //LOGIN/LOGOUT
  public Exception error;  //error to quit game
  public int spawnAreaDonePercent;

  public Item hand, craft[] = new Item[9], crafted;

  public ExtraCrack crack = new ExtraCrack();
  public int crack_cx, crack_cz;
  public int crackTicks;

  public int soundStep;

  public int aniDataIdx;  //animate item in hand

  private void init() {
    for(int a=0;a<craft.length;a++) {
      craft[a] = new Item();
    }
  }

  public void initThread(String name, boolean stdout) {
    Static.initClientThread(world, name, stdout, false);
  }

  public void initTimer(String name, boolean stdout) {
    Static.initClientThread(world, name, stdout, true);
  }

  public Client(ServerTransport transport) {
    this.serverTransport = transport;
    init();
  }

  public Client(ClientTransport transport) {
    this.clientTransport = transport;
    init();
  }

  private Timer tickTimer, chunkTimer;
  public Object chunkTimerAck;

  public void startTimers() {
    Static.log("Client.startTimers()");
    chunkTimerAck = new Object();
    tickTimer = new Timer();
    tickTimer.schedule(new TimerTask() {
      private boolean initThread = true;
      public void run() {
        if (initThread) {
          initTimer("Client ticks", true);
          initThread = false;
        }
        try {
          long start = System.currentTimeMillis();
          tick();
          long stop = System.currentTimeMillis();
          long diff = stop - start;
          if (diff > 50) {
            Static.log("client tick took " + diff + "ms");
          }
        } catch (Exception e) {
          Static.log(e);
        }
      }
    }, 50, 50);
    chunkTimer = new Timer();
    chunkTimer.schedule(new TimerTask() {
      private boolean initThread = true;
      public void run() {
        if (initThread) {
          initTimer("Client chunk requestor", true);
          initThread = false;
        }
        try {
          long start = System.currentTimeMillis();
          doChunks();
          long stop = System.currentTimeMillis();
          long diff = stop - start;
          if (diff > 100) {
            Static.log("client chunks took " + diff + "ms");
          }
      } catch (Exception e) {
          Static.log(e);
        }
      }
    }, 50, 50);
    chunkWorker = new ChunkWorker();
    chunkWorker.client = this;
    chunkWorker.start();
  }

  public void stopTimers() {
    Static.log("Client.stopTimers()");
    if (tickTimer != null) {
      tickTimer.cancel();
      tickTimer = null;
    }
    if (chunkTimer != null) {
      chunkTimer.cancel();
      chunkTimer = null;
    }
    if (chunkWorker != null) {
      chunkWorker.close();
      chunkWorker = null;
    }
  }

  private ArrayList<Coords> pendingChunks = new ArrayList<Coords>();

  private boolean isChunkPending(Coords c) {
    synchronized(pendingChunks) {
      for(int a=0;a<pendingChunks.size();a++) {
        Coords pc = pendingChunks.get(a);
        if (pc.x == c.x && pc.y == c.y && pc.z == c.z) return true;
      }
    }
    return false;
  }

  public void removeChunkPending(int cx, int cz) {
    synchronized(pendingChunks) {
      for(int a=0;a<pendingChunks.size();a++) {
        Coords pc = pendingChunks.get(a);
        if (pc.x == cx && pc.z == cz) {
          pendingChunks.remove(a);
          pc.free();
          return;
        }
      }
    }
  }

  public void look(float dx, float dy) {
    synchronized(ang) {
      ang.x += dy * 1.25f;
      if (ang.x > 90f) ang.x = 90f;
      if (ang.x < -90f) ang.x = -90f;
      ang.y += dx * 1.25f;
      while (ang.y > 360f) {
        ang.y -= 360f;
      }
      while (ang.y < 0f) {
        ang.y += 360f;
      }
    }
  }

  public void tick() {
    if (chatTime > 0) {
      chatTime--;
    }
    if (itemTextTime > 0) {
      itemTextTime--;
    }
    if (sip != null && Settings.current.ptt) {
      sip.setMute(!Static.r_keys[KeyEvent.VK_CONTROL]);
    }
    //update animation frames
    Game.advanceAnimation = true;
    world.time++;
    if (world.time >= 24000) world.time = 0;
    //do keyboard input
    boolean up = false, dn = false, lt = false, rt = false, jump = false, sneak = false, run = false, fup = false, fdn = false;
    boolean b1 = false, b2 = false;
    if (player.hasChunk()) {
      if (Static.inGame) {
        up = Static.keys[KeyEvent.VK_W];
        dn = Static.keys[KeyEvent.VK_S];
        lt = Static.keys[KeyEvent.VK_A];
        rt = Static.keys[KeyEvent.VK_D];
        jump = Static.keys[KeyEvent.VK_SPACE];
        sneak = Static.keys[KeyEvent.VK_SHIFT];
        run = Static.keys[KeyEvent.VK_CONTROL];
        fup = Static.keys[KeyEvent.VK_R];
        fdn = Static.keys[KeyEvent.VK_F];
        if (Static.button[1] || Static.buttonClick[1]) b1 = true;
        Static.buttonClick[1] = false;
        if (Static.button[3] || Static.buttonClick[3]) b2 = true;
        Static.buttonClick[3] = false;

        synchronized(ang) {
          player.ang.copy(ang);
        }

        //rotate player (client side only)
        if (Static.keys[KeyEvent.VK_UP]) {
          player.rotateX(5.0f);
        }
        if (Static.keys[KeyEvent.VK_DOWN]) {
          player.rotateX(-5.0f);
        }
        if (Static.keys[KeyEvent.VK_LEFT]) {
          player.rotateY(-5.0f);
        }
        if (Static.keys[KeyEvent.VK_RIGHT]) {
          player.rotateY(5.0f);
        }

        if (Static.keys[KeyEvent.VK_Q]) {
          Static.keys[KeyEvent.VK_Q] = false;
          clientTransport.drop();
        }
        if (Static.keys[KeyEvent.VK_C]) {
          Static.keys[KeyEvent.VK_C] = false;
          clientTransport.gamemode();
          player.flying = !player.flying;
        }
      }
      move(up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
    }
    //send tick to server
    clientTransport.tick(player, up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
    //tick entities
    EntityBase e[] = world.getEntities();
    for(int a=0;a<e.length;a++) {
      e[a].ctick();
    }
    //do hand item animation
    Item item = player.items[activeSlot];
    ItemBase itembase = Static.items.items[item.id];
    if (Static.inGame)
      itembase.animateItem(handAngle, handPos, b1, b2, selection != null, up | dn | lt | rt);
    else
      itembase.animateItem(handAngle, handPos, false, false, false, false);
  }

  private void doChunks() {
    //load more chunks as needed
    if (teleport) {
      //in limbo currently - do not load chunks yet
      synchronized(chunkTimerAck) {
        chunkTimerAck.notify();
      }
      return;
    }
    int cx = Static.floor(player.pos.x / 16.0f);
    int cz = Static.floor(player.pos.z / 16.0f);
    Coords c = Coords.alloc();
    int loadRange2 = Settings.current.loadRange / 2;
    int cnt = 0;
    int request = 0;
    int pendingChunk = 0;
    for(int z=-loadRange2;z<=loadRange2;z++) {
      c.z = cz + z;
      for(int x=-loadRange2;x<=loadRange2;x++) {
        c.x = cx + x;
        cnt++;
        if (!world.chunks.hasChunk(player.dim, c.x, c.z)) {
          if (!isChunkPending(c)) {
            synchronized(pendingChunks) {
              pendingChunks.add(c);
              clientTransport.getChunk(c);
            }
            request++;
            c = Coords.alloc();
            c.x = cx + x;
            c.z = cz + z;
          } else {
            pendingChunk++;
          }
        }
      }
    }
    c.free();
    if (request > 0) {
//      Static.log("client requested " + request + " chunks");
    }
    if (request == 0 && pendingChunk == 0) {
      loadedSpawnArea = true;
    } else {
      spawnAreaChunksTodo = cnt;
      spawnAreaChunksDone = cnt - request - pendingChunk;
    }
    //remove chunks that are +2 beyond loadRange
    Chunk chunkList[] = world.chunks.getChunks();
    int maxDist = Settings.current.loadRange+2;
    for(int a=0;a<chunkList.length;a++) {
      Chunk chunk = chunkList[a];
      int dx = chunk.cx - cx;
      int dz = chunk.cz - cz;
      if (chunk.dim != player.dim
        || dx > maxDist || dx < -maxDist
        || dz > maxDist || dz < -maxDist
      ) {
        world.chunks.removeChunk(chunk);
      }
    }
  }

  public void clickInventory(byte idx, boolean first) {
    if (player.items[idx].count == 0) {
      if (hand == null) {
        return;
      }
      //place item
      if (first || hand.count == 1) {
        //place all
        clientTransport.invPut(idx, hand.count);
      } else {
        //place 1
        clientTransport.invPut(idx, (byte)1);
      }
    } else {
      //pickup or exchange?
      if (hand == null) {
        //pickup
        if (first || player.items[idx].count == 1) {
          //pick all
          clientTransport.invGet(idx, player.items[idx].count);
        } else {
          //pick 1/2
          clientTransport.invGet(idx, (byte)(player.items[idx].count / 2));
        }
      } else {
        //if same put else exchange
        if (player.items[idx].equals(hand)) {
          if (first || hand.count == 1) {
            //put all
            clientTransport.invPut(idx, hand.count);
          } else {
            //put 1
            clientTransport.invPut(idx, (byte)1);
          }
        } else {
          clientTransport.invExchange(idx);
        }
      }
    }
  }
  public void clickArmor(byte idx, boolean first) {
    if (hand != null) {
      ItemBase itembase = Static.items.items[hand.id];
      if (!itembase.isArmor) return;
      if (player.armors[idx].id != -1) {
        clientTransport.armorExchange(idx);
      } else {
        clientTransport.armorPut(idx);
      }
    } else {
      if (player.armors[idx].count == 0) return;
      clientTransport.armorGet(idx);
    }
  }
  public void clickCraftlInput(byte idx, boolean first) {
    if (craft[idx].count == 0) {
      if (hand == null) {
        return;
      }
      //place item
      if (first || hand.count == 1) {
        //place all
        clientTransport.craftPut(idx, hand.count);
      } else {
        //place 1
        clientTransport.craftPut(idx, (byte)1);
      }
    } else {
      //pickup or exchange?
      if (hand == null) {
        //pickup
        if (first || craft[idx].count == 1) {
          //pick all
          clientTransport.craftGet(idx, craft[idx].count);
        } else {
          //pick 1/2
          clientTransport.craftGet(idx, (byte)(craft[idx].count / 2));
        }
      } else {
        //if same drop else exchange
        if (craft[idx].equals(hand)) {
          if (first || hand.count == 1) {
            //put all
            clientTransport.craftPut(idx, hand.count);
          } else {
            //put 1
            clientTransport.craftPut(idx, (byte)1);
          }
        } else {
          clientTransport.craftExchange(idx);
        }
      }
    }
  }
  public void clickCraftOutput(boolean first) {
    if (hand != null && crafted != null) {
      if (hand.id != crafted.id) return;
    }
    if (Static.keys[KeyEvent.VK_SHIFT]) {
      //make all possible
      clientTransport.craftAll();
    } else {
      if (hand != null) {
        ItemBase item = Static.items.items[hand.id];
        if (hand.count + 1 > item.maxStack) return;
      }
      //make one
      clientTransport.craftOne();
    }
  }
  public void clickContainer(byte idx, boolean first) {
    if (container.items[idx].count == 0) {
      if (hand == null) {
        return;
      }
      //place item
      if (first || hand.count == 1) {
        //place all
        clientTransport.containerPut(idx, hand.count);
      } else {
        //place 1
        clientTransport.containerPut(idx, (byte)1);
      }
    } else {
      //pickup or exchange?
      if (hand == null) {
        //pickup
        if (first || container.items[idx].count == 1) {
          //pick all
          clientTransport.containerGet(idx, container.items[idx].count);
        } else {
          //pick 1/2
          clientTransport.containerGet(idx, (byte)(container.items[idx].count / 2));
        }
      } else {
        //if same drop else exchange
        if (container.items[idx].equals(hand)) {
          if (first || hand.count == 1) {
            //put all
            clientTransport.containerPut(idx, hand.count);
          } else {
            //put 1
            clientTransport.containerPut(idx, (byte)1);
          }
        } else {
          clientTransport.containerExchange(idx);
        }
      }
    }
  }
  /** Adds item to player's inventory (returns false if no room) (server-side) */
  public boolean addItem(Item item, boolean update) {
    if (item == null) return false;
    ItemBase itembase = Static.items.items[item.id];
    if (itembase.maxStack > 1) {
      //see if we can stack it somewhere
      for(byte a=0;a<4*9;a++) {
        if (player.items[a].equals(item) && ((player.items[a].count + item.count) <= itembase.maxStack)) {
          player.items[a].count += item.count;
          if (update) serverTransport.setInvItem(a, player.items[a]);
          return true;
        }
      }
    }
    //add to any empty slot
    for(byte a=0;a<4*9;a++) {
      if (player.items[a].count == 0) {
        player.items[a].copy(item);
        if (update) serverTransport.setInvItem(a, player.items[a]);
        return true;
      }
    }
    Static.log("S:can not add item:" + (int)item.id);
    return false;
  }
  public boolean addItems(Item item[], boolean update) {
    for(int a=0;a<item.length;a++) {
      addItem(item[a], update);
    }
    return true;
  }
  private Vectors v = new Vectors();
  public void dropItem(Item item) {
    if (item.id == 0) return;
    WorldItem e;
    e = new WorldItem();
    e.setItem(item);
    e.init();
    e.dim = player.dim;
    e.uid = Static.world().generateUID();
    e.pos.x = player.pos.x;
    e.pos.y = player.pos.y + player.eyeHeight;
    e.pos.z = player.pos.z;
    player.calcVectors(0.25f, v);
    e.vel.x = v.forward.v[0];
    e.vel.y = Math.max(0, v.forward.v[1]);
    e.vel.z = v.forward.v[2];
//          Static.log("vel=" + e.xVelocity + "," + e.yVelocity + "," + e.zVelocity );
    Chunk chunk = player.getChunk();
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
  }
  public void dropItems(Item items[]) {
    for(int a=0;a<items.length;a++) {
      dropItem(items[a]);
    }
  }

  public void move(boolean up, boolean dn, boolean lt, boolean rt,
    boolean jump, boolean sneak, boolean run, boolean b1, boolean b2,
    boolean fup, boolean fdn)
  {
    if (player.vehicle != null) {
      player.vehicle.move(up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
    } else {
      player.move(up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
    }
  }

  public ChunkWorker chunkWorker;

  public static class ChunkWorker extends Thread {
    public Object lock = new Object();
    public boolean active = true;
    public Client client;
    public void run() {
      client.initThread("Client Chunk Worker", true);
      while (active) {
//        Static.log("ChunkWorker waiting");
        synchronized(lock) {
          try {lock.wait();} catch (Exception e) {}
        }
        if (!active) break;
        Chunk chunks[] = client.world.chunks.getChunks();
        try {
          client.world.chunks.doLightChunks = true;
          client.world.chunks.lightChunks(chunks);
          client.world.chunks.doBuildChunks = true;
          client.world.chunks.buildChunks(chunks);
        } catch (Exception e) {
          Static.log(e);
        }
        client.world.chunks.doCopyChunks = true;
      }
      Static.log("Thread ended:" + Thread.currentThread().getName());
    }
    public void close() {
      active = false;
      synchronized(lock) {
        lock.notify();
      }
    }
    public void process() {
      synchronized(lock) {
        lock.notify();
      }
    }
  }

  public void startVoIP(String server) {
    final String _server = server;
    sip = new VoIPClient();
    new Thread() {
      public void run() {
        sip.start(_server);
      }
    }.start();
  }

  public void stopVoIP() {
    if (sip != null) {
      sip.stop();
      sip = null;
    }
  }
}
