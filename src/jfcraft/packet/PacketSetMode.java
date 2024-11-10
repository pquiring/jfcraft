package jfcraft.packet;

/** Packet with one Byte
 *
 * NOT USED YET!
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class PacketSetMode extends Packet {
  public byte b;

  public PacketSetMode() {}

  public PacketSetMode(byte cmd) {
    super(cmd);
  }

  public PacketSetMode(byte cmd, boolean b) {
    super(cmd);
    this.b = (byte)(b ? 1 : 0);
  }

  public PacketSetMode(byte cmd, byte b) {
    super(cmd);
    this.b = b;
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
