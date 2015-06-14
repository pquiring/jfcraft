package jfcraft.entity;

/** Lever entity
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Lever extends BlockEntity {
  public transient RenderDest dest;  //can not be static, can be damaged
  public transient boolean active;
  public static GLModel model;

  public Lever() {
    id = Entities.LEVER;
  }

  public String getName() {
    return "LEVER";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init() {
    super.init();
    dest = new RenderDest(parts.length);
    isBlock = true;
  }

  public void initStatic() {
    model = loadModel("lever");
  }

  public void initStatic(GL gl) {
  }

  public void initInstance(GL gl) {
    super.initInstance(gl);
  }

  private static String parts[] = {"STICK", "BASE"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    this.active = data.active;
    dest.resetAll();
    SubTexture textures[];
    textures = Static.blocks.getRegisteredBlock("lever").textures;
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = model.getObject(parts[a]);
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        //no cracking
        GLUVMap map = obj.getUVMap("normal");
        float uv1[] = map.uvl.toArray();
        buf.adjustTexture(uv1, textures[a]);
        if (data.crack == -1) {
          buf.addTextureCoords(uv1);
        } else {
          float uv2[] = map.uvl.toArray();
          buf.adjustCrack(uv2, data.crack);
          buf.addTextureCoords(uv1, uv2);
        }
      } else {
        //cracking
        GLUVMap map1 = obj.getUVMap("normal");
        float uv1[] = map1.uvl.toArray();
        buf.adjustTexture(uv1, textures[a]);
        if (data.crack == -1) {
          buf.addTextureCoords(uv1);
        } else {
          GLUVMap map2 = obj.getUVMap("crack");
          float uv2[] = map2.uvl.toArray();
          buf.adjustCrack(uv2, data.crack);
          buf.addTextureCoords(uv1, uv2);
        }
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
  }

  public void bindTexture(GL gl) {
    //bind stitched texture
    Static.blocks.stitched.bind(gl);
  }

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
  }

  private void setMatrixModel(GL gl, int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
//    mat.addRotate(-xAngle, 0, 1, 0);
//    mat.addRotate3(-zAngle, 0, 1, 0);
    switch (bodyPart) {
      case 0:  //stick
        if (active) {
          mat.addRotate(45, 0, 0, 1);
        } else {
          mat.addRotate(-45, 0, 0, 1);
        }
        break;
      case 1:  //base
        break;
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    if (scale != 1.0f) {
      mat.addScale(scale, scale, scale);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
  }

  public void render(GL gl) {
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(gl, a, buf);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix
  }
  public Item[] drop() {
    return new Item[] {new Item(Blocks.LEVER)};
  }
  public boolean canSelect() {
    return false;
  }
}
