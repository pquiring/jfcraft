package jfcraft.env;

/** Renders environment for Nether
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;

public class EnvironmentNether implements EnvironmentBase {

  private static Texture redSky;
  private static RenderBuffers skybox;

  private JFImage makeRedSky() {
    final int size = 128;
    JFImage img = new JFImage(size,size);
    img.fill(0, 0, size, size, 0x993333);
    return img;
  }

  public void init() {
    if (redSky == null) {
      redSky = new Texture();
      redSky.load(makeRedSky());
    }
    if (skybox == null) {
      skybox = new RenderBuffers();
      skybox.addSkyBox(-1000, -1000, -1000, 1000, 1000, 1000);
      skybox.copyBuffers();
    }
  }

  private GLMatrix view = new GLMatrix();

  public void preRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
    float zAngle = time;
    zAngle /= (24000f / 360f);

    view.setIdentity();
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, view.m);  //model matrix
    view.addRotate(Static.camera_ang.x, 1, 0, 0);
    view.addRotate(Static.camera_ang.y, 0, 1, 0);
    view.addRotate(zAngle, 0, 0, 1);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, view.m);  //view matrix

    glDepthMask(false);
    glDepthFunc(GL_ALWAYS);

    glUniform1f(Static.uniformAlphaFactor, sunLight);
    redSky.bind();
    skybox.bindBuffers();
    skybox.render();

    glUniform1f(Static.uniformAlphaFactor, 1.0f);

    glDepthMask(true);
    glDepthFunc(GL_LEQUAL);
  }

  public void postRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
  }

  public void tick() {}
}
