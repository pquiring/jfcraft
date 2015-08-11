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

public class NetworkClientTransport extends ClientTransport {
  private Socket s;
  private InputStream is;
  private OutputStream os;

  public void init(Socket server, Client client) {
    super.init(client);
    s = server;
    try {
      is = s.getInputStream();
      os = s.getOutputStream();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public boolean isLocal() {
    return false;
  }

  public int getServerQueueSize() {
    return 0;  //not known
  }

  public int getClientQueueSize() {
    return 0;  //not known
  }

  private byte sendlen[] = new byte[4];
  public synchronized boolean send(byte[] data) {
    LE.setuint32(sendlen, 0, data.length);
    try {
      os.write(sendlen);
      os.write(data);
      os.flush();
    } catch (Exception e) {
      client.error = e;
      client.active = false;
      Static.log(e);
    }
    return true;
  }

  public void run() {
    client.initThread("Client Network Packet Processor", true);
    client.active = true;
    try {
      while (client.active) {
        byte len[] = readAll(is, 4);
        int packetLength = LE.getuint32(len, 0);
        byte data[] = readAll(is, packetLength);
        process(data);
      }
    } catch (Exception e) {
      client.active = false;
      Static.log(e);
    }
    Static.log("Thread ended:" + Thread.currentThread().getName());
  }

  public void close() {
    client.active = false;
  }
}
