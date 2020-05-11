package jfcraft.packet;

/** Packet with one Byte and Item dmg
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;

public class PacketSetInvDmg extends Packet {
  public float dmg;

  public PacketSetInvDmg() {}

  public PacketSetInvDmg(byte cmd) {
    super(cmd);
  }

  public PacketSetInvDmg(byte cmd, float dmg) {
    super(cmd);
    this.dmg = dmg;
  }

  //process on client side
  public void process(Client client) {
    client.player.items[client.player.activeSlot].dmg = dmg;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(dmg);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    dmg = buffer.readFloat();
    return true;
  }
}
