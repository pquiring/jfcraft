package jfcraft.packet;

/** Packet with 1 string
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.client.MessageMenu;
import jfcraft.data.*;
import static jfcraft.opengl.RenderScreen.client;

public class PacketLoginReply extends Packet {
  public String s1;

  public PacketLoginReply() {}

  public PacketLoginReply(byte cmd) {
    super(cmd);
  }

  public PacketLoginReply(byte cmd, String s1) {
    super(cmd);
    this.s1 = s1;
  }

  //process on client side
  public void process(Client client) {
    Static.log("LoginReply:" + s1);
    client.auth = s1.equals("OK");
    if (!client.auth) {
      client.stopTimers();
      client.stopVoIP();
      MessageMenu message = (MessageMenu)Static.screens.screens[Client.MESSAGE];
      message.setup("Error", s1, Static.screens.screens[Client.MAIN]);
      Static.video.setScreen(message);
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
