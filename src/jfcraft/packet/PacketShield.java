package jfcraft.packet;

/** Packet with 1 int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketShield extends Packet {
  public int i1;

  public PacketShield() {};

  public PacketShield(byte cmd) {
    super(cmd);
  }

  public PacketShield(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    client.player.blockCount = i1;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(i1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    i1 = buffer.readInt();
    return true;
  }
}
