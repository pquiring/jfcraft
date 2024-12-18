package jfcraft.opengl;

/** Render Buffers holds all vertex points, and polygons (usually triangles).
 * All polygons share the same orientation (rotation, translation, scale).
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class RenderBuffers implements Cloneable {
  private JFArrayFloat vpl;  //vertex position list
  private JFArrayFloat uvl1;  //texture coords list (normal)
  private JFArrayFloat uvl2;  //texture coords list (overlay) (cracks)
  private JFArrayInt vil;    //vertex index list
  private JFArrayFloat lcl;  //light color list
  private JFArrayFloat sll;  //sun light list
  private JFArrayFloat bll;  //block light list

  public int type;  //GL_TRIANGLES or GL_QUADS

  public boolean isArrayEmpty() {return vil.size() == 0;}
  public boolean isBufferEmpty() {return idxCnt == 0;}

  public boolean visible;
  public Matrix mat;  //current rotation, translation, scale (not used by chunks)
  public Vertex3 org;  //origin (default = 0.0f,0.0f,0.0f) (pivot point) (loaded from file)
  public Vertex3 center;  //calc center point (to scale) (must call callCenter())
  public boolean alloced = false;
  public int vpb, uvb1, uvb2, vib, lcb, slb, blb;  //GL Buffers
  public int idxCnt;

  private static ArrayList<int[]> freeList = new ArrayList<int[]>();
  private static void addFreeList(int bufs[]) {
    synchronized(freeList) {
      freeList.add(bufs);
    }
  }
  public static void freeBuffers() {
    synchronized(freeList) {
      while(freeList.size() > 0) {
        int ids[] = freeList.remove(0);
//        Static.log("Free GL IDs:" + ids.length);
        glDeleteBuffers(ids.length, ids);
      }
    }
  }

  private static float uvZero[] = {0,0};

  public RenderBuffers() {
    vpl = new JFArrayFloat();
    uvl1 = new JFArrayFloat();
    uvl2 = new JFArrayFloat();
    vil = new JFArrayInt();
    lcl = new JFArrayFloat();
    sll = new JFArrayFloat();
    bll = new JFArrayFloat();
    visible = true;
    org = new Vertex3();
    mat = new Matrix();
    type = GL_QUADS;  //default = QUADS
  }

/*
  public void finalize() {
    if (alloced) {
      addFreeList(new int[] {vpb, uvb1, uvb2, vib, lcb, slb, blb});
    }
  }
*/

  public void setVisible(boolean state) {visible = state;}
  private Matrix tmp = new Matrix();
  public void addRotate(float angle, float x, float y, float z, Vertex3 org) {
    //rotates relative to org
    tmp.setAA(angle, x, y, z);  //set rotation
    tmp.addTranslate(org.x, org.y, org.z);  //set translation
    mat.mult4x4(tmp);  //add it
    //now undo translation
    tmp.setIdentity3x3();  //remove rotation
    tmp.reverseTranslate();
    mat.mult4x4(tmp);
  }
  public void addTranslate(float x, float y, float z) {
    mat.addTranslate(x,y,z);
  }
  public void addScale(float x, float y, float z) {
    mat.addScale(x,y,z);
  }
  public void reset() {
//    mat.setIdentity();  //could be rendering...
    vpl.clear();
    uvl1.clear();
    uvl2.clear();
    vil.clear();
    lcl.clear();
    sll.clear();
    bll.clear();
  }
  public void addVertex(float xyz[]) {
    vpl.append(xyz);
  }
  public void addVertex(float x, float y, float z) {
    vpl.append(x);
    vpl.append(y);
    vpl.append(z);
  }
  /** Add vertex with texture coords. */
  public void addVertex(float xyz[], float uv[]) {
    vpl.append(xyz);
    uvl1.append(uv);
    int cnt = uv.length / 2;
    for(int a=0;a<cnt;a++) {
      uvl2.append(uvZero);
    }
  }
  /** Add vertex with texture coords and overlay texture coords. */
  public void addVertex(float xyz[], float uv1[], float uv2[]) {
    vpl.append(xyz);
    uvl1.append(uv1);
    uvl2.append(uv2);
  }
  public void addVertex(float x, float y, float z, float u, float v) {
    vpl.append(x);
    vpl.append(y);
    vpl.append(z);
    uvl1.append(u);
    uvl1.append(v);
  }
  /** Add texture coords. */
  public void addTextureCoords(float uv[]) {
    uvl1.append(uv);
    int cnt = uv.length / 2;
    for(int a=0;a<cnt;a++) {
      uvl2.append(uvZero);
    }
  }
  public void addTextureCoords(float uv1[], float uv2[]) {
    uvl1.append(uv1);
    uvl2.append(uv2);
  }
  public void addTextureCoords1(float uv[]) {
    uvl1.append(uv);
  }
  public void addTextureCoords2(float uv[]) {
    uvl2.append(uv);
  }
  public void addVertex(Vertex3 v) {
    vpl.append(v.x);
    vpl.append(v.y);
    vpl.append(v.z);
    uvl1.append(v.u);
    uvl1.append(v.v);
    uvl2.append(0);
    uvl2.append(0);
  }
  public void addColor() {
    if (Static.data.isGreen) {
      float clr[] = {0,0.5f + (Static.data.temp / 200.0f),0};
      addColor(clr);
    } else if (Static.data.isRed) {
      float clr[] = {(1 + Static.data.var[X]) / 16.0f,0,0};
      addColor(clr);
    } else if (Static.data.isBlue) {
      float clr[] = {0,0,0.5f + (Static.data.temp / 200.0f)};
      addColor(clr);
    } else {
      if (Static.data.clr != null) {
        addColor(Static.data.clr);
      } else {
        addColor(Static.white);
      }
    }
  }
  public void addColor(float rgb[]) {
    lcl.append(rgb);
  }
  public void addSunLight(float lvl) {
    if (Static.debugLights) {
      lvl = 1.0f;
    }
    sll.append(lvl);
  }
  public void addBlockLight(float lvl) {
    bll.append(lvl);
  }
  private static float base_light = 70f;  //(use 10f to debug)
  private static float adj_light = 10f;  //3 adj block levels  (use 30f to debug)
  //TODO : need lighting from ANE,ANW,etc blocks to better implement this
  private float calcLight(Face f, float[] lvls, int vertex) {
    //vertex are in CW order starting at top left (but Face.rotate() rotates them)
    //A/B faces are viewed from player perspective
    float lvl = lvls[Static.data.dirSide] * base_light;
    vertex = f.rotateVertex(vertex);
    switch (Static.data.dirSide) {
      case N:
        switch (vertex) {
          case 0: lvl += lvls[NE] * adj_light; lvl += lvls[ANE] * adj_light; lvl += lvls[AN] * adj_light; break;
          case 1: lvl += lvls[AN] * adj_light; lvl += lvls[ANW] * adj_light; lvl += lvls[NW] * adj_light; break;
          case 2: lvl += lvls[NW] * adj_light; lvl += lvls[BNW] * adj_light; lvl += lvls[BN] * adj_light; break;
          case 3: lvl += lvls[BN] * adj_light; lvl += lvls[BNE] * adj_light; lvl += lvls[NE] * adj_light; break;
        }
        break;
      case E:
        switch (vertex) {
          case 0: lvl += lvls[SE] * adj_light; lvl += lvls[ASE] * adj_light; lvl += lvls[AE] * adj_light; break;
          case 1: lvl += lvls[AE] * adj_light; lvl += lvls[ANE] * adj_light; lvl += lvls[NE] * adj_light; break;
          case 2: lvl += lvls[NE] * adj_light; lvl += lvls[BNE] * adj_light; lvl += lvls[BE] * adj_light; break;
          case 3: lvl += lvls[BE] * adj_light; lvl += lvls[BSE] * adj_light; lvl += lvls[SE] * adj_light; break;
        }
        break;
      case S:
        switch (vertex) {
          case 0: lvl += lvls[SW] * adj_light; lvl += lvls[ASW] * adj_light; lvl += lvls[AS] * adj_light; break;
          case 1: lvl += lvls[AS] * adj_light; lvl += lvls[ASE] * adj_light; lvl += lvls[SE] * adj_light; break;
          case 2: lvl += lvls[SE] * adj_light; lvl += lvls[BSE] * adj_light; lvl += lvls[BS] * adj_light; break;
          case 3: lvl += lvls[BS] * adj_light; lvl += lvls[BSW] * adj_light; lvl += lvls[SW] * adj_light; break;
        }
        break;
      case W:
        switch (vertex) {
          case 0: lvl += lvls[NW] * adj_light; lvl += lvls[ANW] * adj_light; lvl += lvls[AW] * adj_light; break;
          case 1: lvl += lvls[AW] * adj_light; lvl += lvls[ASW] * adj_light; lvl += lvls[SW] * adj_light; break;
          case 2: lvl += lvls[SW] * adj_light; lvl += lvls[BSW] * adj_light; lvl += lvls[BW] * adj_light; break;
          case 3: lvl += lvls[BW] * adj_light; lvl += lvls[BNW] * adj_light; lvl += lvls[NW] * adj_light; break;
        }
        break;
      case A:
        switch (vertex) {
          case 0: lvl += lvls[AW] * adj_light; lvl += lvls[ANW] * adj_light; lvl += lvls[AN] * adj_light; break;
          case 1: lvl += lvls[AN] * adj_light; lvl += lvls[ANE] * adj_light; lvl += lvls[AE] * adj_light; break;
          case 2: lvl += lvls[AE] * adj_light; lvl += lvls[ASE] * adj_light; lvl += lvls[AS] * adj_light; break;
          case 3: lvl += lvls[AS] * adj_light; lvl += lvls[ASW] * adj_light; lvl += lvls[AW] * adj_light; break;
        }
        break;
      case B:
        switch (vertex) {
          case 0: lvl += lvls[BW] * adj_light; lvl += lvls[BSW] * adj_light; lvl += lvls[BS] * adj_light; break;
          case 1: lvl += lvls[BS] * adj_light; lvl += lvls[BSE] * adj_light; lvl += lvls[BE] * adj_light; break;
          case 2: lvl += lvls[BE] * adj_light; lvl += lvls[BNE] * adj_light; lvl += lvls[BN] * adj_light; break;
          case 3: lvl += lvls[BN] * adj_light; lvl += lvls[BNW] * adj_light; lvl += lvls[BW] * adj_light; break;
        }
        break;
    }
    return lvl / 100.0f;
  }
  public void addSunLight(Face f, int vertex) {
    float lvl;
    if (Static.data.adjLight) {
      //complex adj lighting
      lvl = calcLight(f, Static.data.sl, vertex);
    } else {
      //simple X lighting
      lvl = Static.data.sl[X];
    }
    //dim lights on the sides and bottom
    switch (Static.data.dirSide) {
//      case A:
//        break;
      case N:
      case S:
        if (lvl > 0) lvl -= Static._0_5_15;
        break;
      case E:
      case W:
        if (lvl > 0) lvl -= Static._0_25_15;
        break;
      case B:
        if (lvl > 0) lvl -= Static._0_75_15;
        break;
    }
    if (Static.debugLights) {
      lvl = 1.0f;
    }
    sll.append(lvl);
  }
  public void addBlockLight(Face f, int vertex) {
    float lvl;
    if (Static.data.adjLight) {
      //complex adj lighting
      lvl = calcLight(f, Static.data.bl, vertex);
    } else {
      //simple X lighting
      lvl = Static.data.bl[X];
    }
    //dim lights on the sides and bottom
    switch (Static.data.dirSide) {
//      case A:
//        break;
      case N:
      case S:
        if (lvl > 0) lvl -= Static._0_5_15;
        break;
      case E:
      case W:
        if (lvl > 0) lvl -= Static._0_25_15;
        break;
      case B:
        if (lvl > 0) lvl -= Static._0_75_15;
        break;
    }
    if (Static.debugLights) {
      lvl = 1.0f;
    }
    bll.append(lvl);
  }
  public float sunLight = 1.0f;
  public float blkLight = 0.0f;
  public void addDefault() {
    addColor(Static.white);
    addSunLight(sunLight);
    addBlockLight(blkLight);
  }
  public void addDefault(float color[]) {
    addColor(color);
    addSunLight(sunLight);
    addBlockLight(blkLight);
  }
  public void addPoly(int pts[]) {
    vil.append(pts);
  }
  /** Adds a 2D face (billboard). */
  public void addFace2D(float fx1, float fy1, float fx2, float fy2, float u1, float v1, float u2, float v2, float clr[]) {
    int off = getVertexCount();
    addVertex(new float[] {fx1,fy1,0}, new float[] {u1,v1});
    addVertex(new float[] {fx2,fy1,0}, new float[] {u2,v1});
    addVertex(new float[] {fx2,fy2,0}, new float[] {u2,v2});
    addVertex(new float[] {fx1,fy2,0}, new float[] {u1,v2});
    for(int a=0;a<4;a++) {
      addDefault(clr);
    }
    addPoly(new int[] {off+3,off+2,off+1,off+0});
  }
  /** Adds a 2D face (billboard). */
  public void addFace2D(float fx1, float fy1, float u1, float v1, float w, float h, float clr[]) {
    float fx2 = fx1 + w;
    float fy2 = fy1 + h;
    float u2 = u1 + w;
    float v2 = v1 + h;
    int off = getVertexCount();
    addVertex(new float[] {fx1,fy1,0}, new float[] {u1,v1});
    addVertex(new float[] {fx2,fy1,0}, new float[] {u2,v1});
    addVertex(new float[] {fx2,fy2,0}, new float[] {u2,v2});
    addVertex(new float[] {fx1,fy2,0}, new float[] {u1,v2});
    for(int a=0;a<4;a++) {
      addDefault(clr);
    }
    addPoly(new int[] {off+3,off+2,off+1,off+0});
  }

  /** Adds any face that is upright for Entity. */
  public void addFace(float fx1, float fy1, float fz1, float fx2, float fy2, float fz2, float u1, float v1, float u2, float v2) {
    Face f = new Face();
    f.x[0] = fx1;
    f.x[1] = fx2;
    f.x[2] = fx2;
    f.x[3] = fx1;

    f.y[0] = fy1;
    f.y[1] = fy1;
    f.y[2] = fy2;
    f.y[3] = fy2;

    f.z[0] = fz1;
    f.z[1] = fz2;
    f.z[2] = fz2;
    f.z[3] = fz1;

    f.u1[0] = u1;
    f.v1[0] = v1;
    f.u1[1] = u2;
    f.v1[1] = v1;

    f.u1[2] = u2;
    f.v1[2] = v2;
    f.u1[3] = u1;
    f.v1[3] = v2;
    addFace(f);
  }
  /** Adds a face that is laying flat (up or down) for Entity. */
  public void addFaceAB(float fx1, float fy1, float fz1, float fx2, float fy2, float fz2, float u1, float v1, float u2, float v2) {
    Static.data.sl[X] = 1;
    Face f = new Face();
    f.x[0] = fx1;
    f.x[1] = fx2;
    f.x[2] = fx2;
    f.x[3] = fx1;
    f.y[0] = fy1;
    f.y[1] = fy1;
    f.y[2] = fy2;
    f.y[3] = fy2;
    f.z[0] = fz1;
    f.z[1] = fz1;
    f.z[2] = fz2;
    f.z[3] = fz2;
    f.u1[0] = u1;
    f.v1[0] = v1;
    f.u1[1] = u2;
    f.v1[1] = v1;
    f.u1[2] = u2;
    f.v1[2] = v2;
    f.u1[3] = u1;
    f.v1[3] = v2;
    addFace(f);
  }

  /** Adds a box for the sky.
   * Each face is inward.
   * Texture coords are 0,0-1,1 for each face.
   * @param fx/fy/fz = coords of cube
   */
  public void addSkyBox(float fx1, float fy1, float fz1, float fx2, float fy2, float fz2) {
    addFace  (fx2,fy1,fz1, fx1,fy2,fz1, 0,0,1,1);  //N
    addFace  (fx1,fy1,fz2, fx2,fy2,fz2, 0,0,1,1);  //S
    addFace  (fx1,fy1,fz1, fx1,fy2,fz2, 0,0,1,1);  //W
    addFace  (fx2,fy1,fz2, fx2,fy2,fz1, 0,0,1,1);  //E
    addFaceAB(fx1,fy2,fz2, fx2,fy2,fz1, 0,0,1,1);  //A
    addFaceAB(fx1,fy1,fz1, fx2,fy1,fz2, 0,0,1,1);  //B
  }

  /** Adds a box for the horizon.
   * Each face is inward.
   * Texture coords are 0,0-1,1 for each face.
   * @param fx/fy/fz = coords of cube
   */
  public void addHorizonBox(float fx1, float fy1, float fz1, float fx2, float fy2, float fz2) {
    addFace  (fx2,fy1,fz1, fx1,fy2,fz1, 0,0,1,1);  //N
    addFace  (fx1,fy1,fz2, fx2,fy2,fz2, 0,0,1,1);  //S
    addFace  (fx1,fy1,fz1, fx1,fy2,fz2, 0,0,1,1);  //W
    addFace  (fx2,fy1,fz2, fx2,fy2,fz1, 0,0,1,1);  //E
//    addFaceAB(fx1,fy2,fz2, fx2,fy2,fz1, 0,0,1,1,clr);  //A
    addFaceAB(fx1,fy1,fz1, fx2,fy1,fz2, 0,0,1,1);  //B
  }

  /** Adds a normal box.
   * Each face is outward.
   * Texture coords are 0,0-1,1 for each face.
   * @param fx/fy/fz = coords of cube
   */
  public void addBox(float fx1, float fy1, float fz1, float fx2, float fy2, float fz2) {
    addFace  (fx2,fy1,fz2, fx1,fy2,fz2, 0,0,1,1);  //N
    addFace  (fx1,fy1,fz1, fx2,fy2,fz1, 0,0,1,1);  //S
    addFace  (fx2,fy1,fz1, fx2,fy2,fz2, 0,0,1,1);  //W
    addFace  (fx1,fy1,fz2, fx1,fy2,fz1, 0,0,1,1);  //E
    addFaceAB(fx1,fy1,fz2, fx2,fy1,fz1, 0,0,1,1);  //A
    addFaceAB(fx1,fy2,fz1, fx2,fy2,fz2, 0,0,1,1);  //B
  }

  /** Adds a face for a block. */
  public void addFace(SubTexture st) {
    Face f = new Face();

    f.u1[0] = st.x1;
    f.v1[0] = st.y1;
    f.u1[1] = st.x2;
    f.v1[1] = st.y1;
    f.u1[2] = st.x2;
    f.v1[2] = st.y2;
    f.u1[3] = st.x1;
    f.v1[3] = st.y2;

    switch (Static.data.side) {
      case N:
        f.x[0] = 1;
        f.y[0] = 1;
        f.z[0] = 0;

        f.x[1] = 0;
        f.y[1] = 1;
        f.z[1] = f.z[0];

        f.x[2] = 0;
        f.y[2] = 0;
        f.z[2] = f.z[0];

        f.x[3] = 1;
        f.y[3] = 0;
        f.z[3] = f.z[0];
        break;
      case E:
        f.x[0] = 1;
        f.y[0] = 1;
        f.z[0] = 1;

        f.x[1] = f.x[0];
        f.y[1] = 1;
        f.z[1] = 0;

        f.x[2] = f.x[0];
        f.y[2] = 0;
        f.z[2] = 0;

        f.x[3] = f.x[0];
        f.y[3] = 0;
        f.z[3] = 1;
        break;
      case S:
        f.x[0] = 0;
        f.y[0] = 1;
        f.z[0] = 1;

        f.x[1] = 1;
        f.y[1] = 1;
        f.z[1] = f.z[0];

        f.x[2] = 1;
        f.y[2] = 0;
        f.z[2] = f.z[0];

        f.x[3] = 0;
        f.y[3] = 0;
        f.z[3] = f.z[0];
        break;
      case W:
        f.x[0] = 0;
        f.y[0] = 1;
        f.z[0] = 0;

        f.x[1] = f.x[0];
        f.y[1] = 1;
        f.z[1] = 1;

        f.x[2] = f.x[0];
        f.y[2] = 0;
        f.z[2] = 1;

        f.x[3] = f.x[0];
        f.y[3] = 0;
        f.z[3] = 0;
        break;
      case A:
        f.x[0] = 0;
        f.y[0] = 1;
        f.z[0] = 0;

        f.x[1] = 1;
        f.y[1] = 1;
        f.z[1] = 0;

        f.x[2] = 1;
        f.y[2] = 1;
        f.z[2] = 1;

        f.x[3] = 0;
        f.y[3] = 1;
        f.z[3] = 1;
        break;
      case B:
        f.x[0] = 0;
        f.y[0] = 0;
        f.z[0] = 1;

        f.x[1] = 1;
        f.y[1] = 0;
        f.z[1] = 1;

        f.x[2] = 1;
        f.y[2] = 0;
        f.z[2] = 0;

        f.x[3] = 0;
        f.y[3] = 0;
        f.z[3] = 0;
        break;
      default:
        Static.log("Invalid side:" + Static.data.side);
        return;
    }

    if (Static.data.adjLight)
      addFaceComplexLighting(f);
    else
      addFace(f);
  }

  /** Adds face for Blocks w/ complex lighting. */
  private void addFaceComplexLighting(Face f) {
    f.rotate();

    if (Static.data.crack != -1) {
      SubTexture crack = Static.blocks.subcracks[Static.data.crack];

      f.u2[0] = crack.x1;
      f.v2[0] = crack.y1;
      f.u2[1] = crack.x2;
      f.v2[1] = crack.y1;
      f.u2[2] = crack.x2;
      f.v2[2] = crack.y2;
      f.u2[3] = crack.x1;
      f.v2[3] = crack.y2;
    } else {
      f.u2[0] = 0;
      f.v2[0] = 0;
      f.u2[1] = 0;
      f.v2[1] = 0;
      f.u2[2] = 0;
      f.v2[2] = 0;
      f.u2[3] = 0;
      f.v2[3] = 0;
    }

    for(int a=0;a<4;a++) {
      f.x[a] += Static.data.x;
      f.y[a] += Static.data.y;
      f.z[a] += Static.data.z;
    }

    int off = getVertexCount();
    addVertex(new float[] {f.x[0],f.y[0],f.z[0]}, new float[] {f.u1[0],f.v1[0]}, new float[] {f.u2[0],f.v2[0]});
    addVertex(new float[] {f.x[1],f.y[1],f.z[1]}, new float[] {f.u1[1],f.v1[1]}, new float[] {f.u2[1],f.v2[1]});
    addVertex(new float[] {f.x[2],f.y[2],f.z[2]}, new float[] {f.u1[2],f.v1[2]}, new float[] {f.u2[2],f.v2[2]});
    addVertex(new float[] {f.x[3],f.y[3],f.z[3]}, new float[] {f.u1[3],f.v1[3]}, new float[] {f.u2[3],f.v2[3]});

    for(int a=0;a<4;a++) {
      addColor();
      addSunLight(f, a);
      addBlockLight(f, a);
    }
    addPoly(new int[] {off+3,off+2,off+1,off+0});
    if (Static.data.doubleSided) {
      addPoly(new int[] {off+0,off+1,off+2,off+3});
    }
  }

  /** Adds complex face for Blocks/Entities. */
  public void addFace(Face f) {
    f.rotate();

    if (Static.data.crack != -1) {
      SubTexture crack = Static.blocks.subcracks[Static.data.crack];

      f.u2[0] = crack.x1;
      f.v2[0] = crack.y1;
      f.u2[1] = crack.x2;
      f.v2[1] = crack.y1;
      f.u2[2] = crack.x2;
      f.v2[2] = crack.y2;
      f.u2[3] = crack.x1;
      f.v2[3] = crack.y2;
    } else {
      f.u2[0] = 0;
      f.v2[0] = 0;
      f.u2[1] = 0;
      f.v2[1] = 0;
      f.u2[2] = 0;
      f.v2[2] = 0;
      f.u2[3] = 0;
      f.v2[3] = 0;
    }

    for(int a=0;a<4;a++) {
      f.x[a] += Static.data.x;
      f.y[a] += Static.data.y;
      f.z[a] += Static.data.z;
    }

    int off = getVertexCount();
    addVertex(new float[] {f.x[0],f.y[0],f.z[0]}, new float[] {f.u1[0],f.v1[0]}, new float[] {f.u2[0],f.v2[0]});
    addVertex(new float[] {f.x[1],f.y[1],f.z[1]}, new float[] {f.u1[1],f.v1[1]}, new float[] {f.u2[1],f.v2[1]});
    addVertex(new float[] {f.x[2],f.y[2],f.z[2]}, new float[] {f.u1[2],f.v1[2]}, new float[] {f.u2[2],f.v2[2]});
    addVertex(new float[] {f.x[3],f.y[3],f.z[3]}, new float[] {f.u1[3],f.v1[3]}, new float[] {f.u2[3],f.v2[3]});

    for(int a=0;a<4;a++) {
      addColor();
      addSunLight(Static.data.sl[X]);
      addBlockLight(Static.data.bl[X]);
    }
    addPoly(new int[] {off+3,off+2,off+1,off+0});
    if (Static.data.doubleSided) {
      addPoly(new int[] {off+0,off+1,off+2,off+3});
    }
  }

  public int getVertexCount() {
    return vpl.size() / 3;
  }

  public int getIndexCount() {
    return vil.size();
  }

  /** Changes color of entire object.  Must call copyBuffers(GL) after. */
  public void setClr(float clr[]) {
    int cnt = lcl.size();
    lcl.clear();
    for(int a=0;a<cnt;a++) {
      lcl.append(clr);
    }
  }

  public void alloc() {
    if (alloced) return;
    int ids[] = new int[7];
    glGenBuffers(ids.length, ids);
    vpb = ids[0];
    uvb1 = ids[1];
    uvb2 = ids[2];
    vib = ids[3];
    lcb = ids[4];
    slb = ids[5];
    blb = ids[6];
    alloced = true;
  }

  public void free() {
    if (!alloced) return;
    int ids[] = new int[] {vpb, uvb1, uvb2, vib, lcb, slb, blb};
    glDeleteBuffers(ids.length, ids);
    alloced = false;
  }

  /** Copies stored vertex/poly data to GL buffers */
  public void copyBuffers() {
    float fa[];
    int ia[];

    if (!alloced) {
      alloc();
    }

    glBindBuffer(GL_ARRAY_BUFFER, vpb);
    fa = vpl.getBuffer();  //warning getBuffer() may be larger than toArray()
    glBufferData(GL_ARRAY_BUFFER, vpl.size() * 4, fa, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, uvb1);
    fa = uvl1.getBuffer();
    glBufferData(GL_ARRAY_BUFFER, uvl1.size() * 4, fa, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, uvb2);
    fa = uvl2.getBuffer();
    glBufferData(GL_ARRAY_BUFFER, uvl2.size() * 4, fa, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vib);
    ia = vil.getBuffer();
    idxCnt = vil.size();
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, vil.size() * 4, ia, GL_STREAM_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, lcb);
    fa = lcl.getBuffer();
    glBufferData(GL_ARRAY_BUFFER, lcl.size() * 4, fa, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, slb);
    fa = sll.getBuffer();
    glBufferData(GL_ARRAY_BUFFER, sll.size() * 4, fa, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, blb);
    fa = bll.getBuffer();
    glBufferData(GL_ARRAY_BUFFER, bll.size() * 4, fa, GL_STATIC_DRAW);
  }

  public void bindBuffers() {
    if (idxCnt == 0) {
      //Static.logTrace("Error:RenderBuffer.bindBuffers() but buffers are empty");
      return;
    }
    glBindBuffer(GL_ARRAY_BUFFER, vpb);
    glVertexAttribPointer(Static.attribVertex, 3, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, uvb1);
    glVertexAttribPointer(Static.attribTextureCoords, 2, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, uvb2);
    glVertexAttribPointer(Static.attribTextureCoords2, 2, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, lcb);
    glVertexAttribPointer(Static.attribColor, 3, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, slb);
    glVertexAttribPointer(Static.attribSunLight, 1, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, blb);
    glVertexAttribPointer(Static.attribBlockLight, 1, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vib);
  }

  public void render() {
    if (idxCnt == 0) return;
    glDrawElements(type, idxCnt, GL_UNSIGNED_INT, 0);
  }

  /** Adjust UV coords for a stitched texture */
  public static void adjustTexture(float uv[], SubTexture tex) {
    for(int a=0;a<uv.length;) {
      uv[a] = tex.x1 + (uv[a] * tex.width);
      a++;
      uv[a] = tex.y1 + (uv[a] * tex.height);
      a++;
    }
  }

  /** Adjust UV coords for cracking */
  public static void adjustCrack(float uv[], int crack) {
    adjustTexture(uv, Static.blocks.subcracks[crack]);
  }

  /** Calcs the center of the cube. */
  public void calcCenter() {
    center = new Vertex3();
    float v[] = vpl.getBuffer();
    int cnt = vpl.size() / 3;
    float x1 = v[0];
    float y1 = v[1];
    float z1 = v[2];
    float x2 = x1;
    float y2 = y1;
    float z2 = z1;
    int p = 3;
    for(int a=1;a<cnt;a++) {
      float x = v[p++];
      float y = v[p++];
      float z = v[p++];
      if (x < x1) x1 = x;
      if (x > x2) x2 = x;
      if (y < y1) y1 = y;
      if (y > y2) y2 = y;
      if (z < z1) z1 = z;
      if (z > z2) z2 = z;
    }
    center.x = x1 + (x2 - x1)/2f;
    center.y = y1 + (y2 - y1)/2f;
    center.z = z1 + (z2 - z1)/2f;
  }

  public String toString() {
    return "RenderBuffers:" + idxCnt;
  }
}
