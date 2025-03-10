package jfcraft.env;

/** Renders environment for End world
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl .*;
import static javaforce.gl .GL .*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl .*;
import static jfcraft.opengl.RenderScreen.depth;

public class EnvironmentEnd implements EnvironmentBase {

  private static TextureMap starSky;
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

  public void init() {
    if (starSky == null) {
      starSky = new TextureMap();
      starSky.load(makePurpleStaticSky());
    }
    if (skybox == null) {
      skybox = new RenderBuffers();
      skybox.addSkyBox(-1000, -1000, -1000, 1000, 1000, 1000);
      skybox.copyBuffers();
    }
  }

  private Matrix view = new Matrix();

  public void preRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
    float zAngle = time;
    zAngle /= (24000f / 360f);

    view.setIdentity();
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, view.m);  //model matrix
    view.addRotate(Static.camera_ang.x, 1, 0, 0);
    view.addRotate(Static.camera_ang.y, 0, 1, 0);
    view.addRotate(zAngle, 0, 0, 1);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, view.m);  //view matrix

    depth(false);

    glUniform1f(Static.uniformAlphaFactor, sunLight);
    starSky.bind();
    skybox.bindBuffers();
    skybox.render();

    glUniform1f(Static.uniformAlphaFactor, 1.0f);

    depth(true);
  }

  public void postRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
  }

  public void tick() {}
}
