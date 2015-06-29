package jfcraft.env;

/** Renders environment (sky, water)
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;

public class EnvironmentEarth implements EnvironmentBase {

  private static Texture sun, moon, rain, snow, stars, blueSky, blueWater, black;
  private static RenderBuffers skybox, sunface, moonface[], horizon;
  private static boolean inited = false;

  private JFImage makeStars() {
    final int size = 256;
    JFImage img = new JFImage(size,size);
    img.fill(0, 0, size, size, 0);
    Random r = new Random();
    for(int a=0;a<50;a++) {
      int x = r.nextInt(size);
      int y = r.nextInt(size);
      img.putPixel(x, y, 0xffffff);
    }
    return img;
  }

  private JFImage makeBlueSky() {
    final int size = 128;
    JFImage img = new JFImage(size,size);
    img.fill(0, 0, size, size, 0x333399);
    return img;
  }

  private JFImage makeBlueWater() {
    final int size = 128;
    JFImage img = new JFImage(size,size);
    img.fill(0, 0, size, size, 0x0000ff);
    return img;
  }

  private JFImage makeBlack() {
    final int size = 128;
    JFImage img = new JFImage(size,size);
    img.fill(0, 0, size, size, 0x0);
    return img;
  }

  private JFImage makeAlpha(String name) {
    //make the sun transparent (black pixels)
    JFImage img = Assets.getImage(name).image;
    int px[] = img.getBuffer();
    for(int i=0;i<px.length;i++) {
      int p = px[i];
      int r = (p & 0xff0000) >> 16;
      int g = (p & 0xff00) >> 8;
      int b = (p & 0xff);
      int a = (r + g + b);
      if (a > 255) a = 255;
      p &= 0xffffff;
      p |= a << 24;
      px[i] = p;
    }
    return img;
  }

  public void init(GL gl) {
    if (inited) return;
    RenderData data = new RenderData();
    sun = new Texture();
    sun.load(gl, makeAlpha("environment/sun"));
    moon = new Texture();
    moon.load(gl, makeAlpha("environment/moon_phases"));
    rain = Textures.getTexture(gl, "environment/rain", 0);
//    snow = Textures.getTexture(gl, "environment/snow");
    stars = new Texture();
    stars.load(gl, makeStars());
    blueSky = new Texture();
    blueSky.load(gl, makeBlueSky());
    blueWater = new Texture();
    blueWater.load(gl, makeBlueWater());
    black = new Texture();
    black.load(gl, makeBlack());
    skybox = new RenderBuffers();
    skybox.addSkyBox(-1000, -1000, -1000, 1000, 1000, 1000);
    skybox.copyBuffers(gl);
    sunface = new RenderBuffers();
    sunface.addFaceAB(-10, -50, -10, 10, -50, 10, 0, 0, 1, 1, data);
    sunface.copyBuffers(gl);
    moonface = new RenderBuffers[8];
    int p = 0;
    for(int x=0;x<4;x++) {
      for(int y=0;y<2;y++) {
        moonface[p] = new RenderBuffers();
        float tx = x * 0.25f;
        float ty = y * 0.50f;
        //TODO : rotate coords by 90
        moonface[p].addFaceAB(-10, 50, 10, 10, 50, -10, tx, ty, tx + 0.25f, ty + 0.50f, data);
        moonface[p].copyBuffers(gl);
        p++;
      }
    }
    horizon = new RenderBuffers();
    horizon.blkLight = 0.0f;
    horizon.addHorizonBox(-1000, -1000, -1000, 1000, 0, 1000);
    horizon.copyBuffers(gl);
    inited = true;
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

    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f - sunLight);
    stars.bind(gl);
    skybox.bindBuffers(gl);
    skybox.render(gl);

    gl.glUniform1f(Static.uniformAlphaFactor, sunLight);
    blueSky.bind(gl);
    skybox.render(gl);

    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f);

    //render sun and moon
    sun.bind(gl);
    sunface.bindBuffers(gl);
    sunface.render(gl);

    moon.bind(gl);
    moonface[0].bindBuffers(gl);
    moonface[0].render(gl);

    //add horizon
    view.setIdentity();
    view.addRotate(client.ang.x, 1, 0, 0);
    view.addRotate(client.ang.y, 0, 1, 0);
    //no z rotation
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, view.m);  //view matrix
    gl.glUniform1f(Static.uniformSunLight, sunLight);
    if (client.player.pos.y < 0) {
      black.bind(gl);
    } else {
      blueWater.bind(gl);
    }
    horizon.bindBuffers(gl);
    horizon.render(gl);

    gl.glDepthMask(true);
    gl.glDepthFunc(GL.GL_LEQUAL);
  }
}
