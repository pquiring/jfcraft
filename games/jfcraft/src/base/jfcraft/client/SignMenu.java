package jfcraft.client;

/** Sign Menu
 *
 * @author pquiring
 *
 * Created : Sept 24, 2014
 */

import javaforce.gl.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class SignMenu extends RenderScreen {
  public SignMenu() {
    id = Client.SIGN;
  }

  private Texture t_board;
  private RenderBuffers b_board;

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Done", 56, 390, 400, new Runnable() {public void run() {
      String txt[] = new String[4];
      for(int a=0;a<4;a++) {
        txt[a] = fields.get(a).getText();
      }
      client.clientTransport.setSign(txt);
      client.clientTransport.leaveMenu();
      leaveMenu();
    }});
    for(int a=0;a<4;a++) {
      addTextField(gl, "", 52, 40 + a * fontSize * 2, 408, false, 15, true, 2);
    }
    t_board = Textures.getTexture(gl, "blocks/planks_oak");
    b_board = createMenu(gl, 76, 40, 0, 0, 360, fontSize * 2 * 4);
  }

  public void setup() {
    for(int a=0;a<4;a++) {
      fields.get(a).setText("");
    }
    setFocus(fields.get(0));
  }

  public void render(GL gl, int width, int height) {
    setCursor();
    Static.inGame = false;

    game.render(gl, width, height);
    setMenuSize(512, 512);
    reset();
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, Static.identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix
    renderShade(gl);
    setOrtho(gl);
    t_board.bind(gl);
    b_board.bindBuffers(gl);
    b_board.render(gl);
    renderButtons(gl);
    renderFields(gl);
    renderText(gl);
  }

  public void resize(GL gl, int width, int height) {
    game.resize(gl, width, height);
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
