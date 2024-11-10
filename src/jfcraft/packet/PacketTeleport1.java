package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;

public class PacketTeleport1 extends Packet {

  public PacketTeleport1() {}

  public PacketTeleport1(byte cmd) {
    super(cmd);
  }

  //process on client side
  public void process(Client client) {
    Static.log("TELEPORT1");
    if (client.teleport) {
      Static.log("Error:Client already in limbo");
      return;
    }
    client.teleport = true;
    client.player.offline = true;
    //wait for chunkTimer to settle
    synchronized(client.chunkTimerAck) {
      try {
        client.chunkTimerAck.wait();
      } catch (Exception e) {
        Static.log(e);
      }
    }
    client.world.chunks.removeAll();
    client.spawnAreaChunksTodo = 1;
    client.spawnAreaChunksDone = 0;
    LoadingChunks menu = (LoadingChunks)Static.screens.screens[Client.LOADINGCHUNKS];
    menu.setup(client);
    Static.video.setScreen(menu);
  }
}
