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
import jfcraft.extra.*;
import jfcraft.entity.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class HorseMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private Sprite o_chest_slots;
  private Sprite o_armor_slot;
  private Sprite o_saddle_slot;
  private int mx, my;
  private Slot slots[];
  private Horse horse;

  public HorseMenu() {
    id = Client.HORSE;
    gui_width = 350;
    gui_height = 330;
    sprite_width = gui_width;
    sprite_height = gui_height;
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

    //saddle slot
    slots[p] = new Slot();
    slots[p].x = 16;
    slots[p].y = 36 + 36;
    p++;

    //armor slot
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
    depth(false);

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/horse", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    if (o_chest_slots == null) {
      o_chest_slots = new Sprite("gui/sprites/container/horse/chest_slots", 160,36, 180,108);
    }

    if (o_saddle_slot == null) {
      o_saddle_slot = new Sprite("gui/sprites/container/horse/saddle_slot", 14,36, 36,36);
    }

    if (o_armor_slot == null) {
      o_armor_slot = new Sprite("gui/sprites/container/horse/armor_slot", 14,70, 36,36);
    }

    ExtraHorse container = (ExtraHorse)Static.client.container;

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();
    if (container != null) {
      if (container.items.length > 2) {
        o_chest_slots.render();
      }
      if (container.items[1].id != Blocks.OBSIDIAN) {
        //mule & donkey have OBSIDIAN where the armor would be placed
        o_armor_slot.render();
      }
      o_saddle_slot.render();
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

    //check horse slots
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraHorse.SADDLE, button == 1);
    }

    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraHorse.ARMOR, button == 1);
    }

    //check horse chest
    ExtraHorse container = (ExtraHorse)Static.client.container;
    if (container != null && container.items.length > 2) {
      byte idx = 2;
      for(int a=0;a<15;a++) {
        bx = slots[p].x;
        by = slots[p].y - 36;
        p++;
        if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
          Static.client.clickContainer(idx, button == 1);
        }
        idx++;
      }
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
