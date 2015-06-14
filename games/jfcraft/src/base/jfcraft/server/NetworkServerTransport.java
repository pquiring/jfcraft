package jfcraft.server;

/**
 *
 * @author pquiring
 *
 * Created : Aug 14, 2014
 */

import java.io.*;
import java.net.*;

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.*;

public class NetworkServerTransport extends ServerTransport {
  private Socket s;
  private InputStream is;
  private OutputStream os;

  public void init(Server server, Socket socket, Client client) {
    super.init(server, client);
    s = socket;
    try {
      is = s.getInputStream();
      os = s.getOutputStream();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  private byte sendlen[] = new byte[4];
  public synchronized boolean send(byte[] data) {
    if (!client.active) return false;
    LE.setuint32(sendlen, 0, data.length);
    try {
      os.write(sendlen);
      os.write(data);
      os.flush();
      return true;
    } catch (SocketException e) {
      client.active = false;
//      Static.log(e);
      return false;
    } catch (Exception e) {
      client.active = false;
      Static.log(e);
      return false;
    }
  }

  public void run() {
    server.initThread("Server NetworkServerTransport " + s.getPort(), true);
    client.active = true;
    try {
      while (client.active) {
        byte len[] = readAll(is, 4);
        int packetLength = LE.getuint32(len, 0);
        byte data[] = readAll(is, packetLength);
        process(data);
      }
    } catch (SocketException e) {
      //do nothing
    } catch (Exception e) {
      Static.log(e);
    }
    server.removeClient(client);
    Static.log("Thread ended:" + Thread.currentThread().getName());
  }

  public void close() {
    client.active = false;
    try { s.close(); } catch (Exception e) {}
  }
}
