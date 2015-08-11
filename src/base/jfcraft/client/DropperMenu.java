package jfcraft.client;

/** Dropper Menu
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class DropperMenu extends RenderScreen {
  private Texture t_menu;
  private static RenderBuffers o_menu;
  private int mx, my;
  private final int gui_width = 352, gui_height = 332;  //size of menu

  public DropperMenu() {
    id = Client.DROPPER;
  }

  public void setup() {
    setCursor(true);
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    setMenuSize(gui_width, gui_height);

    if (t_menu == null) {
      t_menu = Textures.getTexture( "gui/container/dispenser", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    ExtraContainer dropper = Static.client.container;

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    super.renderShade();

    glDepthFunc(GL_ALWAYS);

    setOrtho();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

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

    //render dropper slots
    if (dropper != null) {
      x = 122;
      y = 32 + 36;
      for(int a=0;a<3*3;a++) {
        if (a > 0 && a % 3 == 0) {
          x = 122;
          y += 36;
        }
        Item item = dropper.items[a];
        if (item.id != 0) {
          renderItem(item,x,y);
        }
        x += 36;
      }
    }

    //render item in hand
    Item item = Static.client.hand;
    if (item != null) {
      renderItem(item,mx,my);
    }

    renderText();
    renderBars();

    if (item != null) {
      reset();
      renderItemName( item, mx, my);
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
    //check dropper
    bx = 122;
    by = 32;
    for(byte a=0;a<3*3;a++) {
      if (a > 0 && a % 3 == 0) {
        bx = 122;
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
