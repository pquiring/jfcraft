package jfcraft.client;

/** Game render scene
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javaforce.*;
import javaforce.gl.*;

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
  private Robot robot;  //to keep mouse in window
  private int width, height;
  private static RenderBuffers o_slots, o_active, o_icons, o_cross, o_box;
  private World world;
  public static boolean debug;
  private Runtime rt = Runtime.getRuntime();
  private boolean showControls = true;
  private static RenderBuffers hand;

  public static boolean advanceAnimation;

  public int frame;

  public Game() {
    id = Client.GAME;
    try {
      robot = new Robot();
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void setup() {
    game = this;
    world = Static.world();
    setCursor();
    Static.inGame = true;
    if (hand == null) {
      Player player = (Player)Static.entities.entities[Entities.PLAYER];
      hand = player.getRightHand();
    }
  }

  public void setCursor() {
    JFImage cursorImage = new JFImage(32,32);
    cursorImage.fill(0, 0, 32, 32, 0, true);
    Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage.getImage(), new Point(0,0), "hidden");
    Main.frame.setCursor(cursor);
  }

  public void init(GL gl) {
    super.init(gl);
    if (o_slots == null) {
      o_slots = createMenu(gl, 75,470, 0,0, 361,42);
    }
    if (o_cross == null) {
      o_cross = createMenu(gl, 240,240, 0,0, 32,32);
    }
    if (o_icons == null) {
      o_icons = new RenderBuffers();
    }
    if (view == null) {
      view = new GLMatrix();
    }
  }

  private float sunLight;

  public void render(GL gl, int width, int height) {
    synchronized(Static.renderLock) {
      this.width = width;
      this.height = height;
      render(gl);
    }
  }

  private boolean dim_env_inited[] = new boolean[Dims.MAX_ID];

  private Coords c = new Coords();

  public void process(GL gl) {
    if (advanceAnimation) {
      RenderEngine.advanceAnimation(gl);
      advanceAnimation = false;
    }
    client.chunkLighter.process();
    client.chunkBuilder.process();
    client.chunkCopier.process(gl);
  }

  public void render(GL gl) {
    setMenuSize(512, 512);

    gui_position = CENTER;

    int dim = client.player.dim;
    if (!dim_env_inited[dim]) {
      Static.log("Environment init:" + dim);
      Static.dims.dims[dim].getEnvironment().init(gl);
      dim_env_inited[dim] = true;
    }

    if (Static.inGame && client.player.health == 0) {
      Static.video.setScreen(Static.screens.screens[Client.DEAD]);
      return;
    }

    if (client.error != null) {
      client.stopTimers();
      client.stopVoIP();
      MessageMenu message = (MessageMenu)Static.screens.screens[Client.MESSAGE];
      message.setup("Error", client.error.toString(), Static.screens.screens[Client.MAIN]);
      Static.video.setScreen(message);
      return;
    }

    if (o_active == null) {
      o_active = createMenu(gl, 75 + client.activeSlot * 40,470, 1,45, 46,46);
    } else {
      recreateMenu(gl, o_active, 75 + client.activeSlot * 40,470, 1,45, 46,46);
    }

    int cx = Static.floor(client.player.pos.x / 16.0f);
    int cz = Static.floor(client.player.pos.z / 16.0f);

    Chunk chunks[] = client.world.chunks.getChunks();

    //now render stuff
    gl.glDepthMask(true);
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
    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
    if (perspective == null) {
      perspective = new GLMatrix();
      perspective.setIdentity();
      float ratio = ((float)width) / ((float)height);
      perspective.perspective(fov, ratio, zNear, zFar);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, perspective.m);  //perspective matrix

    gl.glUniform1f(Static.uniformSunLight, 1.0f);
    Static.dims.dims[dim].getEnvironment().render(gl, world.time, sunLight, client);

    view.setIdentity();
    synchronized(client.ang) {
      view.addRotate(client.ang.x, 1, 0, 0);
      view.addRotate(client.ang.y, 0, 1, 0);
    }
    view.addTranslate2(-client.player.pos.x, -client.player.pos.y -client.player.eyeHeight, -client.player.pos.z);
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, view.m);  //view matrix

    Static.blocks.stitched.bind(gl);
    int cnt = 0;
    RenderBuffers obj;
    String dmsg = "";

    //render main stitched objects
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      chunk.inRange = false;
      if (!chunk.canRender()) continue;
      if (chunk.isAllEmpty) continue;
      if (Static.abs(chunk.cx - cx) > Settings.current.loadRange) continue;
      if (Static.abs(chunk.cz - cz) > Settings.current.loadRange) continue;
      if (!chunk.ready) continue;
      chunk.inRange = true;
      obj = chunk.dest.getBuffers(Chunk.DEST_NORMAL);
      if (obj.isBufferEmpty()) continue;
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers(gl);
      obj.render(gl);
      cnt++;
    }

    //render box around block
    client.player.findBlock(-1, BlockHitTest.Type.SELECTION, client.selection);
    if (client.selection.block != null) {
      if (o_box == null) {
        o_box = new RenderBuffers();
        o_box.type = GL.GL_LINES;
      }
      o_box.reset();
      ArrayList<Box> boxes = client.selection.block.getBoxes(client.selection, BlockHitTest.Type.SELECTION);
      int boxcnt = boxes.size();
      for(int a=0;a<boxcnt;a++) {
        Box box = boxes.get(a);
        o_box.addVertex(new float[] {box.x1,box.y1,box.z1});
        o_box.addVertex(new float[] {box.x2,box.y1,box.z1});
        o_box.addVertex(new float[] {box.x1,box.y1,box.z2});
        o_box.addVertex(new float[] {box.x2,box.y1,box.z2});
        o_box.addVertex(new float[] {box.x1,box.y2,box.z1});
        o_box.addVertex(new float[] {box.x2,box.y2,box.z1});
        o_box.addVertex(new float[] {box.x1,box.y2,box.z2});
        o_box.addVertex(new float[] {box.x2,box.y2,box.z2});
        for(int b=0;b<8;b++) {
          o_box.addTextureCoords(new float[] {0,0});
          o_box.addDefault(Static.black);
        }
        //add 12 lines
        o_box.addPoly(new int[] {0,1, 2,3, 0,2, 1,3, 4,5, 6,7, 4,6, 5,7, 0,4, 1,5, 2,6, 3,7});
      }
      o_box.copyBuffers(gl);
      o_box.mat.setTranslate(client.selection.x, client.selection.y, client.selection.z);
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, o_box.mat.m);  //model matrix
      o_box.bindBuffers(gl);
      gl.glUniform1i(Static.uniformEnableTextures, 0);
      o_box.render(gl);
      gl.glUniform1i(Static.uniformEnableTextures, 1);
    }

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
        if (e.uid == client.player.uid) continue;  //do not render self
        if (!e.instanceInited) {
          e.initInstance(gl);
        }
        if (!e.isStatic) {
          if (e.dirty) {
            e.buildBuffers(e.getDest(), data);
            e.dirty = false;
          }
          if (e.needCopyBuffers) {
            e.copyBuffers(gl);
            e.needCopyBuffers = false;
          }
        }
        float elight = e.getLight(sunLight);
        gl.glUniform1f(Static.uniformSunLight, elight);
        e.bindTexture(gl);
        e.render(gl);
      }
    }
    gl.glUniform1f(Static.uniformSunLight, sunLight);

    //render text
    t_text.bind(gl);
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      if (!chunk.inRange) continue;
      if (!chunk.dest.exists(Chunk.DEST_TEXT)) continue;
      obj = chunk.dest.getBuffers(Chunk.DEST_TEXT);
      if (obj.isBufferEmpty()) continue;
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers(gl);
      obj.render(gl);
    }

    //now render alpha stuff
    gl.glDepthMask(false);  //turn off depth buffer updates
    Static.blocks.stitched.bind(gl);

    //render stitched chunks (alpha) (ie: iceblock)
    for(int a=0;a<chunks.length;a++) {
      Chunk chunk = chunks[a];
      if (!chunk.inRange) continue;
      if (!chunk.dest.exists(Chunk.DEST_ALPHA)) continue;
      obj = chunk.dest.getBuffers(Chunk.DEST_ALPHA);
      if (obj.isBufferEmpty()) continue;
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, chunk.mat.m);  //model matrix
      obj.bindBuffers(gl);
      obj.render(gl);
    }

    //TODO : render particles, etc.

    if (showControls) {
      gl.glDepthMask(true);  //turn on depth buffer updates

      renderItemInHand(gl);

      gl.glUniform1f(Static.uniformSunLight, 1.0f);

      gl.glDepthFunc(GL.GL_ALWAYS);

      //now render slots at bottom
      gui_position = BOTTOM;
      setOrtho(gl);
      gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, Static.identity.m);  //view matrix
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix

      t_widgets.bind(gl);
      o_slots.bindBuffers(gl);
      o_slots.render(gl);
      o_active.bindBuffers(gl);
      o_active.render(gl);

      //now render items in slots
      reset();

      int x = 75;
      int y = 512 - 2;
      for(int a=0;a<9;a++) {
        Item item = client.player.items[a];
        if (item.id != -1) {
          renderItem(gl,item,x,y);
        }
        x += 40;
      }

      if (client.chatTime > 0) {
        //render chat
        int dx = 0;
        int dy = 512 - fontSize * 7;
        synchronized(client.chat) {
          for(int a=client.chat.size()-1;a>=0;a--) {
            addText(dx, dy, client.chat.get(a));
            dy -= fontSize;
          }
        }
      }

      if (Settings.current.client_voip && Settings.current.ptt && Static.r_keys[KeyEvent.VK_CONTROL]) {
        addText(512 - 4 * fontSize, 512, "Talk");
      }

      if (client.itemTextTime > 0) {
        Item item = client.player.items[client.activeSlot];
        if (item.id != 0) {
          ItemBase itembase = Static.items.items[item.id];
          if (item != null) {
            String txt = itembase.getName(item.var);
            addText(256 - (txt.length() * fontSize / 2), 419, txt);
          }
        }
      }

      renderText(gl);
      renderBars(gl);

      if (debug) {
        reset();
        gui_position = TOP;
        int gx = Static.floor(client.player.pos.x % 16.0f);
        if (client.player.pos.x < 0 && gx != 0) gx = 16 + gx;
        int gy = Static.floor(client.player.pos.y);
        int gz = Static.floor(client.player.pos.z % 16.0f);
        if (client.player.pos.z < 0 && gz != 0) gz = 16 + gz;
        int dx = 0;
        int dy = fontSize;
        addText(dx,dy,"Pos:" + client.player.pos.x + "," + client.player.pos.y + "," + client.player.pos.z);
        dy += fontSize;
        addText(dx,dy,"Ang:" + client.player.ang.x + "," + client.player.ang.y + "," + client.player.ang.z);
        dy += fontSize;
        addText(dx,dy,"Chunk:" + cx + "," + cz + " Block:" + gx + "," + gy + "," + gz);
        dy += fontSize;
        addText(dx,dy,"Chunks:" + chunks.length + " Rendered:" + cnt);
        dy += fontSize;
        long free = rt.freeMemory() / (1024 * 1024);
        long total = rt.totalMemory() / (1024 * 1024);
        addText(dx,dy,"Memory:Free=" + free + "MB of " + total + "MB");
        dy += fontSize;
        addText(dx,dy,"FPS=" + Static.fps);
        dy += fontSize;
        Chunk chunk = client.player.getChunk();
        if (chunk != null) {
          int gp = (gz << 4) + gx;
          addText(dx,dy,"Biome:" + Chunk.getBiomeName(chunk.biome[gp]));
          dy += fontSize;
          addText(dx,dy," Elev:" + chunk.elev[gp]);
          dy += fontSize;
          addText(dx,dy," Temp:" + chunk.temp[gp]);
          dy += fontSize;
          addText(dx,dy," Rain:" + chunk.rain[gp]);
          dy += fontSize;
        }

        if (client.clientTransport instanceof LocalClientTransport) {
          addText(dx,dy,"Queue:S=" + client.clientTransport.getServerQueueSize() + ",C=" + client.clientTransport.getClientQueueSize());
          dy += fontSize;
        }
        addText(dx,dy,"Tick:" + Static.tick);
        dy += fontSize;
        addText(dx,dy,"Time:" + world.time);
        dy += fontSize;
        client.player.findBlock(-1, BlockHitTest.Type.SELECTION, c);
        if (c.block != null && c.chunk != null) {
          addText(dx,dy,"Hit:" + c);
          dy += fontSize;
          addText(dx,dy,"B1:" + (int)c.block.id + "," + c.block.getName(c.var)
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
            addText(dx,dy,"B2:" + (int)id2 + "," + base2.getName(var2)
              + ":bits=" + Integer.toString(bits2, 16));
            dy += fontSize;
          }
        } else if (c.entity != null) {
          addText(dx,dy,"Hit:" + c);
          dy += fontSize;
          addText(dx,dy,"Entity:" + c.entity.id + "," + c.entity.getName());
          dy += fontSize;
        }
        addText(dx,dy,dmsg);
        dy += fontSize;
        renderText(gl);
        gui_position = BOTTOM;
      }

      //render icons
      setOrtho(gl);
      t_icons.bind(gl);
      o_cross.bindBuffers(gl);
      o_cross.render(gl);
      o_icons.reset();
      float health = client.player.health;
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
      float ar = client.player.ar;
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
      float air = client.player.air;
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
      int food = (int)client.player.hunger;
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

      o_icons.copyBuffers(gl);
      o_icons.bindBuffers(gl);
      o_icons.render(gl);
    }  //showControls

    gui_position = CENTER;

    frame++;
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    if (!Static.inGame) return;
    ChatMenu chat;
    switch (vk) {
      case 'E':
        InventoryMenu menu = (InventoryMenu)Static.screens.screens[Client.INVENTORY];
        menu.setup();
        Static.video.setScreen(menu);
        lastx = -1;
        Static.inGame = false;
        break;
      case KeyEvent.VK_F1:
        showControls = !showControls;
        break;
      case KeyEvent.VK_F3:
        debug = !debug;
        break;
      case '/':
        chat = (ChatMenu)Static.screens.screens[Client.CHAT];
        chat.setup("/");
        Static.video.setScreen(chat);
        lastx = -1;
        Static.inGame = false;
        break;
      case 'T':
      case KeyEvent.VK_ENTER:
        chat = (ChatMenu)Static.screens.screens[Client.CHAT];
        chat.setup("");
        Static.video.setScreen(chat);
        lastx = -1;
        Static.inGame = false;
        break;
      case KeyEvent.VK_ESCAPE:
        Static.video.setScreen(Static.screens.screens[Client.PAUSE]);
        lastx = -1;
        Static.inGame = false;
        break;
      case KeyEvent.VK_1:
      case KeyEvent.VK_2:
      case KeyEvent.VK_3:
      case KeyEvent.VK_4:
      case KeyEvent.VK_5:
      case KeyEvent.VK_6:
      case KeyEvent.VK_7:
      case KeyEvent.VK_8:
      case KeyEvent.VK_9:
        int idx = vk - KeyEvent.VK_1;
        client.clientTransport.changeActiveSlot((byte)idx);
        break;
      case KeyEvent.VK_F12:
        int cnt = 0;
        for(int a=0;a<client.player.enderChest.items.length;a++) {
          cnt += client.player.enderChest.items[a].count;
        }
        Static.log("# items in ender chest=" + cnt);
        break;
    }
  }

  public void resize(GL gl, int width, int height) {
    perspective = null;
    this.width = width;
    this.height = height;
    super.resize(gl, width, height);
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
    if (lastx == -1) {
      lastx = x;
      lasty = y;
    } else {
      int dx = x - lastx;
      int dy = y - lasty;
      client.look(dx, dy);
      lastx = x;
      lasty = y;
    }
    //keep mouse inside window
    int w4 = 512/4;
    int h4 = 512/4;
    if ((x < w4) || (x > w4*3) || (y < h4) || (y > h4*3)) {
      Point los = Main.frame.getLocationOnScreen();
      lastx = -1;
      robot.mouseMove(los.x + width/2, los.y + height/2);
    }
  }

  public void mouseWheel(int delta) {
    int activeSlot = client.activeSlot + delta;
    while (activeSlot < 0) activeSlot += 9;
    while (activeSlot > 8) activeSlot -= 9;
    client.clientTransport.changeActiveSlot((byte)activeSlot);
  }

  public void enterMenu(byte idx) {
    super.enterMenu(idx);
    lastx = -1;
  }

  private final XYZ baseHandAngle = new XYZ(35.0f, 45.0f, 0f);
  private final XYZ baseHandPos = new XYZ(3, -3, -5.0f);
  private GLMatrix handMat = new GLMatrix();

  private void renderItemInHand(GL gl) {
    Item item = client.player.items[client.activeSlot];
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, perspective.m);  //perspective matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix

    if (item.id == 0) {
      renderHand(gl);
    } else {
      renderItemInHand(gl, item);
    }
  }

  private RenderDest o_items = new RenderDest(Chunk.buffersCount);
  private RenderData data = new RenderData();

  private void renderItemInHand(GL gl, Item item) {
    o_items.resetAll();
    Texture texture;
    boolean isBlock = false;
    boolean isBlockEntity = false;
    int buffersIdx = -1;
    EntityBase eb = null;
    BlockBase block = null;

    if (Static.isBlock(item.id)) {
      block = Static.blocks.blocks[item.id];
      texture = block.textures[0].texture;
      if (block.renderAsEntity) {
        isBlockEntity = true;
        eb = Static.entities.entities[block.entityID];
        eb.pos.x = 0;
        eb.pos.y = 0;
        eb.pos.z = 0;
        eb.ang.y = 180;
        eb.setScale(1.0f);
      } else if (block.renderAsItem) {
        buffersIdx = 0;
        block.addFaceWorldItem(o_items.getBuffers(0), item.var, block.isGreen);
      } else {
        isBlock = true;
        data.x = 0;
        data.y = 0;
        data.z = 0;
        data.sl[X] = sunLight;
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
    handMat.addRotate(client.handAngle.x, 1, 0, 0);
    handMat.addRotate(client.handAngle.y, 0, 1, 0);
    handMat.addRotate(client.handAngle.z, 0, 0, 1);

    if (isBlock) {
      handMat.addRotate(baseHandAngle.x, 1, 0, 0);
      handMat.addRotate(baseHandAngle.y, 0, 1, 0);
//      handMat.addRotate(baseHandAngle.z, 0, 0, 1);
      handMat.addTranslate(-0.5f, -0.5f, 0);
    }
    handMat.addTranslate(baseHandPos.x, baseHandPos.y, baseHandPos.z);
    handMat.addTranslate(client.handPos.x, client.handPos.y, client.handPos.z);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, handMat.m);  //view matrix

    if (isBlockEntity) {
      eb.bindTexture(gl);
      eb.render(gl);
    } else {
      texture.bind(gl);
      RenderBuffers buf = o_items.getBuffers(buffersIdx);
      buf.copyBuffers(gl);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
  }

  public void renderHand(GL gl) {
    handMat.setIdentity();
    handMat.addTranslate(hand.org.x, hand.org.y, hand.org.z);
    handMat.addScale(2, 2, 2);
    handMat.addRotate2(-170, 0, 0, 1);
    handMat.addRotate4(client.handAngle.x, 1, 0, 0);
    handMat.addRotate4(client.handAngle.y, 0, 1, 0);
    handMat.addRotate4(client.handAngle.z, 0, 0, 1);
    handMat.addTranslate2(-hand.org.x, -hand.org.y, -hand.org.z);
    handMat.addTranslate(-0.5f,-1.5f,0);
    handMat.addTranslate(baseHandPos.x, baseHandPos.y, baseHandPos.z);
    handMat.addTranslate(client.handPos.x, client.handPos.y, client.handPos.z);
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, handMat.m);  //view matrix
    Static.entities.entities[Entities.PLAYER].bindTexture(gl);
    hand.bindBuffers(gl);
    hand.render(gl);
  }
}
