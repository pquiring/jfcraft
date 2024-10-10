package jfcraft.server;

/** ClientTransport
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;

import jfcraft.client.*;
import jfcraft.entity.*;
import jfcraft.data.*;
import jfcraft.packet.*;
import jfcraft.plugin.PluginLoader;

public abstract class ClientTransport extends Transport {
  //put client commands here
  public Client client;
  private ArrayList<Packet> packets = new ArrayList<Packet>();

  public void init(Client client) {
    this.client = client;
  }

  public abstract boolean isLocal();

  public void process(byte[] data) {
    Packet packet = (Packet)coder.decodeObject(data, Static.packets, false);
//    Static.log("client received packet:" + packet.getClass().getName());
    try {
      packet.process(client);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public abstract int getServerQueueSize();
  public abstract int getClientQueueSize();

  public void login() {
    Packet packet = new PacketLoginRequest(Packets.LOGIN_REQUEST, Static.version, client.name, client.pass, PluginLoader.getPluginsString());
    send(coder.encodeObject(packet, false));
  }
  public void logout() {
    Packet packet = new PacketLogout(Packets.LOGOUT);
    send(coder.encodeObject(packet, false));
  }
  public void respawn() {
    Packet packet = new PacketRespawnRequest(Packets.RESPAWN_REQUEST);
    send(coder.encodeObject(packet, false));
  }
  public void getPlayer() {
    Packet packet = new PacketPlayerRequest(Packets.PLAYER_REQUEST);
    send(coder.encodeObject(packet, false));
  }
  public void getWorld() {
    Packet packet = new PacketWorldRequest(Packets.WORLD_REQUEST);
    send(coder.encodeObject(packet, false));
  }
  public void online() {
    Packet packet = new PacketOnline(Packets.ONLINE);
    send(coder.encodeObject(packet, false));
  }
  public void loadChunk(int cx, int cz) {
    Packet packet = new PacketChunkRequest(Packets.CHUNK_REQUEST, cx, cz, true);
    send(coder.encodeObject(packet, false));
  }
  public void unloadChunk(int cx, int cz) {
    Packet packet = new PacketChunkRequest(Packets.CHUNK_REQUEST, cx, cz, false);
    send(coder.encodeObject(packet, false));
  }
  public void tick(Player player,boolean up, boolean dn, boolean lt, boolean rt,
    boolean jump, boolean sneak, boolean run, boolean b1, boolean b2,
    boolean fup, boolean fdn)
  {
    Packet packetArray[];
    int size;
    synchronized(packets) {
      size = packets.size();
      size++;
      packetArray = new Packet[size];
      for(int a=0;a<size-1;a++) {
        packetArray[a+1] = packets.remove(0);
      }
    }
    int bits = 0;
    if (Static.inGame) {
      if (b1) bits += Player.LT_BUTTON;
      if (b2) bits += Player.RT_BUTTON;
      if (up) bits += Player.MOVE_UP;
      if (dn) bits += Player.MOVE_DN;
      if (lt) bits += Player.MOVE_LT;
      if (rt) bits += Player.MOVE_RT;
      if (jump) bits += Player.JUMP;
      if (sneak) bits += Player.SNEAK;
      if (run) bits += Player.RUN;
      if (fup) bits += Player.FLY_UP;
      if (fdn) bits += Player.FLY_DN;
    }
    packetArray[0] = new PacketPos(Packets.POS
      , player.pos.x, player.pos.y, player.pos.z, player.ang.x, player.ang.y, player.ang.z
      , bits, 0);
    Packet packet = new PacketTick(Packets.TICK, packetArray);
    send(coder.encodeObject(packet, false));
  }

  public void invPut(byte idx, byte count) {
    Packet packet = new PacketInvPut(Packets.INVPUT, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void invGet(byte idx, byte count) {
    Packet packet = new PacketInvGet(Packets.INVGET, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void invExchange(byte idx) {
    Packet packet = new PacketInvExchange(Packets.INVEXCHANGE, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void enterInvMenu() {
    Packet packet = new PacketMenuInv(Packets.MENUINV);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void leaveMenu() {
    Packet packet = new PacketMenuLeave(Packets.MENULEAVE);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void armorPut(byte idx) {
    Packet packet = new PacketArmorPut(Packets.ARMORPUT, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void armorGet(byte idx) {
    Packet packet = new PacketArmorGet(Packets.ARMORGET, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void armorExchange(byte idx) {
    Packet packet = new PacketArmorExchange(Packets.ARMOREXCHANGE, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }

  public void craftPut(byte idx, byte count) {
    Packet packet = new PacketCraftPut(Packets.CRAFTPUT, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void craftGet(byte idx, byte count) {
    Packet packet = new PacketCraftGet(Packets.CRAFTGET, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void craftExchange(byte idx) {
    Packet packet = new PacketCraftExchange(Packets.CRAFTEXCHANGE, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void craftOne() {
    Packet packet = new PacketCraftOne(Packets.CRAFTONE);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void craftAll() {
    Packet packet = new PacketCraftAll(Packets.CRAFTALL);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void changeActiveSlot(byte idx) {
    Packet packet = new PacketSetActiveSlot(Packets.SETACTIVESLOT, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void containerPut(byte idx, byte count) {
    Packet packet = new PacketContainerPut(Packets.CONTAINERPUT, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void containerGet(byte idx, byte count) {
    Packet packet = new PacketContainerGet(Packets.CONTAINERGET, idx, count);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void containerExchange(byte idx) {
    Packet packet = new PacketContainerExchange(Packets.CONTAINEREXCHANGE, idx);
    synchronized(packets) {
      packets.add(packet);
    }
  }

  public void openToLan() {
    Packet packet = new PacketOpenToLan(Packets.OPENTOLAN);
    send(coder.encodeObject(packet, false));
  }

  public void sendMsg(String msg) {
    Packet packet = new PacketMsg(Packets.MSG, msg);
    send(coder.encodeObject(packet, false));
  }

  public void drop() {
    Packet packet = new PacketDrop(Packets.DROP);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void changegamemode() {
    //must be sent BEFORE next POS packet
    Packet packet = new PacketToggleGameMode(Packets.TOGGLEGAMEMODE, client.player.creative, client.player.isFlying());
    send(coder.encodeObject(packet, false));
  }
  public void setExtra(int cx,int cz,ExtraBase eb) {
    Packet packet = new PacketSetExtra(Packets.SETEXTRA, cx,cz, eb);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void setSign(String txt[]) {
    Packet packet = new PacketSetSign(Packets.SETSIGN, txt);
    synchronized(packets) {
      packets.add(packet);
    }
  }
  public void useVehicleInventory() {
    Packet packet = new PacketUseVehicleInventory(Packets.USEVEHICLEINVENTORY);
    synchronized(packets) {
      packets.add(packet);
    }
  }
}
