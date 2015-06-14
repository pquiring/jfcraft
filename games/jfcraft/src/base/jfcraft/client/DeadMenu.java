package jfcraft.client;

/** Dead Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import javaforce.gl.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class DeadMenu extends RenderScreen {

  public DeadMenu() {
    id = Client.DEAD;
  }

  public void setup() {
    setCursor();
    super.setMenuSize(512, 512);
    Static.inGame = false;
  }

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Respawn", 56, 40, 400, new Runnable() {public void run() {
      client.clientTransport.respawn();
    }});
    addButton(gl, "Quit", 56, 390, 400, new Runnable() {public void run() {
      game.client.clientTransport.logout();
      game.client.clientTransport.close();
      if (Static.server != null) {
        Static.server.close();
      }
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
    }});
  }

  public void render(GL gl, int width, int height) {
    game.render(gl, width, height);
    setMenuSize(512, 512);
    reset();
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix
    super.renderShade(gl);
    setOrtho(gl);
    renderButtons(gl);
    renderText(gl);
  }

  public void resize(GL gl, int width, int height) {
    game.resize(gl, width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
  }

  public void mouseReleased(int x, int y, int button) {
    game.mouseReleased(x, y, button);
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
