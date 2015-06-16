package jfcraft.client;

/** Chat Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import java.awt.event.*;
import java.util.*;

import javaforce.gl.*;

//import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class ChatMenu extends RenderScreen {
  public ChatMenu() {
    id = Client.CHAT;
  }

  private TextField chat;
  private String initTxt;

  private ArrayList<String> history = new ArrayList<String>();
  private String current;
  private int idx;

  public void init(GL gl) {
    super.init(gl);
    chat = addTextField(gl, "", 5, 512 - fontSize * 6, 512-10, true, 127, false, 1);
  }

  public void setup() {
    setFocus(chat);
  }

  public void setup(String cmd) {
    //init(gl) may not have been called yet
    initTxt = cmd;
  }

  public void render(GL gl, int width, int height) {
    if (initTxt != null) {
      chat.setText(initTxt);
      current = initTxt;
      idx = history.size();
      initTxt = null;
    }
    Static.game.render(gl, width, height);
    setMenuSize(512, 512);
    reset();
    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix
    setOrtho(gl);
    renderFields(gl);
    renderText(gl);

    Static.client.chatTime = 5 * 20;
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    switch (vk) {
      case KeyEvent.VK_UP:
        if (idx == 0) break;
        if (idx == history.size()) {
          current = chat.getText();
        }
        idx--;
        chat.setText(history.get(idx));
        break;
      case KeyEvent.VK_DOWN:
        if (idx == history.size()) break;
        idx++;
        if (idx == history.size()) {
          chat.setText(current);
        } else {
          chat.setText(history.get(idx));
        }
        break;
      case KeyEvent.VK_ESCAPE:
        leaveMenu();
        break;
      case KeyEvent.VK_ENTER:
        String msg = chat.getText();
        if (msg.length() > 0) {
          Static.client.clientTransport.sendMsg(msg);
          history.add(msg);
        }
        Static.video.setScreen(Static.game);
        Static.game.setCursor();
        Static.inGame = true;
        break;
    }
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
