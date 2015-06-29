package jfcraft.data;

/**
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.opengl.*;

public class Screens {
  public static final int MAX_ID = 1024;
  public RenderScreen[] screens = new RenderScreen[MAX_ID];

  public void registerScreen(RenderScreen screen) {
    screens[screen.id] = screen;
  }

  public void registerDefault() {
    registerScreen(new Game());
    registerScreen(new ChatMenu());
    registerScreen(new ChestMenu());
    registerScreen(new ConfirmMenu());
    registerScreen(new CraftingMenu());
    registerScreen(new CreateWorldMenu());
    registerScreen(new DeadMenu());
    registerScreen(new FurnaceMenu());
    registerScreen(new HopperMenu());
    registerScreen(new InventoryMenu());
    registerScreen(new Loading());
    registerScreen(new LoadingChunks());
    registerScreen(new Login());
    registerScreen(new MainMenu());
    registerScreen(new MessageMenu());
    registerScreen(new MultiPlayerMenu());
    registerScreen(new PauseMenu());
    registerScreen(new SinglePlayerMenu());
    registerScreen(new WaitMenu());
    registerScreen(new DropperMenu());
    registerScreen(new SignMenu());
    registerScreen(new HorseMenu());
  }

  public void init(GL gl) {
    for(int a=0;a<MAX_ID;a++) {
      RenderScreen screen = screens[a];
      if (screen == null) continue;
      screen.init(gl);
    }
  }
}
