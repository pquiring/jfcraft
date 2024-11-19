package jfcraft.opengl;

/** Render scene base class
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.block.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;
import static jfcraft.entity.EntityBase.*;

public abstract class RenderScreen {
  public float gui_width = 512;
  public float gui_height = 512;

  public float gui_width_max = 512;
  public float gui_height_max = 512;

  public float sprite_width = 512;
  public float sprite_height = 512;

  public static boolean debug = false;

  public static TextureMap t_widgets;
  public static TextureMap t_icons;
  public static TextureMap t_text;
  public static TextureMap t_white;  //single white pixel
  public static TextureMap t_white50;  //single white pixel (50% alpha)
  private static Timer cursorTimer;

  // top/left=0,0 : bottom/right=1,1
  public static Matrix orthoItem = new Matrix();
  // show block at 3/4 view
  public static Matrix orthoBlock = new Matrix();
  // show two blocks
  public static Matrix orthoPlayer = new Matrix();

  public static int fontSize = 12;

  private static boolean showCursor;

  private static RenderBuffers o_box = new RenderBuffers();

  private static RenderBuffers o_chars[];

  public static Matrix identity = new Matrix();

  /** TextureMap / RenderBuffers combo.
   * Sprites are full image textures.
   */
  public class Sprite {
    public TextureMap texture;
    public RenderBuffers buffers;
    public int x1, y1;
    public int width, height;

    public Sprite(String txt) {
      texture = Textures.getTexture(txt, 0);
      buffers = new RenderBuffers();
    }

    public Sprite(String txt, int x1, int y1, int width, int height) {
      texture = Textures.getTexture(txt, 0);
      buffers = new RenderBuffers();
      createSprite(buffers, x1, y1, width, height, 0f, 0f, 100f, 100f);
    }

    public Sprite(String txt, int x1, int y1, int width, int height, float tx, float ty, float tw, float th) {
      texture = Textures.getTexture(txt, 0);
      buffers = new RenderBuffers();
      createSprite(buffers, x1, y1, width, height, tx, ty, tw, th);
    }

    public void recreate(int x1, int y1, int width, int height, float tx, float ty, float tw, float th) {
      createSprite(buffers, x1, y1, width, height, tx, ty, tw, th);
    }

    public void render() {
      texture.bind();
      buffers.bindBuffers();
      buffers.render();
    }
  }

  public byte id;
  public byte getID() {
    return id;
  }
  public boolean isMain() {
    return false;
  }
  private TextField focus;
  public ArrayList<Button> buttons = new ArrayList<Button>();
  public ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
  public ArrayList<TextField> fields = new ArrayList<TextField>();
  public ArrayList<ScrollBar> scrolls = new ArrayList<ScrollBar>();

  public Button getButton(int idx) {
    return buttons.get(idx);
  }

  public TextField getField(int idx) {
    return fields.get(idx);
  }

  public ScrollBar getScrollBar(int idx) {
    return scrolls.get(idx);
  }

  public static void initStaticGL() {
    //ortho(left, right, bottom, top, near, far)
    orthoItem.ortho(0, 1, 1, 0, 0, 1);
    orthoBlock.ortho(-0.15f, 1.60f, -0.50f, 1.35f, -2, 2);  //trial and error
    orthoBlock.addRotate(35, 1,0,0);
    orthoBlock.addRotate(45, 0,1,0);
    orthoPlayer.ortho(-1, 1, 0, 2, -1, 1);
    o_chars = new RenderBuffers[256];
    for(int a=0;a<o_chars.length;a++) {
      o_chars[a] = new RenderBuffers();
      RenderBuffers buf = o_chars[a];
      float tx1 = (a % 16) / 16.0f;
      float ty1 = Static.floor(a / 16) / 16.0f;
      float tx2 = tx1 + Static._1_16;
      float ty2 = ty1 + Static._1_16;
      int i = buf.getVertexCount();
      buf.addVertex(new float[] {0,0,0}, new float[] {tx1, ty1});
      buf.addVertex(new float[] {1,0,0}, new float[] {tx2, ty1});
      buf.addVertex(new float[] {1,1,0}, new float[] {tx2, ty2});
      buf.addVertex(new float[] {0,1,0}, new float[] {tx1, ty2});
      for(int b=0;b<4;b++) {
        buf.addDefault();
      }
      buf.addPoly(new int[] {i+3,i+2,i+1,i+0});
      buf.copyBuffers();
    }
    o_box = new RenderBuffers();
    o_box.addVertex(new float[] {0,0,0}, new float[] {0, 0});
    o_box.addVertex(new float[] {1,0,0}, new float[] {1, 0});
    o_box.addVertex(new float[] {1,1,0}, new float[] {1, 1});
    o_box.addVertex(new float[] {0,1,0}, new float[] {0, 1});
    for(int b=0;b<4;b++) {
      o_box.addDefault();
    }
    o_box.addPoly(new int[] {3,2,1,0});
    o_box.copyBuffers();
  }

  private int getFieldIndex() {
    if (focus == null) return 0;
    for(int a=0;a<fields.size();a++) {
      if (fields.get(a) == focus) return a;
    }
    return -1;
  }

  public abstract void render(int width, int height);
  public void process() {};
  public void resize(int width, int height) {}
  public void mousePressed(int x,int y,int but) {
    for(int a=0;a<buttons.size();a++) {
      Button button = buttons.get(a);
      if (x >= button.x1 && x <= button.x2 && y >= button.y1 && y <= button.y2) {
        focus = null;
        button.r.run();
        return;
      }
    }
    for(int a=0;a<checkboxes.size();a++) {
      CheckBox checkbox = checkboxes.get(a);
      if (x >= checkbox.x1 && x <= checkbox.x2 && y >= checkbox.y1 && y <= checkbox.y2) {
        focus = null;
        checkbox.setSelected(!checkbox.isSelected());
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

  public static void initStatic() {
    if (t_widgets == null) {
      t_widgets = Textures.getTexture("gui/widgets", 0);
    }
    if (t_icons == null) {
      t_icons = Textures.getTexture("gui/icons", 0);
    }
    if (t_text == null) {
      t_text = Textures.getTexture("font/ascii", 0);
    }
    if (t_white == null) {
      JFImage pixel = new JFImage(1,1);
      pixel.putPixel(0, 0, 0xffffff);  //white pixel
      t_white = new TextureMap();
      t_white.load(pixel);
    }
    if (t_white50 == null) {
      JFImage pixel = new JFImage(1,1);
      pixel.putPixel(0, 0, 0xffffff);  //white pixel
      pixel.putAlpha(0, 0, 0x80);  //50% transparent
      t_white50 = new TextureMap();
      t_white50.load(pixel);
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

  /** Setup anything need to show menu each time.
   * NOTE : This does NOT run on the OpenGL Thread.
   */
  public void setup() {}

  public void init() {
  }

  public static final int TOP = 0;
  public static final int CENTER = 1;
  public static final int BOTTOM = 2;
  public int gui_position = CENTER;  //TOP CENTER BOTTOM

  /** Sets a standard 0,0-1,1 ortho matrix for gui_width/gui_height. */
  public void setOrtho() {
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL_FALSE, orthoItem.m);  //perspective matrix
  }

  public void setViewportFull() {
    glViewport(0, 0, (int)Static.width, (int)Static.height);
  }

  /** Sets viewport for a menu screen. */
  public void setViewportMenu() {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - (gui_height * Static.scale)); break;
      case CENTER: vpy = offsetY; break;
      case BOTTOM: vpy = 0; break;
    }
    glViewport((int)offsetX, (int)vpy, (int)(gui_width * Static.scale), (int)(gui_height * Static.scale));
  }

  public static final int tab_width = 52;
  public static final int tab_height = 64;

  public static final int item_width = 36;
  public static final int item_height = 36;

  /** Sets viewport for tabs row on top. */
  public void setViewportTabTop() {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    offsetY += (gui_height * Static.scale);
    glViewport((int)offsetX, (int)offsetY, (int)(gui_width * Static.scale), (int)(tab_height * Static.scale));
  }

  /** Sets viewport for tabs row on bottom. */
  public void setViewportTabBottom() {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    offsetY -= (tab_height * Static.scale);
    glViewport((int)offsetX, (int)offsetY, (int)(gui_width * Static.scale), (int)(tab_height * Static.scale));
  }

  /** Sets a viewport for a single char. */
  public void setViewportChar(int x, int y, int fontSize) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    y += fontSize;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    glViewport((int)(offsetX + ((float)x) * Static.scale), (int)vpy
      , (int)(fontSize * Static.scale), (int)(fontSize * Static.scale));
  }

  /** Sets a standard 0,0-1,1 ortho matrix for item/text. */
  public void setViewportItem(int x, int y) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    glViewport(
      (int)(offsetX + ((float)x) * Static.scale),
      (int)vpy,
      (int)(36 * Static.scale),
      (int)(36 * Static.scale)
    );
  }

  /** Sets a viewport for a box. */
  public void setViewportBox(int x, int y, int w, int h) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (Static.height - gui_height) - y; break;
      case CENTER: vpy = (offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = ((gui_height - y) * Static.scale); break;
    }
    glViewport(
      (int)(offsetX + ((float)x) * Static.scale),
      (int)vpy,
      (int)(w * Static.scale),
      (int)(h * Static.scale)
    );
  }

  /** Sets a standard 0,0-1,1 ortho matrix for block. */
  public void setOrthoBlock() {
    //left right bottom top near far
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoBlock.m);  //perspective matrix
  }

  /** Sets an ortho matrix to display player in inventory menu */
  public void setOrthoPlayer() {
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL_FALSE, orthoPlayer.m);  //perspective matrix
  }

  public void setViewportPlayer(int x,int y,int w,int h) {
    //left right bottom top near far
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    glViewport((int)(offsetX + x * Static.scale), (int)vpy, (int)(w * Static.scale), (int)(h * Static.scale));
  }

  /** Creates a 0,0-1,1 menu object. */
  public RenderBuffers createOrthoMenu() {
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
    o_menu.copyBuffers();
    return o_menu;
  }

  /** Creates a 0,0-1,1 menu object with a partial image coords (upper left corner) using gui totalSize. */
  public RenderBuffers createMenu() {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    o_menu.addVertex(new float[] {0,0,0}, new float[] {0,0});
    o_menu.addVertex(new float[] {1,0,0}, new float[] {tc(gui_width_max, gui_width),0});
    o_menu.addVertex(new float[] {1,1,0}, new float[] {tc(gui_width_max, gui_width),tc(gui_height_max, gui_height)});
    o_menu.addVertex(new float[] {0,1,0}, new float[] {0,tc(gui_height_max, gui_height)});
    o_menu.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_menu.addDefault();
    }
    o_menu.copyBuffers();
    return o_menu;
  }

  /** Creates a custom sized 3d object with custom images coords.
   * sx/y = screen dest position
   * tx/y = images coords position
   * width/height = totalSize of menu
   */
  public RenderBuffers createMenu(
    int sx1,int sy1,
    int tx1,int ty1,
    int width,int height)
  {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    float fsw = width / gui_width;
    float fsh = height / gui_height;
    float ftw = width / gui_width_max;
    float fth = height / gui_height_max;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / gui_width_max;
    float fty1 = ty1 / gui_height_max;
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
    o_menu.copyBuffers();
    return o_menu;
  }

  /** Creates a custom sized 3d object with custom images coords.
   * sx/y = screen dest position
   * tx/y = images coords position
   * swidth/sheight = totalSize of menu on screen
   * twidth/theight = images totalSize of menu
   */
  public RenderBuffers createMenu(int sx1,int sy1,int tx1,int ty1,int swidth,int sheight,int twidth, int theight) {
    RenderBuffers o_menu = new RenderBuffers();
    //create vertex data
    float fsw = swidth / gui_width;
    float fsh = sheight / gui_height;
    float ftw = twidth / 512f;
    float fth = theight / 512f;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / 512f;
    float fty1 = ty1 / 512f;
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
    o_menu.copyBuffers();
    return o_menu;
  }

  public void recreateMenu(RenderBuffers o_menu, int sx1,int sy1,int tx1,int ty1,int width,int height) {
    o_menu.reset();
    //create vertex data
    float fsw = width / gui_width;
    float fsh = height / gui_height;
    float ftw = width / gui_width_max;
    float fth = height / gui_height_max;
    float fsx1 = sx1 / gui_width;
    float fsy1 = sy1 / gui_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / gui_width_max;
    float fty1 = ty1 / gui_height_max;
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
    o_menu.copyBuffers();
  }

  private void createSprite(RenderBuffers o_sprite, float sx1,float sy1,float sw,float sh, float tx1,float ty1,float tw,float th) {
    o_sprite.reset();
    //create vertex data
    float fsw = sw / sprite_width;
    float fsh = sh / sprite_height;
    float ftw = tw / 100f;
    float fth = th / 100f;
    float fsx1 = sx1 / sprite_width;
    float fsy1 = sy1 / sprite_height;
    float fsx2 = fsx1 + fsw;
    float fsy2 = fsy1 + fsh;
    float ftx1 = tx1 / 100f;
    float fty1 = ty1 / 100f;
    float ftx2 = ftx1 + ftw;
    float fty2 = fty1 + fth;
    o_sprite.addVertex(new float[] {fsx1,fsy1,0}, new float[] {ftx1,fty1});
    o_sprite.addVertex(new float[] {fsx2,fsy1,0}, new float[] {ftx2,fty1});
    o_sprite.addVertex(new float[] {fsx2,fsy2,0}, new float[] {ftx2,fty2});
    o_sprite.addVertex(new float[] {fsx1,fsy2,0}, new float[] {ftx1,fty2});
    o_sprite.addPoly(new int[] {3,2,1,0});
    for(int a=0;a<4;a++) {
      o_sprite.addDefault();
    }
    o_sprite.copyBuffers();
  }

  public void clearUI() {
    buttons.clear();
    fields.clear();
    scrolls.clear();
  }

  private void renderChar(int x,int y, char ch, float clr[], int scale) {
    setViewportChar(x,y,fontSize * scale);
    RenderBuffers buf = o_chars[ch];
    buf.bindBuffers();
    buf.render();
  }

  /** Text over length has ... added to end. */
  public String clampText(String txt, int length) {
    if (length < 3) return txt;
    if (txt.length() > length) {
      return txt.substring(0, length-3) + "...";
    }
    return txt;
  }

  public void renderText(int x,int y,String text, float clr[]) {
    setOrtho();
    t_text.bind();
    char ca[] = text.toCharArray();
    y -= fontSize;
    if (clr != null) {
      float clr4[];
      if (clr.length == 4) {
        clr4 = clr;
      } else {
        clr4 = new float[] {clr[0], clr[1], clr[2], 1.0f};
      }
      glUniform1i(Static.uniformEnableTint, 1);
      glUniform4fv(Static.uniformTintColor, 1, clr4);
    }
    for(int a=0;a<ca.length;a++) {
      renderChar(x, y, ca[a], clr, 1);
      x += fontSize;
    }
    if (clr != null) {
      glUniform1i(Static.uniformEnableTint, 0);
    }
  }

  public void renderText(int x,int y,String text) {
    renderText(x,y,text,null);
  }

  /** Adds an items damage bar. */
  public void renderDmg(int x,int y, float dmg) {
    //render bar (36x2)
    int length = (int)(36.0f * dmg);
    float clr[] = Static.red4;
    if (dmg > 0.6f) {
      clr = Static.green4;
    } else if (dmg > 0.3f) {
      clr = Static.yellow4;
    }
    o_box.bindBuffers();
    glUniform1i(Static.uniformEnableTint, 1);
    setViewportBox(x,y,36,2);
    glUniform4fv(Static.uniformTintColor, 1, Static.grey4);
    o_box.render();
    setViewportBox(x,y,length,2);
    glUniform4fv(Static.uniformTintColor, 1, clr);
    o_box.render();
    glUniform1i(Static.uniformEnableTint, 0);
  }

  /** Adds a simple bar (rectangle). */
  public void renderBar(int x,int y,int width,int height, float clr[]) {
    t_white.bind();
    glUniform1i(Static.uniformEnableTint, 1);
    setOrtho();
    setViewportBox(x,y,width,height);
    float clr4[];
    if (clr.length == 4) {
      clr4 = clr;
    } else {
      clr4 = new float[] {clr[0], clr[1], clr[2], 1.0f};
    }
    glUniform4fv(Static.uniformTintColor, 1, clr4);
    o_box.bindBuffers();
    o_box.render();
    glUniform1i(Static.uniformEnableTint, 0);
  }

  /** Adds a simple bar (rectangle) 50% transparent. */
  public void renderBar50(int x,int y,int width,int height, float clr[]) {
    t_white50.bind();
    glUniform1i(Static.uniformEnableTint, 1);
    setOrtho();
    setViewportBox(x,y,width,height);
    float clr4[];
    if (clr.length == 4) {
      clr4 = clr;
    } else {
      clr4 = new float[] {clr[0], clr[1], clr[2], 1.0f};
    }
    glUniform4fv(Static.uniformTintColor, 1, clr4);
    o_box.bindBuffers();
    o_box.render();
    glUniform1i(Static.uniformEnableTint, 0);
  }

  /** Render an item in an inventory slot. */
  private void renderItem(Item item, int x, int y) {
    if (item.id == 0) return;
    Static.data.reset();
    Static.data.var[X] = item.var;
    Static.data.inventory = true;
    ItemBase itembase = Static.getItemBase(item.id);
    if (itembase == null) return;
    if (itembase.renderAsEntity) {
      Static.data.pos.x = 0.5f;
      Static.data.pos.y = 0.5f;
      Static.data.pos.z = 0.5f;
      Static.data.ang.y = -90;
    }
    setViewportItem(x, y);
    clearZBuffer(0,0, 36,36);
    //TODO : itembase.setMatrixModel() : see Chest.java
    itembase.render();
  }

  private void renderItemName(Item item, int x,int y) {
    ItemBase itembase = Static.items.items[item.id];
    String txt = itembase.getName(item.var);
    x += fontSize;
    y += fontSize;
    int w = txt.length() * fontSize;
    int h = fontSize;
    renderBar50(x-2,y-2,w+4,h+4,Static.blue4);
    renderText(x,y,txt);
  }

  public void renderItems(Slot slots[]) {
    Item item;
    ItemBase itembase;
    BlockBase blockbase;
    //render items
    for(int a=0;a<=X;a++) {
      Static.data.sl[a] = 1f;
      Static.data.bl[a] = 1f;
    }
    setOrtho();
    Static.items.stitched.bind();
    depth(true);
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (Static.isItem(item.id)) {
        renderItem(item, slots[a].x, slots[a].y);
      }
    }
    //render blocks (rendered as items)
    Static.blocks.stitched.bind();
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (Static.isBlock(item.id)) {
        blockbase = Static.blocks.blocks[item.id];
        if (blockbase.renderAsItem) {
          renderItem(item, slots[a].x, slots[a].y);
        }
      }
    }
    //render normal blocks
    setOrthoBlock();
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (Static.isBlock(item.id)) {
        blockbase = Static.blocks.blocks[item.id];
        if (blockbase == null) continue;
        if (blockbase.renderAsEntity || blockbase.renderAsItem) continue;
        renderItem(item, slots[a].x, slots[a].y);
      }
    }
    //render blocks as entities (chest, etc.)
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (Static.isBlock(item.id)) {
        blockbase = Static.blocks.blocks[item.id];
        if (blockbase == null) continue;
        if (blockbase.renderAsEntity) {
          renderItem(item, slots[a].x, slots[a].y);
        }
      }
    }
    //render dmg bars
    setOrtho();
    t_white.bind();
    o_box.bindBuffers();
    depth(false);
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (Static.isBlock(item.id)) continue;
      itembase = Static.items.items[item.id];
      if (itembase == null) continue;
      if (itembase.isDamaged) {
        if (item.dmg != 1.0f) {
          renderDmg(slots[a].x,slots[a].y-2,item.dmg);
        }
      }
    }
    //render text
    t_text.bind();
    for(int a=0;a<slots.length;a++) {
      item = slots[a].item;
      if (item == null || item.id == 0) continue;
      if (item.count > 1) {
        renderText(slots[a].x, slots[a].y, Integer.toString(item.count));
      }
      if (slots[a].renderName) {
        renderItemName(item, slots[a].x, slots[a].y);
      }
    }
  }

  public void enterMenu(byte idx) {
    Static.clearButtons();
    Static.inGame = false;
    Static.video.setScreen(Static.screens.screens[idx]);
  }

  public void leaveMenu() {
    Static.video.setScreen(Static.game);
    setCursor(false);
    Static.inGame = true;
    Static.client.leaveMenuReset();
  }

  private static RenderBuffers o_shade = null;

  /** Render a shade over whole screen.
   * Assumes view/model matrix are set to identity.
   */
  public void renderShade() {
    setViewportFull();
    renderShade(0,0,(int)Static.width,(int)Static.height);
  }

  /** Render a shade over area.
   * Assumes view/model matrix are set to identity.
   */
  public void renderShade(int x, int y, int w, int h) {
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, orthoItem.m);
    glViewport(x, y, w, h);
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
      o_shade.copyBuffers();
    }
    t_white50.bind();
    o_shade.bindBuffers();
    o_shade.render();
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

  public static class CheckBox {
    int x1,y1,x2,y2;
    RenderBuffers on, off;
    boolean selected;
    public boolean isSelected() {
      return selected;
    }
    public void setSelected(boolean selected) {
      this.selected = selected;
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
    float back[];
    boolean center;
    boolean numbersOnly;
    int scale;
    public void setNumbersOnly() {
      numbersOnly = true;
    }
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
      if (ch < 32) return;
      if (txt.length() == max) return;
      if (numbersOnly) {
        boolean ok = false;
        if ((ch == '-' || ch == '+') && cpos == 0) {
          ok = true;
        } else if (ch >= '0' && ch <= '9') {
          ok = true;
        }
        if (!ok) return;
      }
      txt.insert(cpos, ch);
      cpos++;
    }
    public void keyPressed(int code) {
      switch (code) {
        case KeyCode.VK_UP: up(); break;
        case KeyCode.VK_DOWN: down(); break;
      }
      if (center) return;
      switch (code) {
        case KeyCode.VK_BACKSPACE: backspace(); break;
        case KeyCode.VK_DELETE: delete(); break;
        case KeyCode.VK_LEFT: left(); break;
        case KeyCode.VK_RIGHT: right(); break;
        case KeyCode.VK_HOME: home(); break;
        case KeyCode.VK_END: end(); break;
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

  public Button addButton(String txt,int x1,int y1,int width, Runnable r) {
    if (debug) {
      Static.log("addButton(" + x1 + "," + y1 + "," + width);
    }
    int sl = txt.length();  //TODO : calc font chars
    int width_2 = width/2;
    Button button = new Button();
    button.r = r;
    button.x1 = x1;
    button.y1 = y1;
    button.x2 = x1 + width - 1;
    button.y2 = y1 + 40 - 1;
    button.tx = x1 + width_2 - (sl * fontSize)/2;
    button.ty = y1 + 40/2 + fontSize/2;
    button.txt = txt;
    //createMenu(int sx1,int sy1,int tx1,int ty1,int swidth,int sheight,int twidth, int theight)
    button.left = createMenu(x1, y1, 0, 133, width_2, 40, width_2, 40);
    button.right = createMenu(x1 + width_2, y1, 400 - width_2, 133, width_2, 40, width_2, 40);
    buttons.add(button);
    return button;
  }

  public void renderButtons() {
    setOrtho();
    setViewportMenu();
    t_widgets.bind();
    for(int a=0;a<buttons.size();a++) {
      Button button = buttons.get(a);
      button.left.bindBuffers();
      button.left.render();
      button.right.bindBuffers();
      button.right.render();
    }
    for(int a=0;a<buttons.size();a++) {
      Button button = buttons.get(a);
      renderText(button.tx, button.ty, button.txt, button.clr);
    }
  }

  public CheckBox addCheckBox(int x1,int y1) {
    CheckBox checkbox = new CheckBox();
    int width = 29;
    int height = 29;
    checkbox.x1 = x1;
    checkbox.y1 = y1;
    checkbox.x2 = x1 + width - 1;
    checkbox.y2 = y1 + height - 1;
    checkbox.on = createMenu(x1, y1, 416, 0, width, height, width, height);
    checkbox.off = createMenu(x1, y1, 384, 0, width, height, width, height);
    checkboxes.add(checkbox);
    return checkbox;
  }

  public void renderCheckBoxes() {
    setOrtho();
    setViewportMenu();
    t_widgets.bind();
    for(int a=0;a<checkboxes.size();a++) {
      CheckBox checkbox = checkboxes.get(a);
      if (checkbox.isSelected()) {
        checkbox.on.bindBuffers();
        checkbox.on.render();
      } else {
        checkbox.off.bindBuffers();
        checkbox.off.render();
      }
    }
  }

  public TextField addTextField(String txt, int x1, int y1, int width, float back[], int max, boolean center, int scale) {
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
    field.back = back;
    fields.add(field);
    return field;
  }

  public void renderFields() {
    setOrtho();
    for(int a=0;a<fields.size();a++) {
      TextField field = fields.get(a);
      if (field.back != null) {
        renderBar(field.x1, field.y1 + fontSize + 4, field.width, fontSize + 4, field.back);
      }
      int x = field.x1;
      int y = field.y1;
      int sl = field.txt.length();
      int x1 = 0;
      if (field.center) {
        x += (field.width - sl * fontSize * field.scale) / 2;
        x1 = x;
      }
      t_text.bind();
      for(int p=field.dpos;p<field.txt.length();p++) {
        renderChar(x+1,y+1,field.txt.charAt(p),Static.white4, field.scale);
        x += fontSize * field.scale;
      }
      if (showCursor && focus == field) {
        if (field.center) {
          renderChar(x, y,'<', Static.white, field.scale);
          renderChar(x1 - fontSize * field.scale, y,'>', Static.white, field.scale);
        } else {
          renderChar(field.x1 + ((field.cpos - field.dpos) * fontSize), y,(char)219, Static.white, field.scale);
        }
      }
    }
  }

  /** Adds a vertical scroll bar. */
  public ScrollBar addScrollBar(int x1, int y1, int width, int height, int size) {
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
    sb.back.addFace2D(x1 / gui_width, y1 / gui_height, x2 / gui_width, y2 / gui_height, 0,0,1,1,Static.black);
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

  public void renderScrollBars() {
    setOrtho();
    t_white.bind();
    for(int a=0;a<scrolls.size();a++) {
      ScrollBar sb = scrolls.get(a);
      sb.back.bindBuffers();
      sb.back.render();
      sb.button.reset();
      float x1 = sb.x1 / gui_width;
      float y1 = (sb.y1 + sb.pos) / gui_height;
      float x2 = sb.x2 / gui_width;
      float y2 = (sb.y1 + sb.pos + sb.barSize) / gui_height;
      sb.button.addFace2D(x1, y1, x2, y2, 0,0,1,1, Static.white);
      sb.button.copyBuffers();
      sb.button.bindBuffers();
      sb.button.render();
    }
  }

  public void setFocus(TextField field) {
    focus = field;
  }

  public void setScreen(RenderScreen screen) {
    Static.video.setScreen(screen);
  }

  public void setCursor(boolean state) {
    Main.setCursor(state);
  }

  public static void depth(boolean on) {
    if (on) {
      glDepthMask(true);
      glEnable(GL.GL_DEPTH_TEST);
    } else {
      glDepthMask(false);
      glDisable(GL.GL_DEPTH_TEST);
    }
  }

  public static void clear(int x, int y, int width, int height) {
    glViewport(x, y, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  public static void clearZBuffer(int x, int y, int width, int height) {
    glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  public String toString() {
    return getClass().getName();
  }
}
