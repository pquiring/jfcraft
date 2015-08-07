package jfcraft.client;

/**
 *
 * @author pquiring
 */
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import jfcraft.data.Static;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import javaforce.gl.GL;

import jfcraft.opengl.*;

public class MainLWJGL {
  // We need to strongly reference callback instances.

  private GLFWErrorCallback errorCallback;
  private GLFWKeyCallback keyCallback;
  private GLFWCursorPosCallback mousePosCallback;
  private GLFWMouseButtonCallback mouseButtonCallback;

  private float mx, my;
  private int buttons;

  // The window handle
  private long window;

  public void run() {
    this.main = this;
    try {
      init();
      loop();
      // Release window and window callbacks
      glfwDestroyWindow(window);
      keyCallback.release();
      mousePosCallback.release();
      mouseButtonCallback.release();
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

    // Configure our window
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

    int WIDTH = 512;
    int HEIGHT = 512;

    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, "jfCraft", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
      public void invoke(long window, int key, int scancode, int action, int mods) {
        Static.log("     key:" + key + "," + scancode + "," + action + "," + mods);
        Static.video.keyPressed(key, false);
      }
    });

    glfwSetCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
      public void invoke(long window, int button, int action, int mods) {
        Static.log("mouseBut:" + button + "," + action + "," + mods);
        buttons = button;
        Static.video.mouseDown(mx, my, buttons);
      }
    });

    glfwSetCallback(window, mousePosCallback = new GLFWCursorPosCallback() {
      public void invoke(long window, double x, double y) {
        Static.log("mousePos:" + x + "," + y);
        mx = (float) x;
        my = (float) y;
        Static.video.mouseMove(mx, my, buttons);
      }
    });

    // Get the resolution of the primary monitor
    ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    // Center our window
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    );

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(window);

    GL.glInit();

    new RenderEngine(new Loading()).init();
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the ContextCapabilities instance and makes the OpenGL
    // bindings available for use.
    GLContext.createFromCurrent();

    // Set the clear color
    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      Static.video.render();
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }
  }

  public static void main(String[] args) {
    new MainLWJGL().run();
  }

  public static MainLWJGL main;

  private void _swap() {
    glfwSwapBuffers(window); // swap the color buffers
  }

  public static void swap() {
    main._swap();
  }

  private void _setCursor(boolean state) {
    //TODO
  }

  public static void setCursor(boolean state) {
    main._setCursor(state);
  }

  public static int[] pos = new int[2];

  public static int[] getPosOnScreen() {
    return pos;
  }
}
