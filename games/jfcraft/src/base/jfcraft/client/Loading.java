package jfcraft.client;

/** Loading game progress ...
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class Loading extends RenderScreen {
  private Texture t_back;
  private RenderBuffers o_back;
  private int cnt;

  public Loading() {
    id = Client.LOADING;
  }

  public void setup() {
    new Thread() {public void run() {load();}}.start();
  }

  public void render(GL gl, int width, int height) {
    setMenuSize(512, 512);

    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
    gl.glDepthFunc(GL.GL_ALWAYS);
    setOrtho(gl);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    if (t_back == null) {
      t_back = Textures.getTexture2(gl, "title.png");
    }

    if (o_back == null) {
      o_back = createMenu(gl);
    }

    t_back.bind(gl);
    o_back.bindBuffers(gl);
    o_back.render(gl);

    if (done && cnt > 10) {
      load(gl);
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
    }
    cnt++;
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

  private boolean done = false;

  private void load() {
    Static.registerAll(true);
    done = true;
  }

  private void load(GL gl) {
    try {
      Static.blocks.initTexture(gl);
      Static.items.initTexture(gl);
      Static.entities.initStatic();
      Static.entities.initStatic(gl);
      RenderScreen.initStatic(gl);
      Static.screens.init(gl);
    } catch (Exception e) {
      Static.log(e);
    }
  }
}
