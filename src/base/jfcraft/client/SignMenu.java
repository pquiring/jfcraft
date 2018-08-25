package jfcraft.client;

/** Sign Menu
 *
 * @author pquiring
 *
 * Created : Sept 24, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class SignMenu extends RenderScreen {
  public SignMenu() {
    id = Client.SIGN;
  }

  private Texture t_board;
  private RenderBuffers b_board;

  public void init() {
    super.init();
    addButton("Done", 56, 390, 400, new Runnable() {public void run() {
      String txt[] = new String[4];
      for(int a=0;a<4;a++) {
        txt[a] = fields.get(a).getText();
      }
      Static.client.clientTransport.setSign(txt);
      Static.client.clientTransport.leaveMenu();
      leaveMenu();
    }});
    for(int a=0;a<4;a++) {
      addTextField("", 52, 40 + a * fontSize * 2, 408, null, 15, true, 2);
    }
    t_board = Textures.getTexture("block/oak_planks", 0);
    b_board = createMenu(76, 40, 0, 0, 360, fontSize * 2 * 4);
  }

  public void setup() {
    for(int a=0;a<4;a++) {
      fields.get(a).setText("");
    }
    setFocus(fields.get(0));
    setCursor(true);
    Static.inGame = false;
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, Static.identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, Static.identity.m);  //model matrix
    renderShade();
    setOrtho();
    t_board.bind();
    b_board.bindBuffers();
    b_board.render();
    renderButtons();
    renderFields();
  }

  public void resize(int width, int height) {
    Static.game.resize(width, height);
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
