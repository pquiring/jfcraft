package jfcraft.packet;

/** Packet with 1 Page
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketNPCDialogPage extends Packet {
  public Page page;

  public PacketNPCDialogPage() {}

  public PacketNPCDialogPage(byte cmd) {
    super(cmd);
  }

  public PacketNPCDialogPage(byte cmd, Page page) {
    super(cmd);
    this.page = page;
  }

  //process on client side
  public void process(Client client) {
    client.page = page;
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    page.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    page = new Page(null);
    page.read(buffer, file);
    return true;
  }
}
