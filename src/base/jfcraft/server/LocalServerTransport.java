package jfcraft.server;

/** Local server transport
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.util.*;

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.Static;

public class LocalServerTransport extends ServerTransport {
  private LocalClientTransport clientTransport;

  public ArrayList<byte[]> queue = new ArrayList<byte[]>();
  public Object lock = new Object();

  public void init(Server server, LocalClientTransport clientTransport, Client client) {
    super.init(server, client);
    this.clientTransport = clientTransport;
  }

  public boolean addQueue(byte[] data) {
    synchronized(lock) {
      queue.add(data);
      if (queue.size() > 16) {
//        Static.log("Queue Size:" + queue.size());
      }
      lock.notify();
    }
    return true;
  }

  public boolean send(byte[] data) {
    return clientTransport.addQueue(data);
  }

  public void run() {
    server.initThread("Server LocalServerTransport", true);
    client.active = true;
    byte data[];
    while (client.active) {
      synchronized(lock) {
        if (queue.isEmpty()) {
          try {lock.wait();} catch (Exception e) {}
        }
        if (queue.isEmpty()) continue;
        data = queue.remove(0);
      }
      process(data);
    }
    Static.server.removeClient(client);
    Static.server.close();
    Static.log("Thread ended:" + Thread.currentThread().getName());
  }
  public void close() {
    client.active = false;
    synchronized(lock) {
      lock.notify();
    }
  }
}
