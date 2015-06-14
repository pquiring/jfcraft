package jfcraft.packet;

/** Packet with Player
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketPlayer extends Packet {
  public EntityBase entity;

  public PacketPlayer() {}

  public PacketPlayer(byte cmd) {
    super(cmd);
  }

  public PacketPlayer(byte cmd, EntityBase entity) {
    super(cmd);
    this.entity = entity;
  }

  //process on client side
  public void process(Client client) {
    client.player = (Player)entity;
    client.player.init();
    client.ang.copy(client.player.ang);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    entity.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    entity = (EntityBase)Static.entities.create(buffer);
    if (entity == null) {
      Static.log("Error:PacketPlayer:Entity not registered");
      return false;
    }
    entity.read(buffer, file);
    return true;
  }
}
