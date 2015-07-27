package jfcraft.packet;

/** Packet with 3 Floats
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;

public class PacketRespawn extends Packet {
  public float f1, f2, f3;
  public boolean clearInv;

  public PacketRespawn() {}

  public PacketRespawn(byte cmd) {
    super(cmd);
  }

  public PacketRespawn(byte cmd, float f1, float f2, float f3, boolean clearInv) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.clearInv = clearInv;
  }

  //process on client side
  public void process(Client client) {
    Static.log("RESPAWN");
    synchronized(Static.renderLock) {
      client.player.dim = 0;
      client.player.pos.x = f1;
      client.player.pos.y = f2;
      client.player.pos.z = f3;
      client.player.health = 20;
      client.player.hunger = 20;
      client.player.saturation = 20;
      client.player.exhaustion = 0;
      if (clearInv) {
        for(int a=0;a<4*9;a++) {
          client.player.items[a].clear();
        }
        for(int a=0;a<4;a++) {
          client.player.armors[a].clear();
        }
      }
      LoadingChunks menu = (LoadingChunks)Static.screens.screens[Client.LOADINGCHUNKS];
      menu.setup(client);
      Static.video.setScreen(menu);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeBoolean(clearInv);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    clearInv = buffer.readBoolean();
    return true;
  }
}
