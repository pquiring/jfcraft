package jfcraft.entity;

/** Chest entity
 *
 * @author pquiring
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Chest extends BlockEntity {
  public float lidAngle;
  public RenderDest dest;  //can not be static since chest can be damaged
  public static GLModel model;

  //render assets
  private static Texture texture;
  protected static String textureName;

  public Chest() {
    id = Entities.CHEST;
  }

  public String getName() {
    return "chest";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init(World world) {
    super.init(world);
    dest = new RenderDest(parts.length);
    isBlock = true;
  }

  public void initStatic() {
    textureName = "entity/chest/normal";
    model = loadModel("chest");
  }

  public void initStaticGL() {
    texture = Textures.getTexture(textureName, 0);
  }

  public void initInstance() {
    super.initInstance();
  }

  private static String parts[] = {"CONTAINER", "LID", "LATCH"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    dest.resetAll();
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
        //latch doesn't crack
        GLUVMap map = obj.getUVMap("normal");
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        //container & lid
        GLUVMap map1 = obj.getUVMap("normal");
        float uv1[] = map1.uvl.toArray();
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

  public void bindTexture() {
    texture.bind();
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case 0:  //container
        break;
      case 1:  //lid
      case 2:  //lock
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(lidAngle, 1, 0, 0);
        mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
        break;
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    if (scale != 1.0f) {
      mat.addScale(scale, scale, scale);
    }
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public void render() {
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(a, buf);
      buf.bindBuffers();
      buf.render();
    }
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, Static.identity.m);  //model matrix
  }
  public boolean canSelect() {
    return true;
  }
}
