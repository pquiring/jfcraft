package jfcraft.client;

/** Equipment Menu
 *
 * @author pquiring
 *
 * Created : Apr 21, 2014
 */

import java.awt.event.KeyEvent;

import javaforce.*;
import javaforce.gl.*;

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
  private GLMatrix ortho = new GLMatrix();

  public InventoryMenu() {
    id = Client.INVENTORY;
  }

  public void setup() {
    setCursor();
    Static.client.clientTransport.enterInvMenu();
    player = (Player)Static.entities.entities[Entities.PLAYER];
    player.ang.y = 180.0f;  //face the "real" player
  }

  public void render(GL gl, int width, int height) {
    Static.game.render(gl, width, height);
    setMenuSize(gui_width, gui_height);

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "gui/container/inventory", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    renderShade(gl);

    gl.glDepthFunc(GL.GL_ALWAYS);

    setOrtho(gl);

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    setOrthoPlayer(gl);
    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
    player.bindTexture(gl);
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
    player.render(gl);
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    gl.glDepthFunc(GL.GL_ALWAYS);
    setOrtho(gl);

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
        renderItem(gl,item,x,y);
      }
      x += 36;
    }

    //render crafted item
    item = Static.client.crafted;
    if (item != null) {
      x = 287;
      y = 70 + 36;
      renderItem(gl,item,x,y);
    }

    //render item in hand
    item = Static.client.hand;
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
  public void setOrthoPlayer(GL gl) {
    float x = 52;
    float y = 155;
    float w = 104;
    float h = 140;
    //left right bottom top near far
    ortho.ortho(-1, 1, 0, 2, -1, 1);
    float offsetX = (Static.width - (gui_width * Static.scale)) / 2.0f;
    float offsetY = (Static.height - (gui_height * Static.scale)) / 2.0f;
    gl.glUniformMatrix4fv(Static.uniformMatrixPerspective, 1, GL.GL_FALSE, ortho.m);  //perspective matrix
    float vpy = 0;
    switch (gui_position) {
      case TOP: vpy = (int)(Static.height - gui_height) - y; break;
      case CENTER: vpy = (int)(offsetY + (gui_height - y) * Static.scale); break;
      case BOTTOM: vpy = (int)((gui_height - y) * Static.scale); break;
    }
    gl.glViewport((int)(offsetX + x * Static.scale), (int)vpy, (int)(w * Static.scale), (int)(h * Static.scale));
  }

}
