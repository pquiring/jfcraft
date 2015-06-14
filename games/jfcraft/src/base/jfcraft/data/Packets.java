package jfcraft.data;

/**
 *
 * @author pquiring
 */

import java.lang.reflect.*;

import javaforce.*;

import jfcraft.packet.*;

public class Packets implements SerialCreator {
  public Packet packets[] = new Packet[128];

  private byte nextID = 0;

  public void registerPacket(Packet p, String name, Class cls) {
    if (nextID < 0) {
      JF.showError("Error", "Too many packets registered!");
      System.exit(1);
    }
    p.cmd = nextID++;
    packets[p.cmd] = p;
    try {
      Field f = cls.getField(name);
      f.setByte(null, p.cmd);
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void registerDefault() {
    Class cls = this.getClass();
    Static.log("Packets.registerDefault()");
    registerPacket(new PacketLoginRequest(), "LOGIN_REQUEST", cls);  //must be 1st packet registered
    registerPacket(new PacketLoginReply(), "LOGIN_REPLY", cls);
    registerPacket(new PacketLogout(), "LOGOUT", cls);
    registerPacket(new PacketPlayerRequest(), "PLAYER_REQUEST", cls);
    registerPacket(new PacketPlayer(), "PLAYER_REPLY", cls);
    registerPacket(new PacketWorldRequest(), "WORLD_REQUEST", cls);
    registerPacket(new PacketWorld(), "WORLD_REPLY", cls);
    registerPacket(new PacketChunkRequest(), "CHUNK_REQUEST", cls);
    registerPacket(new PacketChunk(), "CHUNK_REPLY", cls);
    registerPacket(new PacketTick(), "TICK", cls);
    registerPacket(new PacketSetActiveSlot(), "SETACTIVESLOT", cls);
    registerPacket(new PacketRespawnRequest(), "RESPAWN_REQUEST", cls);
    registerPacket(new PacketRespawn(), "RESPAWN", cls);
    registerPacket(new PacketMsg(), "MSG", cls);
    registerPacket(new PacketOnline(), "ONLINE", cls);
    registerPacket(new PacketSetMode(), "SETMODE", cls);
    registerPacket(new PacketPos(), "POS", cls);
    registerPacket(new PacketInvPut(), "INVPUT", cls);
    registerPacket(new PacketInvGet(), "INVGET", cls);
    registerPacket(new PacketInvExchange(), "INVEXCHANGE", cls);
    registerPacket(new PacketArmorPut(), "ARMORPUT", cls);
    registerPacket(new PacketArmorGet(), "ARMORGET", cls);
    registerPacket(new PacketArmorExchange(), "ARMOREXCHANGE", cls);
    registerPacket(new PacketCraftPut(), "CRAFTPUT", cls);
    registerPacket(new PacketCraftGet(), "CRAFTGET", cls);
    registerPacket(new PacketCraftExchange(), "CRAFTEXCHANGE", cls);
    registerPacket(new PacketCraftOne(), "CRAFTONE", cls);
    registerPacket(new PacketCraftAll(), "CRAFTALL", cls);
    registerPacket(new PacketContainerPut(), "CONTAINERPUT", cls);
    registerPacket(new PacketContainerGet(), "CONTAINERGET", cls);
    registerPacket(new PacketContainerExchange(), "CONTAINEREXCHANGE", cls);
    registerPacket(new PacketDrop(), "DROP", cls);
    registerPacket(new PacketMenuEnter(), "MENUENTER", cls);
    registerPacket(new PacketMenuLeave(), "MENULEAVE", cls);
    registerPacket(new PacketMenuInv(), "MENUINV", cls);
    registerPacket(new PacketOpenToLan(), "OPENTOLAN", cls);
    registerPacket(new PacketToggleGameMode(), "TOGGLEGAMEMODE", cls);
    registerPacket(new PacketSetBlock(), "SETBLOCK", cls);
    registerPacket(new PacketClearBlock(), "CLEARBLOCK", cls);
    registerPacket(new PacketSetInv(), "SETINV", cls);
    registerPacket(new PacketSetHand(), "SETHAND", cls);
    registerPacket(new PacketSetArmor(), "SETARMOR", cls);
    registerPacket(new PacketSetCraft(), "SETCRAFT", cls);
    registerPacket(new PacketSetCrafted(), "SETCRAFTED", cls);
    registerPacket(new PacketSetContainer(), "SETCONTAINER", cls);
    registerPacket(new PacketSetContainerItem(), "SETCONTAINERITEM", cls);
    registerPacket(new PacketMove(), "MOVE", cls);
    registerPacket(new PacketSpawn(), "SPAWN", cls);
    registerPacket(new PacketDespawn(), "DESPAWN", cls);
    registerPacket(new PacketHealth(), "HEALTH", cls);
    registerPacket(new PacketTime(), "TIME", cls);
    registerPacket(new PacketBedTime(), "BEDTIME", cls);
    registerPacket(new PacketB2E(), "B2E", cls);
    registerPacket(new PacketE2B(), "E2B", cls);
    registerPacket(new PacketMoveBlock(), "MOVEBLOCK", cls);
    registerPacket(new PacketSetExtra(), "SETEXTRA", cls);
    registerPacket(new PacketDelExtra(), "DELEXTRA", cls);
    registerPacket(new PacketSound(), "SOUND", cls);
    registerPacket(new PacketSheepSheared(), "SHEEPSHEARED", cls);
    registerPacket(new PacketKnockBack(), "KNOCKBACK", cls);
    registerPacket(new PacketGenSpawnArea(), "GENSPAWNAREA", cls);
    registerPacket(new PacketClearBlock2(), "CLEARBLOCK2", cls);
    registerPacket(new PacketTeleport1(), "TELEPORT1", cls);
    registerPacket(new PacketTeleport2(), "TELEPORT2", cls);
    registerPacket(new PacketHunger(), "HUNGER", cls);
    registerPacket(new PacketAir(), "AIR", cls);
    registerPacket(new PacketRiding(), "RIDING", cls);
    registerPacket(new PacketMoveBack(), "MOVEBACK", cls);
    registerPacket(new PacketEnderChest(), "ENDERCHEST", cls);
    registerPacket(new PacketSetSign(), "SETSIGN", cls);
  }

  public static byte RIDING;
  public static byte SETMODE; //survival, creative, etc.
  public static byte RESPAWN;
  public static byte SHEEPSHEARED;
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
  //server -> client
  public static byte LOGIN_REPLY;
  public static byte CHUNK_REPLY;
  //client -> server
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
  //client <-> server
  public static byte TICK;
  public static byte SETACTIVESLOT;
  public static byte ARMORPUT;
  public static byte SETBLOCK;
  public static byte CRAFTPUT; //personal craft slots
  public static byte SETCRAFTED;
  public static byte MOVE;
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

  @Override
  public SerialClass create(SerialBuffer buffer) {
    byte cmd = buffer.peekByte();
//    Static.log(), ""packets.create(), ")cmd=" + cmd + ",name=" + packets[cmd].getClass(), ").getName(), "", cls);
    try {
      return packets[cmd].getClass().newInstance();
    } catch (Exception e) {
      Static.log("Packet not found:" + cmd);
      e.printStackTrace();
      return null;
    }
  }
}
