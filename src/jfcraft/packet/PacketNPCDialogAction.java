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
      if (page.choicesCount() > 0) {
        //should not happen
        Static.log("PacketNPCDialogAction:Error:action == ACTION_NEXT but there are choices");
        client.serverTransport.leaveMenu();
        return;
      }
      client.pageIndex++;
      //process any actions in page
      for(String txt : page.text) {
        if (txt.startsWith("#")) {
          doAction(server, client, txt.substring(1));
        }
      }
    } else {
      String text = page.text[action];
      int i1 = text.indexOf('(');
      int i2 = text.indexOf(')');
      String[] acts = text.substring(i1 + 1, i2).split("[,]");
      for(String act : acts) {
        doAction(server, client, act);
      }
    }
    if (client.pageIndex >= client.pages.length) {
      client.serverTransport.leaveMenu();
      return;
    }
    client.serverTransport.sendNPCPage(client.pages[client.pageIndex]);
  }

  private void doAction(Server server, Client client, String action) {
    int idx = action.indexOf(':');
    if (idx == -1) {
      switch (action) {
        case "exit":
          client.pages = null;
          client.serverTransport.leaveMenu();
          break;
        case "reload":
          //TODO
          break;
      }
    } else {
      String key = action.substring(0, idx);
      String value = action.substring(idx + 1);
      switch (key) {
        case "event":
          idx = value.indexOf('=');
          String e_key = value.substring(0, idx);
          String e_value = value.substring(idx + 1);
          client.player.setEvent(e_key, e_value);
          break;
        case "goto":
          client.pageIndex = Integer.valueOf(value);
          break;
      }
    }
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
