package jfcraft.packet;

/** Packet with 1 Byte
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.server.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketNPCDialogAction extends Packet {
  public byte action;

  public PacketNPCDialogAction() {}

  public PacketNPCDialogAction(byte cmd) {
    super(cmd);
  }

  public PacketNPCDialogAction(byte cmd, byte action) {
    super(cmd);
    this.action = action;
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.pages == null) {
      Static.log("PacketNPCDialogAction:Error:client.pages == null");
      client.serverTransport.leaveMenu();
      return;
    }
    if (action == Page.ACTION_EXIT) {
      client.serverTransport.leaveMenu();
      return;
    }
    Page page = client.pages[client.pageIndex];
    if (action == Page.ACTION_NEXT) {
      if (page.choices() > 0) {
        //should not happen
        Static.log("PacketNPCDialogAction:Error:action == ACTION_NEXT but there are choices");
        client.serverTransport.leaveMenu();
        return;
      }
      client.pageIndex++;
    } else {
      //TODO : do action 0-5
    }
    if (client.pageIndex >= client.pages.length) {
      client.serverTransport.leaveMenu();
      return;
    }
    client.serverTransport.sendNPCPage(client.pages[client.pageIndex]);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(action);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    action = buffer.readByte();
    return true;
  }
}
