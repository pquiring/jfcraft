package jfcraft.entity;

/** Zombie Pigman entity
 *
 * @author pquiring
 *
 * Created : May 17, 2015
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import jfcraft.move.*;

public class ZombiePigman extends HumaniodBase {
  private float walkAngle;  //angle of legs/arms as walking
  private float walkAngleDelta;

  //render assets
  private static RenderDest dest;
  private static TextureMap texture;
  private static String textureName;
  private static Model model;

  private static int initHealth = 20;
  private static int initArmor = 2;

  public ZombiePigman() {
    super(1, 4);
    id = Entities.ZOMBIE_PIGMAN;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "Zombie Pigman";
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.6f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    walkAngleDelta = 5.0f;
    if (world.isServer) {
      ar = initArmor;
      eyeHeight = 1.3f;
      jumpVelocity = 0.58f;  //results in jump of 1.42
      //speeds are blocks per second
      walkSpeed = 4.3f;
      runSpeed = 5.6f;
      sneakSpeed = 1.3f;
      swimSpeed = (walkSpeed / 2.0f);
      reach = 5.0f;
      attackRange = 2.0f;
      attackDelay = 30;  //1.5 sec per attack
      attackDmg = 1.0f;
      maxAge = 20 * 60 * 15;  //15 mins
    }
    setMove(new MoveHostile());
  }

  public void initStatic() {
    super.initStatic();
    textureName = "entity/zombie_pigman";
    dest = new RenderDest(parts.length);
    model = loadModel("zombiepigman");
  }

  public void initStaticGL() {
    super.initStaticGL();
    texture = Textures.getTexture(textureName, 0);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "SKULL"};

  public void buildBuffers(RenderDest dest) {
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
        UVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        UVMap map1 = obj.maps.get(0);
        UVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  public void bindTexture() {
    texture.bind();
  }

  public static final int SKULL = 6;

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
      case SKULL:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(-ang.x, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case BODY:
        break;
      case L_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(90.0f, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case R_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(90.0f, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case L_LEG:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case R_LEG:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
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
      if (buf.isBufferEmpty()) continue;
      setMatrixModel(a, buf);
      buf.bindBuffers();
      buf.render();
    }
  }

  public EntityBase spawn(Chunk chunk) {
    World world = Static.server.world;
    float px = r.nextInt(16) + chunk.cx * 16.0f + 0.5f;
    int y = r.nextInt(256);
    float pz = r.nextInt(16) + chunk.cz * 16.0f + 0.5f;
    for(float gy = y;gy>0;gy--) {
      float py = gy;
      if (world.isEmpty(chunk.dim,px,py,pz)
        && world.isEmpty(chunk.dim,px,py-1,pz)
        && (world.canSpawnOn(chunk.dim,px,py-2,pz)))
      {
        py -= 1.0f;
        ZombiePigman e = new ZombiePigman();
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
    return 5.0f;
  }
  public Item[] drop() {
    Random r = new Random();
    Item items[] = new Item[1];
    items[0] = new Item(Items.ROTTEN_FLESH, 0, r.nextInt(2)+1);
    return items;
  }
  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
//      Static.server.broadcastSound(dim, pos.x, pos.y, pos.z, Sounds.SOUND_ZOMBIE, 1);
    }
  }
  public int[] getSpawnDims() {
    return new int[] {Dims.NETHER};
  }
}
