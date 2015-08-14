package jfcraft.client;

/** Waiting screen
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.Textures;
import jfcraft.data.Static;

public class WaitMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private String msg1, msg2;

  public WaitMenu() {
    id = Client.WAIT;
  }

  public void setup(String msg1, String msg2) {
    this.msg1 = msg1;
    this.msg2 = msg2;
  }

  public void init() {
    super.init();
  }

  public void render(int width, int height) {
    if (Static.server == null) {
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
      return;
    }

    if (t_menu == null) {
      t_menu = Textures.getTexture("jfcraft/background", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    //now render stuff
    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    int x1 = (512 - msg1.length() * fontSize) / 2;
    renderText(x1, 50, msg1);
    int x2 = (512 - msg2.length() * fontSize) / 2;
    renderText(x2, 100, msg2);

    renderButtons();
  }

  public void resize(int width, int height) {
    super.resize(width, height);
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
