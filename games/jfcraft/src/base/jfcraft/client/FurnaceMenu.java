package jfcraft.client;

/** Furnace Menu
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

public class FurnaceMenu extends RenderScreen {
  private Texture t_menu;
  private static RenderBuffers o_menu, o_flame, o_arrow;
  private int mx, my;
  private final int gui_width = 350, gui_height = 330;  //size of menu

  public FurnaceMenu() {
    id = Client.FURNACE;
  }

  public void setup() {
    Main.frame.setCursor(Cursor.getDefaultCursor());
  }

  public void render(GL gl, int width, int height) {
    Static.game.render(gl, width, height);
    setMenuSize(gui_width, gui_height);

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "gui/container/furnace");
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    ExtraFurnace furnace = (ExtraFurnace)Static.client.container;
    int heat = 0, heatMax = 0, timer = 0;
    int flame_height, arrow_width;
    if (furnace != null) {
      heat = furnace.heat;
      heatMax = furnace.heatMax;
      timer = furnace.timer;
    }

    flame_height = heatMax == 0 ? 0 : (heat * 100 / heatMax) * 28 / 100;
    if (o_flame == null) {
      o_flame = createMenu(gl, 111,70 + 28 - flame_height, 352,0 + 28 - flame_height, 28,flame_height);
    } else {
      recreateMenu(gl, o_flame, 111,70 + 28 - flame_height, 352,0 + 28 - flame_height, 28,flame_height);
    }

    arrow_width = timer == 0 ? 0 : ((200-timer) * 100 / 200) * 48 / 100;
    if (o_arrow == null) {
      o_arrow = createMenu(gl, 160,67, 352,28, arrow_width,32);
    } else {
      recreateMenu(gl, o_arrow, 160,67, 352,28, arrow_width,32);
    }

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    super.renderShade(gl);

    gl.glDepthFunc(GL.GL_ALWAYS);

    setOrtho(gl);

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    //render flames and arrow
    t_menu.bind(gl);
    if (flame_height > 0) {
      o_flame.bindBuffers(gl);
      o_flame.render(gl);
    }
    if (arrow_width > 0) {
      o_arrow.bindBuffers(gl);
      o_arrow.render(gl);
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
        renderItem(gl,item,x,y);
      }
      x += 36;
    }
    //render active slots
    x = 16;
    y = (int)(gui_height - 11);
    for(int a=0;a<9;a++) {
      Item item = Static.client.player.items[a];
      if (item.id != 0) {
        renderItem(gl,item,x,y);
      }
      x += 36;
    }

    //render furnace slots
    if (furnace != null) {
      if (furnace.items[ExtraFurnace.INPUT].id != 0) {
        renderItem(gl,furnace.items[ExtraFurnace.INPUT],111,67);
      }
      if (furnace.items[ExtraFurnace.FUEL].id != 0) {
        renderItem(gl,furnace.items[ExtraFurnace.FUEL],111,139);
      }
      if (furnace.items[ExtraFurnace.OUTPUT].id != 0) {
        renderItem(gl,furnace.items[ExtraFurnace.OUTPUT],225,109);
      }
    }

    //render item in hand
    Item item = Static.client.hand;
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
        Static.client.clientTransport.leaveMenu();
        leaveMenu();
        break;
    }
  }

  public void resize(GL gl, int width, int height) {
    Static.game.resize(gl, width, height);
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
    //check furnace
    bx = 111;
    by = 32;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraFurnace.INPUT, button == 1);
    }
    bx = 111;
    by = 105;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraFurnace.FUEL, button == 1);
    }
    bx = 225;
    by = 63;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraFurnace.OUTPUT, button == 1);
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
