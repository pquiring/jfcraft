package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import javaforce.gl.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class MainMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;

  public MainMenu() {
    id = Client.MAIN;
  }

  public void init(GL gl) {
    super.init(gl);
    clear();
    addButton(gl, "Single Player", 56, 200, 400, new Runnable() {public void run() {
      //single player
      if (!Static.blocks.valid) return;
      Static.video.setScreen(Static.screens.screens[Client.SINGLE]);
    }});
    addButton(gl, "Multi Player", 56, 300, 400, new Runnable() {public void run() {
      //multi player
      if (!Static.blocks.valid) return;
      Static.video.setScreen(Static.screens.screens[Client.MULTI]);
    }});
    addButton(gl, "Options", 56, 400, 190, new Runnable() {public void run() {
      //options
//      Static.engine.setScene(?);
      return;
    }});
    addButton(gl, "Quit", 266, 400, 190, new Runnable() {public void run() {
      //quit
      System.exit(0);
    }});
  }

  public void render(GL gl, int width, int height) {
    setMenuSize(512, 512);

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "jfcraft/mainmenu");
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    reset();

    //now render stuff
    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

    gl.glDepthFunc(GL.GL_ALWAYS);

    setOrtho(gl);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    renderButtons(gl);
    renderText(gl);

//    Item item = new Item(Blocks.GRASS);
//    renderItem(gl, item, 50, 50);
  }

  public void resize(GL gl, int width, int height) {
    super.resize(gl, width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
  }

  public void mouseReleased(int x, int y, int button) {
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
