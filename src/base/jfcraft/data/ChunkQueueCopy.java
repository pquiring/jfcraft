package jfcraft.data;

/**
 *
 * @author pquiring
 *
 * Created : Jun 14, 2014
 */

import javaforce.gl.*;

public class ChunkQueueCopy {
  private static final int BUFSIZ = 1024 * 4;
  private Chunk[] chunks = new Chunk[BUFSIZ];
  private int tail, head1, head2;
  private int max = 9;
  private Object lock = new Object();

  public ChunkQueueCopy() {}

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
          chunk.copyBuffers();
          chunks[pos] = null;
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt--;
      }
//if (cnt > 0) Static.log("              c:" + cnt);
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void add(Chunk chunk) {
    //scan head1->head2
    synchronized(lock) {
      int pos = head1;
      while (pos != head2) {
        if (chunks[pos] == chunk) {
          return;
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
      }
      //add to queue
      chunks[pos] = chunk;
      pos++;
      if (pos == BUFSIZ) pos = 0;
      if (pos == tail) {
        Static.log("ERROR:Client Chunk Copy processing queue overflow!!!");
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
}
