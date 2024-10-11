package jfcraft.data;

/** Direction or face constants
 *
 * @author pquiring
 */

public class Direction {
  //sides (Z+=toward viewer : Y+=up : X+=right)
  public static final int A = 0;  //Above (default)
  public static final int B = 1;  //Below
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

  //full 3x3 coords (used in lighting)
  public static final int AN = 16;
  public static final int AE = 17;
  public static final int AS = 18;
  public static final int AW = 19;
  public static final int ANW = 20;
  public static final int ANE = 21;
  public static final int ASW = 22;
  public static final int ASE = 23;

  public static final int BN = 24;
  public static final int BE = 25;
  public static final int BS = 26;
  public static final int BW = 27;
  public static final int BNW = 28;
  public static final int BNE = 29;
  public static final int BSW = 30;
  public static final int BSE = 31;

  //used in path selection
  public static final int NS = 32;  //N->S
  public static final int SN = 33;  //S->N
  public static final int WE = 34;  //W->E
  public static final int EW = 34;  //E->W

  //quad upper
  public static final int QUNW = 1;
  public static final int QUNE = 2;
  public static final int QUSW = 4;
  public static final int QUSE = 8;

  //quad lower
  public static final int QLNW = 0x10;
  public static final int QLNE = 0x20;
  public static final int QLSW = 0x40;
  public static final int QLSE = 0x80;

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

  //shape based blocks (fence, glass pane, stone wall, etc.)
  public static final int NB = 0x1;
  public static final int EB = 0x2;
  public static final int SB = 0x4;
  public static final int WB = 0x8;
  public static final int N_S = NB | SB;  //0x5
  public static final int E_W = EB | WB;  //0xa

  //y rotations
  public static final int R0 = 0;
  public static final int R90 = 1;
  public static final int R180 = 2;
  public static final int R270 = 3;

  private static final byte r90dir[] = {
  //B, A, N, E, S, W, X, NW, NE, SW, SE, NW2, NE2, SW2, SE2
    B, A, E, S, W, N, X, NE, SE, NE, NW, NE2, SE2, NE2, NW2, _15
  };
  private static final byte r180dir[] = {
  //B, A, N, E, S, W, X, NW, NE, SW, SE, NW2, NE2, SW2, SE2
    B, A, S, W, N, E, X, SE, SW, NE, NW, SE2, SW2, NE2, NW2, _15
  };
  private static final byte r270dir[] = {  //or -90
  //B, A, N, E, S, W, X, NW, NE, SW, SE, NW2, NE2, SW2, SE2
    B, A, W, N, E, S, X, SW, NW, SE, NE, SW2, NW2, SE2, NE2, _15
  };

  public static byte rotate(byte dir, int rotation) {
    switch (rotation) {
      case R90: return r90dir[dir];
      case R180: return r180dir[dir];
      case R270: return r270dir[dir];
    }
    return dir;
  }

  private static final byte r90shape[] = {
  //0, NB, EB, 0, SB, N_S, 0, 0, WB, 0, E_W
    0, EB, SB, 0, WB, E_W, 0, 0, NB, 0, N_S
  };
  private static final byte r180shape[] = {
  //0, NB, EB, 0, SB, N_S, 0, 0, WB, 0, E_W
    0, SB, WB, 0, NB, N_S, 0, 0, EB, 0, E_W
  };
  private static final byte r270shape[] = {  //or -90
  //0, NB, EB, 0, SB, N_S, 0, 0, WB, 0, E_W
    0, WB, NB, 0, EB, E_W, 0, 0, SB, 0, N_S
  };

  public static byte rotateShape(byte dir, int rotation) {
    switch (rotation) {
      case R90: return r90shape[dir];
      case R180: return r180shape[dir];
      case R270: return r270shape[dir];
    }
    return dir;
  }
}
