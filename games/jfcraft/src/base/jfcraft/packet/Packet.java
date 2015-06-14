package jfcraft.packet;

/** Base class for requests and replies.
 *
 * @author pquiring
 *
 * Created : Mar 14, 2014
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.server.*;

public class Packet implements SerialClass {
  public byte cmd;  //command

  public Packet(byte cmd) {
    this.cmd = cmd;
  }

  public static Packet packet = new Packet();

  public Packet() {}

  //process on client side
  public void process(Client client) {
    Static.logTrace("Error:Packet.process() called (client side)");
  }

  //process on server side
  public void process(Server server, Client client) {
    Static.logTrace("Error:Packet.process() called (server side)");
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(cmd);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    cmd = buffer.readByte();
    return true;
  }
}
