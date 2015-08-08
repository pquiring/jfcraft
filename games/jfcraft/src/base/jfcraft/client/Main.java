package jfcraft.client;

/**
 *
 * @author pquiring
 */

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.libffi.*;

import java.nio.ByteBuffer;
import javaforce.JF;
import jfcraft.data.Static;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import javaforce.gl.GL;

import jfcraft.opengl.*;

public class Main {
  // We need to strongly reference callback instances.
  private GLFWErrorCallback errorCallback;

  private class Window {
    public GLFWKeyCallback keyCallback;
    public GLFWCursorPosCallback mousePosCallback;
    public GLFWMouseButtonCallback mouseButtonCallback;
    public GLFWScrollCallback scrollCallback;
    public GLFWWindowSizeCallback windowSizeCallback;
    public GLFWWindowCloseCallback windowCloseCallback;
    private long handle;
    public void close() {
      glfwDestroyWindow(handle);
      keyCallback.release();
      mousePosCallback.release();
      mouseButtonCallback.release();
      scrollCallback.release();
      windowSizeCallback.release();
      windowCloseCallback.release();
    }
  }

  private float mx, my;
  private int mb;

  private boolean fullscreenMode;
  private boolean toggleFullscreenMode;

  private static final int GL_TRUE = 1;
  private static final int GL_FALSE = 0;

  // The window handle
  private Window window = new Window();
  private Window fullscreen = new Window();
  private Window current;

  public void run() {
    main = this;
    try {
      init();
      createWindow(window, NULL, NULL);
      initGame();
      loop();
      // Release window and window callbacks
      window.close();
      fullscreen.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Terminate GLFW and release the GLFWerrorfun
      glfwTerminate();
      errorCallback.release();
    }
  }

  private void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (glfwInit() != GL11.GL_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }
  }

  private void createWindow(Window win, long device, long shared) {
    // Configure our window
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GL_TRUE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

    int width = 512;
    int height = 512;

    // Create the window
    win.handle = glfwCreateWindow(width, height, "jfCraft", device, shared);
    if (win.handle == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    if (device == NULL) {
      // Get the resolution of the primary monitor
      ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      // Center our window
      glfwSetWindowPos(
        win.handle,
        (GLFWvidmode.width(vidmode) - width) / 2,
        (GLFWvidmode.height(vidmode) - height) / 2
      );
    }

    // Make the OpenGL context current
    glfwMakeContextCurrent(win.handle);
    // Enable v-sync
//    glfwSwapInterval(1);  //Settings???

    // Make the window visible
    glfwShowWindow(win.handle);

    createCallbacks(win);
    current = win;
  }

  private void initGame() {
    // Load GL functions via JavaForce
    GL.glInit();

    // Init the game rendering engine
    new RenderEngine(new Loading()).init();
  }

  private void createCallbacks(Window win) {
    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(win.handle, win.keyCallback = new GLFWKeyCallback() {
      public void invoke(long window, int key, int scancode, int action, int mods) {
        Static.log("key:" + key + "," + scancode + "," + action + "," + mods);
        //convert to VK
        boolean press = action > 0;
        if (press && key >= 32 && key <= 128) {
          Static.video.keyTyped((char)key);
        }
        if (press) {
          Static.video.keyPressed(key);
        } else {
          Static.video.keyReleased(key);
        }
      }
    });

    glfwSetCallback(win.handle, win.mouseButtonCallback = new GLFWMouseButtonCallback() {
      public void invoke(long window, int button, int action, int mods) {
//        Static.log("mouseBut:" + button + "," + action + "," + mods);
        switch (button) {
          case 0: button = 1; break;
          case 1: button = 3; break;
          default: return;
        }
        if (action == 1) {
          mb = button;
          Static.video.mouseDown(mx, my, button);
        } else {
          mb = 0;
          Static.video.mouseUp(mx, my, button);
        }
      }
    });

    glfwSetCallback(win.handle, win.mousePosCallback = new GLFWCursorPosCallback() {
      public void invoke(long window, double x, double y) {
//        Static.log("mousePos:" + x + "," + y);
        mx = (float) x;
        my = (float) y;
        Static.video.mouseMove(mx, my, mb);
      }
    });

    glfwSetCallback(win.handle, win.windowSizeCallback = new GLFWWindowSizeCallback() {
      public void invoke(long window, int x, int y) {
        Static.video.resize(x, y);
      }
    });

    glfwSetCallback(win.handle, win.windowCloseCallback = new GLFWWindowCloseCallback() {
      public void invoke(long window) {
        Static.video.windowClosed();
      }
    });

    glfwSetCallback(win.handle, win.scrollCallback = new GLFWScrollCallback() {
      public void invoke(long window, double x, double y) {
        if (JF.isMac()) {
          //for Mac users
          Static.video.mouseScrolled((int)y);
        } else {
          //for everyone else
          Static.video.mouseScrolled((int)-y);
        }
      }
    });
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the ContextCapabilities instance and makes the OpenGL
    // bindings available for use.
//    GLContext.createFromCurrent();  //I don't use LWJGL for OpenGL API

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (glfwWindowShouldClose(current.handle) == GL_FALSE) {
      Static.video.render();
      // Poll for window events.
      glfwPollEvents();
      if (toggleFullscreenMode) {
        toggleFullscreenMode = false;
        if (fullscreenMode) {
          current = window;
          glfwHideWindow(fullscreen.handle);
          glfwShowWindow(window.handle);
          glfwMakeContextCurrent(window.handle);
        } else {
          current = fullscreen;
          glfwHideWindow(window.handle);
          if (fullscreen.handle == NULL) {
            createWindow(fullscreen, glfwGetPrimaryMonitor(), window.handle);
          }
          glfwShowWindow(fullscreen.handle);
          glfwMakeContextCurrent(fullscreen.handle);
        }
//        GLContext.createFromCurrent();  //I don't use LWJGL for OpenGL API
        fullscreenMode = !fullscreenMode;
        Static.video.reload();
      }
    }
  }

  public static void main(String[] args) {
    new Main().run();
  }

  public static Main main;

  private void _swap() {
    glfwSwapBuffers(current.handle); // swap the color buffers
  }

  public static void swap() {
    main._swap();
  }

  private void _setCursor(boolean state) {
    if (state) {
      glfwSetInputMode(current.handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    } else {
      glfwSetInputMode(current.handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
  }

  public static void setCursor(boolean state) {
    main._setCursor(state);
  }

  public static void toggleFullscreen() {
//    main.toggleFullscreenMode = true;  //Not supported yet
  }
}
