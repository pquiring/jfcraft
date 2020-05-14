package jfcraft.data;

/**
 *
 * @author pquiring
 *
 * Created : Jun 14, 2014
 */

import java.util.*;

public class ChunkQueueLight {
  private static final int BUFSIZ = 1024 * 4;
  private Chunk[] chunks = new Chunk[BUFSIZ];
  private int x1s[] = new int[BUFSIZ];
  private int y1s[] = new int[BUFSIZ];
  private int z1s[] = new int[BUFSIZ];
  private int x2s[] = new int[BUFSIZ];
  private int y2s[] = new int[BUFSIZ];
  private int z2s[] = new int[BUFSIZ];
  private int tail, head1, head2;
  private ChunkQueueBuild next;
  private boolean isClient;
  private int max = 3;
  private Profiler pro = new Profiler("lp:");
  private static class Lock {};
  private Lock lock = new Lock();

  public ChunkQueueLight(ChunkQueueBuild next, boolean isClient) {
    this.next = next;
    this.isClient = isClient;
  }

  /** Max chunks to process per call to process()
   * -1 for infinite.
   * default = 3
   */
  public void setMax(int max) {
    this.max = max;
  }

  public void process() {
    try {
      int cnt = max;
      int pos = tail;
      while (pos != head1 && cnt != 0) {
        Chunk chunk = chunks[pos];
        if (chunk != null) {
          if (chunk.canLights()) {
            int x1 = x1s[pos];
            int y1 = y1s[pos];
            int z1 = z1s[pos];
            int x2 = x2s[pos];
            int y2 = y2s[pos];
            int z2 = z2s[pos];
            pro.start();
            try {
              if (isClient) {
                Static.dims.dims[chunk.dim].getLightingClient().update(chunk, x1,y1,z1, x2,y2,z2);
              } else {
                Static.dims.dims[chunk.dim].getLightingServer().update(chunk, x1,y1,z1, x2,y2,z2);
              }
            } catch (Exception e) {
              Static.log(e);
            }
            pro.next();
  //          pro.print();
            if (next != null) {
              next.add(chunk);
              if (z1 <= 0) next.add(chunk.N);
              if (x2 >= 15) next.add(chunk.E);
              if (z2 >= 15) next.add(chunk.S);
              if (x1 <= 0) next.add(chunk.W);
              if ((z1 <= 0) && (x2 >= 15)) next.add(chunk.N.E);
              if ((z1 <= 0) && (x1 <= 0)) next.add(chunk.N.W);
              if ((z2 >= 15) && (x2 >= 15)) next.add(chunk.S.E);
              if ((z2 >= 15) && (x1 <= 0)) next.add(chunk.S.W);
            }
          }
          chunks[pos] = null;
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt--;
      }
//if (cnt > 0) Static.log("l:" + cnt);
      if (next != null) next.signal();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public Chunk[] getQueue() {
    ArrayList<Chunk> list = new ArrayList<Chunk>();
    int pos = head1;
    while (pos != head2) {
      list.add(chunks[pos]);
      pos++;
      if (pos == BUFSIZ) pos = 0;
    }
    return list.toArray(new Chunk[list.size()]);
  }

  public void add(Chunk chunk,int x1,int y1,int z1,int x2, int y2, int z2) {
    //scan head1->head2
    synchronized(lock) {
      int pos = head1;
      while (pos != head2) {
        if (chunks[pos] == chunk) {
          //combine ranges
          if (x1 < x1s[pos]) x1s[pos] = x1;
          if (y1 < y1s[pos]) y1s[pos] = y1;
          if (z1 < z1s[pos]) z1s[pos] = z1;
          if (x2 > x2s[pos]) x2s[pos] = x2;
          if (y2 > y2s[pos]) y2s[pos] = y2;
          if (z2 > z2s[pos]) z2s[pos] = z2;
          return;
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
      }
      //add to queue
      chunks[pos] = chunk;
      x1s[pos] = x1;
      y1s[pos] = y1;
      z1s[pos] = z1;
      if (y2 > 255) y2 = 255;
      x2s[pos] = x2;
      y2s[pos] = y2;
      z2s[pos] = z2;
      pos++;
      if (pos == BUFSIZ) pos = 0;
      if (pos == tail) {
        Static.log("ERROR:Client Chunk Light processing queue overflow!!!");
        return;
      }
      head2 = pos;
    }
  }

  public void signal() {
    synchronized(lock) {
      head1 = head2;
    }
  }

  public int getSize() {
    if (tail > head2) {
      return BUFSIZ - tail + head2;
    } else {
      return head2 - tail;
    }
  }
}
