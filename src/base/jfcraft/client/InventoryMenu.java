package jfcraft.client;

/** Equipment Menu
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
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

public class InventoryMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private int mx, my;
  private Player player;
  private Slot slots[];

  public InventoryMenu() {
    id = Client.INVENTORY;
    gui_width = 350;
    gui_height = 330;
    slots = new Slot[4*9 + 4 + 1 + 4 + 1 + 1];  //slots(4*9), armor(4), shield(1), craft input(4), craft output(1), hand(1)
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
    //armor slots
    x = 16;
    y = 16 + 36;
    for(int a=0;a<4;a++) {
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      y += 36;
    }
    //shield
    x = 154;
    y = 124 + 36;
    slots[p] = new Slot();
    slots[p].x = x;
    slots[p].y = y;
    p++;

    //crafting slots(4)
    x = 196;
    y = 36 + 36;
    for(int a=0;a<4;a++) {
      if (a > 0 && a % 2 == 0) {
        x = 196;
        y += 36;
      }
      slots[p] = new Slot();
      slots[p].x = x;
      slots[p].y = y;
      p++;
      x += 36;
    }

    //crafted item
    x = 308;
    y = 56 + 36;
    slots[p] = new Slot();
    slots[p].x = x;
    slots[p].y = y;
    p++;

    //item in hand
    slots[p] = new Slot();
    slots[p].x = mx;
    slots[p].y = my;
    slots[p].renderName = true;
  }

  public void setup() {
    setCursor(true);
    Static.client.clientTransport.enterInvMenu();
    player = (Player)Static.entities.entities[Entities.PLAYER];
    player.ang.y = 180.0f;  //face the "real" player
    player.armors = Static.client.player.armors;
    player.items = Static.client.player.items;
  }

  public void render(int width, int height) {
    Static.game.render(width, height);

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/inventory", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
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

    setOrthoPlayer();
    setViewportPlayer();
    glDepthFunc(GL_LEQUAL);
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

    glDepthFunc(GL_ALWAYS);
    setOrtho();

    //inventory slots
    int p = 0;
    for(int a=9;a<4*9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }
    for(int a=0;a<9;a++) {
      slots[p++].item = Static.client.player.items[a];
    }

    //armor slots
    for(int a=0;a<4;a++) {
      slots[p++].item = Static.client.player.armors[a];
    }

    //shield
    slots[p++].item = Static.client.player.items[Player.shield_idx];

    //crafting slots(4)
    for(int a=0;a<4;a++) {
      slots[p++].item = Static.client.craft[a];
    }

    //crafted slot
    slots[p++].item = Static.client.crafted;

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
    //check armor
    bx = 15;
    by = 15;
    for(byte a=0;a<4;a++) {
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickArmor(a, button == 1);
      }
      by += 36;
    }
    //check shield
    bx = 154;
    by = 124;
    if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
      Static.client.clickShield();
    }
    //check crafting area (4)
    bx = 196;
    by = 36;
    for(byte a=0;a<4;a++) {
      if (a != 0 && a % 2 == 0) {
        bx = 196;
        by += 36;
      }
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickCraftlInput(a, button == 1);
      }
      bx += 36;
    }
    //check craft output
    bx = 308;
    by = 56;
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
