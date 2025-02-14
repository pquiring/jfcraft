package jfcraft.client;

/** Furnace Menu
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.extra.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class FurnaceMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private Sprite o_flame;
  private Sprite o_arrow;
  private int mx, my;
  private Slot slots[];

  public FurnaceMenu() {
    id = Client.FURNACE;
    gui_width = 350;
    gui_height = 330;
    sprite_width = 350;
    sprite_height = 330;
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
    Static.game.render( width, height);
    depth(false);

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
      o_flame = new Sprite("gui/sprites/container/furnace/lit_progress");
    }
    // 111,70 + 28 - flame_height, 352,0 + 28 - flame_height, 28,flame_height -- FIX ME !!!
    o_flame.recreate(111,70 + 28 - flame_height, 28,flame_height, 0,(28 - flame_height) * 100 / 28, 100f,flame_height * 100 / 28);

    arrow_width = timer == 0 ? 0 : ((200-timer) * 100 / 200) * 48 / 100;
    if (o_arrow == null) {
      o_arrow = new Sprite("gui/sprites/container/furnace/burn_progress");
    }
    o_arrow.recreate(160,67, arrow_width,32, 0,0, arrow_width * 100 / 48,100f);

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    //flames and arrow
    if (flame_height > 0) {
      o_flame.render();
    }
    if (arrow_width > 0) {
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
      case KeyCode.VK_E:
      case KeyCode.VK_ESCAPE:
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
    int p = 0;
    int bx;
    int by;
    for(byte a=9;a<4*9;a++) {
      bx = slots[p].x;
      by = slots[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
    }
    //check active slots
    for(byte a=0;a<9;a++) {
      bx = slots[p].x;
      by = slots[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
    }
    //check furnace
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraFurnace.INPUT, button == 1);
    }
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraFurnace.FUEL, button == 1);
    }
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
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
