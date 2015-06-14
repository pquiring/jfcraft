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

  public PacketSetContainer() {}

  public PacketSetContainer(byte cmd) {
    super(cmd);
  }

  public PacketSetContainer(byte cmd, int cx, int cz, ExtraBase extra) {
    super(cmd);
    this.cx = cx;
    this.cz = cz;
    this.extra = extra;
  }

  //process on client side
  public void process(Client client) {
    Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx, cz);
    if (chunk != null) {
      chunk.addExtra(extra);  //update extra on client side
    }
    client.container = (ExtraContainer)chunk.getExtra(extra.x, extra.y, extra.z, extra.id);  //get client copy
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeInt(cx);
    buffer.writeInt(cz);
    extra.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    cx = buffer.readInt();
    cz = buffer.readInt();
    extra = (ExtraBase)Static.extras.create(buffer);
    extra.read(buffer, file);
    return true;
  }
}
