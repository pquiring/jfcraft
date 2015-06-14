package jfcraft.server;

import java.util.*;

/** VoIPConference */

public class VoIPConference {

  public static final int bufs = 5;

  public static class Member {
    public boolean dropped;
    public short buf[][] = new short[bufs][160];
    public int idx;  //points to head buffer in 3 buffers (0-2) (last buffer is always volatile)
    public int idxs[];  // = new int[memberList.size()];
    public VoIPCallDetails cd;
    public Object lock = new Object();
    public Vector<Member> memberList;
  }

  public static HashMap<String, Vector<Member>> list = new HashMap<String, Vector<Member>>();
  public static Object lock = new Object();
}
