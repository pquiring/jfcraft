package jfcraft.env;

/** Renders environment for End world
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;

public class EnvironmentEnd implements EnvironmentBase {

  private static Texture starSky;
  private static RenderBuffers skybox;

  private JFImage makePurpleStaticSky() {
    Random r = new Random();
    int size = 128;
    JFImage img = new JFImage(size,size);
    int px[] = img.getBuffer();
    for(int p=0;p<size*size;p++) {
      int v = r.nextInt(128) + 128;
      px[p] = (v << 16) + v;
    }
    return img;
  }

  public void init(GL gl) {
    if (starSky == null) {
      starSky = new Texture();
      starSky.load(gl, makePurpleStaticSky());
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
    starSky.bind(gl);
    skybox.bindBuffers(gl);
    skybox.render(gl);

    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f);

    gl.glDepthMask(true);
    gl.glDepthFunc(GL.GL_LEQUAL);
  }
}
