package jfcraft.packet;

/** Packet with 3 strings
 *
 * @author pquiring
 */

import java.util.*;

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.plugin.*;
import jfcraft.server.Server;

public class PacketLoginRequest extends Packet {
  public String s1, s2, s3, s4;

  public PacketLoginRequest() {}
  public PacketLoginRequest(byte cmd) {super(cmd);}

  public PacketLoginRequest(byte cmd, String s1, String s2, String s3, String s4) {
    super(cmd);
    this.s1 = s1;
    this.s2 = s2;
    this.s3 = s3;
    this.s4 = s4;
  }

  private String getPluginsDiff(String client, String server) {
    ArrayList<String> diff = new ArrayList<String>();
    String cs[] = client.split(",");
    String ss[] = server.split(",");
    for(int c=0;c<cs.length;c++) {
      if (cs[c].length() == 0) continue;
      boolean ok = false;
      for(int s=0;c<ss.length;s++) {
        if (cs[c].equals(ss[s])) {
          ok = true;
          break;
        }
      }
      if (!ok) {
        diff.add("-" + cs[c]);
      }
    }
    for(int s=0;s<ss.length;s++) {
      if (ss[s].length() == 0) continue;
      boolean ok = false;
      for(int c=0;s<cs.length;c++) {
        if (cs[c].equals(ss[s])) {
          ok = true;
          break;
        }
      }
      if (!ok) {
        diff.add("+" + ss[s]);
      }
    }
    StringBuilder sb = new StringBuilder();
    for(int a=0;a<diff.size();a++) {
      if (a > 0) sb.append(",");
      sb.append(diff.get(a));
    }
    return sb.toString();
  }

  //process on server side
  public void process(Server server, Client client) {
    Static.log("LoginRequest:" + s1 + "," + s2 + "," + s3 + "," + s4);
    if (client.name != null) {
      client.serverTransport.login("Already logged in");
      Static.log("Client already logged in");
      client.active = false;
      return;
    }
    if ((!s1.equals(Static.version))) {
      client.serverTransport.login("Version mismatch (" + Static.version + ")");
      Static.log("Client version mismatch");
      client.active = false;
      return;
    }
    String plugins = PluginLoader.getPluginsString();
    if (!s4.equals(plugins)) {
      String badPlugins = getPluginsDiff(s4, plugins);
      client.serverTransport.login("Plugins mismatch (" + badPlugins + ")");
      Static.log("Client version mismatch");
      client.active = false;
      return;
    }
    client.name = s2;
    //TODO : validate password (s3)
    Static.log("Client.name=" + client.name);
    if (Static.iface != null) {
      Static.iface.clientAdded(client.name);
    }
    client.serverTransport.login("OK");
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    byte b1[] = s1.getBytes();
    buffer.writeInt(b1.length);
    buffer.writeBytes(b1);
    byte b2[] = s2.getBytes();
    buffer.writeInt(b2.length);
    buffer.writeBytes(b2);
    byte b3[] = s3.getBytes();
    buffer.writeInt(b3.length);
    buffer.writeBytes(b3);
    byte b4[] = s4.getBytes();
    buffer.writeInt(b4.length);
    buffer.writeBytes(b4);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);

    int sl1 = buffer.readInt();
    byte b1[] = new byte[sl1];
    buffer.readBytes(b1);
    s1 = new String(b1);

    int sl2 = buffer.readInt();
    byte b2[] = new byte[sl2];
    buffer.readBytes(b2);
    s2 = new String(b2);

    int sl3 = buffer.readInt();
    byte b3[] = new byte[sl3];
    buffer.readBytes(b3);
    s3 = new String(b3);

    int sl4 = buffer.readInt();
    byte b4[] = new byte[sl4];
    buffer.readBytes(b4);
    s4 = new String(b4);

    return true;
  }
}
