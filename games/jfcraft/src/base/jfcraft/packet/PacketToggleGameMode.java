package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
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
    client.player.flying = !client.player.flying;
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
