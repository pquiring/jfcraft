package jfcraft.client;

/** Pause Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import javaforce.gl.*;

import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class PauseMenu extends RenderScreen {
  public PauseMenu() {
    id = Client.PAUSE;
  }

  private Button openToLan;

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Back to Game", 56, 40, 400, new Runnable() {public void run() {
      Static.video.setScreen(Static.game);
      Static.game.setCursor();
      Static.inGame = true;
    }});
    addButton(gl, "Options", 56, 300, 190, new Runnable() {public void run() {
    }});
    openToLan = addButton(gl, "Open to LAN", 266, 300, 190, new Runnable() {public void run() {
      if (Static.client.openToLan) return;
      Static.client.clientTransport.openToLan();
      Static.client.openToLan = true;
      openToLan.setClr(Static.grey);
    }});
    addButton(gl, "Quit", 56, 390, 400, new Runnable() {public void run() {
      Static.client.clientTransport.logout();
      Static.client.clientTransport.close();
      Static.client.stopTimers();
      Static.client.stopVoIP();
      boolean isLocal = Static.client.isLocal;
      Static.game = null;
      if (isLocal) {
        WaitMenu wait = (WaitMenu)Static.screens.screens[Client.WAIT];
        wait.setup("Please wait...", "Shutting down server");
        Static.video.setScreen(wait);
      } else {
        Static.video.setScreen(Static.screens.screens[Client.MAIN]);
      }
    }});
  }

  public void setup() {
    setCursor();
    if (Static.client.openToLan) {
      openToLan.setClr(Static.grey);
    }
  }

  public void render(GL gl, int width, int height) {
    Static.game.render(gl, width, height);
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
    Static.game.resize(gl, width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
  }

  public void mouseReleased(int x, int y, int button) {
    Static.game.mouseReleased(x, y, button);
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
