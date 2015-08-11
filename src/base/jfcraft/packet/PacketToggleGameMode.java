package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.server.Server;

public class PacketToggleGameMode extends Packet {
  public byte b;

  public PacketToggleGameMode() {}

  public PacketToggleGameMode(byte cmd) {
    super(cmd);
  }

  public PacketToggleGameMode(byte cmd, byte b) {
    super(cmd);
    this.b = b;
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.player.mode == EntityBase.MODE_FLYING)
      client.player.mode = EntityBase.MODE_IDLE;
    else
      client.player.mode = EntityBase.MODE_FLYING;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(b);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b = buffer.readByte();
    return true;
  }
}
