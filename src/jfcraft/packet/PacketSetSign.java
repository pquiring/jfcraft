package jfcraft.packet;

/** Packet with Extra
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.server.Server;
import jfcraft.data.*;

public class PacketSetSign extends Packet {
  public String txt[];

  public PacketSetSign() {}

  public PacketSetSign(byte cmd) {
    super(cmd);
  }

  public PacketSetSign(byte cmd, String txt[]) {
    super(cmd);
    this.txt = txt;
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.menu != Client.SIGN) {
      Static.log("Error:PacketSetSign:Client tried to set sign but not in sign menu");
      return;
    }
    if (client.sign == null) {
      Static.log("Error:PacketSetSign:sign == null");
      return;
    }
    for(int a=0;a<4;a++) {
      client.sign.txt[a] = txt[a];
    }
    float x = client.chunk.cx * 16.0f + client.sign.x;
    float y = client.sign.y;
    float z = client.chunk.cz * 16.0f + client.sign.z;
    server.broadcastExtra(client.player.dim, x, y, z, client.sign, false);
//        client.serverTransport.leaveMenu();  //assumed
    client.menu = Client.GAME;
    client.sign = null;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    for(int a=0;a<4;a++) {
      String t = txt[a];
      buffer.writeByte((byte)t.length());
      buffer.writeBytes(t.getBytes());
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    txt = new String[4];
    for(int a=0;a<4;a++) {
      byte len = buffer.readByte();
      byte str[] = new byte[len];
      buffer.readBytes(str);
      txt[a] = new String(str);
    }
    return true;
  }
}
