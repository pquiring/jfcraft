package jfcraft.packet;

/** Packet with one Byte
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.opengl.*;

public class PacketMenuEnter extends Packet {
  public byte b1;

  public PacketMenuEnter() {}

  public PacketMenuEnter(byte cmd) {
    super(cmd);
  }

  public PacketMenuEnter(byte cmd, byte b) {
    super(cmd);
    this.b1 = b;
  }

  //process on client side
  public void process(Client client) {
    Static.game.enterMenu(b1);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b1 = buffer.readByte();
    return true;
  }
}
