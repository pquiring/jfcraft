package jfcraft.packet;

/** Packet with Player
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketPlayer extends Packet {
  public Player player;
  public int uid;

  public PacketPlayer() {}

  public PacketPlayer(byte cmd) {
    super(cmd);
  }

  public PacketPlayer(byte cmd, Player player) {
    super(cmd);
    this.player = player;
  }

  //process on client side
  public void process(Client client) {
    client.player = player;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    player.write(buffer, true);  //must write everything
    buffer.writeInt(player.uid);  //player.write(file) writes cid, not uid
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    player = new Player();
    player.init(Static.client.world);
    player.read(buffer, true);  //must read everything
    player.uid = buffer.readInt();
    return true;
  }
}
