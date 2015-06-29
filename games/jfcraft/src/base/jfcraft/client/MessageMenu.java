package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.gl.*;

import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.Textures;
import jfcraft.data.Static;

public class MessageMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private String msg1, msg2;
  private RenderScreen scene;

  public MessageMenu() {
    id = Client.MESSAGE;
  }

  public void setup(String msg1, String msg2, RenderScreen scene) {
    this.msg1 = msg1;
    this.msg2 = msg2;
    this.scene = scene;
  }

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Okay", 20, 390, 226, new Runnable() {public void run() {
      Static.video.setScreen(scene);
    }});
  }

  public void render(GL gl, int width, int height) {
    setMenuSize(512, 512);
    reset();

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "jfcraft/background", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    //now render stuff
    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

    setOrtho(gl);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    int x1 = (512 - msg1.length() * fontSize) / 2;
    addText(x1, 50, msg1);
    int x2 = (512 - msg2.length() * fontSize) / 2;
    addText(x2, 100, msg2);

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    renderButtons(gl);
    renderText(gl);
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
