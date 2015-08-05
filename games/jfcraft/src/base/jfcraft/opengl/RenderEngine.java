package jfcraft.opengl;

/** Render Engine
 *
 * @author pquiring
 *
 * Created Sept 18, 2013
 */

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.opengl.*;
import org.eclipse.swt.events.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.client.MainSWT;
import jfcraft.data.*;
import static jfcraft.opengl.RenderScreen.gui_height;
import static jfcraft.opengl.RenderScreen.gui_width;
import jfcraft.plugin.PluginLoader;

public class RenderEngine implements MouseListener, KeyListener, MouseMoveListener, MouseWheelListener, FocusListener, ControlListener, ShellListener {
  public static ArrayList<AssetImage> animatedTextures = new ArrayList<AssetImage>();
  private java.util.Timer frTimer, fpsTimer, hsTimer;
  private final Object fpsLock = new Object();
  private int fpsCounter;

  private final int FPS = 60;

  private Object screenLock = new Object();
  private RenderScreen screen;

  public int fragShader, vertexShader, program;

  private Widget focus;

  public RenderEngine(RenderScreen initScreen) {
    screen = initScreen;
    screen.setup();
    Static.video = this;
    RenderScreen.initStaticGL();
  }

  public void loadProgram() {
    //TODO : delete old program
    vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, new String[] {VertexShader.source}, null);
    glCompileShader(vertexShader);
    Static.log("vertex log=" + glGetShaderInfoLog(vertexShader));

    fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, 1, new String[] {FragmentShader.source}, null);
    glCompileShader(fragShader);
    Static.log("fragment log=" + glGetShaderInfoLog(fragShader));

    program = glCreateProgram();
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragShader);
    glLinkProgram(program);
    Static.log("program log=" + glGetProgramInfoLog(program));
    glUseProgram(program);
  }

  public void init(GLCanvas canvas, Shell shell) {
    Static.initClientThread("init (EDT)", true, false);  //actually EDT

    shell.addShellListener(this);
    shell.addControlListener(this);

    canvas.addMouseListener(this);
    canvas.addKeyListener(this);
    canvas.addFocusListener(this);
    canvas.addMouseMoveListener(this);
    canvas.addMouseWheelListener(this);

    Static.log("JVM.version=" + System.getProperty("java.version"));
    Static.log("JVM.vendor=" + System.getProperty("java.vendor"));
    Static.log("JVM.name=" + System.getProperty("java.vm.name"));

    Static.log("JF.version=" + JF.getVersion());

    Static.log("GL Version=" + glGetString(GL_VERSION));

    Static.glver = getVersion();
    if (Static.glver[0] < 2) {
      JF.showError("Error", "OpenGL Version < 2.0");
      System.exit(0);
    }

    int max[] = new int[1];
    glGetIntegerv(GL_MAX_TEXTURE_SIZE, max);
    Static.log("max texture size=" + max[0]);
    Static.max_texture_size = max[0];

    glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, max);
    Static.log("max texture units=" + max[0]);

    Point pt = canvas.getSize();
    resize(pt.x, pt.y);

    //setup opengl
    glFrontFace(GL_CCW);  //3DS uses GL_CCW
    glEnable(GL_CULL_FACE);  //don't draw back sides
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glEnable(GL_TEXTURE_2D);
    glActiveTexture(GL_TEXTURE0);

    loadProgram();

    Static.attribTextureCoords = glGetAttribLocation(program, "aTextureCoord");
    glEnableVertexAttribArray(Static.attribTextureCoords);
    Static.attribTextureCoords2 = glGetAttribLocation(program, "aTextureCoord2");
    glEnableVertexAttribArray(Static.attribTextureCoords2);
    Static.attribVertex = glGetAttribLocation(program, "aVertexPosition");
    glEnableVertexAttribArray(Static.attribVertex);
    Static.attribColor = glGetAttribLocation(program, "aLightColor");
    glEnableVertexAttribArray(Static.attribColor);
    Static.attribSunLight = glGetAttribLocation(program, "aSunLightPercent");
    glEnableVertexAttribArray(Static.attribSunLight);
    Static.attribBlockLight = glGetAttribLocation(program, "aBlockLightPercent");
    glEnableVertexAttribArray(Static.attribBlockLight);

    Static.uniformMatrixPerspective = glGetUniformLocation(program, "uPMatrix");
    Static.uniformMatrixModel = glGetUniformLocation(program, "uMMatrix");
    Static.uniformMatrixView = glGetUniformLocation(program, "uVMatrix");

    Static.uniformSunLight = glGetUniformLocation(program, "uSunLightNow");
    Static.uniformAlphaFactor = glGetUniformLocation(program, "uAlphaFactor");
    Static.uniformEnableTextures = glGetUniformLocation(program, "uUseTextures");
    Static.uniformEnableFog = glGetUniformLocation(program, "uUseFog");
    Static.uniformEnableHorsePattern = glGetUniformLocation(program, "uUseHorsePattern");
    Static.uniformEnableHorseArmor = glGetUniformLocation(program, "uUseHorseArmor");
    Static.uniformFogColor = glGetUniformLocation(program, "uFogColor");
    Static.uniformFogNear = glGetUniformLocation(program, "uFogNear");
    Static.uniformFogFar = glGetUniformLocation(program, "uFogFar");
    Static.uniformTexture = glGetUniformLocation(program, "uTexture");
    Static.uniformCrack = glGetUniformLocation(program, "uCrack");
    Static.uniformHorsePattern = glGetUniformLocation(program, "uHorsePattern");
    Static.uniformHorseArmor = glGetUniformLocation(program, "uHorseArmor");

    glUniform1f(Static.uniformSunLight, 1.0f);
    glUniform1f(Static.uniformAlphaFactor, 1.0f);
    glUniform1i(Static.uniformEnableTextures, 1);
    glUniform1i(Static.uniformEnableFog, 0);
    glUniform1f(Static.uniformFogNear, Settings.current.loadRange * 16f);
    glUniform1f(Static.uniformFogFar, Settings.current.loadRange * 16f + 16f);
    glUniform1i(Static.uniformEnableHorsePattern, 0);
    glUniform1i(Static.uniformEnableHorseArmor, 0);
    glUniform4fv(Static.uniformFogColor, 1, new float[] {0.2f, 0.2f, 0.6f, 1.0f});  //sky blue
    glUniform1i(Static.uniformTexture, 0);
    glUniform1i(Static.uniformCrack, 1);
    glUniform1i(Static.uniformHorsePattern, 2);
    glUniform1i(Static.uniformHorseArmor, 3);

    //setup timers
    frTimer = new java.util.Timer();
    int delay = 1000 / FPS;
    frTimer.scheduleAtFixedRate(new TimerTask() {
      public final void run() {
        nextFrame();
      }
    }, delay, delay);
    hsTimer = new java.util.Timer();
    hsTimer.scheduleAtFixedRate(new TimerTask() {
      public final void run() {
        repaint();
      }
    }, 1, 1);
    fpsTimer = new java.util.Timer();
    fpsTimer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        synchronized(fpsLock) {
          Static.fps = fpsCounter;
          fpsCounter = 0;
        }
      }
    }, 1000, 1000);
  }

  public RenderScreen getScreen() {
    return screen;
  }

  public void setScreen(RenderScreen iface) {
    synchronized(screenLock) {
      iface.setup();
      screen = iface;
      Static.log("RenderEngine.setScreen() " + iface);
    }
  }

  private final void repaint() {
    MainSWT.render();
  }

  private boolean nextFrame;
  private boolean processed;

  private final void nextFrame() {
    nextFrame = true;
  }

  public void render() {
//    Static.log("render");
    RenderBuffers.freeBuffers();
    if (screen != null) {
      try {
        long start = System.currentTimeMillis();
        glDepthFunc(GL_LEQUAL);
        synchronized(screenLock) {
          if (nextFrame && processed) {
            screen.render((int)Static.width, (int)Static.height);
            MainSWT.swap();
            nextFrame = false;
            processed = false;
            synchronized(fpsLock) {
              fpsCounter++;
            }
          } else {
            processed = true;
            if (Static.game != null) {
              Static.game.process();
            }
          }
        }
        long stop = System.currentTimeMillis();
        long diff = stop - start;
        if (diff > 1000) {
          Static.log("render took " + diff + "ms");
        }
      } catch (Throwable t) {
        Static.log(t);
      }
    }
  }

  public void resize(int width, int height) {
    Static.width = width;
    Static.height = height;

    if (false) {
      //integer scaling
      int scalex = (int)(Static.width / 512);
      int scaley = (int)(Static.height / 512);
      if (scalex > scaley) Static.scale = scaley;
      if (scaley > scalex) Static.scale = scalex;
      if (Static.scale < 1) Static.scale = 1;
    } else {
      //float scaling
      if (width > height) {
        Static.scale = ((float)height) / 512.0f;
      } else {
        Static.scale = ((float)width) / 512.0f;
      }
    }

    if (screen != null) {
      screen.resize(width, height);
    }
  }

  /** Advance all animated textures to next frame. */
  public static void advanceAnimation() {
    int cnt = animatedTextures.size();
    Static.blocks.stitched.bind();
    for(int a=0;a<cnt;a++) {
      animatedTextures.get(a).nextFrame();
    }
  }

