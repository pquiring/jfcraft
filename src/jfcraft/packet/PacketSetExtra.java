package jfcraft.packet;

/** Packet with Extra
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.server.Server;
import jfcraft.data.*;

public class PacketSetExtra extends Packet {
  public int cx, cz;
  public ExtraBase extra;

  public PacketSetExtra() {}

  public PacketSetExtra(byte cmd) {
    super(cmd);
  }

  public PacketSetExtra(byte cmd, int cx, int cz, ExtraBase extra) {
    super(cmd);
    this.cx = cx;
    this.cz = cz;
    if (extra == null) {
      Static.logTrace("PacketSetExtra:extra == null:use PacketDelExtra instead!!!");
    }
    this.extra = extra;
  }

  //process on client side
  public void process(Client client) {
    Chunk chunk = client.world.chunks.getChunk(client.player.dim, cx, cz);
    if (chunk != null) {
      chunk.addExtra(extra);
    }
    client.chunkBuilder.add(chunk);
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
