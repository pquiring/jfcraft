package jfcraft.entity;

/** Cow entity
 *
 * @author pquiring
 *
 * Created : Aug 10, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.data.*;
import jfcraft.move.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Cow extends CreatureBase {
  public float walkAngle;  //angle of legs/arms as walking
  public float walkAngleDelta;
  public static RenderDest dest;

  //render assets
  public static Texture texture;
  protected static String textureName;
  private static GLModel model;

  public static int initHealth = 10;

  public Cow() {
    id = Entities.COW;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "cow";
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.0f;
    height2 = height/2;
    depth = 1.5f;
    depth2 = depth/2;
    walkAngleDelta = 5.0f;
    if (world.isServer) {
      eyeHeight = 0.5f;
      jumpVelocity = 0.58f;  //results in jump of 1.42
      //speeds are blocks per second
      walkSpeed = 2.3f;
      runSpeed = 3.9f;
      sneakSpeed = 1.3f;
      swimSpeed = (walkSpeed / 2.0f);
    }
    setMove(new MoveCreature());
  }

  public void initStatic() {
    super.initStatic();
    textureName = "entity/cow/cow";
    dest = new RenderDest(parts.length);
    model = loadModel("cow");
  }

  public void initStaticGL() {
    super.initStaticGL();
    texture = Textures.getTexture(textureName, 0);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "L_HORN", "R_HORN"};

  private static final int L_HORN = 6;
  private static final int R_HORN = 7;

  public void buildBuffers(RenderDest dest, RenderData data) {
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
        GLUVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        GLUVMap map1 = obj.maps.get(0);
        GLUVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
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

  //transforms are applied in reverse
  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
      case L_HORN:
      case R_HORN:
        mat.addTranslate2(0, buf.org.y, 0);
        mat.addRotate2(-ang.x, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case BODY:
        break;
      case L_ARM:
      case R_LEG:
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(walkAngle, 1, 0, 0);
        mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
        break;
      case R_ARM:
      case L_LEG:
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
        break;
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public void ctick() {
    float delta = 0;
    switch (mode) {
      case MODE_IDLE:
        walkAngle = 0.0f;
        return;
      case MODE_RUN:
        delta = walkAngleDelta * 2f;
        break;
      case MODE_SWIM:
      case MODE_WALK:
        delta = walkAngleDelta;
        break;
      case MODE_SNEAK:
        delta = walkAngleDelta / 2f;
        break;
    }
    walkAngle += delta;
    if ((walkAngle < -45.0) || (walkAngle > 45.0)) {
      walkAngleDelta *= -1;
    }
  }

  public void render() {
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(a, buf);
      buf.bindBuffers();
      buf.render();
    }
  }

  private static Random r = new Random();
  public EntityBase spawn(Chunk chunk) {
    World world = Static.server.world;
    float px = r.nextInt(16) + chunk.cx * 16.0f + 0.5f;
    float pz = r.nextInt(16) + chunk.cz * 16.0f + 0.5f;
    for(float gy = 255;gy>0;gy--) {
      float py = gy;
      if (world.isEmpty(chunk.dim,px,py,pz)
        && world.isEmpty(chunk.dim,px,py-1,pz)
        && world.canSpawnOn(chunk.dim,px,py-2,pz))
      {
        py -= 1.0f;
        Cow e = new Cow();
        e.init(world);
        e.dim = chunk.dim;
        e.health = initHealth;
        e.pos.x = px;
        e.pos.y = py;
        e.pos.z = pz;
        e.ang.y = r.nextInt(360);
        return e;
      }
    }
    return null;
  }
  public float getSpawnRate() {
    return 2.0f;
  }
  public Item[] drop() {
    Random r = new Random();
    Item items[] = new Item[2];
    items[0] = new Item(Items.STEAK_RAW, 0, r.nextInt(2)+1);
    items[1] = new Item(Items.LEATHER, 0, r.nextInt(2)+1);
    return items;
  }
  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
      Static.server.broadcastSound(dim, pos.x, pos.y, pos.z, Sounds.SOUND_COW, 1);
    }
  }
  public int[] getGenerateDims() {
    return new int[] {Dims.EARTH};
  }
}
