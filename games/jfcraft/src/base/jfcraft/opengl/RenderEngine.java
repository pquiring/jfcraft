package jfcraft.opengl;

/** Render Engine
 *
 * @author pquiring
 *
 * Created Sept 18, 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.data.*;
import static jfcraft.opengl.RenderScreen.gui_height;
import static jfcraft.opengl.RenderScreen.gui_width;
import jfcraft.plugin.PluginLoader;

public class RenderEngine implements WindowListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
, FocusListener, GLInterface {
  public static ArrayList<AssetImage> animatedTextures = new ArrayList<AssetImage>();
  private java.util.Timer frTimer, fpsTimer, hsTimer;
  private final Object fpsLock = new Object();
  private int fpsCounter;

  private Component comp, focus;

  private final int FPS = 60;

  private boolean ready = false;

  private Object screenLock = new Object();
  private RenderScreen screen;

  public int fragShader, vertexShader, program;

  public RenderEngine(RenderScreen initScreen) {
    screen = initScreen;
    screen.setup();
    Static.video = this;
  }

  public void loadProgram(GL gl) {
    //TODO : delete old program
    vertexShader = gl.glCreateShader(GL.GL_VERTEX_SHADER);
    gl.glShaderSource(vertexShader, 1, new String[] {VertexShader.source}, null);
    gl.glCompileShader(vertexShader);
    Static.log("vertex log=" + gl.glGetShaderInfoLog(vertexShader));

    fragShader = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
    gl.glShaderSource(fragShader, 1, new String[] {FragmentShader.source}, null);
    gl.glCompileShader(fragShader);
    Static.log("fragment log=" + gl.glGetShaderInfoLog(fragShader));

    program = gl.glCreateProgram();
    gl.glAttachShader(program, vertexShader);
    gl.glAttachShader(program, fragShader);
    gl.glLinkProgram(program);
    Static.log("program log=" + gl.glGetProgramInfoLog(program));
    gl.glUseProgram(program);
  }

//interface GLInterface
  public void init(GL gl, Component comp) {
    Static.initClientThread("init (EDT)", true, false);  //actually EDT

    this.comp = comp;

    Window w;

    if (comp instanceof Window) {
      w = (Window)comp;
    } else {
      w = SwingUtilities.getWindowAncestor(comp);
    }

    w.addWindowListener(this);
    comp.addMouseListener(this);
    comp.addKeyListener(this);
    comp.addFocusListener(this);
    comp.addMouseMotionListener(this);
    comp.addMouseWheelListener(this);

    Static.log("JVM.version=" + System.getProperty("java.version"));
    Static.log("JVM.vendor=" + System.getProperty("java.vendor"));
    Static.log("JVM.name=" + System.getProperty("java.vm.name"));

    Static.log("JF.version=" + JF.getVersion());

    Static.log("GL Version=" + gl.glGetString(GL.GL_VERSION));
    Static.glver = gl.getVersion();
    if (Static.glver[0] < 2) {
      w.setVisible(false);
      w.dispose();
      JF.showError("Error", "OpenGL Version < 2.0");
      System.exit(0);
    }

    int max[] = new int[1];
    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, max);
    Static.log("max texture size=" + max[0]);
    Static.max_texture_size = max[0];

    gl.glGetIntegerv(GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, max);
    Static.log("max texture units=" + max[0]);

    resize(gl, comp.getWidth(), comp.getHeight());

    //setup opengl
    gl.glFrontFace(GL.GL_CCW);  //3DS uses GL_CCW
    gl.glEnable(GL.GL_CULL_FACE);  //don't draw back sides
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glActiveTexture(GL.GL_TEXTURE0);

    loadProgram(gl);

    Static.attribTextureCoords = gl.glGetAttribLocation(program, "aTextureCoord");
    gl.glEnableVertexAttribArray(Static.attribTextureCoords);
    Static.attribTextureCoords2 = gl.glGetAttribLocation(program, "aTextureCoord2");
    gl.glEnableVertexAttribArray(Static.attribTextureCoords2);
    Static.attribVertex = gl.glGetAttribLocation(program, "aVertexPosition");
    gl.glEnableVertexAttribArray(Static.attribVertex);
    Static.attribColor = gl.glGetAttribLocation(program, "aLightColor");
    gl.glEnableVertexAttribArray(Static.attribColor);
    Static.attribSunLight = gl.glGetAttribLocation(program, "aSunLightPercent");
    gl.glEnableVertexAttribArray(Static.attribSunLight);
    Static.attribBlockLight = gl.glGetAttribLocation(program, "aBlockLightPercent");
    gl.glEnableVertexAttribArray(Static.attribBlockLight);

    Static.uniformMatrixPerspective = gl.glGetUniformLocation(program, "uPMatrix");
    Static.uniformMatrixModel = gl.glGetUniformLocation(program, "uMMatrix");
    Static.uniformMatrixView = gl.glGetUniformLocation(program, "uVMatrix");

    Static.uniformSunLight = gl.glGetUniformLocation(program, "uSunLightNow");
    Static.uniformAlphaFactor = gl.glGetUniformLocation(program, "uAlphaFactor");
    Static.uniformEnableTextures = gl.glGetUniformLocation(program, "uUseTextures");
    Static.uniformEnableFog = gl.glGetUniformLocation(program, "uUseFog");
    Static.uniformEnableHorsePattern = gl.glGetUniformLocation(program, "uUseHorsePattern");
    Static.uniformEnableHorseArmor = gl.glGetUniformLocation(program, "uUseHorseArmor");
    Static.uniformFogColor = gl.glGetUniformLocation(program, "uFogColor");
    Static.uniformTexture = gl.glGetUniformLocation(program, "uTexture");
    Static.uniformCrack = gl.glGetUniformLocation(program, "uCrack");
    Static.uniformHorsePattern = gl.glGetUniformLocation(program, "uHorsePattern");
    Static.uniformHorseArmor = gl.glGetUniformLocation(program, "uHorseArmor");

    gl.glUniform1f(Static.uniformSunLight, 1.0f);
    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f);
    gl.glUniform1i(Static.uniformEnableTextures, 1);
    gl.glUniform1i(Static.uniformEnableFog, 0);
    gl.glUniform1i(Static.uniformEnableHorsePattern, 0);
    gl.glUniform1i(Static.uniformEnableHorseArmor, 0);
    gl.glUniform3fv(Static.uniformFogColor, 1, new float[] {0.0f, 0.0f, 0.0f});
    gl.glUniform1i(Static.uniformTexture, 0);
    gl.glUniform1i(Static.uniformCrack, 1);
    gl.glUniform1i(Static.uniformHorsePattern, 2);
    gl.glUniform1i(Static.uniformHorseArmor, 3);

    ready = true;

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
    comp.repaint();
  }

  private boolean nextFrame;
  private boolean processed;

  private final void nextFrame() {
    nextFrame = true;
  }

  public void render(GL gl) {
    if (!ready) return;
    RenderBuffers.freeBuffers(gl);
    if (screen != null) {
      try {
        long start = System.currentTimeMillis();
        gl.glDepthFunc(GL.GL_LEQUAL);
        synchronized(screenLock) {
          if (nextFrame && processed) {
            screen.render(gl, (int)Static.width, (int)Static.height);
            gl.swap();
            nextFrame = false;
            processed = false;
            synchronized(fpsLock) {
              fpsCounter++;
            }
          } else {
            processed = true;
            if (Static.game != null) {
              Static.game.process(gl);
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

  public void resize(GL gl, int width, int height) {
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
      screen.resize(gl, width, height);
    }
  }

  /** Advance all animated textures to next frame. */
  public static void advanceAnimation(GL gl) {
    int cnt = animatedTextures.size();
    Static.blocks.stitched.bind(gl);
    for(int a=0;a<cnt;a++) {
      animatedTextures.get(a).nextFrame(gl);
    }
  }

