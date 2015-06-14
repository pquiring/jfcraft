package jfcraft.client;

/** Crafting Table Menu
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import jfcraft.item.Item;
import java.awt.Cursor;
import java.awt.event.KeyEvent;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class CraftingMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private int mx, my;
  private final int gui_width = 350, gui_height = 330;  //size of menu

  public CraftingMenu() {
    id = Client.CRAFTTABLE;
  }

  public void setup() {
    setCursor();
  }

  public void render(GL gl, int width, int height) {
    game.render(gl, width, height);
    setMenuSize(gui_width, gui_height);

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "gui/container/crafting_table");
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    super.renderShade(gl);

    gl.glDepthFunc(GL.GL_ALWAYS);

    setOrtho(gl);

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    reset();

    //render inventory blocks
    int x = 16, y = (int)(gui_height - 131);
    for(int a=9;a<4*9;a++) {
      if (a > 9 && a % 9 == 0) {
        x = 16;
        y += 36;
      }
      Item item = game.client.player.items[a];
      if (item.id != 0) {
        renderItem(gl,item,x,y);
      }
      x += 36;
    }
    //render active slots
    x = 16;
    y = (int)(gui_height - 11);
    for(int a=0;a<9;a++) {
      Item item = game.client.player.items[a];
      if (item.id != 0) {
        renderItem(gl,item,x,y);
      }
      x += 36;
    }

    Item item;

    //render crafting slots(9)
    x = 59;
    y = 32 + 36;
    for(int a=0;a<9;a++) {
      if (a > 0 && a % 3 == 0) {
        x = 59;
        y += 36;
      }
      item = game.client.craft[a];
      if (item.id != 0) {
        renderItem(gl,item,x,y);
      }
      x += 36;
    }

    //render crafted item
    item = game.client.crafted;
    if (item != null) {
      renderItem(gl,item,239,60 + 36);
    }

    //render item in hand
    item = game.client.hand;
    if (item != null) {
      renderItem(gl,item,mx,my);
    }

    renderText(gl);
    renderBars(gl);

    if (item != null) {
      reset();
      renderItemName(gl, item, mx, my);
      renderBars50(gl);
      renderText(gl);
    } else {
      //TODO : render item name under mouse
    }
  }

  public void keyPressed(int vk) {
    super.keyPressed(vk);
    switch (vk) {
      case KeyEvent.VK_E:
      case KeyEvent.VK_ESCAPE:
        game.client.clientTransport.leaveMenu();
        leaveMenu();
        break;
    }
  }

  public void resize(GL gl, int width, int height) {
    game.resize(gl, width, height);
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
        game.client.clickInventory(a, button == 1);
      }
      bx += 36;
    }
    //check active slots
    bx = 16;
    by = (int)(gui_height - 11) - 36;
    for(byte a=0;a<9;a++) {
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        game.client.clickInventory(a, button == 1);
      }
      bx += 36;
    }
    //check armor
    bx = 15;
    by = 15;
    for(byte a=0;a<4;a++) {
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        game.client.clickArmor(a, button == 1);
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
        game.client.clickCraftlInput(a, button == 1);
      }
      bx += 36;
    }
    //check craft output
    bx = 239;
    by = 60;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      game.client.clickCraftOutput(button == 1);
    }
  }

  public void mouseReleased(int x, int y, int button) {
    game.mouseReleased(x, y, button);
  }

  public void mouseMoved(int x, int y, int button) {
    mx = x;
    my = y;
  }

  public void mouseWheel(int delta) {
  }
}
