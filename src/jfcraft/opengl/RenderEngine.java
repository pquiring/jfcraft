package jfcraft.opengl;

/** Render Engine
 *
 * @author pquiring
 *
 * Created Sept 18, 2013
 */

import java.util.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.client.*;
import jfcraft.audio.*;
import jfcraft.data.*;
import static jfcraft.opengl.RenderScreen.depth;

public class RenderEngine {
  public static ArrayList<AssetImage> animatedTextures = new ArrayList<AssetImage>();
  private java.util.Timer frTimer, fpsTimer;
  private final Object fpsLock = new Object();
  private int fpsCounter;

  private Object screenLock = new Object();
  private RenderScreen screen;

  public int fragShader, vertexShader, program;

  public RenderEngine() {
    Static.video = this;
    RenderScreen.initStaticGL();
  }

  public void loadProgram() {
    GL gl = GL.getInstance();
    //TODO : delete old program
    vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
    gl.glShaderSource(vertexShader, 1, new String[] {VertexShader.source}, null);
    gl.glCompileShader(vertexShader);
    Static.log("vertex log=" + gl.glGetShaderInfoLog(vertexShader));

    fragShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
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

  public void init() {
    GL gl = GL.getInstance();
    Static.initClientThread("EventThread", true, false);  //actually EDT

    Static.log("JVM.version=" + System.getProperty("java.version"));
    Static.log("JVM.vendor=" + System.getProperty("java.vendor"));
    Static.log("JVM.name=" + System.getProperty("java.vm.name"));

    Static.log("JF.version=" + JF.getVersion());

    Static.log("GL Version=" + gl.glGetString(GL_VERSION));

    Static.glver = getVersion();
    if (Static.glver[0] < 2) {
      JFAWT.showError("Error", "OpenGL Version < 2.0");
      System.exit(0);
    }

    int max[] = new int[1];
    gl.glGetIntegerv(GL_MAX_TEXTURE_SIZE, max);
    Static.log("max texture size=" + max[0]);
    Static.max_texture_size = max[0];

    gl.glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, max);
    Static.log("max texture units=" + max[0]);

    resize(Static.INIT_X, Static.INIT_Y);

    //setup opengl
    gl.glFrontFace(GL_CCW);  //3DS uses GL_CCW
    gl.glEnable(GL_CULL_FACE);  //don't draw back sides
    gl.glEnable(GL_DEPTH_TEST);
    gl.glDepthFunc(GL_LESS);
    gl.glEnable(GL_BLEND);
    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    gl.glEnable(GL_TEXTURE_2D);
    gl.glActiveTexture(GL_TEXTURE0);

    loadProgram();

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
    Static.uniformEnableHorsePattern = gl.glGetUniformLocation(program, "uUseHorsePattern");
    Static.uniformEnableHorseArmor = gl.glGetUniformLocation(program, "uUseHorseArmor");
    Static.uniformEnableFog = gl.glGetUniformLocation(program, "uUseFog");
    Static.uniformFogColor = gl.glGetUniformLocation(program, "uFogColor");
    Static.uniformFogNear = gl.glGetUniformLocation(program, "uFogNear");
    Static.uniformFogFar = gl.glGetUniformLocation(program, "uFogFar");
    Static.uniformEnableTint = gl.glGetUniformLocation(program, "uUseTint");
    Static.uniformTintColor = gl.glGetUniformLocation(program, "uTintColor");
    Static.uniformTexture = gl.glGetUniformLocation(program, "uTexture");
    Static.uniformCrack = gl.glGetUniformLocation(program, "uCrack");
    Static.uniformHorsePattern = gl.glGetUniformLocation(program, "uHorsePattern");
    Static.uniformHorseArmor = gl.glGetUniformLocation(program, "uHorseArmor");

    gl.glUniform1f(Static.uniformSunLight, 1.0f);
    gl.glUniform1f(Static.uniformAlphaFactor, 1.0f);
    gl.glUniform1i(Static.uniformEnableTextures, 1);
    gl.glUniform1i(Static.uniformEnableFog, 0);
    gl.glUniform1f(Static.uniformFogNear, Settings.current.loadRange * 16f);
    gl.glUniform1f(Static.uniformFogFar, Settings.current.loadRange * 16f + 16f);
    gl.glUniform1i(Static.uniformEnableHorsePattern, 0);
    gl.glUniform1i(Static.uniformEnableHorseArmor, 0);
    gl.glUniform4fv(Static.uniformFogColor, 1, Static.skyblue4);
    gl.glUniform1i(Static.uniformTexture, 0);
    gl.glUniform1i(Static.uniformCrack, 1);
    gl.glUniform1i(Static.uniformHorsePattern, 2);
    gl.glUniform1i(Static.uniformHorseArmor, 3);
    gl.glUniform1i(Static.uniformEnableTint, 0);
    gl.glUniform4fv(Static.uniformFogColor, 1, Static.white4);

    //setup timers
    if (Settings.current.FPS != -1) {
      frTimer = new java.util.Timer();
      int delay = 1000 / Settings.current.FPS;
      frTimer.scheduleAtFixedRate(new TimerTask() {
        public final void run() {
          nextFrame();
        }
      }, delay, delay);
    } else {
      nextFrame = true;
    }
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
        depth(true);
        synchronized(screenLock) {
          if (nextFrame && processed) {
            if (Settings.current.FPS != -1) nextFrame = false;
            screen.render((int)Static.width, (int)Static.height);
            Main.swap();
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

    int min;

    if (width > height) {
      min = height;
    } else {
      min = width;
    }

    if (min < 512) {
      Static.scale = 0.5f;  //ouch
    } else if (min < 768) {
      Static.scale = 1.0f;
    } else if (min < 1024) {
      Static.scale = 1.5f;
    } else {
      Static.scale = 2.0f;
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
  public void keyPressed(int vk) {
    if (vk >= 1024) return;
    Static.keys[vk] = true;
    screen.keyPressed(vk);
  }
  public void keyReleased(int vk) {
    if (vk >= 1024) return;
    Static.keys[vk] = false;
    screen.keyReleased(vk);
  }
  public void keyTyped(char ch) {
    screen.keyTyped(ch);
  }

//interface MouseListener
  public void mouseDown(float fx,float fy,int button) {
    float offsetX = (Static.width - (screen.gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (screen.gui_height * Static.scale)) / 2.0f;
    int x = (int)((fx - offsetX) / Static.scale);
    int y = (int)((fy - offsetY) / Static.scale);
    screen.mousePressed(x, y, button);
  }
  public void mouseUp(float fx,float fy,int button) {
    float offsetX = (Static.width - (screen.gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (screen.gui_height * Static.scale)) / 2.0f;
    int x = (int)((fx - offsetX) / Static.scale);
    int y = (int)((fy - offsetY) / Static.scale);
    screen.mouseReleased(x, y, button);
  }

  public void mouseMove(float fx, float fy, int button) {
    float offsetX = (Static.width - (screen.gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (screen.gui_height * Static.scale)) / 2.0f;
    int x = (int)((fx - offsetX) / Static.scale);
    int y = (int)((fy - offsetY) / Static.scale);
    screen.mouseMoved(x, y, button);
  }

  public void mouseScrolled(int cnt) {
    screen.mouseWheel(cnt);
  }

  public void windowResized(int x,int y) {
  }

  public void windowClosed() {
    //TODO : shutdown gracefully
    System.exit(0);
  }

  public void reload() {
    //TODO : reload all OpenGL textures/buffers/etc.
  }
}
