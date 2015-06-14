package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;

public class PacketEnderChest extends Packet {

  public PacketEnderChest() {}

  public PacketEnderChest(byte cmd) {
    super(cmd);
  }

  //process on client side
  public void process(Client client) {
    client.container = client.player.enderChest;
  }
}
