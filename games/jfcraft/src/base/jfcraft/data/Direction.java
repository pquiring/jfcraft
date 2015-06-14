package jfcraft.data;

/** Direction or face constants
 *
 * @author pquiring
 */

public class Direction {
  //sides (Z+=toward viewer : Y+=up : X+=right)
  public static final int B = 0;  //Below (default)
  public static final int A = 1;  //Above
  public static final int N = 2;  //North : must NOT be zero (see BlockRail)
  public static final int E = 3;  //East
  public static final int S = 4;  //South
  public static final int W = 5;  //West
  public static final int X = 6;  //this block (or centered side)
  //corners (used in steps and stairs)
  //L shape for steps/stairs
  public static final int NW = 7;
  public static final int NE = 8;
  public static final int SW = 9;
  public static final int SE = 10;
  //small step/stairs
  public static final int NW2 = 11;
  public static final int NE2 = 12;
  public static final int SW2 = 13;
  public static final int SE2 = 14;

  public static final int _15 = 15;  //reserved

  public static int opposite(int dir) {
    switch (dir) {
      case A: return B;
      case B: return A;
      case N: return S;
      case E: return W;
      case S: return N;
      case W: return E;
      case NE: return SW;
      case NW: return SE;
      case SE: return NW;
      case SW: return NE;
    }
    return -1;
  }
}