//interface KeyListener
  public void keyPressed(KeyEvent e) {
    Static.log("keyP:" + e.keyCode + "," + e.character + "," + e.keyLocation);
    int vk = SWTVK.convert(e.keyCode);
    if (vk >= 1024) return;
    if (e.keyLocation == SWT.RIGHT) {
      Static.r_keys[vk] = true;
    } else {
      Static.keys[vk] = true;
    }
    if (e.character != 0) {
      screen.keyTyped(e.character);
    }
    screen.keyPressed(vk);
  }
  public void keyReleased(KeyEvent e) {
    Static.log("keyR:" + e.keyCode + "," + e.character + "," + e.keyLocation);
    int vk = SWTVK.convert(e.keyCode);
    if (vk >= 1024) return;
    if (e.keyLocation == SWT.RIGHT) {
      Static.r_keys[vk] = false;
    } else {
      Static.keys[vk] = false;
    }
    screen.keyReleased(vk);
  }

//interface MouseListener
  public void mouseDoubleClick(MouseEvent e) {}
  public void mouseDown(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.x) - offsetX) / Static.scale);
    int y = (int)((((float)e.y) - offsetY) / Static.scale);
    screen.mousePressed(x, y, e.button);
  }
  public void mouseUp(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.x) - offsetX) / Static.scale);
    int y = (int)((((float)e.y) - offsetY) / Static.scale);
    screen.mouseReleased(x, y, e.button);
  }

//interface FocusListener
  public void focusGained(FocusEvent e) {
    focus = e.widget;
  }

  public void focusLost(FocusEvent e) {
    focus = null;
  }

  public void mouseMove(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.x) - offsetX) / Static.scale);
    int y = (int)((((float)e.y) - offsetY) / Static.scale);
    screen.mouseMoved(x, y, e.button);
  }

  public void mouseScrolled(MouseEvent e) {
    screen.mouseWheel(e.count);
  }

//interface ControlListener
  public void controlMoved(ControlEvent e) {
  }

  public void controlResized(ControlEvent e) {
    MainSWT.layout();
    Point pt = MainSWT.getCanvasSize();
    resize(pt.x, pt.y);
  }

//interface ShellListener
  public void shellActivated(ShellEvent e) {}

  public void shellClosed(ShellEvent e) {
    //TODO : shutdown gracefully
    System.exit(0);
  }

  public void shellDeactivated(ShellEvent e) {}

  public void shellDeiconified(ShellEvent e) {}

  public void shellIconified(ShellEvent e) {}
}
