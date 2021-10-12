package jfcraft.opengl;

/** Contains all 4 coords for a face which can then be rotated.
 *
 * Coords should be within 0,0,0 -> 1,1,1
 *
 * @author pquiring
 */

import javaforce.gl.*;

import static jfcraft.data.Direction.*;

public class Face {
  public float x[] = new float[4];
  public float y[] = new float[4];
  public float z[] = new float[4];
  public float u1[] = new float[4];
  public float v1[] = new float[4];
  public float u2[] = new float[4];
  public float v2[] = new float[4];

  private static Matrix mat = new Matrix();
  private static Vector3 vec = new Vector3();

  private void add(float d, boolean ay) {
    for(int a=0;a<4;a++) {
      x[a] += d;
      if (ay) y[a] += d;
      z[a] += d;
    }
  }

  public void rotate(float rx, float ry, float rz) {
    mat.setIdentity();
    if (rx != 0.0f) mat.addRotate(rx, 1, 0, 0);
    if (ry != 0.0f) mat.addRotate(ry, 0, 1, 0);
    if (rz != 0.0f) mat.addRotate(rz, 0, 0, 1);
    for(int a=0;a<4;a++) {
      vec.set(x[a], y[a], z[a]);
      mat.mult(vec);
      x[a] = vec.v[0];
      y[a] = vec.v[1];
      z[a] = vec.v[2];
    }
  }

  private void rotate(float rx, float ry, float rz, boolean ay) {
    mat.setIdentity();
    if (rx != 0.0f) mat.addRotate(rx, 1, 0, 0);
    if (ry != 0.0f) mat.addRotate(ry, 0, 1, 0);
    if (rz != 0.0f) mat.addRotate(rz, 0, 0, 1);
    add(-0.5f, ay);
    for(int a=0;a<4;a++) {
      vec.set(x[a], y[a], z[a]);
      mat.mult(vec);
      x[a] = vec.v[0];
      y[a] = vec.v[1];
      z[a] = vec.v[2];
    }
    add(0.5f, ay);
  }

  public void rotate(RenderData data) {
    if (data.isDir) {
      if (!data.isDirXZ) {
        switch (data.dir[X]) {
          case A:
            //default
            break;
          case B:
            //rotate A to B
            rotateAB();
            break;
          case N:
            //rotate A to N
            rotateAN();
            break;
          case E:
            //rotate A to E
            rotateAE();
            break;
          case S:
            //rotate A to S
            rotateAS();
            break;
          case W:
            //rotate A to W
            rotateAW();
            break;
        }
      } else {
        switch (data.dir[X]) {
          case N:
            //default
            break;
          case E:
            //rotate N to E
            rotateNE();
            break;
          case S:
            //rotate N to S
            rotateNS();
            break;
          case W:
            //rotate N to W
            rotateNW();
            break;
        }
      }
    }
  }

  public void rotateAB() {
    rotate(0, 0, 180, true);
  }

  public void rotateAN() {
    //x90 A -> N
    //z180 to rotate face
    rotate(90, 0, 180, true);
  }

  public void rotateAE() {
    //z90 A -> E
    //y-90 to rotate face
    rotate(0, -90, 90, true);
  }

  public void rotateAS() {
    //x-90 A -> S
    rotate(-90, 0, 0, true);
  }

  public void rotateAW() {
    //z-90 A -> W
    //y90 to rotate face
    rotate(0, 90, -90, true);
  }

  public void rotateNE() {
    //y90 N -> E
    rotate(0, 90, 0, true);
  }

  public void rotateNS() {
    //y180 N -> S
    rotate(0, 180, 0, true);
  }

  public void rotateNW() {
    //y-90 N -> W
    rotate(0, -90, 0, true);
  }

  //isDirXZ (dirSide __ dir[X])
  //A :
  private static int[] vA__A = {-1,-1,-1,-1};  //error
  private static int[] vA__B = {-1,-1,-1,-1};  //error
  private static int[] vA__N = {0,1,2,3};
  private static int[] vA__E = {1,2,3,0};
  private static int[] vA__S = {3,0,1,2};
  private static int[] vA__W = {2,3,0,1};

  //B :
  private static int[] vB__A = {-1,-1,-1,-1};  //error
  private static int[] vB__B = {-1,-1,-1,-1};  //error
  private static int[] vB__N = {0,1,2,3};
  private static int[] vB__E = {3,0,1,2};
  private static int[] vB__S = {2,3,0,1};
  private static int[] vB__W = {2,3,0,1};

