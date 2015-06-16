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
  private static final int max = 9;

  public ChunkQueueBuild(ChunkQueueCopy next) {
    this.next = next;
  }

  public void process() {
    try {
      int cnt = 0;
      int pos = tail;
      while (pos != head1 && cnt < max) {
        Chunk chunk = chunks[pos];
        if (chunk.canRender()) {
          chunk.buildBuffers();
          next.add(chunk);
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt++;
      }
//if (cnt > 0) Static.log("     b:" + cnt);
      next.signal();
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
