package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.server.*;
import jfcraft.opengl .*;
import jfcraft.data.*;

public class CreateWorldMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private TextField world_name;
  private String initTxt;

  public CreateWorldMenu() {
    id = Client.CREATEWORLD;
  }

  public void setup() {
    initTxt = "New World";
  }

  public void init() {
    super.init();
    world_name = addTextField("New World", 5, 32, 512-10, Static.black4, 64, false, 1);
    addButton("Start", 20, 390, 226, new Runnable() {public void run() {
      createWorld();
    }});
    addButton("Cancel", 266, 390, 226, new Runnable() {public void run() {
      Static.video.setScreen(Static.screens.screens[Client.SINGLE]);
    }});
    setFocus(world_name);
  }

  public void render(int width, int height) {
    if (initTxt != null) {
      world_name.setText(initTxt);
      initTxt = null;
    }

    if (t_menu == null) {
      t_menu = Textures.getTexture("jfcraft/createmenu", 0);
    }

    if (o_menu == null) {
      o_menu = createMenu();
    }

    //now render stuff
    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    setOrtho();
    setViewportMenu();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    t_menu.bind();
    o_menu.bindBuffers();
    o_menu.render();

    renderButtons();
    renderFields();
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

  private void createWorld() {
    String name = world_name.getText();
    if (name.length() == 0) return;
    Server server = new Server();
    if (!server.createWorld(name)) {
      MessageMenu message = (MessageMenu)Static.screens.screens[Client.MESSAGE];
      message.setup("Error", server.errmsg, Static.screens.screens[Client.MAIN]);
      Static.video.setScreen(message);
      return;
    }
    LocalServerTransport serverTransport = new LocalServerTransport();
    LocalClientTransport clientTransport = new LocalClientTransport();
    Client clientClient = new Client(clientTransport);
    clientClient.isLocal = true;
    clientClient.name = Settings.current.player;
    clientClient.pass = Settings.current.pass;
    Client serverClient = new Client(serverTransport);
    clientTransport.init(serverTransport, clientClient);
    serverTransport.init(server, clientTransport, serverClient);
    serverClient.isLocal = true;
    server.addClient(serverTransport, serverClient);
    Login login = (Login)Static.screens.screens[Client.LOGIN];
    login.setup(clientClient);
    Static.video.setScreen(login);
  }
}