//interface WindowListener
  public void windowOpened(WindowEvent e) { }
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
  public void windowClosed(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }

//interface KeyListener
  public void keyPressed(KeyEvent e) {
    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
      Static.r_keys[e.getKeyCode()] = true;
    } else {
      Static.keys[e.getKeyCode()] = true;
    }
    screen.keyPressed(e.getKeyCode());
  }
  public void keyReleased(KeyEvent e) {
    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
      Static.r_keys[e.getKeyCode()] = false;
    } else {
      Static.keys[e.getKeyCode()] = false;
    }
    screen.keyReleased(e.getKeyCode());
  }
  public void keyTyped(KeyEvent e) {
//    Static.log("key=" + e);
    screen.keyTyped(e.getKeyChar());
  }

//interface MouseListener
  public void mouseClicked(MouseEvent e) { }
  public void mousePressed(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.getX()) - offsetX) / Static.scale);
    int y = (int)((((float)e.getY()) - offsetY) / Static.scale);
    screen.mousePressed(x, y, e.getButton());
  }
  public void mouseReleased(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.getX()) - offsetX) / Static.scale);
    int y = (int)((((float)e.getY()) - offsetY) / Static.scale);
    screen.mouseReleased(x, y, e.getButton());
  }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }

//interface FocusListener
  public void focusGained(FocusEvent e) {
    focus = e.getComponent();
  }

  public void focusLost(FocusEvent e) {
    focus = null;
  }

  public void mouseDragged(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.getX()) - offsetX) / Static.scale);
    int y = (int)((((float)e.getY()) - offsetY) / Static.scale);
    screen.mouseMoved(x, y, e.getButton());
  }

  public void mouseMoved(MouseEvent e) {
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    int x = (int)((((float)e.getX()) - offsetX) / Static.scale);
    int y = (int)((((float)e.getY()) - offsetY) / Static.scale);
    screen.mouseMoved(x, y, e.getButton());
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    screen.mouseWheel(e.getWheelRotation());
  }
}
