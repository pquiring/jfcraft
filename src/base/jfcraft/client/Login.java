package jfcraft.client;

/** Login, get Player and World
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class Login extends RenderScreen {
  private Texture t_back;
  private RenderBuffers o_back;
  private boolean reqWorld = false;
  private boolean reqPlayer = false;

  public Login() {
    id = Client.LOGIN;
  }

  public void setup(Client client) {
    //assume already logged in
    Static.client = client;
    Static.client = client;
    client.clientTransport.start();
    client.clientTransport.login();
    client.player = null;
    reqWorld = false;
    reqPlayer = false;
    Static.dims.initEnvironments();
  }

  public void render(int width, int height) {

    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    setOrtho();
    setViewportMenu();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    if (t_back == null) {
      t_back = new Texture();
      t_back.load(Assets.getImage("jfcraft/background").image);
    }

    if (o_back == null) {
      o_back = createMenu();
    }

    t_back.bind();
    o_back.bindBuffers();
    o_back.render();

    if (!Static.client.auth) {
      renderText(250, 200, "Login...");
    } else {
      if (Static.client.world != null && Static.client.spawnAreaDonePercent != 100)
        renderText(150, 200, "Generating spawn area...");
      else
        renderText(150, 200, "Loading...");
    }

    if (Static.client.auth) {
      if (Static.client.world == null && !reqWorld) {
        Static.client.clientTransport.getWorld();
        reqWorld = true;
        return;
      }
      if (Static.client.world != null && Static.client.spawnAreaDonePercent == 100 && !reqPlayer) {
        Static.client.clientTransport.getPlayer();
        reqPlayer = true;
        return;
      }
      if (Static.client.world != null && Static.client.player != null) {
        Static.initClientThread("Client render (EDT)", true, false);  //actually EDT
        Static.client.startTimers();
        LoadingChunks loading = (LoadingChunks)Static.screens.screens[Client.LOADINGCHUNKS];
        loading.setup(Static.client);
        Static.video.setScreen(loading);
        return;
      }
    }
  }

  public void resize(int width, int height) {
    super.resize(width, height);
  }

  public void mousePressed(int x, int y, int button) {
  }

  public void mouseReleased(int x, int y, int button) {
  }

  public void mouseMoved(int x, int y, int button) {
  }

  public void mouseWheel(int delta) {
  }
}
