package jfcraft.client;

/** Furnace Menu
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class FurnaceMenu extends RenderScreen {
  private Texture t_menu;
  private static RenderBuffers o_menu, o_flame, o_arrow;
  private int mx, my;
  private Slot slots[];

  public FurnaceMenu() {
    id = Client.FURNACE;
    gui_width = 350;
    gui_height = 330;
    slots = new Slot[4*9 + 1 + 1 + 1 + 1];  //slots(4*9), fuel(1), input(1), output(1), hand(1)
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

    //furnace slots
    slots[p] = new Slot();
    slots[p].x = 111;
    slots[p].y = 67;
    p++;

    slots[p] = new Slot();
    slots[p].x = 111;
    slots[p].y = 139;
    p++;

    slots[p] = new Slot();
    slots[p].x = 225;
    slots[p].y = 109;
    p++;
  }

  public void setup() {
    setCursor(true);
  }

  public void render(int width, int height) {
    Static.game.render( width, height);

    if (t_menu == null) {
      t_menu = Textures.getTexture( "gui/container/furnace", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
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
      o_flame = createMenu( 111,70 + 28 - flame_height, 352,0 + 28 - flame_height, 28,flame_height);
    } else {
      recreateMenu( o_flame, 111,70 + 28 - flame_height, 352,0 + 28 - flame_height, 28,flame_height);
    }

    arrow_width = timer == 0 ? 0 : ((200-timer) * 100 / 200) * 48 / 100;
    if (o_arrow == null) {
      o_arrow = createMenu( 160,67, 352,28, arrow_width,32);
    } else {
      recreateMenu( o_arrow, 160,67, 352,28, arrow_width,32);
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    glDepthFunc(GL_ALWAYS);

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    //flames and arrow
    t_menu.bind();
    if (flame_height > 0) {
      o_flame.bindBuffers();
      o_flame.render();
    }
    if (arrow_width > 0) {
      o_arrow.bindBuffers();
      o_arrow.render();
    }

    //inventory slots
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //furnace slots
    if (furnace != null) {
      slots[p++].item = furnace.items[ExtraFurnace.INPUT];
      slots[p++].item = furnace.items[ExtraFurnace.FUEL];
      slots[p++].item = furnace.items[ExtraFurnace.OUTPUT];
    } else {
      slots[p++].item = null;
      slots[p++].item = null;
      slots[p++].item = null;
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
      case GLVK.VK_E:
      case GLVK.VK_ESCAPE:
        Static.client.clientTransport.leaveMenu();
        leaveMenu();
        break;
    }
  }

  public void resize(int width, int height) {
    Static.game.resize( width, height);
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
