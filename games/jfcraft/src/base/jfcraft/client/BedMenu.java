package jfcraft.client;

/** Bed Menu
 *
 * @author pquiring
 *
 * Created : Sept 24, 2014
 */

import javaforce.gl.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class BedMenu extends RenderScreen {
  public BedMenu() {
    id = Client.BED;
    setCursor();
    Static.inGame = false;
  }

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Leave Bed", 56, 390, 400, new Runnable() {public void run() {
      Static.client.clientTransport.leaveMenu();
      leaveMenu();
    }});
  }

  public void render(GL gl, int width, int height) {
    Static.game.render(gl, width, height);
    setMenuSize(512, 512);
    reset();
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, Static.identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix
    renderShade(gl);
    setOrtho(gl);
    renderButtons(gl);
    renderText(gl);
  }

  public void resize(GL gl, int width, int height) {
    Static.game.resize(gl, width, height);
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
