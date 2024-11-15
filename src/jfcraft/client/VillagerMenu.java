package jfcraft.client;

/** Villager Menu
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
import jfcraft.entity.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class VillagerMenu extends RenderScreen {
  private TextureMap t_menu;
  private static RenderBuffers o_menu;
  private int mx, my;
  private Slot slots[];
  private Slot trade_slots[];
  private int trade_slot;
  private int trade_index;

  public VillagerMenu() {
    id = Client.VILLAGER;
    gui_width_max = 1024;
    gui_height_max = 512;
    gui_width = 550;
    gui_height = 330;
    slots = new Slot[4*9 + 1 + 1 + 1 + 1];  //slots(4*9), give(1), give(1), output(1), hand(1)
    //inventory blocks
    int p = 0;
    int x = 216, y = (int)(gui_height - 131);
    for(int a=9;a<4*9;a++) {
      if (a > 9 && a % 9 == 0) {
        x = 216;
        y += 36;
      }
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }
    //active slots
    x = 216;
    y = (int)(gui_height - 11);
    for(int a=0;a<9;a++) {
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }

    //trade slots
    slots[p] = new Slot();
    slots[p].x = 272;
    slots[p].y = 74 + 36;
    p++;

    slots[p] = new Slot();
    slots[p].x = 324;
    slots[p].y = 74 + 36;
    p++;

    trade_slot = p;
    slots[p] = new Slot();
    slots[p].x = 440;
    slots[p].y = 74 + 36;
    p++;

    //item in hand
    slots[p] = new Slot();
    slots[p].x = mx;
    slots[p].y = my;
    slots[p].renderName = true;
  }

  public void setup() {
    setCursor(true);
    clearUI();
    trade_slots = null;
    trade_index = -1;
    Item[][] offers = Static.client.villager.getOfferings();
    Slot slot;
    if (offers != null) {
      int len = offers.length;
      trade_slots = new Slot[len * 3];
      int p = 0;
      int x = 10;
      int y = 36;
      int width = 177;
      for(int a=0;a<len;a++) {
        Item trade = offers[a][2];
        int trade_index = a;
        addButton("", x, y, width, new Runnable() {public void run() {
          //change item shown
          slots[trade_slot].item = trade;
          //tell server trade index
          Static.client.clientTransport.craftSelect((byte)trade_index);
          //update client index
          Static.client.villager.trade_index = trade_index;
        }});
        y += 36;
        //setup slots to render items
        slot = trade_slots[p++] = new Slot();
        slot.x = x;
        slot.y = y;
        slot.item = offers[a][0];
        slot = trade_slots[p++] = new Slot();
        slot.x = x + 36;
        slot.y = y;
        slot.item = offers[a][1];
        slot = trade_slots[p++] = new Slot();
        slot.x = x + 141;
        slot.y = y;
        slot.item = offers[a][2];
        y += 4;  //buttons are 40 px high
      }
    }
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);

    if (t_menu == null) {
      t_menu = Textures.getTexture( "gui/container/villager", 0);
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

    if (Static.client.villager == null) {
      Static.client.clientTransport.leaveMenu();
      return;
    }

    //inventory slots
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //give, give, output
    slots[p++].item = Static.client.craft[0];
    slots[p++].item = Static.client.craft[1];
    slots[p++].item = Static.client.crafted;

    //item in hand
    slots[p].item = Static.client.hand;
    slots[p].x = mx;
    slots[p].y = my;

    renderItems(slots);

    renderButtons();
    
    if (trade_slots != null) {
      renderItems(trade_slots);
    }
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
    int bx = 0;
    int by = 0;
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
    //check villager trade options
    //check villager slots
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickCraftInput((byte)0, button == 1);
    }
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickCraftInput((byte)1, button == 1);
    }
    bx = slots[p].x;
    by = slots[p].y - 36;
    p++;
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
