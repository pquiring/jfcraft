package jfcraft.client;

/** Crafting Table Menu
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class CraftingMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private int mx, my;
  private Slot slots[];

  public CraftingMenu() {
    id = Client.CRAFTTABLE;
    gui_width = 350;
    gui_height = 330;
    slots = new Slot[4*9 + 9 + 1 + 1];  //slots(4*9), craft input(9), craft output(1), hand(1)
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

    //crafting slots(9)
    x = 59;
    y = 32 + 36;
    for(int a=0;a<9;a++) {
      if (a > 0 && a % 3 == 0) {
        x = 59;
        y += 36;
      }
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }

    //crafted item
    slots[p] = new Slot();
    slots[p].x = 239;
    slots[p].y = 60 + 36;
    p++;

    //item in hand
    slots[p] = new Slot();
    slots[p].x = mx;
    slots[p].y = my;
    slots[p].renderName = true;
  }

  public void setup() {
    setCursor(true);
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/crafting_table", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    //inventory slots
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //crafting slots(9)
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.craft[a];
    }

    //crafted slot
    slots[p++].item = Static.client.crafted;

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
    //check armor
    bx = 15;
    by = 15;
    for(byte a=0;a<4;a++) {
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickArmor(a, button == 1);
      }
      by += 36;
    }
    //check crafting area
    bx = 59;
    by = 33;
    for(byte a=0;a<9;a++) {
      if (a != 0 && a % 3 == 0) {
        bx = 59;
        by += 36;
      }
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickCraftInput(a, button == 1);
      }
      bx += 36;
    }
    //check craft output
    bx = 239;
    by = 60;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickCraftOutput(button == 1);
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
