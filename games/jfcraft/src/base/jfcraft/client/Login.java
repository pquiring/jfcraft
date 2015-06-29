package jfcraft.client;

/** Login, get Player and World
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.*;
import javaforce.gl.*;

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
    client.uid = -1;
    reqWorld = false;
    reqPlayer = false;
  }

  public void render(GL gl, int width, int height) {
    setMenuSize(512, 512);
    reset();

    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

    setOrtho(gl);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix

    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    if (t_back == null) {
      t_back = new Texture();
      t_back.load(gl, Assets.getImage("jfcraft/background").image);
    }

    if (o_back == null) {
      o_back = createMenu(gl);
    }

    reset();

    if (!Static.client.auth) {
      addText(250, 200, "Login...");
    } else {
      if (Static.client.world != null && Static.client.spawnAreaDonePercent != 100)
        addText(150, 200, "Generating spawn area...");
      else
        addText(150, 200, "Loading...");
    }

    //render stuff
    t_back.bind(gl);
    o_back.bindBuffers(gl);
    o_back.render(gl);

    renderText(gl);

    if (Static.client.auth) {
      if (Static.client.world == null && Static.client.uid == -1 && !reqWorld) {
        Static.client.clientTransport.getWorld();
        reqWorld = true;
        return;
      }
      if (Static.client.world != null && Static.client.spawnAreaDonePercent == 100 && Static.client.uid == -1 && !reqPlayer) {
        Static.client.clientTransport.getPlayer();
        reqPlayer = true;
        return;
      }
      if (Static.client.world != null && Static.client.uid != -1) {
        Static.initClientThread(Static.client.world, "Client render (EDT)", true, false);  //actually EDT
        Static.client.startTimers();
        LoadingChunks loading = (LoadingChunks)Static.screens.screens[Client.LOADINGCHUNKS];
        loading.setup(Static.client);
        Static.video.setScreen(loading);
        return;
      }
    }
  }

  public void resize(GL gl, int width, int height) {
    super.resize(gl, width, height);
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
