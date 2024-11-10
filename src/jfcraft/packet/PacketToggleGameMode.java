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

  public PacketToggleGameMode(byte cmd, boolean creative, boolean flying) {
    super(cmd);
    byte bits = 0;
    if (creative) bits |= 1;
    if (flying) bits |= 2;
    this.b = bits;
  }

  //process on server side
  public void process(Server server, Client client) {
    byte bits = b;
    boolean creative = (bits & 1) != 0;
    boolean flying = (bits & 2) != 0;
    client.player.creative = creative;
    client.player.mode = flying ? EntityBase.MODE_FLYING : EntityBase.MODE_WALK;
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
