package jfcraft.entity;

/** Piston entity
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.block.*;
import static jfcraft.data.Direction.X;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Piston extends BlockEntity {
  //persistent data
  public boolean sticky;

  public float extend;
  public RenderDest dest;  //can not be static, can be damaged
  public static GLModel model;

  public Piston() {
    id = Entities.PISTON;
  }

  public Piston setSticky() {
    id = Entities.PISTON_STICKY;
    sticky = true;
    return this;
  }

  public String getName() {
    if (!sticky)
      return "PISTON";
    else
      return "PISTON_STICKY";
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
    super.initStatic();
    model = loadModel("piston");
  }

  public void initStaticGL() {
    super.initStaticGL();
  }

  public void initInstance() {
    super.initInstance();
  }

  private static String parts[] = {"BASE_BOTTOM", "BASE_SIDES", "BASE_TOP", "SHAFT", "PLATE_BOTTOM", "PLATE_SIDES", "PLATE_TOP"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    extend = Static._1_16 * data.var[X] * 2.0f;
    dest.resetAll();
    SubTexture textures[];
    if (!sticky) {
      BlockBase bb = Static.blocks.getRegisteredBlock("piston");
      textures = bb.textures;
    } else {
      BlockBase bb = Static.blocks.getRegisteredBlock("piston_sticky");
      textures = bb.textures;
    }
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

  public void bindTexture() {
    //bind stitched texture
    Static.blocks.stitched.bind();
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.x, 1, 0, 0);
    mat.addRotate3(-ang.z, 0, 0, 1);
    switch (bodyPart) {
      case 0:  //base_bottom
      case 1:  //base_sides
      case 2:  //base_top
        break;
      case 3:  //shaft
        if (extend > 0.25f) {
          mat.addTranslate2(0, extend - 0.25f, 0);
        }
        break;
      case 4:  //plate_bottom
      case 5:  //plate_side
      case 6:  //plate_top
        if (extend > 0) {
          mat.addTranslate2(0, extend, 0);
        }
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
    return false;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeBoolean(sticky);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    sticky = buffer.readBoolean();
    return true;
  }
}
