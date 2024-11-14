package jfcraft.data;

/**
 *
 * @author pquiring
 */

import java.lang.reflect.*;

import javaforce.*;
import javaforce.awt.*;

import jfcraft.packet.*;

public class Packets implements SerialCreator {
  public Packet packets[] = new Packet[128];

  private byte nextID = 0;

  public void registerPacket(Packet p, String name) {
    if (nextID < 0) {
      JFAWT.showError("Error", "Too many packets registered!");
      System.exit(1);
    }
    p.cmd = nextID++;
    packets[p.cmd] = p;
    setID(name, p.cmd);
  }

  public void registerDefault() {
    Static.log("Packets.registerDefault()");
    registerPacket(new PacketLoginRequest(), "LOGIN_REQUEST");  //must be 1st packet registered
    registerPacket(new PacketLoginReply(), "LOGIN_REPLY");
    registerPacket(new PacketLogout(), "LOGOUT");
    registerPacket(new PacketPlayerRequest(), "PLAYER_REQUEST");
    registerPacket(new PacketPlayer(), "PLAYER_REPLY");
    registerPacket(new PacketWorldRequest(), "WORLD_REQUEST");
    registerPacket(new PacketWorld(), "WORLD_REPLY");
    registerPacket(new PacketChunkRequest(), "CHUNK_REQUEST");
    registerPacket(new PacketChunk(), "CHUNK_REPLY");
    registerPacket(new PacketTick(), "TICK");
    registerPacket(new PacketSetActiveSlot(), "SETACTIVESLOT");
    registerPacket(new PacketRespawnRequest(), "RESPAWN_REQUEST");
    registerPacket(new PacketRespawn(), "RESPAWN");
    registerPacket(new PacketMsg(), "MSG");
    registerPacket(new PacketOnline(), "ONLINE");
    registerPacket(new PacketSetMode(), "SETMODE");
    registerPacket(new PacketPos(), "POS");
    registerPacket(new PacketInvPut(), "INVPUT");
    registerPacket(new PacketInvGet(), "INVGET");
    registerPacket(new PacketInvExchange(), "INVEXCHANGE");
    registerPacket(new PacketArmorPut(), "ARMORPUT");
    registerPacket(new PacketArmorGet(), "ARMORGET");
    registerPacket(new PacketArmorExchange(), "ARMOREXCHANGE");
    registerPacket(new PacketCraftPut(), "CRAFTPUT");
    registerPacket(new PacketCraftGet(), "CRAFTGET");
    registerPacket(new PacketCraftExchange(), "CRAFTEXCHANGE");
    registerPacket(new PacketCraftOne(), "CRAFTONE");
    registerPacket(new PacketCraftAll(), "CRAFTALL");
    registerPacket(new PacketContainerPut(), "CONTAINERPUT");
    registerPacket(new PacketContainerGet(), "CONTAINERGET");
    registerPacket(new PacketContainerExchange(), "CONTAINEREXCHANGE");
    registerPacket(new PacketDrop(), "DROP");
    registerPacket(new PacketMenuEnter(), "MENUENTER");
    registerPacket(new PacketMenuLeave(), "MENULEAVE");
    registerPacket(new PacketMenuInv(), "MENUINV");
    registerPacket(new PacketOpenToLan(), "OPENTOLAN");
    registerPacket(new PacketToggleGameMode(), "TOGGLEGAMEMODE");
    registerPacket(new PacketSetBlock(), "SETBLOCK");
    registerPacket(new PacketClearBlock(), "CLEARBLOCK");
    registerPacket(new PacketSetInv(), "SETINV");
    registerPacket(new PacketSetHand(), "SETHAND");
    registerPacket(new PacketSetArmor(), "SETARMOR");
    registerPacket(new PacketSetCraft(), "SETCRAFT");
    registerPacket(new PacketSetCrafted(), "SETCRAFTED");
    registerPacket(new PacketSetContainer(), "SETCONTAINER");
    registerPacket(new PacketSetContainerItem(), "SETCONTAINERITEM");
    registerPacket(new PacketMove(), "MOVE");
    registerPacket(new PacketSpawn(), "SPAWN");
    registerPacket(new PacketDespawn(), "DESPAWN");
    registerPacket(new PacketHealth(), "HEALTH");
    registerPacket(new PacketTime(), "TIME");
    registerPacket(new PacketBedTime(), "BEDTIME");
    registerPacket(new PacketB2E(), "B2E");
    registerPacket(new PacketE2B(), "E2B");
    registerPacket(new PacketMoveBlock(), "MOVEBLOCK");
    registerPacket(new PacketSetExtra(), "SETEXTRA");
    registerPacket(new PacketDelExtra(), "DELEXTRA");
    registerPacket(new PacketSound(), "SOUND");
    registerPacket(new PacketSetFlags(), "SETFLAGS");
    registerPacket(new PacketKnockBack(), "KNOCKBACK");
    registerPacket(new PacketGenSpawnArea(), "GENSPAWNAREA");
    registerPacket(new PacketClearBlock2(), "CLEARBLOCK2");
    registerPacket(new PacketTeleport1(), "TELEPORT1");
    registerPacket(new PacketTeleport2(), "TELEPORT2");
    registerPacket(new PacketHunger(), "HUNGER");
    registerPacket(new PacketAir(), "AIR");
    registerPacket(new PacketRiding(), "RIDING");
    registerPacket(new PacketMoveBack(), "MOVEBACK");
    registerPacket(new PacketEnderChest(), "ENDERCHEST");
    registerPacket(new PacketSetSign(), "SETSIGN");
    registerPacket(new PacketUseVehicleInventory(), "USEVEHICLEINVENTORY");
    registerPacket(new PacketEntityArmor(), "ENTITY_ARMOR");
    registerPacket(new PacketWorldItemSetCount(), "WORLDITEM_SET_COUNT");
    registerPacket(new PacketShield(), "SHIELD");
    registerPacket(new PacketBow(), "BOW");
    registerPacket(new PacketSetInvDmg(), "SETINVDMG");
    registerPacket(new PacketVillager(), "VILLAGER");
  }

