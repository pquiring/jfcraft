package jfcraft.packet;

/** Packet tick (multiple sub packets)
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.server.Server;

public class PacketTick extends Packet {
  public Packet packets[];

  public PacketTick() {}

  public PacketTick(byte cmd) {
    super(cmd);
  }

  public PacketTick(byte cmd, Packet packets[]) {
    super(cmd);
    this.packets = packets;
  }

  //process on client side
  public void process(Client client) {
//Static.log("tick");
    int cnt = packets.length;
    for(int a=0;a<cnt;a++) {
      packets[a].process(client);
    }
    client.chunkLighter.signal();
  }

  //process on server side
  public void process(Server server, Client client) {
    for(int a=0;a<packets.length;a++) {
      packets[a].process(server, client);
    }
    Packet updates[];
    synchronized(client.serverTransport.updates) {
      updates = client.serverTransport.updates.toArray(new Packet[0]);
      client.serverTransport.updates.clear();
    }
    client.serverTransport.tick(updates);
    if (client.crackTicks > 0) {
      client.crackTicks--;
      if (client.crackTicks == 0) {
        client.crack.dmg = 0.0f;
        server.broadcastDelExtra(client.player.dim, client.crack_cx * 16 + client.crack.x, client.crack.y, client.crack_cz * 16 + client.crack.z, Extras.CRACK);
      }
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    int cnt = packets.length;
//    Static.log("tick.write:cnt=" + cnt);
    buffer.writeShort((short)cnt);
    for(int a=0;a<cnt;a++) {
//      Static.log("tick.write:" + packets[a].getClass().getName());
      packets[a].write(buffer, file);
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    int cnt = buffer.readShort();
//    Static.log("tick.read:cnt=" + cnt);
    packets = new Packet[cnt];
    for(int a=0;a<cnt;a++) {
      Packet packet = (Packet)Static.packets.create(buffer);
//      Static.log("tick.read:" + packet.getClass().getName());
      if (packet == null) {
        Static.log("Error:Unknown packet received:" + buffer.peekByte());
//        Client.errmsg = "Error:Unknown packet";
        return false;
      }
      packet.read(buffer, file);
      packets[a] = packet;
    }
    return true;
  }
}
