package jfcraft.entity;

/** End portal
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class EndPortal extends BlockEntity {
  public float dia;

  public RenderDest dest;

  //render assets
  public static Texture texture;
  //texture size
  protected static float tw;
  protected static float th;
  protected static float ts;  //scale
  protected static String textureName;

  public EndPortal() {
    id = Entities.END_PORTAL;
  }

  public EndPortal setDiameter(float dia) {
    this.dia = dia;
    return this;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "END_PORTAL";
  }

  public void init() {
    super.init();
    isBlock = true;
  }

  public Coords[] getBlocks(float x, float y, float z) {
    return null;
  }

  public void initStatic() {
    tw = 256;
    th = 256;
    textureName = "entity/end_portal";
    ts = 1.0f;
  }

  public void initStatic(GL gl) {
    texture = Textures.getTexture(gl, textureName, 0);
  }

  public void initInstance(GL gl) {
    super.initInstance(gl);
    dest = new RenderDest(1);
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    float dia2 = dia/2.0f;
    dest.getBuffers(0).addFaceAB(-dia2, 0.5f, -dia2, dia2, 0.5f, dia2, 0, 0, 1, 1, data);
  }

  public void bindTexture(GL gl) {
    texture.bind(gl);
  }

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
  }

  private void setMatrixModel(GL gl, int bodyPart) {
    mat.setIdentity();
    mat.addTranslate(pos.x, pos.y, pos.z);
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
  }

  public void render(GL gl) {
    setMatrixModel(gl, 0);
    dest.getBuffers(0).bindBuffers(gl);
    dest.getBuffers(0).render(gl);
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeFloat(dia);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    dia = buffer.readFloat();
    return true;
  }
}
