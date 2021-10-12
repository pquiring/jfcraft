package jfcraft.entity;

/** Domino entity
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.entity.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import jfcraft.move.*;
import static jfcraft.data.Direction.X;

public class Domino extends BlockEntity {
  public boolean fall;
  public float dir;

  public RenderDest dest;  //can not be static, can be damaged
  public static Model model;
  private static TextureMap texture;
  protected static String textureName;

  public static int DOMINO;

  public Domino() {
    id = DOMINO;
  }

  public String getName() {
    return "DOMINO";
  }

  public void getIDs(World world) {
    DOMINO = world.getEntityID("DOMINO");
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init(World world) {
    super.init(world);
    dest = new RenderDest(parts.length);
    width = 0.5f;
    width2 = width/2;
    height = 1.0f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    isBlock = true;
  }

  public void initStatic() {
    model = loadModel("domino");
    textureName = "entity/domino";
  }

  public void initStaticGL() {
    texture = Textures.getTexture(textureName, 0);
  }

  public void initInstance() {
    super.initInstance();
  }

  private static String parts[] = {"DOMINO"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    dest.resetAll();
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      Object3 obj = model.getObject(parts[a]);
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        //latch doesn't crack
        UVMap map = obj.getUVMap("normal");
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        //container & lid
        UVMap map1 = obj.getUVMap("normal");
        float uv1[] = map1.uvl.toArray();
        if (data.crack == -1) {
          buf.addTextureCoords(uv1);
        } else {
          UVMap map2 = obj.getUVMap("crack");
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
    mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
    mat.addRotate2(-ang.x, 1, 0, 0);
    mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
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

  public void fall(float dir) {
    if (ang.x == 0) {
      this.dir = dir;
      fall = true;
    }
  }
  public void tick() {
    float dx = 0, dz = 0;
    if (fall) {
      ang.x += 10.0f * dir;
      Static.server.broadcastEntityMove(this, false);
      //fake moving forward a bit to make next domino fall
      Matrix mat = new Matrix();
      mat.addRotate(ang.y, 0, 1, 0);
      Vector3 vec = new Vector3();
      vec.set(0, 0, -dir);
      mat.mult(vec);
      dx = vec.v[0] * Math.abs(ang.x) / 90.0f;
      dz = vec.v[2] * Math.abs(ang.x) / 90.0f;
      pos.x += dx;
      pos.z += dz;
    }
    super.tick();  //this will trigger etick() on next Domino
    if (fall) {
      pos.x -= dx;
      pos.z -= dz;
      if (Math.abs(ang.x) >= 90.0f) {
        fall = false;
      }
    }
  }

  public boolean canSelect() {
    return false;
  }
}
