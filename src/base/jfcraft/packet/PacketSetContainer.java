package jfcraft.packet;

/** Packet with Extra
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;

public class PacketSetContainer extends Packet {
  public int cx, cz;
  public ExtraBase extra;
  public boolean entity;  //used for horse (minecart w/ chest?)

  public PacketSetContainer() {}

  public PacketSetContainer(byte cmd) {
    super(cmd);
  }

  public PacketSetContainer(byte cmd, ExtraBase extra, int cx, int cz) {
    super(cmd);
    this.cx = cx;
    this.cz = cz;
    this.entity = false;
    this.extra = extra;
  }

  public PacketSetContainer(byte cmd, ExtraBase extra) {
    super(cmd);
    this.cx = 0;
    this.cz = 0;
    this.entity = true;
    this.extra = extra;
  }

  //process on client side
  public void process(Client client) {
    if (entity) {
      client.container = (ExtraContainer)extra;
    } else {
      Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx, cz);
      if (chunk != null) {
        chunk.addExtra(extra);  //update extra on client side
      }
      client.container = (ExtraContainer)chunk.getExtra(extra.x, extra.y, extra.z, extra.id);  //get client copy
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeBoolean(entity);
    if (!entity) {
      buffer.writeInt(cx);
      buffer.writeInt(cz);
    }
    extra.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    entity = buffer.readBoolean();
    if (!entity) {
      cx = buffer.readInt();
      cz = buffer.readInt();
    }
    extra = (ExtraBase)Static.extras.create(buffer);
    extra.read(buffer, file);
    return true;
  }
}
