package jfcraft.client;

/** Loading chunks
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
import jfcraft.server.*;

public class LoadingChunks extends RenderScreen {
  private Texture t_back;
  private RenderBuffers o_back;
  private Client client;

  public LoadingChunks() {
    id = Client.LOADINGCHUNKS;
  }

  public void setup(Client client) {
    this.client = client;
    client.spawnAreaChunksDone = 0;
    client.spawnAreaChunksTodo = 0;
    client.loadedSpawnArea = false;
    Static.inGame = false;
  }

  public void render(int width, int height) {
    setMenuSize(512, 512);
    reset();
    //vertex and fragment shaders are already loaded

    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    setOrtho();

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, identity.m);  //view matrix

    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, identity.m);  //model matrix

    if (t_back == null) {
      t_back = new Texture();
      t_back.load(Assets.getImage("jfcraft/background").image);
    }

    if (o_back == null) {
      o_back = createMenu();
    }

    client.chunkBuilder.signal();
    client.chunkBuilder.process();
    client.chunkCopier.signal();
    client.chunkCopier.process();

    reset();

    int percent = 0;
    if (client.spawnAreaChunksTodo > 0) {
      percent = client.spawnAreaChunksDone * 100 / client.spawnAreaChunksTodo;
    }
    addText(150, 200, "Loading chunks " + percent + "%");

    //render stuff
    t_back.bind();
    o_back.bindBuffers();
    o_back.render();

    renderText();

    if (client.loadedSpawnArea && client.player != null) {
      client.clientTransport.online();
      client.player.offline = false;
      Static.video.setScreen(Static.screens.screens[Client.GAME]);
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
