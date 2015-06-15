package jfcraft.data;

/**
 *
 * @author pquiring
 *
 * Created : Jun 14, 2014
 */

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
  private static final int max = 5;

  public ChunkQueueLight(ChunkQueueBuild next, boolean isClient) {
    this.next = next;
    this.isClient = isClient;
  }

  public void process() {
    try {
      int cnt = 0;
      int pos = tail;
      while (pos != head1 && cnt < 5) {
        Chunk chunk = chunks[pos];
        int x1 = x1s[pos];
        int y1 = y1s[pos];
        int z1 = z1s[pos];
        int x2 = x2s[pos];
        int y2 = y2s[pos];
        int z2 = z2s[pos];
        if (isClient)
          Static.dims.dims[chunk.dim].getLightingClient().update(chunk, x1,y1,z1, x2,y2,z2);
        else
          Static.dims.dims[chunk.dim].getLightingServer().update(chunk, x1,y1,z1, x2,y2,z2);
        if (next != null) {
          next.add(chunk);
          if (z1 < 0) next.add(chunk.N);
          if (x2 > 15) next.add(chunk.E);
          if (z2 > 15) next.add(chunk.S);
          if (x1 < 0) next.add(chunk.W);
          if ((z1 < 0) && (x2 > 15)) next.add(chunk.N.E);
          if ((z1 < 0) && (x1 < 0)) next.add(chunk.N.W);
          if ((z2 > 15) && (x2 > 15)) next.add(chunk.S.E);
          if ((z2 > 15) && (x1 < 0)) next.add(chunk.S.W);
        }
        pos++;
        if (pos == BUFSIZ) pos = 0;
        tail = pos;
        cnt++;
      }
      if (next != null) next.signal();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void add(Chunk chunk,int x1,int y1,int z1,int x2, int y2, int z2) {
    //add to queue
    int pos = head2;
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
      Static.log("ERROR:Client Chunk processing queue overflow!!!");
      return;
    }
    head2 = pos;
  }

  public void signal() {
    head1 = head2;
  }
}
