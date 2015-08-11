package jfcraft.data;

/** Chunk Key - for use in HashMap's
 *
 * @author pquiring
 */

import java.util.*;

public class ChunkKey implements Cloneable {
  public int dim, cx, cz;
  public ChunkKey(int dim,int cx,int cz) {
    this.dim = dim;
    this.cx = cx;
    this.cz = cz;
  }
  /** Generate hashCode (not guaranteed to be unique) */
  public int hashCode() {
    return (dim << 16) ^ (cx << 8) ^ cz;
  }
  public boolean equals(Object obj) {
    ChunkKey key = (ChunkKey)obj;
    if (key.dim != dim) return false;
    if (key.cx != cx) return false;
    if (key.cz != cz) return false;
    return true;
  }
  public ChunkKey clone() {
    return new ChunkKey(dim,cx,cz);
  }
  //ChunkKey's are heavy turn over, so use a pool to avoid massive GC usage
  //this is anti Java style coding but is necessary for performance
  private static ArrayList<ChunkKey> pool = new ArrayList<ChunkKey>();
  public static int cnt;  //200-300
  public static ChunkKey alloc(int dim, int cx, int cz) {
    ChunkKey c;
    synchronized(pool) {
      int size = pool.size();
      if (size == 0) {
        cnt++;
        return new ChunkKey(dim,cx,cz);
      }
      c = pool.remove(size-1);  //always take from end
    }
    c.dim = dim;
    c.cx = cx;
    c.cz = cz;
    return c;
  }
  private static void free(ChunkKey c) {
    synchronized(pool) {
      pool.add(c);
    }
  }
  public void free() {
    free(this);
  }
}
