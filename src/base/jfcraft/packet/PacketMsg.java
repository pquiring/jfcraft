package jfcraft.packet;

/** Packet with 1 string
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketMsg extends Packet {
  public String s1;

  public PacketMsg() {}

  public PacketMsg(byte cmd) {
    super(cmd);
  }

  public PacketMsg(byte cmd, String s1) {
    super(cmd);
    this.s1 = s1;
  }

  //process on client side
  public void process(Client client) {
    synchronized(client.chat) {
      client.chat.add(s1);
      if (client.chat.size() > 16) {
        client.chat.remove(0);
      }
    }
    client.chatTime = 5 * 20;
  }

  //process on server side
  public void process(Server server, Client client) {
    String msg = s1;
    if (msg.length() == 0) return;
    msg = server.cleanString(msg);
    if (msg.charAt(0) == '/') {
      server.doCommand(client, msg);
    } else {
      server.broadcastMsg(client.player.name + ">" + msg);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    byte b1[] = s1.getBytes();
    buffer.writeInt(b1.length);
    buffer.writeBytes(b1);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    int sl1 = buffer.readInt();
    byte b1[] = new byte[sl1];
    buffer.readBytes(b1);
    s1 = new String(b1);
    return true;
  }
}
