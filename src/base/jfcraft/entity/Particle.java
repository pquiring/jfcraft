package jfcraft.entity;

/** Particles.
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;

import jfcraft.opengl.*;

public class Particle extends EntityBase {
  private static Matrix mat = new Matrix();  //used in Client.tick() thread (different from EntityBase.mat which is used in Game.render() thread)
  private static Vector3 eye = new Vector3();
  private static Vector3 at = new Vector3();
  private static Vector3 up = new Vector3();
  private static Random r = new Random();

  public static TextureMap t_particles;
  public static SubTexture t_heart;

  public SubTexture subtexture;
  public RenderDest dest;
  public boolean isGreen;
  public boolean full;  //full texture (else random partial)

  public Particle() {
    uid = -1;
  }

  public Particle(float x, float y, float z, SubTexture texture, boolean full) {
    super();
    pos.x = x;
    pos.y = y;
    pos.z = z;
    subtexture = texture;
    uid = -1;
    this.full = full;
  }

  public void init(World world) {
    super.init(world);
    width = 0.01f;
    width2 = width/2;
    height = 0.01f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    yDrag = Static.dragSpeed;
    xzDrag = yDrag * 4.0f;
    dest = new RenderDest(1);
  }

  public void initStaticGL() {
    t_particles = Textures.getTexture("particle/particles", 0);
    t_heart = t_particles.getSubTexture(0f / 256f, 80f / 256f, 16f / 256f, 96f / 256f);
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(0);
    float u1,v1,u2,v2;
    if (full) {
      u1 = subtexture.x1;
      v1 = subtexture.y1;
      u2 = subtexture.x2;
      v2 = subtexture.y2;
    } else {
      u1 = r.nextFloat();
      v1 = r.nextFloat();
      u2 = u1 + r.nextFloat();
      if (u2 > 1) u2 = 1.0f;
      v2 = v1 + r.nextFloat();
      if (v2 > 1) v2 = 1.0f;
      u1 = subtexture.x1 + subtexture.width * u1;
      v1 = subtexture.y1 + subtexture.height * v1;
      u2 = subtexture.x1 + subtexture.width * u2;
      v2 = subtexture.y1 + subtexture.height * v2;
    }
    data.isRed = false;
    data.isGreen = isGreen;
    data.isBlue = false;
    buf.addFace(0.5f, 0.5f, 0,   -0.5f, -0.5f, 0,   u1, v1, u2, v2, data);
    needCopyBuffers = true;
  }

  public void bindTexture() {
    subtexture.texture.bind();
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  public void render() {
    eye.v[0] = pos.x;
    eye.v[1] = pos.y;
    eye.v[2] = pos.z;
    at.v[0] = Static.camera_pos.x;
    at.v[1] = Static.camera_pos.y;
    at.v[2] = Static.camera_pos.z;
    up.v[0] = 0;
    up.v[1] = 1;
    up.v[2] = 0;
    mat.lookAt(eye, at, up);
    mat.addScale(scale, scale, scale);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
    RenderBuffers buf = getDest().getBuffers(0);
    buf.bindBuffers();
    buf.render();
  }

  public String getName() {
    return "Particle";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void ctick() {
    age++;
    if (age == maxAge) {
      Chunk chunk = getChunk();
      chunk.delEntity(this);
      return;
    }
    move.move(this);
  }

  private static float nextFloat5() {
    return r.nextFloat() - 0.5f;
  }

  private static float nextFloat() {
    return 1.0f + r.nextFloat();
  }

  public void createVelocity() {
    vel.x = nextFloat5() / 5.0f;
    vel.y = nextFloat() / 5.0f;
    vel.z = nextFloat5() / 5.0f;
  }

  public float getMaxDistance() {
    return 16f;
  }

  public boolean canSelect() {
    return false;
  }

  public float getBuoyant() {
    return 0;
  }
}
