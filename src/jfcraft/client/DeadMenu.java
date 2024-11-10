package jfcraft.client;

/** Dead Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class DeadMenu extends RenderScreen {

  public DeadMenu() {
    id = Client.DEAD;
  }

  public void setup() {
    setCursor(true);
    Static.inGame = false;
  }

  public void init() {
    super.init();
    addButton("Respawn", 56, 40, 400, new Runnable() {public void run() {
      Static.client.clientTransport.respawn();
    }});
    addButton("Quit", 56, 390, 400, new Runnable() {public void run() {
      Static.client.clientTransport.logout();
      Static.client.clientTransport.close();
      if (Static.server != null) {
        Static.server.close();
      }
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
    }});
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix
    renderShade();
    setOrtho();
    renderButtons();
  }

  public void resize(int width, int height) {
    Static.game.resize(width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
  }

  public void mouseReleased(int x, int y, int button) {
    super.mouseReleased(x, y, button);
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
