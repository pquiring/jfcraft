package jfcraft.packet;

/** Packet with 1 Int
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketBedTime extends Packet {
  public int i1;

  public PacketBedTime() {}

  public PacketBedTime(byte cmd) {
    super(cmd);
  }

  public PacketBedTime(byte cmd, int i1) {
    super(cmd);
    this.i1 = i1;
  }

  //process on client side
  public void process(Client client) {
    client.bedtime = i1;
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