  //isDir : full rotation
  //NOTE : rotation : look towards zero, rotation is CW
  //A : no rotation
  private static int[] vA_A = {0,1,2,3};
  private static int[] vA_B = {0,1,2,3};
  private static int[] vA_N = {0,1,2,3};
  private static int[] vA_E = {0,1,2,3};
  private static int[] vA_S = {0,1,2,3};
  private static int[] vA_W = {0,1,2,3};

  //B : 0,0,180
  private static int[] vB_A = {2,3,0,1};
  private static int[] vB_B = {2,3,0,1};
  private static int[] vB_N = {2,3,0,1};
  private static int[] vB_E = {2,3,0,1};
  private static int[] vB_S = {2,3,0,1};
  private static int[] vB_W = {2,3,0,1};

  //N : 90,0,180
  private static int[] vN_A = {0,1,2,3};
  private static int[] vN_B = {2,3,0,1};
  private static int[] vN_N = {0,1,2,3};
  private static int[] vN_E = {1,2,3,0};
  private static int[] vN_S = {2,3,0,1};
  private static int[] vN_W = {3,0,1,2};

  //E : 0,-90,90
  private static int[] vE_A = {1,2,3,0};
  private static int[] vE_B = {1,2,3,0};
  private static int[] vE_N = {3,0,1,2};
  private static int[] vE_E = {0,1,2,3};
  private static int[] vE_S = {1,2,3,0};
  private static int[] vE_W = {2,3,0,1};

  //S : -90,0,0
  private static int[] vS_A = {2,3,0,1};
  private static int[] vS_B = {0,1,2,3};
  private static int[] vS_N = {2,3,0,1};
  private static int[] vS_E = {3,0,1,2};
  private static int[] vS_S = {0,1,2,3};
  private static int[] vS_W = {1,2,3,0};

  //W : 0,90,-90
  private static int[] vW_A = {3,0,1,2};
  private static int[] vW_B = {3,0,1,2};
  private static int[] vW_N = {1,3,2,0};
  private static int[] vW_E = {2,3,0,1};
  private static int[] vW_S = {3,0,1,2};
  private static int[] vW_W = {0,1,2,3};

  /** Rotates a vertex when a Face is rotated.
   Effectively the order of the vertex is changed.
   */
  public int rotateVertex(RenderData data, int vertex) {
    if (data.isDirXZ) {
      switch (data.dirSide) {
        case A:
          switch (data.dir[X]) {
            case A: return vA__A[vertex];
            case B: return vA__B[vertex];
            case N: return vA__N[vertex];
            case E: return vA__E[vertex];
            case S: return vA__S[vertex];
            case W: return vA__W[vertex];
          }
          break;
        case B:
          switch (data.dir[X]) {
            case A: return vB__A[vertex];
            case B: return vB__B[vertex];
            case N: return vB__N[vertex];
            case E: return vB__E[vertex];
            case S: return vB__S[vertex];
            case W: return vB__W[vertex];
          }
          break;
        default:
          return vertex;
      }
    } else if (data.isDir) {
      switch (data.dir[X]) {
        case A:
          switch (data.dirSide) {
            case A: return vA_A[vertex];
            case B: return vA_B[vertex];
            case N: return vA_N[vertex];
            case E: return vA_E[vertex];
            case S: return vA_S[vertex];
            case W: return vA_W[vertex];
          }
          break;
        case B:
          switch (data.dirSide) {
            case A: return vB_A[vertex];
            case B: return vB_B[vertex];
            case N: return vB_N[vertex];
            case E: return vB_E[vertex];
            case S: return vB_S[vertex];
            case W: return vB_W[vertex];
          }
        case N:
          switch (data.dirSide) {
            case A: return vN_A[vertex];
            case B: return vN_B[vertex];
            case N: return vN_N[vertex];
            case E: return vN_E[vertex];
            case S: return vN_S[vertex];
            case W: return vN_W[vertex];
          }
        case E:
          switch (data.dirSide) {
            case A: return vE_A[vertex];
            case B: return vE_B[vertex];
            case N: return vE_N[vertex];
            case E: return vE_E[vertex];
            case S: return vE_S[vertex];
            case W: return vE_W[vertex];
          }
        case S:
          switch (data.dirSide) {
            case A: return vS_A[vertex];
            case B: return vS_B[vertex];
            case N: return vS_N[vertex];
            case E: return vS_E[vertex];
            case S: return vS_S[vertex];
            case W: return vS_W[vertex];
          }
        case W:
          switch (data.dirSide) {
            case A: return vW_A[vertex];
            case B: return vW_B[vertex];
            case N: return vW_N[vertex];
            case E: return vW_E[vertex];
            case S: return vW_S[vertex];
            case W: return vW_W[vertex];
          }
      }
    }
    return vertex;
  }
}
