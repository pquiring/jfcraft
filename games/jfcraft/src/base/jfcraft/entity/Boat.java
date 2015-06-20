package jfcraft.entity;

/** Boat entity
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Boat extends CreatureBase {
  //render assets
  public static Texture texture;
  private static String textureName;
  private static GLModel model;
  private static int initHealth = 5;

  public float waterSpeed, fastWaterSpeed, landSpeed;
  public RenderDest dest;

  public Boat() {
    id = Entities.BOAT;
    health = initHealth;
  }

  public String getName() {
    return "boat";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init() {
    super.init();
    yDrag = Static.dragSpeed;
    xzDrag = yDrag * 4.0f;
    waterSpeed = 6.2f;
    fastWaterSpeed = 7.0f;
    landSpeed = 0.39f;
    width = 1.0f;
    width2 = width/2f;
    height = 1.0f;
    height2 = height/2f;
    depth = 1.0f;
    depth2 = depth/2f;
    dest = new RenderDest(parts.length);
  }

  public void initStatic() {
    textureName = "entity/boat";
  }

  public void initStatic(GL gl) {
    texture = Textures.getTexture(gl, textureName);
    model = loadModel("boat");
  }

  public void initInstance(GL gl) {
    super.initInstance(gl);
  }

  private static String parts[] = {"BASE", "NORTH", "EAST", "SOUTH", "WEST"};

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
        GLUVMap map = obj.getUVMap(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        GLUVMap map1 = obj.getUVMap("normal");
        float uv1[] = map1.uvl.toArray();
        int crack = 10 - (int)(health * 2);
        if (crack > 9) crack = 9;  //it's broken
        if (crack == 0) {
          buf.addTextureCoords(uv1);
        } else {
          GLUVMap map2 = obj.getUVMap("crack");
          float uv2[] = map2.uvl.toArray();
          buf.adjustCrack(uv2, crack);
          buf.addTextureCoords(uv1, uv2);
        }
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
    needCopyBuffers = true;
  }

  public void bindTexture(GL gl) {
    texture.bind(gl);
  }

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
  }

  private void setMatrixModel(GL gl) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    mat.addTranslate(pos.x, pos.y, pos.z);
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
  }

  public void render(GL gl) {
    setMatrixModel(gl);  //all parts share the same matrix
    int cnt = parts.length;
    for(int a=0;a<cnt;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix
  }

  public void tick() {
    super.tick();
    if (occupant != null) return;
    updateFlags(0,0,0);
    boolean fell = gravity(0.25f);
    boolean moved = move(false, true, false, -1, AVOID_NONE);
    if (fell || moved) {
      Static.server.broadcastEntityMove(this);
    }
  }

  public boolean canUse() {
    return true;
  }

  public void use(Client c) {
    if (occupant != null) return;  //in use
    occupant = c.player;
    c.player.vehicle = this;
    c.player.pos.x = pos.x;
    c.player.pos.y = pos.y;
    c.player.pos.z = pos.z;
    c.serverTransport.setRiding(occupant, this);
  }

  /** Player move. */
  public synchronized void move(boolean up, boolean dn, boolean lt, boolean rt,
    boolean jump, boolean sneak, boolean run, boolean b1, boolean b2,
    boolean fup, boolean fdn)
  {
    gravity(0.25f);
    float speed = 0;
    if (onWater) {
      if (run)
        speed = fastWaterSpeed;
      else
        speed = waterSpeed;
    }
    else {
      speed = landSpeed;
    }
    if (up || dn) {
      occupant.calcVectors(speed / 20.0f, move_vectors);
      float xv = 0, zv = 0;
      if (up) {
        xv += move_vectors.forward.v[0];
        zv += move_vectors.forward.v[2];
      }
      if (dn) {
        xv += -move_vectors.forward.v[0];
        zv += -move_vectors.forward.v[2];
      }
      if (xv != 0) setXVel(xv);
      if (zv != 0) setZVel(zv);
      ang.y = occupant.ang.y;
    }
//    Static.log(Static.CS() + ":b:" + x + "," + y + "," + z + ":" + speed + ":" + v.forward.v[0] + "," + v.forward.v[2] + ":" + xv + "," + zv + ":" + up + dn + ":" + occupant.yAngle);
    //to keep the boat and player synced this must be synced with render engine
    synchronized(Static.renderLock) {
      move(sneak, false, false, -1, AVOID_NONE);
      occupant.pos.x = pos.x;
      occupant.pos.y = pos.y;
      occupant.pos.z = pos.z;
    }
//    Static.log(Static.CS() + ":a:" + x + "," + y + "," + z);
    if (Static.isServer() && sneak) {
      //eject occupant
      occupant.vehicle = null;
      occupant.client.serverTransport.setRiding(occupant, null);
      occupant = null;
    }
  }
  public boolean canSelect() {
    return true;
  }
  public Item[] drop() {
    return new Item[] {new Item(Items.BOAT)};
  }
  public boolean isVehicle() {
    return true;
  }
  public boolean cracks() {
    return true;
  }
}
