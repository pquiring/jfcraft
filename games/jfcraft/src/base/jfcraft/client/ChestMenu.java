package jfcraft.client;

/** Chest Menu
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class ChestMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu, o_menu2;
  private int mx, my;
  private int gui_width = 352, gui_height = 330;  //size of menu
  private int cnt;

  public ChestMenu() {
    id = Client.CHEST;
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
    setMenuSize(gui_width, gui_height);
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

    super.renderShade();

    glDepthFunc(GL_ALWAYS);

    setOrtho();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();
    if (o_menu2 != null) {
      o_menu2.bindBuffers();
      o_menu2.render();
    }

    reset();

    //render inventory blocks
    int x = 16, y = (int)(gui_height - 131);
    for(int a=9;a<4*9;a++) {
      if (a > 9 && a % 9 == 0) {
        x = 16;
        y += 36;
      }
      Item item = Static.client.player.items[a];
      if (item.id != 0) {
        renderItem(item,x,y);
      }
      x += 36;
    }
    //render active slots
    x = 16;
    y = (int)(gui_height - 11);
    for(int a=0;a<9;a++) {
      Item item = Static.client.player.items[a];
      if (item.id != 0) {
        renderItem(item,x,y);
      }
      x += 36;
    }

    Item item;

    //render chest items
    x = 16;
    y = 36 + 36;
    for(int a=0;a<cnt;a++) {
      if (a > 0 && a % 9 == 0) {
        x = 16;
        y += 36;
      }
      item = chest.items[a];
      if (item != null && item.id != 0) {
        renderItem(item,x,y);
      }
      x += 36;
    }

    //render item in hand
    item = Static.client.hand;
    if (item != null) {
      renderItem(item,mx,my);
    }

    renderText();
    renderBars();

    if (item != null) {
      reset();
      renderItemName(item, mx, my);
      renderBars50();
      renderText();
    } else {
      //TODO : render item name under mouse
    }
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    switch (vk) {
      case GLVK.VK_E:
      case GLVK.VK_ESCAPE:
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
