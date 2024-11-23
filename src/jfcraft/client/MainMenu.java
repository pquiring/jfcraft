package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.opengl.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class MainMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;

  public MainMenu() {
    id = Client.MAIN;
  }

  public boolean isMain() {
    return true;
  }

  private void reset() {
    Static.client = null;
    Static.server = null;
    Static.audio.stopMusic();
  }

  public void setup() {
    if (Static.audio.soundExists(Sounds.SOUND_INTRO)) {
      Static.audio.addSound(Sounds.SOUND_INTRO, 1, 50);
    } else {
      Static.audio.playMusic(Songs.FUR_ELISE, 10);
    }
  }

  public void init() {
    super.init();
    clearUI();
    addButton("Single Player", 56, 200, 400, new Runnable() {public void run() {
      //single player
      reset();
      if (!Static.blocks.valid) return;
      Static.video.setScreen(Static.screens.screens[Client.SINGLE]);
    }});
    addButton("Multi Player", 56, 300, 400, new Runnable() {public void run() {
      //multi player
      reset();
      if (!Static.blocks.valid) return;
      Static.video.setScreen(Static.screens.screens[Client.MULTI]);
    }});
    addButton("Options", 56, 400, 190, new Runnable() {public void run() {
      //options
//      Static.engine.setScene(?);
      return;
    }});
    addButton("Quit", 266, 400, 190, new Runnable() {public void run() {
      //quit
      System.exit(0);
    }});
  }

  public void render(int width, int height) {
    depth(false);

    if (t_menu == null) {
      t_menu = Textures.getTexture("jfcraft/mainmenu", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    //now render stuff
    clear(0, 0, width, height);

    setOrtho();
    setViewportMenu();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    renderButtons();

//    Item item = new Item(Blocks.GRASS);
//    renderItem(item, 50, 50);
  }

  public void resize(int width, int height) {
    super.resize(width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
  }

  public void mouseReleased(int x, int y, int button) {
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
