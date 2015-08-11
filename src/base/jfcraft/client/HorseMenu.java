package jfcraft.client;

/** Horse Menu
 *
 * @author pquiring
 *
 * Created : Jun 27, 2015
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class HorseMenu extends RenderScreen {
  private Texture t_menu;
  private static RenderBuffers o_menu, o_15, o_armor;
  private int mx, my;
  private final int gui_width = 350, gui_height = 330;  //size of menu

  public HorseMenu() {
    id = Client.HORSE;
  }

  public void setup() {
    setCursor(true);
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    setMenuSize(gui_width, gui_height);

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

    super.renderShade();

    glDepthFunc(GL_ALWAYS);

    setOrtho();

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

    //render 2 slots
    if (container != null) {
      if (container.items[ExtraHorse.SADDLE].id != 0) {
        renderItem(container.items[ExtraHorse.SADDLE],16,36 + 36);
      }
      char id = container.items[ExtraHorse.ARMOR].id;
      if (id != 0 && id != Blocks.OBSIDIAN) {
        renderItem(container.items[ExtraHorse.ARMOR],16,36*2 + 36);
      }
    }

    //render chest items
    Item item;
    if (container != null && container.items.length > 2) {
      x = 160;
      y = 36 + 36;
      for(int a=0;a<15;a++) {
        if (a > 0 && a % 5 == 0) {
          x = 160;
          y += 36;
        }
        item = container.items[a+2];
        if (item != null && item.id != 0) {
          renderItem(item,x,y);
        }
        x += 36;
      }
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