  public static byte RIDING;
  public static byte SETMODE; //survival, creative, etc.
  public static byte RESPAWN;
  public static byte DROP;
  public static byte TOGGLEGAMEMODE;
  public static byte ENDERCHEST;
  public static byte HUNGER;
  public static byte ONLINE; //done loading chunks
  public static byte E2B;
  public static byte DELEXTRA;
  public static byte CHUNK_REQUEST;
  public static byte SOUND;
  public static byte CLEARBLOCK2;
  public static byte SPAWN;
  public static byte MENULEAVE;
  public static byte MOVEBACK;
  public static byte GENSPAWNAREA;
  public static byte ARMOREXCHANGE;
  public static byte SETARMOR;
  public static byte B2E;
  public static byte SETEXTRA;
  public static byte MENUENTER;
  public static byte CONTAINERGET;
  public static byte TELEPORT1; //basically go offline
  public static byte CONTAINERPUT;
  public static byte SETCONTAINERITEM;
  public static byte SETINV;
  public static byte CRAFTONE;
  public static byte LOGIN_REPLY;
  public static byte CHUNK_REPLY;
  public static byte SETFLAGS;
  public static byte LOGIN_REQUEST;
  public static byte INVGET;
  public static byte MENUINV;
  public static byte SETCONTAINER;
  public static byte INVPUT;
  public static byte AIR;
  public static byte WORLD_REQUEST;
  public static byte LOGOUT;
  public static byte PLAYER_REPLY;
  public static byte HEALTH;
  public static byte KNOCKBACK;
  public static byte CRAFTALL;
  public static byte POS;
  public static byte CLEARBLOCK;
  public static byte MOVEBLOCK;
  public static byte CONTAINEREXCHANGE;
  public static byte TELEPORT2; //set position and go online
  public static byte CRAFTEXCHANGE;
  public static byte ARMORGET;
  public static byte SETSIGN;
  public static byte USEVEHICLEINVENTORY;
  public static byte TICK;
  public static byte SETACTIVESLOT;
  public static byte ARMORPUT;
  public static byte SETBLOCK;
  public static byte CRAFTPUT; //personal craft slots
  public static byte SETCRAFTED;
  public static byte MOVE;
  public static byte ENTITY_ARMOR;
  public static byte PLAYER_REQUEST;
  public static byte CRAFTGET;
  public static byte DESPAWN;
  public static byte BEDTIME; //fade out
  public static byte MSG; //chat
  public static byte SETHAND;
  public static byte RESPAWN_REQUEST;
  public static byte TIME;
  public static byte OPENTOLAN;
  public static byte WORLD_REPLY;
  public static byte INVEXCHANGE;
  public static byte SETCRAFT;
  public static byte WORLDITEM_SET_COUNT;
  public static byte SHIELD;
  public static byte BOW;
  public static byte SETINVDMG;
  public static byte VILLAGER;

