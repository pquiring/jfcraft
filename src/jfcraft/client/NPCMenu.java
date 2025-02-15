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
import jfcraft.entity.*;
import jfcraft.extra.*;
import jfcraft.block.*;
import jfcraft.item.*;

public class NPCMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private Sprite o_backpack_slot;
  private Sprite o_down_arrow;
  private Sprite[] o_choices;
  private int mx, my;
  private Slot slots[];
  private HumaniodBase npc;

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
    npc = (NPC)Static.entities.entities[Entities.NPC];
    if (npc != null) {
      //TODO : setup npc
    }
  }

  private static final int eyes_x = 104;
  private static final int eyes_y = 85;

  public void render(int width, int height) {
    Static.game.render(width, height);
    depth(false);

    if (t_menu == null) {
      t_menu = Textures.getTexture("gui/container/horse", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
//      o_menu = new Sprite("gui/container/horse", 0,0, (int)gui_width,(int)gui_height);
    }

    if (o_backpack_slot == null) {
      o_backpack_slot = new Sprite("gui/sprites/container/slot", 14,36, 36,36);
    }

    if (o_down_arrow == null) {
      o_down_arrow = new Sprite("gui/sprites/arrow_down", 305,139, 18,20);
    }

    if (o_choices == null) {
      o_choices = new Sprite[6];
      int x = 160;
      int y = 36;
      for(int a=0;a<6;a++) {
        o_choices[a] = new Sprite("gui/sprites/arrow_right", x,y, 20,18);
        y += fontSize + 3;
      }
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    renderShade();

    setOrtho();
    setViewportMenu();

    //render menu
    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    //render NPC
    if (npc != null) {
      setOrthoPlayer();
      setViewportPlayer(52,36+104, 104,104);

      depth(true);

      glClear(GL_DEPTH_BUFFER_BIT);
      npc.bindTexture();
      //rotate player to point head towards mouse coords
      float ey = my - eyes_y;
      ey /= 2.0f;
      if (ey < -45.0f) {
        ey = -45.0f;
      } else if (ey > 45.0f) {
        ey = 45.0f;
      }
      npc.ang.x = ey;
      float ex = mx - eyes_x;
      ex /= 2.0f;
      if (ex < -45.0f) {
        ex = -45.0f;
      } else if (ex > 45.0f) {
        ex = 45.0f;
      }
      npc.ang.y = 180.0f - ex;
      npc.activeSlot = 0;
      npc.render();

      glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
      glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

      setOrtho();
      setViewportMenu();
    }

    Page page = Static.client.page;
    if (page != null) {
      //render page (text)
      t_text.bind();
      int tx = 160 + 3;
      int ty = 36 + fontSize + 3;
      for(String text : page.text) {
        int ox = 0;
        if (text.startsWith("#")) {
          //#choice(actions)...
          int idx = text.indexOf(')');
          if (idx != -1) {
            text = text.substring(idx + 1).trim();
          } else {
            text = "";
          }
          ox = 24;
        }
        renderText(tx + ox, ty, text);
        ty += fontSize + 3;
      }
      //render arrow on choices or down arrow
      setOrtho();
      setViewportMenu();
      boolean blink = (Static.client.world.time & 2) == 0;
      int cnt = page.choicesCount();
      if (cnt == 0) {
        //render down arrow (blinking)
        if (blink) {
          o_down_arrow.render();
        }
      } else {
        //render choice arrow (blinking)
        if (blink) {
          o_choices[Static.client.choiceIndex].render();
        }
      }
      setOrtho();
      setViewportMenu();
    }

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
    if (Static.client.page == null) return;
    int cnt = Static.client.page.choicesCount();
    int pc = Static.client.page.count();
    byte pc_1 = (byte)(pc - 1);
    boolean choices[] = Static.client.page.getChoices();
    switch (vk) {
      case KeyCode.VK_ESCAPE:
        Static.client.clientTransport.leaveMenu();
        leaveMenu();
        break;
      case KeyCode.VK_E:
      case KeyCode.VK_D:
        if (cnt == 0) {
          Static.client.clientTransport.sendDialogAction(Page.ACTION_NEXT);
        } else {
          Static.client.clientTransport.sendDialogAction(Static.client.choiceIndex);
        }
        break;
      case KeyCode.VK_W:
      case KeyCode.VK_UP:
        if (Static.client.choiceIndex == -1) break;
        do {
          if (Static.client.choiceIndex == 0) {
            Static.client.choiceIndex = pc_1;
          } else {
            Static.client.choiceIndex--;
          }
        } while (!choices[Static.client.choiceIndex]);
        break;
      case KeyCode.VK_S:
      case KeyCode.VK_DOWN:
        if (Static.client.choiceIndex == -1) break;
        do {
          if (Static.client.choiceIndex == pc_1) {
            Static.client.choiceIndex = 0;
          } else {
            Static.client.choiceIndex++;
          }
        } while (!choices[Static.client.choiceIndex]);
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
