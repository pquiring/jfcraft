package jfcraft.client;

/** Chat Menu
 *
 * @author pquiring
 *
 * Created : Apr 2, 2014
 */

import java.util.*;

import javaforce.gl.*;
import javaforce.ui.*;
import static javaforce.gl.GL.*;

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

  public void init() {
    super.init();
    chat = addTextField("", 5, 512 - fontSize * 6, 512-10, Static.black4, 127, false, 1);
  }

  public void setup() {
    setFocus(chat);
  }

  public void setup(String cmd) {
    //init(gl) may not have been called yet
    initTxt = cmd;
  }

  public void render(int width, int height) {
    if (initTxt != null) {
      chat.setText(initTxt);
      current = initTxt;
      idx = history.size();
      initTxt = null;
    }
    Static.game.render(width, height);
    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix
    setOrtho();
    renderFields();

    Static.client.chatTime = 5 * 20;
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    switch (vk) {
      case KeyCode.VK_UP:
        if (idx == 0) break;
        if (idx == history.size()) {
          current = chat.getText();
        }
        idx--;
        chat.setText(history.get(idx));
        break;
      case KeyCode.VK_DOWN:
        if (idx == history.size()) break;
        idx++;
        if (idx == history.size()) {
          chat.setText(current);
        } else {
          chat.setText(history.get(idx));
        }
        break;
      case KeyCode.VK_ESCAPE:
        leaveMenu();
        break;
      case KeyCode.VK_ENTER:
        String msg = chat.getText();
        if (msg.length() > 0) {
          Static.client.clientTransport.sendMsg(msg);
          history.add(msg);
        }
        Static.video.setScreen(Static.game);
        setCursor(false);
        Static.inGame = true;
        break;
    }
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
