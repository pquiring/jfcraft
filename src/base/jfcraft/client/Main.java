package jfcraft.client;

/**
 *
 * @author pquiring
 */

import javaforce.JF;
import jfcraft.data.*;

import javaforce.ui.*;
import javaforce.gl.*;

import jfcraft.opengl.*;

public class Main implements KeyEvents, MouseEvents, WindowEvents {
  // We need to strongly reference callback instances.
  private Window window;
  private Window fullscreen;
  private Window current;

  private float mx, my;
  private int mb;

  private boolean fullscreenMode;
  private boolean toggleFullscreenMode;

  public void run() {
    main = this;
    try {
      Settings.load();
      init();
      initGame();
      loop();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Terminate GLFW and release the GLFWerrorfun
    }
  }

  private void init() {
    Window.init();
    window = createWindow(Window.STYLE_VISIBLE | Window.STYLE_RESIZABLE | Window.STYLE_TITLEBAR,512,512,null);
    if (JF.isWindows()) {
      window.setIcon("jfcraft.ico", 16, 16);
    }
    current = window;
    GL.glInit();  //load gl api
  }

  private Window createWindow(int style, int x,int y,Window shared) {
    Window win = new Window();
    win.create(style, "jfCraft", x, y, shared);
    win.setKeyListener(this);
    win.setMouseListener(this);
    win.setWindowListener(this);
    return win;
  }

  private void initGame() {
    new RenderEngine().init();
    Static.video.setScreen(new Loading());
  }

  private void loop() {
    while (true) {
      Static.video.render();
      // Poll for window events.
      Window.pollEvents();
      if (toggleFullscreenMode) {
        toggleFullscreenMode = false;
        if (fullscreenMode) {
          current = window;
          fullscreen.hide();
          window.show();
          window.setCurrent();
        } else {
          current = fullscreen;
          window.hide();
          if (fullscreen == null) {
            fullscreen = createWindow(0,1,1,window);
          }
          fullscreen.show();
          fullscreen.setCurrent();
        }
//        GLContext.createFromCurrent();  //I don't use LWJGL for OpenGL API
        fullscreenMode = !fullscreenMode;
        Static.video.reload();
      }
    }
  }

  public static void main(String[] args) {
    for(int a=0;a<args.length;a++) {
      String arg = args[a];
      if (arg.equals("-debug")) {
        Static.debugTest = true;
      }
      if (arg.equals("-debuglights")) {
        Static.debugLights = true;
      }
      if (arg.equals("-music=false")) {
        Static.optionMusic = false;
      }
    }
    new Main().run();
  }

  public static Main main;

  private void _swap() {
    current.swap();
  }

  public static void swap() {
    main._swap();
  }

  private void _setCursor(boolean state) {
    if (state) {
      current.showCursor();
    } else {
      current.lockCursor();
    }
  }

  public static void setCursor(boolean state) {
    main._setCursor(state);
  }

  public static void toggleFullscreen() {
//    main.toggleFullscreenMode = true;  //Not supported yet
  }

  @Override
  public void keyTyped(char c) {
    Static.video.keyTyped(c);
  }

  @Override
  public void keyPressed(int vk) {
    Static.video.keyPressed(vk);
  }

  @Override
  public void keyReleased(int vk) {
    Static.video.keyReleased(vk);
  }

  @Override
  public void mouseMove(int x, int y) {
    mx = x;
    my = y;
    Static.video.mouseMove(mx,my,mb);
  }

  @Override
  public void mouseDown(int button) {
    Static.video.mouseDown(mx,my,button);
    mb = button;
  }

  @Override
  public void mouseUp(int button) {
    Static.video.mouseUp(mx,my,button);
    mb = 0;
  }

  @Override
  public void mouseScroll(int x, int y) {
    Static.video.mouseScrolled(y);
  }

  @Override
  public void windowResize(int x, int y) {
    Static.video.resize(x,y);
  }

  @Override
  public void windowClosing() {
    System.exit(0);
  }
}
