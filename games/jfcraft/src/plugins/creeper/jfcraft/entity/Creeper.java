package jfcraft.entity;

/** Creeper entity
 *
 * @author pquiring
 *
 * Created : Aug 10, 2014
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.item.Item;
import jfcraft.opengl.*;

public class Creeper extends CreatureBase {
  private float walkAngle;  //angle of legs/arms as walking
  private float walkAngleDelta;
  public static RenderDest dest;

  //render assets
  private static Texture texture;
  private static String textureName;

  private static int initHealth = 20;
  private static int initArmor = 2;

  public static int CREEPER;

  public Creeper() {
    id = CREEPER;
  }

  public String getName() {
    return "Creeper";
  }

  public Class getIDClass() {
    return Creeper.class;
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init() {
    super.init();
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.6f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    walkAngleDelta = 1.0f;
    if (Static.isServer()) {
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
  }

  public void initStatic() {
    textureName = "entity/creeper/creeper";
    dest = new RenderDest(parts.length);
  }

  public void initStatic(GL gl) {
    texture = Textures.getTexture(gl, textureName, 0);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    GLModel mod = loadModel("creeper");
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = mod.getObject(parts[a]);
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

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
  }

  public void bindTexture(GL gl) {
    texture.bind(gl);
  }

  private void setMatrixModel(GL gl, int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
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
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
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

  public void render(GL gl) {
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      if (buf.isBufferEmpty()) continue;
      setMatrixModel(gl, a, buf);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
  }

  public boolean walking;
  public int walkLength;

  public void tick() {
    updateFlags(0,0,0);
    boolean fell;
    if (inWater && mode != MODE_FLYING) {
      fell = gravity(0.5f + (float)Math.sin(floatRad) * 0.25f);
      floatRad += 0.314f;
      if (floatRad > Static.PIx2) floatRad = 0f;
    } else {
      fell = gravity(0);
    }
    if (target == null) {
      //getTarget();  //test!
    } else {
      if (target.health == 0 || target.offline) {
        target = null;
      }
    }
    boolean wasMoving = mode != MODE_IDLE;
    if (Static.debugRotate) {
      //test rotate in a spot
      ang.y += 1.0f;
      if (ang.y > 180f) { ang.y = -180f; }
      ang.x += 1.0f;
      if (ang.x > 45.0f) { ang.x = -45.0f; }
      mode = MODE_WALK;
    } else {
      if (target != null) {
        moveToTarget();
      } else {
        randomWalking();
      }
      if (mode != MODE_IDLE) {
        moveEntity();
      } else {
        vel.x = 0;
        vel.z = 0;
      }
    }
    if (fell || target != null || mode != MODE_IDLE || wasMoving) Static.server.broadcastEntityMove(this, false);
    super.tick();
  }

  private static Random r = new Random();
  public EntityBase spawn(Chunk chunk) {
    World world = Static.world();
    float px = r.nextInt(16) + chunk.cx * 16.0f + 0.5f;
    int y = r.nextInt(256);
    float pz = r.nextInt(16) + chunk.cz * 16.0f + 0.5f;
    for(float gy = y;gy>0;gy--) {
      float py = gy;
      if (world.isEmpty(chunk.dim,px,py,pz)
        && world.isEmpty(chunk.dim,px,py-1,pz)
        && (!world.isEmpty(chunk.dim,px,py-2,pz)))
      {
        py -= 1.0f;
        Creeper e = new Creeper();
        e.init();
        e.health = initHealth;
        e.pos.x = px;
        e.pos.y = py;
        e.pos.z = pz;
        return e;
      }
    }
    return null;
  }
  public float getSpawnRate() {
    return 5.0f;
  }
  public Item[] drop() {
    Item items[] = new Item[1];
    items[0] = new Item(Items.GUN_POWDER);
    return items;
  }
  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
//      Static.server.broadcastSound(dim, x, y, z, Sounds.SOUND_ZOMBIE, 1);
    }
  }
  public int[] getDims() {
    return new int[] {Dims.EARTH};
  }
}
