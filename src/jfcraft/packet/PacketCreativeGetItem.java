package jfcraft.packet;

/** Packet with two Bytes
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCreativeGetItem extends Packet {
  public char b1;
  public byte b2;

  public PacketCreativeGetItem() {}

  public PacketCreativeGetItem(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      if (!Settings.current.creativeMode) {
        Static.log("CreativeGetItem:Player cheat:creative mode disabled");
        return;
      }
      if (!client.player.creative) {
        Static.log("CreativeGetItem:Player cheat:not in creative mode");
        return;
      }
      ItemBase itembase = Static.items.items[b1];
      if (itembase == null) {
        Static.log("CreativeGetItem:invalid item:" + (int)b1);
        return;
      }
      client.hand.id = b1;
      if (itembase.isDamaged) {
        client.hand.dmg = 1f;
        client.hand.count = 1;
      } else {
        client.hand.count += b2;
      }
      client.serverTransport.setHand(client.hand);
    }
  }

  public PacketCreativeGetItem(byte cmd, char id, byte count) {
    super(cmd);
    this.b1 = id;
    this.b2 = count;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeChar(b1);
    buffer.writeByte(b2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    b1 = buffer.readChar();
    b2 = buffer.readByte();
    return true;
  }
}
