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
import static jfcraft.data.Direction.*;

public class EnvironmentEarth implements EnvironmentBase {

  private static Texture sun, moon, rain, snow, stars, blueSky, blueWater, black, t_clouds;
  private static RenderBuffers skybox, sunface, moonface[], horizon, o_clouds;
  private static boolean inited = false;
  private static boolean cloudMap[];  //128x128

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

  private JFImage makeClouds() {
    JFImage img = new JFImage(1,1);
    img.putPixel(0, 0, 0xffffff);
    img.putAlpha(0, 0, 0xaa);
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
    t_clouds = new Texture();
    t_clouds.load(makeClouds());
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
    o_clouds = new RenderBuffers();
    buildCloud();
    o_clouds.copyBuffers();
    cloudMap = new boolean[128*128];
    Random r = new Random();
    for(int x=0;x<128;x++) {
      for(int z=0;z<128;z++) {
        int rate = 20;
        cloudMap[z * 128 + x] = r.nextInt(100) < rate;
      }
    }
    for(int x=1;x<127;x++) {
      for(int z=1;z<127;z++) {
        int rate = 0;
        if (cloudMap[(z + 1) * 128 + x + 0]) rate += 20;
        if (cloudMap[(z + 0) * 128 + x + 1]) rate += 20;
        if (cloudMap[(z - 1) * 128 + x - 0]) rate += 20;
        if (cloudMap[(z - 0) * 128 + x - 1]) rate += 20;
        cloudMap[z * 128 + x] = r.nextInt(100) < rate;
      }
    }
    //create a few init cloud space (can grow later)
    clouds = new Cloud[128];
    for(int a=0;a<clouds.length;a++) {
      clouds[a] = new Cloud();
    }
    inited = true;
  }

  private GLMatrix view = new GLMatrix();

  public void preRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
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

  public void postRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks) {
    if (Settings.current.clouds) renderClouds(camera);
  }

  //cloud stuff
  //see http://gamedev.stackexchange.com/questions/105753/how-does-minecraft-render-its-clouds

  private void buildCloud() {
    //use 0.99f to avoid z-fighting when inside clouds
    float fx1 = -5.99f;
    float fy1 = -2f;
    float fz1 = -5.99f;
    float fx2 = +5.99f;
    float fy2 = +2f;
    float fz2 = +5.99f;
    RenderData data = new RenderData();
    data.sl[X] = 0.8f;
    o_clouds.addFace  (fx2,fy1,fz2, fx1,fy2,fz2, 0,0,1,1, data);  //N
    o_clouds.addFace  (fx1,fy1,fz1, fx2,fy2,fz1, 0,0,1,1, data);  //S
    data.sl[X] = 0.9f;
    o_clouds.addFace  (fx2,fy1,fz1, fx2,fy2,fz2, 0,0,1,1, data);  //W
    o_clouds.addFace  (fx1,fy1,fz2, fx1,fy2,fz1, 0,0,1,1, data);  //E
    data.sl[X] = 1.0f;
    o_clouds.addFaceAB(fx1,fy1,fz2, fx2,fy1,fz1, 0,0,1,1, data);  //A
    o_clouds.addFaceAB(fx1,fy2,fz1, fx2,fy2,fz2, 0,0,1,1, data);  //B
  }

  private float cloudOffset = 0f;
  private static final float cloudSpeed = 0.1f;

  //each cloud is 12x12x4
  private static class Cloud implements java.lang.Comparable<Cloud> {
    public XYZ pos = new XYZ();  //center of cloud
    public float dist;
    public Cloud() {
      pos.y = 128f;
    }
    public void set(float px, float pz) {
      pos.x = px + 6f;
      pos.z = pz + 6f;
    }
    public void calcDist(XYZ cam) {
      float dx = pos.x - cam.x;
      float dz = pos.z - cam.z;
      dist = (float)Math.sqrt(dx * dx + dz * dz);
    }
    public int compareTo(Cloud oc) {
      return Float.compare(dist, oc.dist);
    }
    public String toString() {
      return "Cloud:" + pos.x + "," + pos.y + "," + pos.z;
    }
  }

  private void updateClouds() {
    cloudOffset += cloudSpeed;
    if (cloudOffset >= 12*128f) {
      cloudOffset -= 12*128f;
    }
  }

  private GLMatrix mat = new GLMatrix();

  private Cloud clouds[] = new Cloud[0];

  private void renderClouds(XYZ camera) {
    //cam x/z
    float cx = camera.x;
    float cz = camera.z;
    //map coords
    int mmx;
    if (cx >= 0)
      mmx = (int)Math.floor(cx / (12*128f));
    else
      mmx = (int)Math.ceil(cx / (12*128f));
    int mmz;
    if (cz >= 0)
      mmz = (int)Math.floor(cz / (12*128f));
    else
      mmz = (int)Math.ceil(cz / (12*128f));
    int mx = (int)Math.floor((cx / 12f) % 128f);
    int mz = (int)Math.floor((cz / 12f) % 128f);

    int lx = (int)(cloudOffset / 12f);
    float co = cloudOffset % 12f;

    int cc = 0;  //cloud count
    int px = mx + lx;
    while (px < 0) px += 128;
    while (px >= 128) px -= 128;
    for(int x=-12;x<=12;x++) {
      int pz = mz;
      while (pz < 0) pz += 128;
      while (pz >= 128) pz -= 128;
      for(int z=-12;z<=12;z++) {
        if (cloudMap[pz * 128 + px]) {
          if (clouds.length == cc) {
            clouds = Arrays.copyOf(clouds, cc+1);
            clouds[cc] = new Cloud();
          }
          clouds[cc++].set(mmx * 12*128f + (mx + x) * 12f - co,mmz * 12*128f + (mz + z) * 12f);
        }
        pz++;
        if (pz == 128) pz = 0;
      }
      px++;
      if (px == 128) px = 0;
    }
    for(int a=0;a<cc;a++) {
      clouds[a].calcDist(camera);
    }
    Arrays.sort(clouds, 0, cc);
    t_clouds.bind();
    o_clouds.bindBuffers();
    for(int a=0;a<cc;a++) {
      Cloud cloud = clouds[a];
      mat.setTranslate(cloud.pos.x, cloud.pos.y, cloud.pos.z);
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
      glCullFace(GL_BACK);
      o_clouds.render();  //render outside faces
      glCullFace(GL_FRONT);
      o_clouds.render();  //render inside faces
    }
    glCullFace(GL_BACK);
  }

  public void tick() {
    if (Settings.current.clouds) updateClouds();
  }
}
