package jfcraft.packet;

/** Packet with Player
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketPlayer extends Packet {
  public int uid, dim;
  public float x, z;

  public PacketPlayer() {}

  public PacketPlayer(byte cmd) {
    super(cmd);
  }

  public PacketPlayer(byte cmd, int uid, int dim, float x, float z) {
    super(cmd);
    this.uid = uid;
    this.dim = dim;
    this.x = x;
    this.z = z;
  }

  //process on client side
  public void process(Client client) {
    client.uid = uid;
    client.dim = dim;
    client.x = x;
    client.z = z;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(uid);
    buffer.writeInt(dim);
    buffer.writeFloat(x);
    buffer.writeFloat(z);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    uid = buffer.readInt();
    dim = buffer.readInt();
    x = buffer.readFloat();
    z = buffer.readFloat();
    return true;
  }
}
