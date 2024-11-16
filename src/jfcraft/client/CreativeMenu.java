package jfcraft.client;

/** Creative Inventory Menu
 *
 * @author pquiring
 *
 * Created : Nov 15, 2024
 */

import javaforce.*;
import javaforce.ui.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class CreativeMenu extends RenderScreen {
  private TextureMap t_menu_inv;
  private TextureMap t_menu_items;
  private TextureMap t_menu_search;
  private RenderBuffers o_menu;
  private Sprite tab_1;
  private Sprite tab_7;
  private int mx, my;
  private Player player;
  private int tab = 1;  //or 7 for now

  private Slot slots_tab_1[];  //tab 1

  private Slot slots_tab_7[];  //tab 7
  private TextField search;
  private ScrollBar scroll;

  private static final int[] armor_x = {108, 108, 216, 216};
  private static final int[] armor_y = {12,  66,  12,  66};

  public CreativeMenu() {
    id = Client.CREATIVE;
    gui_width = 390;
    gui_height = 270;
    sprite_width = 390;
    sprite_height = tab_height;
    tab = 1;
    init_tab_1();
    init_tab_7();
  }

  private void init_tab_1() {
    slots_tab_1 = new Slot[4*9 + 4 + 1 + 1];  //slots(4*9), armor(4), shield(1), hand(1)
    //inventory blocks
    int p = 0;
    int x = 18, y = 108 + 36;
    for(int a=9;a<4*9;a++) {
      if (a > 9 && a % 9 == 0) {
        x = 18;
        y += 36;
      }
      slots_tab_1[p] = new Slot();
      slots_tab_1[p].x = x;
      slots_tab_1[p].y = y;
      p++;
      x += 36;
    }
    //active slots
    x = 18;
    y = 226 + 36;
    for(int a=0;a<9;a++) {
      slots_tab_1[p] = new Slot();
      slots_tab_1[p].x = x;
      slots_tab_1[p].y = y;
      p++;
      x += 36;
    }
    //armor slots
    for(int a=0;a<4;a++) {
      x = armor_x[a];
      y = armor_y[a] + 36;
      slots_tab_1[p] = new Slot();
      slots_tab_1[p].x = x;
      slots_tab_1[p].y = y;
      p++;
    }
    //shield
    x = 70;
    y = 40 + 36;
    slots_tab_1[p] = new Slot();
    slots_tab_1[p].x = x;
    slots_tab_1[p].y = y;
    p++;

    //item in hand
    slots_tab_1[p] = new Slot();
    slots_tab_1[p].x = mx;
    slots_tab_1[p].y = my;
    slots_tab_1[p].renderName = true;
  }

  private void init_tab_7() {
    //add text field
    search = addTextField("", 162, 10, 176, null, 14, false, 1);

    //add scroll bar
    int rows = (Static.items.itemCount + Static.blocks.blockCount + 8) / 9;
    int size = rows * 36;
    scroll = addScrollBar(350, 36, 24, 220, size);

    slots_tab_7 = new Slot[6*9 + 1];  //slots(6*9), hand(1)
    //inventory blocks
    int p = 0;
    int x = 18, y = 36 + 36;
    for(int a=0;a<5*9;a++) {
      if (a > 0 && a % 9 == 0) {
        x = 18;
        y += 36;
      }
      slots_tab_7[p] = new Slot();
      slots_tab_7[p].x = x;
      slots_tab_7[p].y = y;
      p++;
      x += 36;
    }
    //active slots
    x = 18;
    y = 226 + 36;
    for(int a=0;a<9;a++) {
      slots_tab_7[p] = new Slot();
      slots_tab_7[p].x = x;
      slots_tab_7[p].y = y;
      p++;
      x += 36;
    }

    //item in hand
    slots_tab_7[p] = new Slot();
    slots_tab_7[p].x = mx;
    slots_tab_7[p].y = my;
    slots_tab_7[p].renderName = true;
  }

  public void setup() {
    setCursor(true);
    Static.client.clientTransport.enterMenu(Client.CREATIVE);
    player = (Player)Static.entities.entities[Entities.PLAYER];
    player.ang.y = 180.0f;  //face the "real" player
    player.armors = Static.client.player.armors;
    player.items = Static.client.player.items;
    Static.client.crafted = null;
    tab = 1;
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);

    if (t_menu_inv == null) {
      t_menu_inv = Textures.getTexture("gui/container/creative_inventory/tab_inventory", 0);
    }

    if (t_menu_items == null) {
      t_menu_items = Textures.getTexture("gui/container/creative_inventory/tab_items", 0);
    }

    if (t_menu_search == null) {
      t_menu_search = Textures.getTexture("gui/container/creative_inventory/tab_item_search", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    if (tab_1 == null) {
      tab_1 = new Sprite("gui/sprites/container/creative_inventory/tab_top_selected_1", 0,0, tab_width,tab_height);
    }

    if (tab_7 == null) {
      tab_7 = new Sprite("gui/sprites/container/creative_inventory/tab_top_selected_7", (int)gui_width - tab_width,0, tab_width,tab_height);
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();

    setViewportMenu();

    switch (tab) {
      case 1: t_menu_inv.bind(); break;
      case 7: t_menu_search.bind(); break;
    }
    o_menu.bindBuffers();
    o_menu.render();

    setViewportTabTop();

    tab_1.render();
    tab_7.render();

    setViewportMenu();

    switch (tab) {
      case 1:
        renderSlots_tab_1();
        break;
      case 7:
        renderFields();
        renderScrollBars();
        renderSlots_tab_7();
        break;
    }
  }

  void renderPlayer() {
    setOrthoPlayer();
    setViewportPlayer();
    depth(true);
    glClear(GL_DEPTH_BUFFER_BIT);
    player.bindTexture();
    //rotate player to point head towards mouse coords
    float ey = my - 52;
    ey /= 2.0f;
    if (ey < -45.0f) {
      ey = -45.0f;
    } else if (ey > 45.0f) {
      ey = 45.0f;
    }
    player.ang.x = ey;
    float ex = mx - 104;
    ex /= 2.0f;
    if (ex < -45.0f) {
      ex = -45.0f;
    } else if (ex > 45.0f) {
      ex = 45.0f;
    }
    player.ang.y = 180.0f - ex;
    player.activeSlot = Static.client.player.activeSlot;
    player.render();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    setOrtho();
  }

  private void renderSlots_tab_1() {
    //inventory slots
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots_tab_1[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots_tab_1[p++].item = Static.client.player.items[a];
    }

    //armor slots
    for(int a=0;a<4;a++) {
      slots_tab_1[p++].item = Static.client.player.armors[a];
    }

    //shield
    slots_tab_1[p++].item = Static.client.player.items[Player.shield_idx];

    //item in hand
    slots_tab_1[p].item = Static.client.hand;
    slots_tab_1[p].x = mx;
    slots_tab_1[p].y = my;

    renderItems(slots_tab_1);
  }

  private void renderSlots_tab_7() {
    //inventory slots
    int p = 0;
    int ii = scroll.getPosition();
    String txt = search.getText();
    boolean doSearch = txt.length() > 0;
    //available items (5 rows)
    for(int a=0;a<5*9;a++) {
      Item item = null;
      while (ii < Static.items.items.length) {
        ItemBase itembase = Static.items.items[ii++];
        if (itembase != null) {
          if (doSearch) {
            String name = itembase.getName();
            if (!name.contains(txt)) continue;
          }
          item = itembase.toItem(1);
          break;
        }
      }
      slots_tab_7[p++].item = item;
    }

    //active slot
    for(int a=0;a<9;a++) {
      slots_tab_7[p++].item = Static.client.player.items[a];
    }

    //item in hand
    slots_tab_7[p].item = Static.client.hand;
    slots_tab_7[p].x = mx;
    slots_tab_7[p].y = my;

    renderItems(slots_tab_7);
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
    if (x < 0 || x > gui_width) return;
    if (y < 0) {
      //clicked top tab
      if (x > tab_width * 5) {
        x -= 26;  //mind the gap
      }
      int new_tab = 1 + (x / tab_width);
      switch (new_tab) {
        case 1:
        case 7:
          tab = new_tab;
          break;
      }
    } else if (y > gui_height) {
      //clicked bottom tab
      if (x > tab_width * 5) {
        x -= 26;  //mind the gap
      }
      int new_tab = 8 + (x / tab_width);
      //not in use yet
    } else {
      switch (tab) {
        case 1: mousePressed_tab_1(x,y,button); break;
        case 7: mousePressed_tab_7(x,y,button); break;
      }
    }
  }

  public void mousePressed_tab_1(int x, int y, int button) {
    //check inventory
    int p = 0;
    int bx;
    int by;
    for(byte a=9;a<4*9;a++) {
      bx = slots_tab_1[p].x;
      by = slots_tab_1[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
    }
    //check active slots
    for(byte a=0;a<9;a++) {
      bx = slots_tab_1[p].x;
      by = slots_tab_1[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
      }
    }
    //check armor
    for(byte a=0;a<4;a++) {
      bx = slots_tab_1[p].x;
      by = slots_tab_1[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickArmor(a, button == 1);
      }
    }
    //check shield
    bx = slots_tab_1[p].x;
    by = slots_tab_1[p].y - 36;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickShield();
    }
    //check trash
    bx = 346;
    by = 224;
    p++;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickTrash();
    }
  }

  public void mousePressed_tab_7(int x, int y, int button) {
    //check inventory
    int p = 0;
    int bx;
    int by;
    for(byte a=0;a<5*9;a++) {
      Item item = slots_tab_7[p].item;
      bx = slots_tab_7[p].x;
      by = slots_tab_7[p].y - 36;
      p++;
      if (item == null) continue;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickCreative(item.id, button == 1);
      }
    }
    //check active slots
    for(byte a=0;a<9;a++) {
      bx = slots_tab_7[p].x;
      by = slots_tab_7[p].y - 36;
      p++;
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickInventory(a, button == 1);
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
