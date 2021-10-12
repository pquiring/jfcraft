package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.net.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.server.*;
import jfcraft.opengl .*;
import jfcraft.data.*;

public class MultiPlayerMenu extends RenderScreen {
  private TextureMap t_menu;
  private RenderBuffers o_menu;
  private TextField serverAddress;

  private static String host = "127.0.0.1";

  public MultiPlayerMenu() {
    id = Client.MULTI;
  }

  public void init() {
    super.init();
    serverAddress = addTextField(host, 5, 32, 512-10, Static.black4, 64, false, 1);
    addButton("Start", 20, 390, 226, new Runnable() {public void run() {
      joinWorld();
    }});
    addButton("Cancel", 266, 390, 226, new Runnable() {public void run() {
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
    }});
  }

  public void setup() {
    setFocus(serverAddress);
  }

  public void render(int width, int height) {
    if (t_menu == null) {
      t_menu = Textures.getTexture( "jfcraft/multimenu", 0);
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

  private void joinWorld() {
    host = serverAddress.getText();
    if (host.length() == 0) return;
    NetworkClientTransport clientTransport = new NetworkClientTransport();
    Client client = new Client(clientTransport);
    client.isLocal = false;
    client.name = Settings.current.player;
    client.pass = Settings.current.pass;
    Socket socket = null;
    try {
      socket = new Socket(host, Settings.current.tcpPort);
    } catch (Exception e) {
      Static.log(e);
      MessageMenu msg = (MessageMenu)Static.screens.screens[Client.MESSAGE];
      msg.setup("Connection Failed", e.toString(), this);
      Static.video.setScreen(msg);
      return;
    }
    clientTransport.init(socket, client);
    if (Settings.current.client_voip) {
      client.startVoIP(host);
    }
    Login login = (Login)Static.screens.screens[Client.LOGIN];
    login.setup(client);
    Static.video.setScreen(login);
  }
}
