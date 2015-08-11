package jfcraft.server;

/**
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.util.*;

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.*;

public class LocalClientTransport extends ClientTransport {
  private LocalServerTransport server;

  public ArrayList<byte[]> queue = new ArrayList<byte[]>();
  public Object lock = new Object();

  public void init(LocalServerTransport server, Client client) {
    super.init(client);
    this.server = server;
  }

  public boolean isLocal() {
    return true;
  }

  public boolean addQueue(byte[] data) {
    synchronized(lock) {
      queue.add(data);
      lock.notify();
    }
    return true;
  }

  public boolean send(byte[] data) {
    return server.addQueue(data);
  }

  public int getServerQueueSize() {
    return server.queue.size();
  }

  public int getClientQueueSize() {
    return queue.size();
  }

  public void run() {
    client.initThread("Client LocalClientTransport", true);
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
    Static.log("Thread ended:" + Thread.currentThread().getName());
  }
  public void close() {
    client.active = false;
    synchronized(lock) {
      lock.notify();
    }
  }
}
