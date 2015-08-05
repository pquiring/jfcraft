package jfcraft.client;

/** Equipment Menu
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class InventoryMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private int mx, my;
  private final int gui_width = 350, gui_height = 330;  //size of menu
  private Player player;

  public InventoryMenu() {
    id = Client.INVENTORY;
  }

  public void setup() {
    setCursor(true);
    Static.client.clientTransport.enterInvMenu();
    player = (Player)Static.entities.entities[Entities.PLAYER];
    player.ang.y = 180.0f;  //face the "real" player
    player.armors = Static.client.player.armors;
  }

  public void render(int width, int height) {
    Static.game.render(width, height);
    setMenuSize(gui_width, gui_height);

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

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    setOrthoPlayer();
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
    player.render();
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    glDepthFunc(GL_ALWAYS);
    setOrtho();

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
    //render armor slots
    x = 16;
    y = 16 + 36;
    for(int a=0;a<4;a++) {
      Item item = Static.client.player.armors[a];
      if (item.id != 0) {
        renderItem(item,x,y);
      }
      y += 36;
    }

    //render crafting slots(4)
    Item item;
    x = 175;
    y = 50 + 36;
    for(int a=0;a<4;a++) {
//      Static.log("------------------------------------");
      if (a > 0 && a % 2 == 0) {
        x = 175;
        y += 36;
      }
      item = Static.client.craft[a];
      if (item.id != 0) {
        renderItem(item,x,y);
      }
      x += 36;
    }

    //render crafted item
    item = Static.client.crafted;
    if (item != null) {
      x = 287;
      y = 70 + 36;
      renderItem(item,x,y);
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
      case SWTVK.VK_E:
      case SWTVK.VK_ESCAPE:
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
    //check crafting area
    bx = 175;
    by = 50;
    for(byte a=0;a<4;a++) {
      if (a != 0 && a % 2 == 0) {
        bx = 175;
        by += 36;
      }
      if (x >= bx && x <= bx+36 && y >= by && y <= by+36) {
        Static.client.clickCraftlInput(a, button == 1);
      }
      bx += 36;
    }
    //check craft output
    bx = 287;
    by = 70;
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

  /** Sets an ortho matrix to display player in inventory menu */
  public void setOrthoPlayer() {
    glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL_FALSE, orthoPlayer.m);  //perspective matrix
    float x = 52;
    float y = 155;
    float w = 104;
    float h = 140;
    //left right bottom top near far
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    glViewport((int)(offsetX + x * Static.scale), (int)vpy, (int)(w * Static.scale), (int)(h * Static.scale));
  }
}
