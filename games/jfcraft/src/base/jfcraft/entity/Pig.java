package jfcraft.entity;

/** Pig entity
 *
 * @author pquiring
 *
 * Created : Aug 10, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.data.*;
import jfcraft.item.Item;
import jfcraft.opengl.*;

public class Pig extends CreatureBase {
  private float walkAngle;  //angle of legs/arms as walking
  private float walkAngleDelta;

  //render assets
  private RenderDest dest;
  private static Texture texture;
  private static String textureName;

  public static int initHealth = 10;

  public Pig() {
    id = Entities.PIG;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "PIG";
  }

  public void init(World world) {
    super.init(world);
    width = 0.6f;
    width2 = width/2;
    height = 0.8f;
    height2 = height/2;
    depth = 1.3f;
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
  }

  public void initStatic() {
    textureName = "entity/pig/pig";
  }

  public void initStatic(GL gl) {
    texture = Textures.getTexture(gl, textureName, 0);
  }

  public void initInstance(GL gl) {
    super.initInstance(gl);
    dest = new RenderDest(parts.length);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "SNOUT"};

  private static final int SNOUT = 6;

  public void buildBuffers(RenderDest dest, RenderData data) {
    GLModel mod = loadModel("pig");
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

  public void setMatrixModel(GL gl, int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
      case SNOUT:
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
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(gl, a, buf);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
  }

  public boolean walking;
  public int walkLength;

  public void tick() {
    super.tick();
    //do AI
    boolean moved;
    //random walking
    if (Static.debugRotate) {
      //test rotate in a spot
      ang.y += 1.0f;
      if (ang.y > 180f) { ang.y = -180f; }
      ang.x += 1.0f;
      if (ang.x > 45.0f) { ang.x = -45.0f; }
      mode = MODE_WALK;
      moved = true;
    } else {
      randomWalking();
      moved = moveEntity();
    }
    if (moved) Static.server.broadcastEntityMove(this, false);
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
        Pig e = new Pig();
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
    Item items[] = new Item[1];
    items[0] = new Item(Items.PORK_RAW, 0, r.nextInt(2)+1);
    return items;
  }
  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
      Static.server.broadcastSound(dim, pos.x, pos.y, pos.z, Sounds.SOUND_PIG, 1);
    }
  }
  public int[] getGenerateDims() {
    return new int[] {Dims.EARTH};
  }
}
