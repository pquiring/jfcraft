package jfcraft.env;

/** Renders environment for Nether
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

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

  public void init(GL gl) {
    if (redSky == null) {
      redSky = new Texture();
      redSky.load(gl, makeRedSky());
    }
    if (skybox == null) {
      skybox = new RenderBuffers();
      skybox.addSkyBox(-1000, -1000, -1000, 1000, 1000, 1000);
      skybox.copyBuffers(gl);
    }
  }

  private GLMatrix view = new GLMatrix();

  public void render(GL gl, int time, float sunLight, Client client) {
    float zAngle = time;
    zAngle /= (24000f / 360f);

    view.setIdentity();
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, view.m);  //model matrix
    view.addRotate(client.ang.x, 1, 0, 0);
    view.addRotate(client.ang.y, 0, 1, 0);
    view.addRotate(zAngle, 0, 0, 1);
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, view.m);  //view matrix

    gl.glDepthMask(false);
    gl.glDepthFunc(GL.GL_ALWAYS);

    gl.glUniform1f(Static.uniformAlphaFactor, sunLight);
    redSky.bind(gl);
    skybox.bindBuffers(gl);
    skybox.render(gl);

    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f);

    gl.glDepthMask(true);
    gl.glDepthFunc(GL.GL_LEQUAL);
  }
}
