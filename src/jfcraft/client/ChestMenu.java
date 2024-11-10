package jfcraft.client;

/** Chest Menu
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import javaforce.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class ChestMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu, o_menu2;
  private int mx, my;
  private int cnt;
  private Slot slots[];

  public ChestMenu() {
    id = Client.CHEST;
    gui_width = 352;
    gui_height = 330;
    slots = new Slot[4*9 + 3*9 + 1];  //slots(4*9), chest(3*9), hand(1)
    //inventory blocks
    int p = 0;
    int x = 16, y = (int)(gui_height - 131);
    for(int a=9;a<4*9;a++) {
      if (a > 9 && a % 9 == 0) {
        x = 16;
        y += 36;
      }
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }
    //active slots
    x = 16;
    y = (int)(gui_height - 11);
    for(int a=0;a<9;a++) {
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }

    //chest items
    x = 16;
    y = 36 + 36;
    for(int a=0;a<3*9;a++) {
      if (a > 0 && a % 9 == 0) {
        x = 16;
        y += 36;
      }
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }

    //item in hand
    slots[p] = new Slot();
    slots[p].x = mx;
    slots[p].y = my;
    slots[p].renderName = true;
  }

  public void setup() {
    setCursor(true);
    ExtraContainer chest = Static.client.container;
    if (chest == null) {
      Static.log("Error:ChestMenu setup but not ready");
      return;
    }
    cnt = chest.items.length;
    gui_width = 352;
    if (cnt == 6*9) {
      //6*9 double chest
      gui_height = 443;
    } else {
      //3*9 small chest
      gui_height = 443 - 36*3;
    }
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);

    ExtraContainer chest = Static.client.container;
    if (chest == null) return;

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/generic_54", 0);
    }

    if (o_menu == null) {
      if (chest.items.length == 6*9) {
        o_menu = createMenu();
      } else {
        o_menu = createMenu(0,0, 0,0, 351,34);
        o_menu2 = createMenu(0,34, 0,34 + 3*36, 351,443 - 3*36);
      }
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();
    if (o_menu2 != null) {
      o_menu2.bindBuffers();
      o_menu2.render();
    }

    int p = 0;
    //inventory slots
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //chest slots
    if (chest != null) {
      for(int a=0;a<3*9;a++) {
        slots[p++].item = chest.items[a];
      }
    } else {
      for(int a=0;a<3*9;a++) {
        slots[p++].item = null;
      }
    }

    //item in hand
    slots[p].item = Static.client.hand;
    slots[p].x = mx;
    slots[p].y = my;

    renderItems(slots);
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    switch (vk) {
      case KeyCode.VK_E:
      case KeyCode.VK_ESCAPE:
        Static.client.container = null;
        Static.client.clientTransport.leaveMenu();
        leaveMenu();
        break;
    }
  }

  public void resize(int width, int height) {
    Static.game.resize(width, height);
  }

  public void mousePressed(int x, int y, int button) {
    //check inventory
    int bx = 16, by = ((int)(gui_height - 131)) - 36;
    for(byte a=9;a<4*9;a++) {
      if (a != 9 && a % 9 == 0) {
        bx = 16;
        by += 36;
      }
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
      bx += 36;
    }
    //check active slots
    bx = 16;
    by = (int)(gui_height - 11) - 36;
    for(byte a=0;a<9;a++) {
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
      bx += 36;
    }
    //check chest items
    bx = 16;
    by = 36;
    for(byte a=0;a<cnt;a++) {
      if (a != 0 && a % 9 == 0) {
        bx = 16;
        by += 36;
      }
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickContainer(a, button == 1);
      }
      bx += 36;
    }
  }

  public void mouseReleased(int x, int y, int button) {
    Static.game.mouseReleased(x, y, button);
  }

  public void mouseMoved(int x, int y, int button) {
    mx = x;
    my = y;
  }

  public void mouseWheel(int delta) {
  }
}
