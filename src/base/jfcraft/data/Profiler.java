package jfcraft.data;

/**
 *
 * @author pquiring
 */

public class Profiler {
  private static final int MAX = 16;
  private long ts[] = new long[MAX];
  private int cnt;
  private String name;

  public Profiler(String name) {
    this.name = name;
  }

  public void start() {
    cnt = 0;
    next();
  }

  public void next() {
    ts[cnt++] = System.nanoTime() / 1000;
  }

  public void print() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    for(int a=1;a<cnt;a++) {
      long diff = ts[a] - ts[a-1];
      if (a > 1) sb.append(',');
      sb.append(Long.toString(diff));
    }
    Static.log(sb.toString());
  }
}
