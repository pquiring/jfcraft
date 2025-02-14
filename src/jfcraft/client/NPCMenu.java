package jfcraft.client;

/** NPC Menu
 *
 * Uses the horse UI container
 *
 * Text is rendered where horse chest normally is placed.
 *
 * NPC can have a backpack to give items.
 *
 * @author pquiring
 *
 * Created : Feb 14, 2025
 */

import javaforce.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.extra.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class NPCMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private Sprite o_backpack_slot;
  private int mx, my;
  private Slot slots[];

  public NPCMenu() {
    id = Client.NPC;
    gui_width = 350;
    gui_height = 330;
    sprite_width = gui_width;
    sprite_height = gui_height;
    slots = new Slot[4*9 + 1 + 1];  //slots(4*9), backpack(1), hand(1)
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

    //backpack slot
    slots[p] = new Slot();
    slots[p].x = 16;
    slots[p].y = 36 + 36;
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
      t_menu = Textures.getTexture("gui/container/horse", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    if (o_backpack_slot == null) {
      o_backpack_slot = new Sprite("gui/sprites/container/horse/saddle_slot", 14,36, 36,36);
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    //inventory blocks
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //backpack
    slots[p++].item = null;

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

    //check backpack slot
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickContainer((byte)ExtraBackpack.BACKPACK, button == 1);
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
