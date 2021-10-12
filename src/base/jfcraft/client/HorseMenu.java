package jfcraft.client;

/** Horse Menu
 *
 * @author pquiring
 *
 * Created : Jun 27, 2015
 */

import javaforce.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class HorseMenu extends RenderScreen {
  private TextureMap t_menu;
  private static RenderBuffers o_menu, o_15, o_armor;
  private int mx, my;
  private Slot slots[];

  public HorseMenu() {
    id = Client.HORSE;
    gui_width = 350;
    gui_height = 330;
    slots = new Slot[4*9 + 1 + 1 + 15 + 1];  //slots(4*9), saddle(1), armor(1), chest(15), hand(1)
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

    //2 slots
    slots[p] = new Slot();
    slots[p].x = 16;
    slots[p].y = 36 + 36;
    p++;
    slots[p] = new Slot();
    slots[p].x = 16;
    slots[p].y = 36*2 + 36;
    p++;

    //chest items
    x = 160;
    y = 36 + 36;
    for(int a=0;a<15;a++) {
      if (a > 0 && a % 5 == 0) {
        x = 160;
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
  }

  public void render(int width, int height) {
    Static.game.render(width, height);

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/horse", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    if (o_15 == null) {
      o_15 = createMenu(160,36, 0,332, 180,108);
    }

    if (o_armor == null) {
      o_armor = createMenu(14,70, 0,440, 36,36);
    }

    ExtraHorse container = (ExtraHorse)Static.client.container;

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    glDepthFunc(GL_ALWAYS);

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();
    if (container != null) {
      if (container.items.length > 2) {
        o_15.bindBuffers();
        o_15.render();
      }
      if (container.items[1].id != Blocks.OBSIDIAN) {
        //mule & donkey have OBSIDIAN where the armor would be placed
        o_armor.bindBuffers();
        o_armor.render();
      }
    }

    //inventory blocks
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //2 slots
    if (container != null) {
      slots[p++].item = container.items[ExtraHorse.SADDLE];
      slots[p++].item = container.items[ExtraHorse.ARMOR];
    } else {
      slots[p++].item = null;
      slots[p++].item = null;
    }

    //chest items
    if (container != null && container.items.length > 2) {
      for(int a=0;a<15;a++) {
        slots[p++].item = container.items[a+2];
      }
    } else {
      for(int a=0;a<15;a++) {
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
    //check horse chest
    ExtraHorse container = (ExtraHorse)Static.client.container;
    if (container != null && container.items.length > 2) {
      bx = 160;
      by = 36;
      byte idx = 2;
      for(int a=0;a<15;a++) {
        if (a > 0 && a % 5 == 0) {
          bx = 160;
          by += 36;
        }
        if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
          Static.client.clickContainer(idx, button == 1);
        }
        bx += 36;
        idx++;
      }
    }

    //check horse slots
    bx = 16;
    by = 36;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraHorse.SADDLE, button == 1);
    }
    bx = 16;
    by = 36+36;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraHorse.ARMOR, button == 1);
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
