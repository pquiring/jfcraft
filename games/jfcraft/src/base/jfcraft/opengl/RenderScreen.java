package jfcraft.opengl;

/** Render scene base class
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

import javaforce.gl.*;
import javaforce.*;

import jfcraft.block.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;

public abstract class RenderScreen {
  public static float gui_height = 512;
  public static float gui_width = 512;

  public static Texture t_widgets;
  public static Texture t_icons;
  public static Texture t_text;
  public static Texture t_white;  //single white pixel
  public static Texture t_white50;  //single white pixel (50% alpha)
  private static Timer cursorTimer;

  private static RenderBuffers o_text;

  public static GLMatrix orthoItem = new GLMatrix();
  public static GLMatrix orthoBlock = new GLMatrix();
  public static GLMatrix orthoPlayer = new GLMatrix();

  public static int fontSize = 12;

  private static boolean showCursor;

  private static RenderDest o_items;
  private static RenderBuffers o_bars = new RenderBuffers();
  private static RenderBuffers o_bars50 = new RenderBuffers();
  private static RenderData data = new RenderData();

  public static GLMatrix identity = new GLMatrix();

  public byte id;
  private TextField focus;
  public ArrayList<Button> buttons = new ArrayList<Button>();
  public ArrayList<TextField> fields = new ArrayList<TextField>();
  public ArrayList<ScrollBar> scrolls = new ArrayList<ScrollBar>();

  public static void initStatic() {
    //ortho(left, right, bottom, top, near, far)
    orthoItem.ortho(0, 1, 1, 0, 0, 1);
    orthoBlock.ortho(-0.15f, 1.60f, -0.50f, 1.35f, -2, 2);  //trial and error
    orthoBlock.addRotate(35, 1,0,0);
    orthoBlock.addRotate(45, 0,1,0);
    orthoPlayer.ortho(-1, 1, 0, 2, -1, 1);
  }

  private int getFieldIndex() {
    if (focus == null) return 0;
    for(int a=0;a<fields.size();a++) {
      if (fields.get(a) == focus) return a;
    }
    return -1;
  }

  public abstract void render(GL gl, int width, int height);
  public void process() {};
  public void resize(GL gl, int width, int height) {}
  public void mousePressed(int x,int y,int but) {
    for(int a=0;a<buttons.size();a++) {
      Button button = buttons.get(a);
      if (x >= button.x1 && x <= button.x2 && y >= button.y1 && y <= button.y2) {
        focus = null;
        button.r.run();
        return;
      }
    }
    for(int a=0;a<fields.size();a++) {
      TextField field = fields.get(a);
      if (x >= field.x1 && x <= field.x2 && y >= field.y1 && y <= field.y2) {
        focus = field;
        return;
      }
    }
    for(int a=0;a<scrolls.size();a++) {
      ScrollBar sb = scrolls.get(a);
      if (x >= sb.x1 && x <= sb.x2 && y >= sb.y1 && y <= sb.y2) {
        sb.setPosition(y);
        return;
      }
    }
    focus = null;
  }
  public void mouseReleased(int x,int y,int button) {}
  public void mouseMoved(int x,int y,int button) {}
  public void mouseWheel(int delta) {}

  public void keyTyped(char ch) {
    if (focus != null) {
      focus.keyTyped(ch);
    }
  }

  public void keyPressed(int vk) {
    if (focus != null) {
      focus.keyPressed(vk);
    }
  }

  public void keyReleased(int vk) {}

  public float tc(float max, float pt) {
    return pt / max;
  }

  public static void initStatic(GL gl) {
    if (t_widgets == null) {
      t_widgets = Textures.getTexture(gl, "gui/widgets", 0);
    }
    if (t_icons == null) {
      t_icons = Textures.getTexture(gl, "gui/icons", 0);
    }
    if (t_text == null) {
      t_text = Textures.getTexture(gl, "font/ascii", 0);
    }
    if (o_text == null) {
      o_text = new RenderBuffers();
    }
    if (t_white == null) {
      JFImage pixel = new JFImage(1,1);
      pixel.putPixel(0, 0, 0xffffff);  //white pixel
      t_white = new Texture();
      t_white.load(gl, pixel);
    }
    if (t_white50 == null) {
      JFImage pixel = new JFImage(1,1);
      pixel.putPixel(0, 0, 0xffffff);  //white pixel
      pixel.putAlpha(0, 0, 0x80);  //50% transparent
      t_white50 = new Texture();
      t_white50.load(gl, pixel);
    }
    if (cursorTimer == null) {
      cursorTimer = new Timer();
      cursorTimer.schedule(new TimerTask() {
        public void run() {
          showCursor = !showCursor;
        }
      }, 333, 333);
    }
  }

  /** Setup anything need to show menu each time. */
  public void setup() {}

  public void init(GL gl) {
    data.crack = -1;
  }

  public static final int TOP = 0;
  public static final int CENTER = 1;
  public static final int BOTTOM = 2;
  public static int gui_position = CENTER;  //TOP CENTER BOTTOM

  /** Sets a standard 0,0-1,1 ortho matrix for gui_width/gui_height. */
  public void setOrtho(GL gl) {
    //left right bottom top near far
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoItem.m);  //perspective matrix
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - (gui_height * Static.scale)); break;
      case CENTER: vpy = offsetY; break;
      case BOTTOM: vpy = 0; break;
    }
    gl.glViewport((int)offsetX, (int)vpy, (int)(gui_width * Static.scale), (int)(gui_height * Static.scale));
  }

  /** Sets a standard 0,0-1,1 ortho matrix for item/text. */
  public void setOrthoItem(GL gl, int x, int y) {
    //left right bottom top near far
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoItem.m);  //perspective matrix
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    gl.glViewport((int)(offsetX + ((float)x) * Static.scale), (int)vpy, (int)(36 * Static.scale), (int)(36 * Static.scale));
  }

  /** Sets a standard 0,0-1,1 ortho matrix for block. */
  public void setOrthoBlock(GL gl, int x, int y) {
    //left right bottom top near far
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoBlock.m);  //perspective matrix
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (Static.height - gui_height) - y; break;
      case CENTER: vpy = (offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = ((gui_height - y) * Static.scale); break;
    }
    gl.glViewport((int)(offsetX + ((float)x) * Static.scale), (int)vpy, (int)(36 * Static.scale), (int)(36 * Static.scale));
  }

  /** Creates a 0,0-1,1 menu object. */
  public RenderBuffers createOrthoMenu(GL gl) {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    o_menu.addVertex(0,0,0, 0,0);  //OPTZ : switch all to this format
    o_menu.addVertex(new float[] {1,0,0}, new float[] {1,0});
    o_menu.addVertex(new float[] {1,1,0}, new float[] {1,1});
    o_menu.addVertex(new float[] {0,1,0}, new float[] {0,1});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers(gl);
    return o_menu;
  }

  /** Creates a 0,0-1,1 menu object with a partial image coords (upper left corner) using gui totalSize. */
  public RenderBuffers createMenu(GL gl) {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    o_menu.addVertex(new float[] {0,0,0}, new float[] {0,0});
    o_menu.addVertex(new float[] {1,0,0}, new float[] {tc(512.0f, gui_width),0});
    o_menu.addVertex(new float[] {1,1,0}, new float[] {tc(512.0f, gui_width),tc(512.0f, gui_height)});
    o_menu.addVertex(new float[] {0,1,0}, new float[] {0,tc(512.0f, gui_height)});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers(gl);
    return o_menu;
  }

  /** Creates a custom sized 3d object with custom images coords.
   * sx/y = screen dest position
   * tx/y = images coords position
   * width/height = totalSize of menu
   */
  public RenderBuffers createMenu(GL gl,
    int sx1,int sy1,
    int tx1,int ty1,
    int width,int height)
  {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    float fsw = width / gui_width;
    float fsh = height / gui_height;
    float ftw = width / 512.0f;
    float fth = height / 512.0f;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / 512.0f;
    float fty1 = ty1 / 512.0f;
    float ftx2 = ftx1 + ftw;
    float fty2 = fty1 + fth;
    o_menu.addVertex(new float[] {fsx1,fsy1,0.0f}, new float[] {ftx1,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy1,0.0f}, new float[] {ftx2,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy2,0.0f}, new float[] {ftx2,fty2});
    o_menu.addVertex(new float[] {fsx1,fsy2,0.0f}, new float[] {ftx1,fty2});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers(gl);
    return o_menu;
  }

  /** Creates a custom sized 3d object with custom images coords.
   * sx/y = screen dest position
   * tx/y = images coords position
   * swidth/sheight = totalSize of menu on screen
   * twidth/theight = images totalSize of menu
   */
  public RenderBuffers createMenu(GL gl, int sx1,int sy1,int tx1,int ty1,int swidth,int sheight,int twidth, int theight) {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    float fsw = swidth / gui_width;
    float fsh = sheight / gui_height;
    float ftw = twidth / 512.0f;
    float fth = theight / 512.0f;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / 512.0f;
    float fty1 = ty1 / 512.0f;
    float ftx2 = ftx1 + ftw;
    float fty2 = fty1 + fth;
    o_menu.addVertex(new float[] {fsx1,fsy1,0.0f}, new float[] {ftx1,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy1,0.0f}, new float[] {ftx2,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy2,0.0f}, new float[] {ftx2,fty2});
    o_menu.addVertex(new float[] {fsx1,fsy2,0.0f}, new float[] {ftx1,fty2});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers(gl);
    return o_menu;
  }

  public void recreateMenu(GL gl, RenderBuffers o_menu, int sx1,int sy1,int tx1,int ty1,int width,int height) {
    o_menu.reset();
    //create vertex data
    float fsw = width / gui_width;
    float fsh = height / gui_height;
    float ftw = width / 512.0f;
    float fth = height / 512.0f;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / 512.0f;
    float fty1 = ty1 / 512.0f;
    float ftx2 = ftx1 + ftw;
    float fty2 = fty1 + fth;
    o_menu.addVertex(new float[] {fsx1,fsy1,0}, new float[] {ftx1,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy1,0}, new float[] {ftx2,fty1});
    o_menu.addVertex(new float[] {fsx2,fsy2,0}, new float[] {ftx2,fty2});
    o_menu.addVertex(new float[] {fsx1,fsy2,0}, new float[] {ftx1,fty2});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers(gl);
  }

  /** Resets text and bars. */
  public void reset() {
    o_text.reset();
    o_bars.reset();
    o_bars50.reset();
  }

  public void clear() {
    buttons.clear();
    fields.clear();
    scrolls.clear();
  }

  private void addChar(int x,int y, char ch, float clr[], int scale) {
    float fx1 = x / gui_width;
    float fy1 = y / gui_height;
    x += fontSize * scale;
    y += fontSize * scale;
    float fx2 = x / gui_width;
    float fy2 = y / gui_height;
    float tx1 = (ch % 16) / 16.0f;
    float ty1 = Static.floor(ch / 16) / 16.0f;
    float tx2 = tx1 + Static._1_16;
    float ty2 = ty1 + Static._1_16;
    int i = o_text.getVertexCount();
    o_text.addVertex(new float[] {fx1,fy1,0}, new float[] {tx1, ty1});
    o_text.addVertex(new float[] {fx2,fy1,0}, new float[] {tx2, ty1});
    o_text.addVertex(new float[] {fx2,fy2,0}, new float[] {tx2, ty2});
    o_text.addVertex(new float[] {fx1,fy2,0}, new float[] {tx1, ty2});
    for(int a=0;a<4;a++) {
      o_text.addDefault(clr);
    }
    o_text.addPoly(new int[] {i+3,i+2,i+1,i+0});
  }

  public void addText(int x,int y,String text, float clr[]) {
    if (text == null) {
      //if you try to display an invalid world, text will be null
      Static.log("addText(null)");
      return;
    }
    char ca[] = text.toCharArray();
    y -= fontSize;
    for(int a=0;a<ca.length;a++) {
      addChar(x, y, ca[a], clr, 1);
      x += fontSize;
    }
  }

  public void addText(int x,int y,String text) {
    addText(x,y,text,Static.white);
  }

  /** Adds an items damage bar. */
  public void addBar(int x,int y, float dmg) {
    //render bar (36x2)
    int length = (int)(36.0f * dmg);
    float clr[] = Static.red;
    if (dmg > 0.6f) {
      clr = Static.green;
    } else if (dmg > 0.3f) {
      clr = Static.yellow;
    }
    o_bars.addFace2D(x / 512.0f, y / 512.0f, (x + 36) / 512.0f, (y + 2) / 512.0f
      , 0, 0, 1, 1, Static.grey);
    o_bars.addFace2D(x / 512.0f, y / 512.0f, (x + length) / 512.0f, (y + 2) / 512.0f
      , 0, 0, 1, 1, clr);
  }

  /** Adds a simple bar (rectangle). */
  public void addBar(int x1,int y1,int width,int height, float clr[]) {
    int x2 = x1 + width;
    int y2 = y1 + height;
    o_bars.addFace2D(x1 / 512.0f, y1 / 512.0f, x2 / 512.0f, y2 / 512.0f
      , 0, 0, 1, 1, clr);
  }

  /** Adds a simple bar (rectangle) 50% transparent. */
  public void addBar50(int x1,int y1,int width,int height, float clr[]) {
    int x2 = x1 + width;
    int y2 = y1 + height;
    o_bars50.addFace2D(x1 / gui_width, y1 / gui_height, x2 / gui_width, y2 / gui_height
      , 0, 0, 1, 1, clr);
  }

  public void renderText(GL gl) {
    setOrtho(gl);
    t_text.bind(gl);
    o_text.copyBuffers(gl);
    o_text.bindBuffers(gl);
    o_text.render(gl);
  }

  public void renderBars(GL gl) {
    if (!o_bars.isArrayEmpty()) {
      setOrtho(gl);
      t_white.bind(gl);
      o_bars.copyBuffers(gl);
      o_bars.bindBuffers(gl);
      o_bars.render(gl);
    }
  }

  public void renderBars50(GL gl) {
    if (!o_bars50.isArrayEmpty()) {
      setOrtho(gl);
      t_white50.bind(gl);
      o_bars50.copyBuffers(gl);
      o_bars50.bindBuffers(gl);
      o_bars50.render(gl);
    }
  }

  public void renderItem(GL gl, Item item, int x, int y) {
    if (item.id == 0) return;
    if (o_items == null) {
      o_items = new RenderDest(Chunk.buffersCount);
    }
    o_items.resetAll();
    if (item.count > 1) {
      addText(x,y,"" + item.count);
    }
    Texture texture;
    int buffersIdx;
    if (Static.isBlock(item.id)) {
      BlockBase block = Static.blocks.blocks[item.id];
      texture = Static.blocks.stitched;
      if (block.renderAsEntity) {
        setOrthoBlock(gl, x, y);
        EntityBase eb = Static.entities.entities[block.entityID];
        eb.pos.x = 0.5f;
        eb.pos.y = 0.5f;
        eb.pos.z = 0.5f;
        eb.ang.y = -90;
        eb.setScale(1.0f);
        eb.bindTexture(gl);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthFunc(GL.GL_LEQUAL);
        eb.render(gl);
        gl.glDepthFunc(GL.GL_ALWAYS);
        return;
      } else if (block.renderAsItem) {
        setOrthoItem(gl, x, y);
        block.addFaceInvItem(o_items.getBuffers(0), item.var, block.isGreen);
        buffersIdx = 0;
      } else {
        setOrthoBlock(gl, x, y);
        data.x = 0;
        data.y = 0;
        data.z = 0;
        data.sl[X] = 1.0f;
        data.bl[X] = 1.0f;
        data.crack = -1;
        data.dir[X] = block.getPreferredDir();
        if (block.isVar) {
          data.var[X] = item.var;
        } else {
          data.var[X] = 0;
        }
        block.buildBuffers(o_items, data);
        buffersIdx = block.buffersIdx;
      }
    } else {
      setOrthoItem(gl, x, y);
      ItemBase itembase = Static.items.items[item.id];
      texture = Static.items.stitched;
      buffersIdx = 0;
      itembase.addFaceInvItem(o_items.getBuffers(0), item.var, false);
      if (itembase.isDamaged) {
        if (item.dmg != 1.0f) {
          addBar(x,y-2,item.dmg);
        }
      }
    }
    texture.bind(gl);
    RenderBuffers obj = o_items.getBuffers(buffersIdx);
    obj.copyBuffers(gl);
    obj.bindBuffers(gl);
    obj.render(gl);
  }

  public void renderItemName(GL gl, Item item, int x,int y) {
    ItemBase itembase = Static.items.items[item.id];
    String txt = itembase.getName(item.var);
    x += fontSize;
    y += fontSize;
    addText(x,y,txt);
    int w = txt.length() * fontSize;
    int h = fontSize;
    y -= fontSize;
    addBar50(x-2,y-2,w+4,h+4,Static.blue);
  }

  public void enterMenu(byte idx) {
    Static.clearButtons();
    Static.inGame = false;
    Static.video.setScreen(Static.screens.screens[idx]);
  }

  public void leaveMenu() {
    Static.video.setScreen(Static.game);
    Static.game.setCursor();
    Static.inGame = true;
  }

  /** Sets a default cursor */
  public void setCursor() {
    Main.frame.setCursor(Cursor.getDefaultCursor());
  }

  /** Sets the size of the menu on screen (effects mouse coords, etc.). */
  public void setMenuSize(int width, int height) {
    gui_width = width;
    gui_height = height;
  }

  private static RenderBuffers o_shade = null;

  /** Render a shade over whole screen.
   * Assumes view/model matrix are set to identity.
   */
  public void renderShade(GL gl) {
    renderShade(gl,0,0,(int)Static.width,(int)Static.height);
  }

  /** Render a shade over area.
   * Assumes view/model matrix are set to identity.
   */
  public void renderShade(GL gl, int x, int y, int w, int h) {
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoItem.m);
    gl.glViewport(x, y, w, h);
    if (o_shade == null) {
      o_shade = new RenderBuffers();
      o_shade.addVertex(new float[]{0, 0, 0}, new float[]{0, 0});
      o_shade.addVertex(new float[]{1, 0, 0}, new float[]{1, 0});
      o_shade.addVertex(new float[]{1, 1, 0}, new float[]{1, 1});
      o_shade.addVertex(new float[]{0, 1, 0}, new float[]{0, 1});
      o_shade.addPoly(new int[]{3, 2, 1, 0});
      for (int a = 0; a < 4; a++) {
        o_shade.addDefault(Static.black);
      }
      o_shade.copyBuffers(gl);
    }
    t_white50.bind(gl);
    o_shade.bindBuffers(gl);
    o_shade.render(gl);
  }

  public static class Button {
    int x1,y1,x2,y2;
    int tx,ty;  //text position
    String txt;
    RenderBuffers left, right;
    Runnable r;
    float clr[] = Static.white;
    /** Changes color of text. */
    public void setClr(float clr[]) {
      this.clr = clr;
    }
    public void setText(String txt) {
      this.txt = txt;
    }
  }

  public class TextField {
    StringBuffer txt;
    int dpos;  //display position
    int cpos;  //cursor position (relative to start)
    int chars;  //# full chars displayable
    int x1,y1,x2,y2;
    int width;
    int max;
    JFImage i_back;
    RenderBuffers o_back;
    Texture t_back;
    boolean center;
    int scale;
    public String getText() {
      return txt.toString();
    }
    public void setText(String newTxt) {
      txt = new StringBuffer(newTxt);
      dpos = 0;
      cpos = newTxt.length();
      findCursor();
    }
    public void keyTyped(char ch) {
//      Static.log("key=" + (int)ch);
      if (ch == 8) {
        backspace();
      } else if (ch == 127) {
        //delete();  //done in keyPressed()
      } else {
        if (ch < 13) return;
        if (txt.length() == max) return;
        txt.insert(cpos, ch);
        cpos++;
      }
    }
    public void keyPressed(int code) {
      switch (code) {
        case KeyEvent.VK_UP: up(); break;
        case KeyEvent.VK_DOWN: down(); break;
      }
      if (center) return;
      switch (code) {
        case KeyEvent.VK_DELETE: delete(); break;
        case KeyEvent.VK_LEFT: left(); break;
        case KeyEvent.VK_RIGHT: right(); break;
        case KeyEvent.VK_HOME: home(); break;
        case KeyEvent.VK_END: end(); break;
      }
    }
    public void findCursor() {
      int d1 = dpos;
      int d2 = dpos + chars;
      while (cpos < d1) {
        dpos--;
        d1--;
        d2--;
      }
      while (cpos > d2) {
        dpos++;
//        d1++;
        d2++;
      }
    }
    public void left() {
      if (cpos == 0) return;
      cpos--;
      findCursor();
    }
    public void right() {
      if (cpos == txt.length()) return;
      cpos++;
      findCursor();
    }
    public void home() {
      cpos = 0;
      findCursor();
    }
    public void end() {
      cpos = txt.length();
      findCursor();
    }
    public void up() {
      int idx = getFieldIndex();
      idx--;
      if (idx == -1) idx = fields.size() - 1;
      focus = fields.get(idx);
    }
    public void down() {
      int idx = getFieldIndex();
      idx++;
      if (idx == fields.size()) idx = 0;
      focus = fields.get(idx);
    }
    public void backspace() {
      if (txt.length() == 0) return;
      if (cpos == 0) return;
      txt.deleteCharAt(cpos-1);
      cpos--;
    }
    public void delete() {
      if (cpos == txt.length()) return;
      txt.deleteCharAt(cpos);
    }
  }

  public static class ScrollBar {
    RenderBuffers back, button;
    int x1,y1,x2,y2;
    int width, height;
    int totalSize, pos;
    int barSize;
    float scale;
    void setPosition(int y) {
      if (totalSize < height) {
        pos = 0;
        return;
      }
      pos = (y - y1);
      if (pos < 0) pos = 0;
      if (pos > height - barSize) pos = height - barSize;
    }
    public int getPosition() {
      return (int)(pos * scale);
    }
  }

  public Button addButton(GL gl, String txt,int x1,int y1,int width, Runnable r) {
    int sl = txt.length();  //TODO : calc font chars
    Button button = new Button();
    button.r = r;
    button.x1 = x1;
    button.y1 = y1;
    button.x2 = x1 + width - 1;
    button.y2 = y1 + 40 - 1;
    button.tx = x1 + width/2 - (sl * fontSize)/2;  //fontSize-1 ???
    button.ty = y1 + 40/2 + fontSize/2;
    button.txt = txt;
    button.left = createMenu(gl, x1, y1, 0, 133, width/2, 40, width/2, 40);
    button.right = createMenu(gl, x1 + width/2, y1, 400 - width/2, 133, width/2, 40, width/2, 40);
    buttons.add(button);
    return button;
  }

  public void renderButtons(GL gl) {
    setOrtho(gl);
    t_widgets.bind(gl);
    for(int a=0;a<buttons.size();a++) {
      Button button = buttons.get(a);
      addText(button.tx, button.ty, button.txt, button.clr);
      button.left.bindBuffers(gl);
      button.left.render(gl);
      button.right.bindBuffers(gl);
      button.right.render(gl);
    }
  }

  public TextField addTextField(GL gl, String txt, int x1, int y1, int width, boolean back, int max, boolean center, int scale) {
    TextField field = new TextField();
    field.x1 = x1;
    field.y1 = y1;
    field.x2 = x1 + width - 1;
    field.y2 = y1 + 14;
    field.width = width;
    field.txt = new StringBuffer(txt);
    if (max > 127) max = 127;
    field.max = max;
    field.center = center;
    field.scale = scale;
    field.chars = width / fontSize;
    field.cpos = txt.length();
    field.findCursor();
    if (back) {
      field.i_back = new JFImage(width, 15);
      field.i_back.fill(0, 0, width, 15, 0);
//      field.i_back.box(0, 0, width-1, 14, 0xffffff);
      field.t_back = new Texture();
      field.t_back.load(gl, field.i_back);
      field.o_back = new RenderBuffers();
      field.o_back.addFace2D(x1 / 512.0f, y1 / 512.0f, 0, 0, width / 512.0f, 15 / 512.0f, Static.white);
      field.o_back.copyBuffers(gl);
    }
    fields.add(field);
    return field;
  }

  public void renderFields(GL gl) {
    setOrtho(gl);
    for(int a=0;a<fields.size();a++) {
      TextField field = fields.get(a);
      if (field.t_back != null) {
        field.t_back.bind(gl);
        field.o_back.bindBuffers(gl);
        field.o_back.render(gl);
      }
      int x = field.x1;
      int y = field.y1;
      int sl = field.txt.length();
      int x1 = 0;
      if (field.center) {
        x += (field.width - sl * fontSize * field.scale) / 2;
        x1 = x;
      }
      for(int p=field.dpos;p<field.txt.length();p++) {
        addChar(x,y,field.txt.charAt(p),Static.white, field.scale);
        x += fontSize * field.scale;
      }
      if (showCursor && focus == field) {
        if (field.center) {
          addChar(x, y,'<', Static.white, field.scale);
          addChar(x1 - fontSize * field.scale, y,'>', Static.white, field.scale);
        } else {
          addChar(field.x1 + ((field.cpos - field.dpos) * fontSize), y,(char)219, Static.white, field.scale);
        }
      }
    }
  }

  /** Adds a vertical scroll bar. */
  public ScrollBar addScrollBar(GL gl, int x1, int y1, int width, int height, int size) {
    ScrollBar sb = new ScrollBar();
    int x2 = x1 + width;
    int y2 = y1 + height;
    sb.x1 = x1;
    sb.y1 = y1;
    sb.x2 = x2;
    sb.y2 = y2;
    sb.width = width;
    sb.height = height;
    sb.back = new RenderBuffers();
    sb.back.addFace2D(x1 / 512.0f, y1 / 512.0f, x2 / 512.0f, y2 / 512.0f, 0,0,1,1,Static.black);
    sb.button = new RenderBuffers();
    sb.totalSize = size;
    sb.scale = (((float)height) / ((float)size));
    if (sb.scale >= 1.0f) {
      sb.barSize = sb.height;
    } else {
      sb.barSize = (int)(sb.scale * ((float)height));
    }
    scrolls.add(sb);
    return sb;
  }

  public void renderScrollBars(GL gl) {
    setOrtho(gl);
    t_white.bind(gl);
    for(int a=0;a<scrolls.size();a++) {
      ScrollBar sb = scrolls.get(a);
      sb.button.addFace2D(sb.x1 / 512.0f, sb.y1 / 512.0f, sb.x2 / 512.0f, sb.y2 / 512.0f, 0,0,1,1,Static.white);
      sb.back.bindBuffers(gl);
      sb.back.render(gl);
      sb.button.copyBuffers(gl);
      sb.button.bindBuffers(gl);
      sb.button.render(gl);
    }
  }

  public void setFocus(TextField field) {
    focus = field;
  }

  public void setScreen(RenderScreen screen) {
    Static.video.setScreen(screen);
  }
}
