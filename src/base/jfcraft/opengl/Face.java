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

  private static GLMatrix mat = new GLMatrix();
  private static GLVector3 vec = new GLVector3();

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
}
