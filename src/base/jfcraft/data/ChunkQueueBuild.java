package jfcraft.data;

/**
 *
 * @author pquiring
 *
 * Created : Jun 14, 2014
 */

public class ChunkQueueBuild {
  private static final int BUFSIZ = 1024 * 4;
  private Chunk[] chunks = new Chunk[BUFSIZ];
  private int tail, head1, head2;
  private ChunkQueueCopy next;
  private int max = 9;
  private Object lock = new Object();

  public ChunkQueueBuild(ChunkQueueCopy next) {
    this.next = next;
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
          if (chunk.canRender()) {
            try {
              chunk.buildBuffers();
            } catch (Exception e) {
              Static.log(e);
            }
            if (next != null) next.add(chunk);
          }
          chunks[pos] = null;
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt--;
      }
//if (cnt > 0) Static.log("     b:" + cnt);
      if (next != null) next.signal();
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
        Static.log("ERROR:Client Chunk Build processing queue overflow!!!");
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
