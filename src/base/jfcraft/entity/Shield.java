package jfcraft.entity;

/** Shield entity
 *
 * @author pquiring
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Shield extends BlockEntity {
  public float lidAngle;
  public RenderDest dest;  //TODO : make this static since Shield's can not be damaged
  public static GLModel model;

  //render assets
  private static Texture texture;
  protected static String textureName;

  public Shield() {
    id = Entities.SHIELD;
    part = L_ITEM;
  }

  public String getName() {
    return "shield";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init(World world) {
    super.init(world);
    dest = new RenderDest(parts.length);
  }

  public void initStatic() {
    super.initStatic();
    textureName = "entity/shield_base_nopattern";
    model = loadModel("shield");
  }

  public void initStaticGL() {
    super.initStaticGL();
    texture = Textures.getTexture(textureName, 0);
  }

  public void initInstance() {
    super.initInstance();
  }

  private static String parts[] = {null, null, null, null, null, "L_SHIELD", "R_SHIELD"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    dest.resetAll();
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      if (parts[a] == null) continue;
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = model.getObject(parts[a]);
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        GLUVMap map = obj.getUVMap("normal");
        buf.addTextureCoords(map.uvl.toArray());
      } else {
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

  public void render() {
    RenderBuffers buf = dest.getBuffers(part);
    buf.bindBuffers();
    buf.render();
  }
}
