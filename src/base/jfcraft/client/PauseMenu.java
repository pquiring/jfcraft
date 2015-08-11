package jfcraft.client;

/** Pause Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class PauseMenu extends RenderScreen {
  public PauseMenu() {
    id = Client.PAUSE;
  }

  private Button openToLan;

  public void init() {
    super.init();
    addButton( "Back to Game", 56, 40, 400, new Runnable() {public void run() {
      Static.video.setScreen(Static.game);
      setCursor(false);
      Static.inGame = true;
    }});
    addButton( "Options", 56, 300, 190, new Runnable() {public void run() {
    }});
    openToLan = addButton( "Open to LAN", 266, 300, 190, new Runnable() {public void run() {
      if (Static.client.openToLan) return;
      Static.client.clientTransport.openToLan();
      Static.client.openToLan = true;
      openToLan.setClr(Static.grey);
    }});
    addButton( "Quit", 56, 390, 400, new Runnable() {public void run() {
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
    setCursor(true);
    if (Static.client.openToLan || !Static.client.clientTransport.isLocal()) {
      openToLan.setClr(Static.grey);
    } else {
      openToLan.setClr(Static.white);
    }
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    setMenuSize(512, 512);
    reset();
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix
    super.renderShade();
    setOrtho();
    renderButtons();
    renderText();
  }

  public void resize(int width, int height) {
    Static.game.resize(width, height);
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
