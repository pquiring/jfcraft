package jfcraft.server;

/** Server transport
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import jfcraft.client.*;
//import jfcraft.entity.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.packet.*;

public abstract class ServerTransport extends Transport {
  //put server replies here
  public Client client;
  public Server server;
  public ArrayList<Packet> updates = new ArrayList<Packet>();

  public void init(Server server, Client client) {
    this.server = server;
    this.client = client;
  }

  public void process(byte[] data) {
    Packet packet = (Packet)coder.decodeObject(data, Static.packets, false);
//    Static.log("server received packet:" + packet.getClass().getName());
    try {
      packet.process(server, client);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void login(String state) {
    Packet packet = new PacketLoginReply(Packets.LOGIN_REPLY, state);
    send(coder.encodeObject(packet, false));
  }
  public void logout() {
    Packet packet = new Packet(Packets.LOGOUT);
    send(coder.encodeObject(packet, false));
  }
  public void respawn(float x, float y, float z, boolean clearEnv) {
    Packet packet = new PacketRespawn(Packets.RESPAWN, x,y,z, clearEnv);
    send(coder.encodeObject(packet, false));
  }
  public void sendPlayer(Client client) {
    Packet packet = new PacketPlayer(Packets.PLAYER_REPLY, client.player);
    send(coder.encodeObject(packet, false));
  }
  public void sendWorld(World world) {
    Packet packet = new PacketWorld(Packets.WORLD_REPLY, world);
    send(coder.encodeObject(packet, false));
  }
  public void sendChunk(Chunk chunk) {
    Packet packet = new PacketChunk(Packets.CHUNK_REPLY, chunk);
    send(coder.encodeObject(packet, false));
  }
  public void tick(Packet updates[]) {
    Packet packet = new PacketTick(Packets.TICK, updates);
    send(coder.encodeObject(packet, false));
  }
  public void genSpawnAreaDone(int percent) {
    Packet packet = new PacketGenSpawnArea(Packets.GENSPAWNAREA, percent);
    send(coder.encodeObject(packet, false));
  }

  public void addUpdate(Packet packet) {
    synchronized(updates) {
      updates.add(packet);
    }
  }

  public void setInvItem(byte idx, Item item) {
    Packet packet = new PacketSetInv(Packets.SETINV, idx, item);
    addUpdate(packet);
  }

  public void setHand(Item item) {
    Packet packet = new PacketSetHand(Packets.SETHAND, item);
    addUpdate(packet);
  }

  public void setArmorItem(byte idx, Item item) {
    Packet packet = new PacketSetArmor(Packets.SETARMOR, idx, item);
    addUpdate(packet);
  }

  public void setCraftItem(byte idx, Item item) {
    Packet packet = new PacketSetCraft(Packets.SETCRAFT, idx, item);
    addUpdate(packet);
  }

  public void setCraftedItem(Item item) {
    Packet packet = new PacketSetCrafted(Packets.SETCRAFTED, item);
    addUpdate(packet);
  }

  public void setActiveSlot(byte idx) {
    Packet packet = new PacketSetActiveSlot(Packets.SETACTIVESLOT, idx);
    addUpdate(packet);
  }

  public void setContainer(int cx, int cz, ExtraContainer item) {
    Packet packet = new PacketSetContainer(Packets.SETCONTAINER, item, cx, cz);
    addUpdate(packet);
  }

  public void setContainer(ExtraContainer item) {
    Packet packet = new PacketSetContainer(Packets.SETCONTAINER, item);
    addUpdate(packet);
  }

  public void setContainerItem(byte idx, Item item) {
    Packet packet = new PacketSetContainerItem(Packets.SETCONTAINERITEM, idx, item);
    addUpdate(packet);
  }

  public void openEnderChest() {
    Packet packet = new PacketEnderChest(Packets.ENDERCHEST);
    addUpdate(packet);
  }

  public void leaveMenu() {
    Packet packet = new Packet(Packets.MENULEAVE);
    addUpdate(packet);
  }

  public void enterMenu(byte idx) {
    Packet packet = new PacketMenuEnter(Packets.MENUENTER, idx);
    addUpdate(packet);
  }

  public void setTime(int time) {
    Packet packet = new PacketTime(Packets.TIME, new Integer(time));
    addUpdate(packet);
  }

  public void setBedTime(int time) {
    Packet packet = new PacketBedTime(Packets.BEDTIME, new Integer(time));
    addUpdate(packet);
  }

  public void sendMsg(String msg) {
    Packet packet = new PacketMsg(Packets.MSG, msg);
    addUpdate(packet);
  }

  public void sendHealth(Player player) {
    Packet packet = new PacketHealth(Packets.HEALTH, player.uid, player.health);
    addUpdate(packet);
  }

  public void sendHunger(Player player) {
    Packet packet = new PacketHunger(Packets.HUNGER, player.uid, player.hunger);
    addUpdate(packet);
  }

  public void sendAir(Player player) {
    Packet packet = new PacketAir(Packets.AIR, player.uid, player.air);
    addUpdate(packet);
  }

  public void teleport1() {
    Packet packet = new PacketTeleport1(Packets.TELEPORT1);
    send(coder.encodeObject(packet, false));
  }

  public void teleport2(EntityBase e) {
    Packet packet = new PacketTeleport2(Packets.TELEPORT2, e.pos.x,e.pos.y,e.pos.z, e.dim);
    send(coder.encodeObject(packet, false));
  }
}