  public void setID(String name, byte cmd) {
    switch (name) {
      case "RIDING": RIDING = cmd; break;
      case "SETMODE": SETMODE = cmd; break;
      case "RESPAWN": RESPAWN = cmd; break;
      case "DROP": DROP = cmd; break;
      case "TOGGLEGAMEMODE": TOGGLEGAMEMODE = cmd; break;
      case "ENDERCHEST": ENDERCHEST = cmd; break;
      case "HUNGER": HUNGER = cmd; break;
      case "ONLINE": ONLINE = cmd; break;
      case "E2B": E2B = cmd; break;
      case "DELEXTRA": DELEXTRA = cmd; break;
      case "CHUNK_REQUEST": CHUNK_REQUEST = cmd; break;
      case "SOUND": SOUND = cmd; break;
      case "CLEARBLOCK2": CLEARBLOCK2 = cmd; break;
      case "SPAWN": SPAWN = cmd; break;
      case "MENULEAVE": MENULEAVE = cmd; break;
      case "MOVEBACK": MOVEBACK = cmd; break;
      case "GENSPAWNAREA": GENSPAWNAREA = cmd; break;
      case "ARMOREXCHANGE": ARMOREXCHANGE = cmd; break;
      case "SETARMOR": SETARMOR = cmd; break;
      case "B2E": B2E = cmd; break;
      case "SETEXTRA": SETEXTRA = cmd; break;
      case "MENUENTER": MENUENTER = cmd; break;
      case "CONTAINERGET": CONTAINERGET = cmd; break;
      case "TELEPORT1": TELEPORT1 = cmd; break;
      case "CONTAINERPUT": CONTAINERPUT = cmd; break;
      case "SETCONTAINERITEM": SETCONTAINERITEM = cmd; break;
      case "SETINV": SETINV = cmd; break;
      case "CRAFTONE": CRAFTONE = cmd; break;
      case "LOGIN_REPLY": LOGIN_REPLY = cmd; break;
      case "CHUNK_REPLY": CHUNK_REPLY = cmd; break;
      case "SETFLAGS": SETFLAGS = cmd; break;
      case "LOGIN_REQUEST": LOGIN_REQUEST = cmd; break;
      case "INVGET": INVGET = cmd; break;
      case "MENUINV": MENUINV = cmd; break;
      case "SETCONTAINER": SETCONTAINER = cmd; break;
      case "INVPUT": INVPUT = cmd; break;
      case "AIR": AIR = cmd; break;
      case "WORLD_REQUEST": WORLD_REQUEST = cmd; break;
      case "LOGOUT": LOGOUT = cmd; break;
      case "PLAYER_REPLY": PLAYER_REPLY = cmd; break;
      case "HEALTH": HEALTH = cmd; break;
      case "KNOCKBACK": KNOCKBACK = cmd; break;
      case "CRAFTALL": CRAFTALL = cmd; break;
      case "POS": POS = cmd; break;
      case "CLEARBLOCK": CLEARBLOCK = cmd; break;
      case "MOVEBLOCK": MOVEBLOCK = cmd; break;
      case "CONTAINEREXCHANGE": CONTAINEREXCHANGE = cmd; break;
      case "TELEPORT2": TELEPORT2 = cmd; break;
      case "CRAFTEXCHANGE": CRAFTEXCHANGE = cmd; break;
      case "ARMORGET": ARMORGET = cmd; break;
      case "SETSIGN": SETSIGN = cmd; break;
      case "USEVEHICLEINVENTORY": USEVEHICLEINVENTORY = cmd; break;
      case "TICK": TICK = cmd; break;
      case "SETACTIVESLOT": SETACTIVESLOT = cmd; break;
      case "ARMORPUT": ARMORPUT = cmd; break;
      case "SETBLOCK": SETBLOCK = cmd; break;
      case "CRAFTPUT": CRAFTPUT = cmd; break;
      case "SETCRAFTED": SETCRAFTED = cmd; break;
      case "MOVE": MOVE = cmd; break;
      case "ENTITY_ARMOR": ENTITY_ARMOR = cmd; break;
      case "PLAYER_REQUEST": PLAYER_REQUEST = cmd; break;
      case "CRAFTGET": CRAFTGET = cmd; break;
      case "DESPAWN": DESPAWN = cmd; break;
      case "BEDTIME": BEDTIME = cmd; break;
      case "MSG": MSG = cmd; break;
      case "SETHAND": SETHAND = cmd; break;
      case "RESPAWN_REQUEST": RESPAWN_REQUEST = cmd; break;
      case "TIME": TIME = cmd; break;
      case "OPENTOLAN": OPENTOLAN = cmd; break;
      case "WORLD_REPLY": WORLD_REPLY = cmd; break;
      case "INVEXCHANGE": INVEXCHANGE = cmd; break;
      case "SETCRAFT": SETCRAFT = cmd; break;
      case "WORLDITEM_SET_COUNT": WORLDITEM_SET_COUNT = cmd; break;
      case "SHIELD": SHIELD = cmd; break;
      case "BOW": BOW = cmd; break;
      case "SETINVDMG": SETINVDMG = cmd; break;
      case "VILLAGER": VILLAGER = cmd; break;
    }
  }

  public SerialClass create(SerialBuffer buffer) {
    byte cmd = buffer.peekByte();
//    Static.log(), ""packets.create(), ")cmd=" + cmd + ",name=" + packets[cmd].getClass(), ").getName(), "");
    try {
      Class<?> cls = packets[cmd].getClass();
      Constructor ctor = cls.getConstructor();
      return (Packet)ctor.newInstance();
    } catch (Exception e) {
      Static.log("Packet not found:" + cmd);
      e.printStackTrace();
      return null;
    }
  }
}
