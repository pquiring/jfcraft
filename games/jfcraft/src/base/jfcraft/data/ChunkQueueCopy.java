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
  private static final int max = 5;

  public ChunkQueueCopy() {}

  public void process(GL gl) {
    try {
      int cnt = 0;
      int pos = tail;
      while (pos != head1 && cnt < 5) {
        Chunk chunk = chunks[pos];
        chunk.copyBuffers(gl);
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt++;
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void add(Chunk chunk) {
    //scan head1->head2
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
      Static.log("ERROR:Client Chunk processing queue overflow!!!");
      return;
    }
    head2 = pos;
  }

  public void signal() {
    head1 = head2;
  }
}
