package jfcraft.env;

/** Renders environment (sky, water)
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

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

  public void init() {
    if (inited) return;
    RenderData data = new RenderData();
    sun = new Texture();
    sun.load(makeAlpha("environment/sun"));
    moon = new Texture();
    moon.load(makeAlpha("environment/moon_phases"));
    rain = Textures.getTexture("environment/rain", 0);
//    snow = Textures.getTexture("environment/snow");
    stars = new Texture();
    stars.load(makeStars());
    blueSky = new Texture();
    blueSky.load(makeBlueSky());
    blueWater = new Texture();
    blueWater.load(makeBlueWater());
    black = new Texture();
    black.load(makeBlack());
    skybox = new RenderBuffers();
    skybox.addSkyBox(-1000, -1000, -1000, 1000, 1000, 1000);
    skybox.copyBuffers();
    sunface = new RenderBuffers();
    sunface.addFaceAB(-10, -50, -10, 10, -50, 10, 0, 0, 1, 1, data);
    sunface.copyBuffers();
    moonface = new RenderBuffers[8];
    int p = 0;
    for(int x=0;x<4;x++) {
      for(int y=0;y<2;y++) {
        moonface[p] = new RenderBuffers();
        float tx = x * 0.25f;
        float ty = y * 0.50f;
        //TODO : rotate coords by 90
        moonface[p].addFaceAB(-10, 50, 10, 10, 50, -10, tx, ty, tx + 0.25f, ty + 0.50f, data);
        moonface[p].copyBuffers();
        p++;
      }
    }
    horizon = new RenderBuffers();
    horizon.blkLight = 0.0f;
    horizon.addHorizonBox(-1000, -1000, -1000, 1000, 0, 1000);
    horizon.copyBuffers();
    inited = true;
  }

  private GLMatrix view = new GLMatrix();

  public void render(int time, float sunLight, Client client) {
    float zAngle = time;
    zAngle /= (24000f / 360f);

    view.setIdentity();
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, view.m);  //model matrix
    view.addRotate(client.ang.x, 1, 0, 0);
    view.addRotate(client.ang.y, 0, 1, 0);
    view.addRotate(zAngle, 0, 0, 1);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, view.m);  //view matrix

    glDepthMask(false);
    glDepthFunc(GL_ALWAYS);

    glUniform1f(Static.uniformAlphaFactor, 1.0f - sunLight);
    stars.bind();
    skybox.bindBuffers();
    skybox.render();

    glUniform1f(Static.uniformAlphaFactor, sunLight);
    blueSky.bind();
    skybox.render();

    glUniform1f(Static.uniformAlphaFactor, 1.0f);

    //render sun and moon
    sun.bind();
    sunface.bindBuffers();
    sunface.render();

    moon.bind();
    moonface[0].bindBuffers();
    moonface[0].render();

    //add horizon
    view.setIdentity();
    view.addRotate(client.ang.x, 1, 0, 0);
    view.addRotate(client.ang.y, 0, 1, 0);
    //no z rotation
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, view.m);  //view matrix
    glUniform1f(Static.uniformSunLight, sunLight);
    if (client.player.pos.y < 0) {
      black.bind();
    } else {
      blueWater.bind();
    }
    horizon.bindBuffers();
    horizon.render();

    glDepthMask(true);
    glDepthFunc(GL_LEQUAL);
  }
}
