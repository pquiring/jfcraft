package jfcraft.client;

/** Game render scene
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import jfcraft.server.*;
import static jfcraft.data.Direction.*;

public class Game extends RenderScreen {
  private GLMatrix perspective, view;
  private float fov = 70.0f;
  private float zNear = 0.1f;  //do NOT use zero!
  private float zFar = 10000.0f;
  private int width, height;
  private static RenderBuffers o_slots, o_active, o_icons, o_cross, o_box;
  private World world;
  public static boolean debug;
  private Runtime rt = Runtime.getRuntime();
  private boolean showControls = true;
  private static RenderBuffers hand;
  private GLFrustum frustum = new GLFrustum();
  private GLVector4 p4 = new GLVector4();  //position
  private GLVector4 l4 = new GLVector4();  //center (looking at)
  private GLVector4 u4 = new GLVector4();  //up
  private GLVector3 p3 = new GLVector3();  //position
  private GLVector3 l3 = new GLVector3();  //center (looking at)
  private GLVector3 u3 = new GLVector3();  //up
  private GLVector3 pts[];  //chunk points
  private Slot slots[];
  private XYZ camera = new XYZ();

  public static boolean advanceAnimation;

  public Game() {
    id = Client.GAME;
    slots = new Slot[9];  //active slots
    int p = 0;
    int x = 75;
    int y = 512 - 2;
    for(int a=0;a<9;a++) {
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 40;
    }
  }

  public void setup() {
    Static.game = this;
    world = Static.client.world;
    setCursor(false);
    Static.inGame = true;
    if (hand == null) {
      Player player = (Player)Static.entities.entities[Entities.PLAYER];
      hand = player.getRightHand();
    }
    if (o_box == null) {
      o_box = new RenderBuffers();
      o_box.type = GL_LINES;
    }
    if (pts == null) {
      pts = new GLVector3[8];
      for(int a=0;a<8;a++) {
        pts[a] = new GLVector3();
        if (a > 3) {
          pts[a].v[1] = 256f;
        }
      }
    }
    lastx = -1;
  }

  public void init() {
    super.init();
    if (o_slots == null) {
      o_slots = createMenu(75,470, 0,0, 361,42);
    }
    if (o_cross == null) {
      o_cross = createMenu(240,240, 0,0, 32,32);
    }
    if (o_icons == null) {
      o_icons = new RenderBuffers();
    }
    if (view == null) {
      view = new GLMatrix();
    }
    if (o_active == null) {
      o_active = createMenu(75, 470, 1,45, 46,46);
    }
  }

  private float sunLight;

  public void render(int width, int height) {
    synchronized(Static.renderLock) {
      this.width = width;
      this.height = height;
      render();
    }
  }

  private Coords c = new Coords();

  private float v3[] = new float[3];
  private float[] setv3(float x,float y,float z) {
    v3[0] = x;
    v3[1] = y;
    v3[2] = z;
    return v3;
  }
  private float v2[] = new float[2];
  private int boxPoly[] = new int[] {0,1, 2,3, 0,2, 1,3, 4,5, 6,7, 4,6, 5,7, 0,4, 1,5, 2,6, 3,7};

  public void process() {
    if (advanceAnimation) {
      RenderEngine.advanceAnimation();
      advanceAnimation = false;
    }
    Static.client.chunkLighter.process();
    Static.client.chunkBuilder.process();
    Static.client.chunkCopier.process();
  }

  private Profiler pro = new Profiler("r:");

  public void render() {
    pro.start();

    gui_position = CENTER;

    int dim = Static.client.player.dim;

    if (Static.inGame && Static.client.player.health == 0) {
      Static.video.setScreen(Static.screens.screens[Client.DEAD]);
      return;
    }

    if (Static.client.error != null) {
      Static.client.stopTimers();
      Static.client.stopVoIP();
      MessageMenu message = (MessageMenu)Static.screens.screens[Client.MESSAGE];
      message.setup("Error", Static.client.error.toString(), Static.screens.screens[Client.MAIN]);
      Static.video.setScreen(message);
      Static.game = null;
      return;
    }

    recreateMenu(o_active, 75 + Static.client.player.activeSlot * 40,470, 1,45, 46,46);

    int cx = Static.floor(Static.client.player.pos.x / 16.0f);
    int cz = Static.floor(Static.client.player.pos.z / 16.0f);
    pro.next();

    Chunk chunks[] = Static.client.world.chunks.getChunks();

    //now render stuff
    glDepthMask(true);
    //set sunlight level
    if (world.time >= 19000 || world.time <= 5000) {
      //moon light
      sunLight = 0.1f;
    } else if (world.time >= 7000 && world.time <= 17000) {
      //daylight
      sunLight = 1.0f;
    } else if (world.time < 7000) {
      //morning fade in 5000 -> 7000
      sunLight = (((float)world.time) - 5000.0f) / 2000.0f;
      if (sunLight < 0.1f) sunLight = 0.1f;
    } else {
      //night fade out 17000 -> 19000
      sunLight = 1.0f - ((((float)world.time) - 17000.0f) / 2000.0f);
      if (sunLight < 0.1f) sunLight = 0.1f;
    }
    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    if (perspective == null) {
      perspective = new GLMatrix();
      perspective.setIdentity();
      float ratio = ((float)width) / ((float)height);
      perspective.perspective(fov, ratio, zNear, zFar);
      frustum.setPerspecive(fov, ratio, zNear, zFar);
    }
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL_FALSE, perspective.m);  //perspective matrix

    camera.x = Static.client.player.pos.x;
    camera.y = Static.client.player.pos.y -Static.client.player.eyeHeight;
    camera.z = Static.client.player.pos.z;

    glUniform1f(Static.uniformSunLight, 1.0f);
    if (!Static.debugDisableFog) glUniform1i(Static.uniformEnableFog, 0);
    Static.dims.dims[dim].getEnvironment().preRender(world.time, sunLight, Static.client, camera, chunks);
    if (!Static.debugDisableFog) glUniform1i(Static.uniformEnableFog, 1);

    view.setIdentity();
    synchronized(Static.client.ang) {
      view.addRotate(Static.client.ang.x, 1, 0, 0);
      view.addRotate(Static.client.ang.y, 0, 1, 0);
    }
    view.addTranslate2(-camera.x, -camera.y, -camera.z);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, view.m);  //view matrix

    //setup frustum culling
    p3.set(camera.x, camera.y, camera.z);
    l3.set(0, 0, -1f);
    view.mult(l3);
    l3.add(p3);
    u3.set(0, 1f, 0);
    view.mult(u3);
    u3.add(p3);
    frustum.setPosition(p3, l3, u3);
    if (debug) {
//      frustum.print();
    }

    Static.blocks.stitched.bind();
    int cnt = 0;
    int fcnt = 0;
    RenderBuffers obj;
    String dmsg = "";

    pro.next();

    //render main stitched objects
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      chunk.inRange = false;
      if (!chunk.canRender()) continue;
      if (chunk.isAllEmpty) continue;
      if (Static.abs(chunk.cx - cx) > Settings.current.loadRange) continue;
      if (Static.abs(chunk.cz - cz) > Settings.current.loadRange) continue;
      if (!chunk.ready) continue;
      //frustum culling
      float px1 = chunk.cx * 16f;
      float pz1 = chunk.cz * 16f;
      float px2 = px1 + 16f;
      float pz2 = pz1 + 16f;
      pts[0].v[0] = px1;
      pts[0].v[2] = pz1;
      pts[1].v[0] = px2;
      pts[1].v[2] = pz1;
      pts[2].v[0] = px2;
      pts[2].v[2] = pz2;
      pts[3].v[0] = px1;
      pts[3].v[2] = pz2;

      pts[4].v[0] = px1;
      pts[4].v[2] = pz1;
      pts[5].v[0] = px2;
      pts[5].v[2] = pz1;
      pts[6].v[0] = px2;
      pts[6].v[2] = pz2;
      pts[7].v[0] = px1;
      pts[7].v[2] = pz2;
      if (frustum.boxInside(pts) == GLFrustum.OUTSIDE) {
        fcnt++;
        continue;
      }
      chunk.inRange = true;
      obj = chunk.dest.getBuffers(Chunk.DEST_NORMAL);
      if (obj.isBufferEmpty()) continue;
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers();
      obj.render();
      cnt++;
    }

    pro.next();

    //render box around block
    Static.client.player.findBlock(-1, BlockHitTest.Type.SELECTION, Static.client.player.vehicle, Static.client.selection);
    if (Static.client.selection.block != null) {
      o_box.reset();
      ArrayList<Box> boxes = Static.client.selection.block.getBoxes(Static.client.selection, BlockHitTest.Type.SELECTION);
      int boxcnt = boxes.size();
      for(int a=0;a<boxcnt;a++) {
        Box box = boxes.get(a);
        o_box.addVertex(setv3(box.x1,box.y1,box.z1));
        o_box.addVertex(setv3(box.x2,box.y1,box.z1));
        o_box.addVertex(setv3(box.x1,box.y1,box.z2));
        o_box.addVertex(setv3(box.x2,box.y1,box.z2));
        o_box.addVertex(setv3(box.x1,box.y2,box.z1));
        o_box.addVertex(setv3(box.x2,box.y2,box.z1));
        o_box.addVertex(setv3(box.x1,box.y2,box.z2));
        o_box.addVertex(setv3(box.x2,box.y2,box.z2));
        for(int b=0;b<8;b++) {
          o_box.addTextureCoords(v2,v2);
          o_box.addDefault(Static.black);
        }
        //add 12 lines
        o_box.addPoly(boxPoly);
      }
      o_box.copyBuffers();
      o_box.mat.setTranslate(Static.client.selection.x, Static.client.selection.y, Static.client.selection.z);
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, o_box.mat.m);  //model matrix
      o_box.bindBuffers();
      glUniform1i(Static.uniformEnableTextures, 0);
      o_box.render();
      glUniform1i(Static.uniformEnableTextures, 1);
    }
    if (Static.client.selection.entity != null) {
      o_box.reset();
      EntityBase e = Static.client.selection.entity;
      float width2 = e.width2;
      float height = e.height;
      float depth2 = e.depth2;
      float x = e.pos.x;
      float y = e.pos.y;
      float z = e.pos.z;
      float x1 = x - width2;
      float y1 = y;
      float z1 = z - depth2;
      float x2 = x + width2;
      float y2 = y + height;
      float z2 = z + depth2;
      o_box .addVertex(setv3(x1,y1,z1));
      o_box .addVertex(setv3(x2,y1,z1));
      o_box .addVertex(setv3(x1,y1,z2));
      o_box .addVertex(setv3(x2,y1,z2));
      o_box .addVertex(setv3(x1,y2,z1));
      o_box .addVertex(setv3(x2,y2,z1));
      o_box .addVertex(setv3(x1,y2,z2));
      o_box .addVertex(setv3(x2,y2,z2));
      for(int b=0;b<8;b++) {
        o_box.addTextureCoords(v2, v2);
        o_box.addDefault(Static.black);
      }
      //add 12 lines
      o_box.addPoly(boxPoly);
      o_box.copyBuffers();
      o_box.mat.setTranslate(0,0,0);
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, o_box.mat.m);  //model matrix
      o_box.bindBuffers();
      glUniform1i(Static.uniformEnableTextures, 0);
      o_box.render();
      glUniform1i(Static.uniformEnableTextures, 1);
    }

    pro.next();

    //render entities
    //these will change the view/model matrix
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      if (!chunk.inRange) continue;
      EntityBase es[] = chunk.getEntities();
      int ne = es.length;
      data.reset();
      for(int b=0;b<ne;b++) {
        EntityBase e = es[b];
        if (e.uid == Static.client.player.uid) continue;  //do not render self
        if (e.distance(Static.client.player) > 64f) continue;
        if (!e.instanceInited) {
          e.initInstance();
        }
        if (!e.isStatic) {
          if (e.dirty) {
            e.buildBuffers(e.getDest(), data);
            e.dirty = false;
          }
          if (e.needCopyBuffers) {
            e.copyBuffers();
            e.needCopyBuffers = false;
          }
        }
        float elight = e.getLight(sunLight);
        glUniform1f(Static.uniformSunLight, elight);
        e.bindTexture();
        e.render();
      }
    }
    glUniform1f(Static.uniformSunLight, sunLight);

    pro.next();

    //render text (signs)
    t_text.bind();
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      if (!chunk.inRange) continue;
      if (!chunk.dest.exists(Chunk.DEST_TEXT)) continue;
      obj = chunk.dest.getBuffers(Chunk.DEST_TEXT);
      if (obj.isBufferEmpty()) continue;
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers();
      obj.render();
    }

    //now render alpha stuff
    glDepthMask(false);  //turn off depth buffer updates
    Static.blocks.stitched.bind();

    pro.next();

    //render stitched chunks (alpha) (ie: iceblock)
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      if (!chunk.inRange) continue;
      if (!chunk.dest.exists(Chunk.DEST_ALPHA)) continue;
      obj = chunk.dest.getBuffers(Chunk.DEST_ALPHA);
      if (obj.isBufferEmpty()) continue;
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers();
      obj.render();
    }

    //TODO : render particles, etc.
    pro.next();

    glDepthMask(true);  //turn on depth buffer updates

    //render environment post stage
    Static.dims.dims[dim].getEnvironment().postRender(world.time, sunLight, Static.client, camera, chunks);

    if (showControls) {
      renderItemInHand();

      glUniform1f(Static.uniformSunLight, 1.0f);

      glDepthFunc(GL_ALWAYS);

      //now render slots at bottom
      gui_position = BOTTOM;
      setOrtho();
      setViewportMenu();
      glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, Static.identity.m);  //view matrix
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, Static.identity.m);  //model matrix

      t_widgets.bind();
      o_slots.bindBuffers();
      o_slots.render();
      o_active.bindBuffers();
      o_active.render();

      //now render items in slots

      for(int a=0;a<9;a++) {
        slots[a].item = Static.client.player.items[a];
      }
      renderItems(slots);

      if (Static.client.chatTime > 0) {
        //render chat
        int dx = 0;
        int dy = 512 - fontSize * 7;
        synchronized(Static.client.chat) {
          for(int a=Static.client.chat.size()-1;a>=0;a--) {
            renderText(dx, dy, Static.client.chat.get(a));
            dy -= fontSize;
          }
        }
      }

      if (Settings.current.client_voip && Settings.current.ptt && Static.keys[GLVK.VK_CONTROL_R]) {
        renderText(512 - 4 * fontSize, 512, "Talk");
      }

      if (Static.client.itemTextTime > 0) {
        Item item = Static.client.player.items[Static.client.player.activeSlot];
        if (item.id != 0) {
          ItemBase itembase = Static.items.items[item.id];
          if (item != null) {
            String txt = itembase.getName(item.var);
            renderText(256 - (txt.length() * fontSize / 2), 419, txt);
          }
        }
      }

      if (debug) {
        gui_position = CENTER;  //should be TOP
        int gx = Static.floor(Static.client.player.pos.x % 16.0f);
        if (Static.client.player.pos.x < 0 && gx != 0) gx = 16 + gx;
        int gy = Static.floor(Static.client.player.pos.y);
        int gz = Static.floor(Static.client.player.pos.z % 16.0f);
        if (Static.client.player.pos.z < 0 && gz != 0) gz = 16 + gz;
        int dx = 0;
        int dy = fontSize;
        renderText(dx,dy,"Pos:" + Static.client.player.pos.x + "," + Static.client.player.pos.y + "," + Static.client.player.pos.z);
        dy += fontSize;
        renderText(dx,dy,"Ang:" + Static.client.player.ang.x + "," + Static.client.player.ang.y + "," + Static.client.player.ang.z);
        dy += fontSize;
        renderText(dx,dy,"Chunk:" + cx + "," + cz + " Block:" + gx + "," + gy + "," + gz);
        dy += fontSize;
        renderText(dx,dy,"Chunks:" + chunks.length + " Rendered:" + cnt + " Frustum:" + fcnt);
        dy += fontSize;
        long free = rt.freeMemory() / (1024 * 1024);
        long total = rt.totalMemory() / (1024 * 1024);
        renderText(dx,dy,"Memory:Free=" + free + "MB of " + total + "MB");
        dy += fontSize;
        renderText(dx,dy,"FPS=" + Static.fps);
        dy += fontSize;
        Chunk chunk = Static.client.player.getChunk();
        if (chunk != null) {
          int gp = (gz << 4) + gx;
          renderText(dx,dy,"Biome:" + Chunk.getBiomeName(chunk.biome[gp]));
          dy += fontSize;
          renderText(dx,dy," Elev:" + chunk.elev[gp]);
          dy += fontSize;
          renderText(dx,dy," Temp:" + chunk.temp[gp]);
          dy += fontSize;
          renderText(dx,dy," Rain:" + chunk.rain[gp]);
          dy += fontSize;
        }

        if (Static.client.clientTransport instanceof LocalClientTransport) {
          renderText(dx,dy,"Queue:S=" + Static.client.clientTransport.getServerQueueSize() + ",C=" + Static.client.clientTransport.getClientQueueSize());
          dy += fontSize;
        }
        renderText(dx,dy,"Tick:" + Static.tick);
        dy += fontSize;
        renderText(dx,dy,"Time:" + world.time);
        dy += fontSize;
        Static.client.player.findBlock(-1, BlockHitTest.Type.SELECTION, Static.client.player.vehicle, c);
        if (c.block != null && c.chunk != null) {
          renderText(dx,dy,"Hit:" + c);
          dy += fontSize;
          renderText(dx,dy,"B1:" + (int)c.block.id + "," + c.block.getName(c.var)
            + ":bits=" + Integer.toString(c.bits, 16)
            + ":bl=" + world.getBlockLight(c.chunk.dim, c.x, c.y, c.z)
            + ":sl=" + world.getSunLight(c.chunk.dim, c.x, c.y, c.z)
            + ":pl=" + world.getPowerLevel(c.chunk.dim, c.x, c.y, c.z, c));
          dy += fontSize;
          int id2 = c.chunk.getID2(c.gx, c.gy, c.gz);
          if (id2 > 0 && id2 != c.block.id) {
            int bits2 = c.chunk.getBits2(c.gx, c.gy, c.gz);
//            int dir2 = Chunk.getDir(bits2);
            int var2 = Chunk.getDir(bits2);
            BlockBase base2 = Static.blocks.blocks[id2];
            renderText(dx,dy,"B2:" + (int)id2 + "," + base2.getName(var2)
              + ":bits=" + Integer.toString(bits2, 16));
            dy += fontSize;
          }
        } else if (c.entity != null) {
          renderText(dx,dy,"Hit:" + c);
          dy += fontSize;
          renderText(dx,dy,"Entity:" + c.entity.id + "," + c.entity.getName());
          dy += fontSize;
        }
        renderText(dx,dy,dmsg);
        dy += fontSize;
        if (Static.debugMsg != null) {
          renderText(dx,dy,Static.debugMsg);
          dy += fontSize;
        }
      }

      //render icons
      gui_position = CENTER;
      setOrtho();
      setViewportMenu();
      t_icons.bind();
      o_cross.bindBuffers();
      o_cross.render();
      gui_position = BOTTOM;
      setViewportMenu();
      o_icons.reset();
      float health = Static.client.player.health;
      for(int a=0;a<10;a++) {
        if (health >= 2) {
          //full heart
          o_icons.addFace2D((75 + a * 18) / 512.0f,453 / 512.0f, 104 / 512.0f, 0, 17 / 512.0f, 17 / 512.0f, Static.white);
        } else if (health >= 1) {
          //half heart
          o_icons.addFace2D((75 + a * 18) / 512.0f,453 / 512.0f, 32 / 512.0f, 0, 17f / 512.0f, 17 / 512.0f, Static.white);
          o_icons.addFace2D((75 + a * 18) / 512.0f,453 / 512.0f, 122 / 512.0f, 0, 17f / 512.0f, 17 / 512.0f, Static.white);
        } else {
          //no heart
          o_icons.addFace2D((75 + a * 18) / 512.0f,453 / 512.0f, 32 / 512.0f, 0, 17 / 512.0f, 17 / 512.0f, Static.white);
        }
        health -= 2;
      }
      float ar = Static.client.player.ar;
      if (ar > 0) {
        for(int a=0;a<10;a++) {
          if (ar >= 2) {
            //full
            o_icons.addFace2D((75 + a * 18) / 512.0f,436 / 512.0f, 68 / 512.0f, 18 / 512.0f, 17 / 512.0f, 18 / 512.0f, Static.white);
          } else if (ar >= 1) {
            //half
            o_icons.addFace2D((75 + a * 18) / 512.0f,436 / 512.0f, 50 / 512.0f, 18 / 512.0f, 17 / 512.0f, 18 / 512.0f, Static.white);
          } else {
            //none
            o_icons.addFace2D((75 + a * 18) / 512.0f,436 / 512.0f, 32 / 512.0f, 18 / 512.0f, 17 / 512.0f, 18 / 512.0f, Static.white);
          }
          ar -= 2;
        }
      }
      float air = Static.client.player.air;
      if (air != 20f) {
        for(int a=0;a<10;a++) {
          if (air >= 2) {
            //full
            o_icons.addFace2D((257 + a * 18) / 512.0f,436 / 512.0f, 32 / 512.0f, 36 / 512.0f, 18 / 512.0f, 18 / 512.0f, Static.white);
          } else if (air >= 1) {
            //half (popped)
            o_icons.addFace2D((257 + a * 18) / 512.0f,436 / 512.0f, 50 / 512.0f, 36 / 512.0f, 18 / 512.0f, 18 / 512.0f, Static.white);
          } else {
            //none
          }
          air -= 2;
        }
      }
      int food = (int)Static.client.player.hunger;
      for(int a=0;a<10;a++) {
        if (food >= 2) {
          //full heart
          o_icons.addFace2D((257 + a * 18) / 512.0f,453 / 512.0f, 104 / 512.0f, 54 / 512.0f, 17 / 512.0f, 17 / 512.0f, Static.white);
        } else if (food >= 1) {
          //half heart
          o_icons.addFace2D((257 + a * 18) / 512.0f,453 / 512.0f, 32 / 512.0f, 54 / 512.0f, 17f / 512.0f, 17 / 512.0f, Static.white);
          o_icons.addFace2D((257 + a * 18) / 512.0f,453 / 512.0f, 122 / 512.0f, 54 / 512.0f, 17f / 512.0f, 17 / 512.0f, Static.white);
        } else {
          //no heart
          o_icons.addFace2D((257 + a * 18) / 512.0f,453 / 512.0f, 32 / 512.0f, 54 / 512.0f, 17 / 512.0f, 17 / 512.0f, Static.white);
        }
        food -= 2;
      }

      o_icons.copyBuffers();
      o_icons.bindBuffers();
      o_icons.render();
    }  //showControls

    gui_position = CENTER;

//    pro.print();
    if (!Static.debugDisableFog) glUniform1i(Static.uniformEnableFog, 0);
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    if (!Static.inGame) return;
    ChatMenu chat;
    switch (vk) {
      case GLVK.VK_E:
        RenderScreen menu;
        if (Static.client.player.vehicle != null) {
          int idx = Static.client.player.vehicle.getMenu();
          if (idx == -1) return;
          if (idx != Client.INVENTORY) {
            Static.client.clientTransport.useVehicleInventory();
            return;
          }
        }
        menu = Static.screens.screens[Client.INVENTORY];
        menu.setup();
        Static.video.setScreen(menu);
        Static.inGame = false;
        break;
      case GLVK.VK_F1:
        showControls = !showControls;
        break;
      case GLVK.VK_F3:
        debug = !debug;
        break;
      case GLVK.VK_F11:
        Main.toggleFullscreen();
        break;
      case GLVK.VK_F10:
        //toggle fancy/fast graphics
        Settings.current.isFancy = !Settings.current.isFancy;
        Static.blocks.initPerf();
        Static.client.rebuildAll();
        break;
      case '/':
        chat = (ChatMenu)Static.screens.screens[Client.CHAT];
        chat.setup("/");
        Static.video.setScreen(chat);
        Static.inGame = false;
        break;
      case 'T':
      case GLVK.VK_ENTER:
        chat = (ChatMenu)Static.screens.screens[Client.CHAT];
        chat.setup("");
        Static.video.setScreen(chat);
        Static.inGame = false;
        break;
      case GLVK.VK_ESCAPE:
        Static.video.setScreen(Static.screens.screens[Client.PAUSE]);
        Static.inGame = false;
        break;
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        int idx = vk - '1';
        Static.client.clientTransport.changeActiveSlot((byte)idx);
        break;
      case GLVK.VK_F12:
        int cnt = 0;
        for(int a=0;a<Static.client.player.enderChest.items.length;a++) {
          cnt += Static.client.player.enderChest.items[a].count;
        }
        Static.log("# items in ender chest=" + cnt);
        break;
    }
  }

  public void resize(int width, int height) {
    perspective = null;
    this.width = width;
    this.height = height;
    super.resize(width, height);
  }

  public void mousePressed(int x, int y, int button) {
    Static.button[button] = true;
    Static.buttonClick[button] = true;
  }

  public void mouseReleased(int x, int y, int button) {
    Static.button[button] = false;
  }

  public int lastx = -1, lasty = -1;

  public void mouseMoved(int x, int y, int button) {
//    Static.log("mouse pos:" + x + "," + y);
    if (lastx == -1) {
      lastx = x;
      lasty = y;
    } else {
      int dx = x - lastx;
      int dy = y - lasty;
      Static.client.look(dx, dy);
      lastx = x;
      lasty = y;
    }
  }

  public void mouseWheel(int delta) {
    int activeSlot = Static.client.player.activeSlot + delta;
    while (activeSlot < 0) activeSlot += 9;
    while (activeSlot > 8) activeSlot -= 9;
    Static.client.clientTransport.changeActiveSlot((byte)activeSlot);
  }

  private final XYZ baseHandAngle = new XYZ(35.0f, 45.0f, 0f);
  private final XYZ baseHandPos = new XYZ(3, -3, -5.0f);
  private GLMatrix handMat = new GLMatrix();

  private void renderItemInHand() {
    Item item = Static.client.player.items[Static.client.player.activeSlot];
    glClear(GL_DEPTH_BUFFER_BIT);

    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL_FALSE, perspective.m);  //perspective matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, Static.identity.m);  //model matrix

    float elight = Static.client.player.getLight(sunLight);
    glUniform1f(Static.uniformSunLight, elight);
    if (item.id == 0) {
      renderHand();
    } else {
      renderItemInHand(item, elight);
    }
  }

  private RenderDest o_items = new RenderDest(Chunk.buffersCount);
  private RenderData data = new RenderData();

  private void renderItemInHand(Item item, float light) {
    o_items.resetAll();
    Texture texture = null;
    boolean isBlock = false;
    boolean isBlockEntity = false;
    int buffersIdx = -1;
    EntityBase eb = null;
    BlockBase block = null;

    if (Static.isBlock(item.id)) {
      block = Static.blocks.blocks[item.id];
      if (block.renderAsEntity) {
        isBlockEntity = true;
        eb = Static.entities.entities[block.entityID];
        eb.pos.x = 0;
        eb.pos.y = 0;
        eb.pos.z = 0;
        eb.ang.y = 180;
        eb.setScale(1.0f);
      } else if (block.renderAsItem) {
        texture = block.textures[0].texture;
        buffersIdx = 0;
        block.addFaceWorldItem(o_items.getBuffers(0), item.var, block.isGreen);
      } else {
        texture = block.textures[0].texture;
        isBlock = true;
        data.x = 0;
        data.y = 0;
        data.z = 0;
        data.sl[X] = 1.0f;
        data.crack = -1;
        if (block.isDirXZ) {
          data.dir[X] = S;
        } else {
          data.dir[X] = A;  //zero
        }
        block.buildBuffers(o_items, data);
        buffersIdx = block.textures[0].buffersIdx;  //BUG : zero?
      }
    } else {
      ItemBase itembase = Static.items.items[item.id];
      texture = itembase.textures[0].texture;  //BUG : zero?
      itembase.addFaceWorldItem(o_items.getBuffers(0), item.var, itembase.isGreen);
      buffersIdx = 0;
    }

    handMat.setIdentity();
    handMat.addRotate(Static.client.handAngle.x, 1, 0, 0);
    handMat.addRotate(Static.client.handAngle.y, 0, 1, 0);
    handMat.addRotate(Static.client.handAngle.z, 0, 0, 1);

    if (isBlock) {
      handMat.addRotate(baseHandAngle.x, 1, 0, 0);
      handMat.addRotate(baseHandAngle.y, 0, 1, 0);
//      handMat.addRotate(baseHandAngle.z, 0, 0, 1);
      handMat.addTranslate(-0.5f, -0.5f, 0);
    }
    handMat.addTranslate(baseHandPos.x, baseHandPos.y, baseHandPos.z);
    handMat.addTranslate(Static.client.handPos.x, Static.client.handPos.y, Static.client.handPos.z);

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, handMat.m);  //view matrix

    if (isBlockEntity) {
      eb.bindTexture();
      eb.render();
    } else {
      texture.bind();
      RenderBuffers buf = o_items.getBuffers(buffersIdx);
      buf.copyBuffers();
      buf.bindBuffers();
      buf.render();
    }
  }

  public void renderHand() {
    handMat.setIdentity();
    handMat.addTranslate(hand.org.x, hand.org.y, hand.org.z);
    handMat.addScale(2, 2, 2);
    handMat.addRotate2(-170, 0, 0, 1);
    handMat.addRotate4(Static.client.handAngle.x, 1, 0, 0);
    handMat.addRotate4(Static.client.handAngle.y, 0, 1, 0);
    handMat.addRotate4(Static.client.handAngle.z, 0, 0, 1);
    handMat.addTranslate2(-hand.org.x, -hand.org.y, -hand.org.z);
    handMat.addTranslate(-0.5f,-1.5f,0);
    handMat.addTranslate(baseHandPos.x, baseHandPos.y, baseHandPos.z);
    handMat.addTranslate(Static.client.handPos.x, Static.client.handPos.y, Static.client.handPos.z);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, handMat.m);  //view matrix
    Static.entities.entities[Entities.PLAYER].bindTexture();
    hand.bindBuffers();
    hand.render();
  }
}
