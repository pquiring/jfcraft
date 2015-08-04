package jfcraft.entity;

/** Turtle entity
 *
 * @author pquiring
 *
 * Created : Jun 20, 2015
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.data.*;
import jfcraft.dim.DimMarioWorld;
import jfcraft.item.Item;
import jfcraft.opengl.*;

public class Turtle extends CreatureBase {
  private float walkAngle;  //angle of legs/arms as walking
  private float walkAngleDelta;

  public static int TURTLE;

  //render assets
  private static RenderDest dest;
  private static Texture textures[];

  private static int initHealth = 20;
  private static int initArmor = 2;

  public byte color;

  public Turtle() {
    super();
    id = TURTLE;
  }

  public Class getIDClass() {
    return this.getClass();
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "Turtle";
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
    walkDutyCycle = 2;  //walk 50% of the time
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
  }

  public void initStatic() {
    dest = new RenderDest(parts.length);
  }

  public void initStatic(GL gl) {
    textures = new Texture[3];
    textures[0] = Textures.getTexture(gl, "entity/turtle/green", 0);
    textures[1] = Textures.getTexture(gl, "entity/turtle/blue", 0);
    textures[2] = Textures.getTexture(gl, "entity/turtle/red", 0);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "TAIL"};

  private static final int TAIL = 6;

  public void buildBuffers(RenderDest dest, RenderData data) {
    GLModel mod = loadModel("turtle");
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
    textures[color].bind(gl);
  }

  public void setMatrixModel(GL gl, int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(-ang.x, 1, 0, 0);
        mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
        break;
      case BODY:
      case TAIL:
        break;
      case R_ARM:
      case L_LEG:
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(-buf.org.x, -buf.org.y, -buf.org.z);
        break;
      case L_ARM:
      case R_LEG:
        mat.addTranslate2(buf.org.x, buf.org.y, buf.org.z);
        mat.addRotate2(walkAngle, 1, 0, 0);
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

  public void tick() {
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
      //turtles are always moving
      moveEntity();
    }
    if (target != null || mode != MODE_IDLE || wasMoving) Static.server.broadcastEntityMove(this, false);
    super.tick();
  }

  public EntityBase spawn(Chunk chunk) {
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
        Turtle e = new Turtle();
        e.init(world);
        e.dim = chunk.dim;
        e.health = initHealth;
        e.color = (byte)r.nextInt(3);
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
    return 10.0f;
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

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeByte(color);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    color = buffer.readByte();
    return true;
  }

  public int[] getGenerateDims() {
    return new int[] {DimMarioWorld.MARIO_WORLD};
  }

  public int[] getSpawnDims() {
    return new int[] {DimMarioWorld.MARIO_WORLD};
  }
}
