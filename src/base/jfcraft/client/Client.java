package jfcraft.client;

/** Holds game data for one client (player).
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.ui.*;
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
  public boolean leavebed = false;
  public Object lock = new Object();  //lock for inventory/hand
  public boolean loadedSpawnArea;
  public int spawnAreaChunksTodo;
  public int spawnAreaChunksDone;
  public boolean teleport;
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
  public static int ACTION_NONE = 7;

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
  public static byte HORSE = 24;

  public byte menu = GAME;  //current menu

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
    Static.initClientThread(name, stdout, false);
  }

  public void initTimer(String name, boolean stdout) {
    Static.initClientThread(name, stdout, true);
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
    chunkCopier = new ChunkQueueCopy();
    chunkBuilder = new ChunkQueueBuild(chunkCopier);
    if (Static.debugChunkThreads) chunkBuilder.start();
    chunkLighter = new ChunkQueueLight(chunkBuilder, true);
    if (Static.debugChunkThreads) chunkLighter.start();
    chunkBuilder.setChunkQueueLight(chunkLighter);
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
    if (Static.debugChunkThreads) {
      chunkBuilder.cancel();
      chunkBuilder = null;
      chunkLighter.cancel();
      chunkLighter = null;
    }
  }

  public ArrayList<CXCZ> loadedChunks = new ArrayList<CXCZ>();  //server side

  public void loadChunk(int cx,int cz) {
    synchronized(loadedChunks) {
      loadedChunks.add(new CXCZ(cx,cz));
    }
  }

  public void unloadChunk(int cx,int cz) {
    synchronized(loadedChunks) {
      int cnt = loadedChunks.size();
      for(int a=0;a<cnt;a++) {
        CXCZ cc = loadedChunks.get(a);
        if (cc.cx == cx && cc.cz == cz) {
          loadedChunks.remove(a);
          return;
        }
      }
    }
  }

  public boolean hasChunk(int cx, int cz) {
    synchronized(loadedChunks) {
      int cnt = loadedChunks.size();
      for(int a=0;a<cnt;a++) {
        CXCZ cc = loadedChunks.get(a);
        if (cc.cx == cx && cc.cz == cz) {
          return true;
        }
      }
    }
    return false;
  }

  private ArrayList<CXCZ> pendingChunks = new ArrayList<CXCZ>();  //client side

  private boolean isChunkPending(int cx, int cz) {
    synchronized(pendingChunks) {
      for(int a=0;a<pendingChunks.size();a++) {
        CXCZ pc = pendingChunks.get(a);
        if (pc.cx == cx && pc.cz == cz) return true;
      }
    }
    return false;
  }

  public void removeChunkPending(int cx, int cz) {
    synchronized(pendingChunks) {
      for(int a=0;a<pendingChunks.size();a++) {
        CXCZ pc = pendingChunks.get(a);
        if (pc.cx == cx && pc.cz == cz) {
          pendingChunks.remove(a);
          return;
        }
      }
    }
  }

  public void look(float dx, float dy, float scale) {
    synchronized(ang) {
      ang.x += dy * scale;
      if (ang.x > 90f) ang.x = 90f;
      if (ang.x < -90f) ang.x = -90f;
      ang.y += dx * scale;
      while (ang.y > 360f) {
        ang.y -= 360f;
      }
      while (ang.y < 0f) {
        ang.y += 360f;
      }
    }
  }

  public void tick() {
    if (player == null) return;
    if (chatTime > 0) {
      chatTime--;
    }
    if (itemTextTime > 0) {
      itemTextTime--;
    }
    if (sip != null && Settings.current.ptt) {
      sip.setMute(!Static.keys[KeyCode.VK_CONTROL_R]);
    }
    //update animation frames
    Game.advanceAnimation = true;
    world.time++;
    if (world.time >= 24000) world.time = 0;
    //do keyboard input
    boolean up = false, dn = false, lt = false, rt = false, jump = false, sneak = false, run = false, fup = false, fdn = false;
    boolean b1 = false, b2 = false;
    if (world != null && player != null && player.hasChunk() && player.health > 0) {
      if (Static.inGame) {
        up = Static.keys[KeyCode.VK_W];
        dn = Static.keys[KeyCode.VK_S];
        lt = Static.keys[KeyCode.VK_A];
        rt = Static.keys[KeyCode.VK_D];
        jump = Static.keys[KeyCode.VK_SPACE];
        sneak = Static.keys[KeyCode.VK_SHIFT_L];
        run = Static.keys[KeyCode.VK_CONTROL_L];
        fup = Static.keys[KeyCode.VK_R];
        fdn = Static.keys[KeyCode.VK_F];
        if (Static.button[MouseButton.LEFT] || Static.buttonClick[MouseButton.LEFT]) b1 = true;
        Static.buttonClick[MouseButton.LEFT] = false;
        if (Static.button[MouseButton.RIGHT] || Static.buttonClick[MouseButton.RIGHT]) b2 = true;
        Static.buttonClick[MouseButton.RIGHT] = false;

        synchronized(ang) {
          player.ang.copy(ang);
        }

        //rotate player (client side only)
        if (Static.keys[KeyCode.VK_UP]) {
          player.rotateX(5.0f);
        }
        if (Static.keys[KeyCode.VK_DOWN]) {
          player.rotateX(-5.0f);
        }
        if (Static.keys[KeyCode.VK_LEFT]) {
          player.rotateY(-5.0f);
        }
        if (Static.keys[KeyCode.VK_RIGHT]) {
          player.rotateY(5.0f);
        }

        if (Static.keys[KeyCode.VK_Q]) {
          Static.keys[KeyCode.VK_Q] = false;
          clientTransport.drop();
        }
        if (Static.keys[KeyCode.VK_C]) {
          Static.keys[KeyCode.VK_C] = false;
          if (Static.debugTest) {
            clientTransport.gamemode();
            if (player.mode == EntityBase.MODE_FLYING)
              player.mode = EntityBase.MODE_WALK;
            else
              player.mode = EntityBase.MODE_FLYING;
          }
        }
      }
      if (player.vehicle == null) {
        synchronized(Static.clientMoveLock) {
          player.move(up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
        }
      }
    }
    //send tick to server
    clientTransport.tick(player, up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
    //tick entities
    Chunk chunks[] = world.chunks.getChunks();
    for(int c=0;c<chunks.length;c++) {
      Chunk chunk = chunks[c];
      if (chunk.isBorder()) continue;
      EntityBase e[] = chunk.getEntities();
      for(int a=0;a<e.length;a++) {
        e[a].ctick();
      }
    }
    //do hand item animation
    Item item = player.items[player.activeSlot];
    ItemBase itembase = Static.items.items[item.id];
    if (Static.inGame)
      itembase.animateItem(handAngle, handPos, b1, b2, selection != null, up | dn | lt | rt);
    else
      itembase.animateItem(handAngle, handPos, false, false, false, false);
    //tick environment
    Static.dims.dims[player.dim].getEnvironment().tick();
  }

  public void resetMoveKeys() {
    //reset movement keys if something bad happens
    Static.keys[KeyCode.VK_W] = false;
    Static.keys[KeyCode.VK_S] = false;
    Static.keys[KeyCode.VK_A] = false;
    Static.keys[KeyCode.VK_D] = false;
    Static.keys[KeyCode.VK_SPACE] = false;
    if (Static.debugTest) {
      Static.keys[KeyCode.VK_R] = false;
      Static.keys[KeyCode.VK_F] = false;
    }
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
    int dim,cx,cz;
    if (player == null) {
      return;
    }
    cx = Static.floor(player.pos.x / 16.0f);
    cz = Static.floor(player.pos.z / 16.0f);
    dim = player.dim;

    int loadRange = Settings.current.loadRange;
    int cnt = 0;
    int request = 0;
    int pendingChunk = 0;
    int _cx, _cz;
    for(int z=-loadRange;z<=loadRange;z++) {
      _cz = cz + z;
      for(int x=-loadRange;x<=loadRange;x++) {
        _cx = cx + x;
        cnt++;
        if (!world.chunks.hasChunk(dim, _cx, _cz)) {
          if (!isChunkPending(_cx, _cz)) {
            synchronized(pendingChunks) {
              pendingChunks.add(new CXCZ(_cx, _cz));
              clientTransport.loadChunk(_cx, _cz);
            }
            request++;
          } else {
            pendingChunk++;
          }
        }
      }
    }
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
    int maxDist = Settings.current.loadRange*2+2;
    for(int a=0;a<chunkList.length;a++) {
      Chunk chunk = chunkList[a];
      int dx = chunk.cx - cx;
      int dz = chunk.cz - cz;
      if (chunk.dim != dim
        || dx > maxDist || dx < -maxDist
        || dz > maxDist || dz < -maxDist
      ) {
        world.chunks.removeChunk(chunk);
        clientTransport.unloadChunk(chunk.cx, chunk.cz);  //let server know it's out of range
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
      if (player.armors[idx].id != 0) {
        clientTransport.armorExchange(idx);
      } else {
        clientTransport.armorPut(idx);
      }
    } else {
      if (player.armors[idx].count == 0) return;
      clientTransport.armorGet(idx);
    }
  }
  public void clickShield() {
    if (hand != null) {
      ItemBase itembase = Static.items.items[hand.id];
      if (itembase.id != Items.SHIELD) return;
      if (player.items[Player.shield_idx].id != 0) {
        clientTransport.invExchange(Player.shield_idx);
      } else {
        clientTransport.invPut(Player.shield_idx, (byte)1);
      }
    } else {
      if (player.items[Player.shield_idx].count == 0) return;
      clientTransport.invGet(Player.shield_idx, (byte)1);
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
    if (Static.keys[KeyCode.VK_SHIFT_L]) {
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
    e.init(world);
    e.dim = player.dim;
    e.uid = Static.server.world.generateUID();
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

  public ChunkQueueLight chunkLighter;
  public ChunkQueueBuild chunkBuilder;
  public ChunkQueueCopy chunkCopier;

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

  public int getUID() {
    if (player == null) return -1;
    return player.uid;
  }

  public void rebuildAll() {
    Chunk chunks[] = world.chunks.getChunks();
    for(int a=0;a<chunks.length;a++) {
      chunkBuilder.add(chunks[a]);
    }
  }
}
