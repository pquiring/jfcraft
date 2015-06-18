package jfcraft.client;

/**
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import jfcraft.data.World;
import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.server.*;
import jfcraft.opengl.*;
import jfcraft.data.*;

public class SinglePlayerMenu extends RenderScreen {
  private Texture t_menu;
  private RenderBuffers o_menu;
  private ScrollBar sb;
  private int selectedWorld = -1;

  public SinglePlayerMenu() {
    id = Client.SINGLE;
  }

  public void init(GL gl) {
    super.init(gl);
    addButton(gl, "Play World", 20, 390, 226, new Runnable() {public void run() {
      playWorld();
    }});
    addButton(gl, "Create World", 266, 390, 226, new Runnable() {public void run() {
      Static.video.setScreen(Static.screens.screens[Client.CREATEWORLD]);
    }});
    addButton(gl, "Rename", 20, 450, 103, new Runnable() {public void run() {
      //TODO
    }});
    addButton(gl, "Delete", 143, 450, 103, new Runnable() {public void run() {
      deleteWorld();
    }});
    addButton(gl, "ReCreate", 266, 450, 103, new Runnable() {public void run() {
      //TODO
    }});
    addButton(gl, "Cancel", 389, 450, 103, new Runnable() {public void run() {
      Static.video.setScreen(Static.screens.screens[Client.MAIN]);
    }});
    listWorlds();
    sb = addScrollBar(gl, 452, 33, 10, 336, worlds.size() * 4 * fontSize);
  }

  public void setup() {
    selectedWorld = -1;
  }

  public void render(GL gl, int width, int height) {
    setMenuSize(512, 512);
    reset();

    if (t_menu == null) {
      t_menu = Textures.getTexture(gl, "jfcraft/singlemenu");
    }

    if (o_menu == null) {
      o_menu = createMenu(gl);
    }

    //now render stuff
    gl.glViewport(0, 0, width, height);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

    setOrtho(gl);

    gl.glUniformMatrix4fv(Static.uniformMatrixView, 1, GL.GL_FALSE, identity.m);  //view matrix
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, identity.m);  //model matrix

    t_menu.bind(gl);
    o_menu.bindBuffers(gl);
    o_menu.render(gl);

    //render worlds
    int pos = sb.getPosition() / (4 * fontSize);
    int x = 40;
    int y = 33 + fontSize;
    for(int a=pos;a<worlds.size();a++) {
      WorldInfo wi = worlds.get(a);
      if (selectedWorld == a) {
        //show highlight under this world
        addBar(x,y - fontSize,512-100,fontSize*4,Static.blue);
      }
      addText(x, y, wi.name);
      y += fontSize;
      addText(x, y, wi.shortFolder + wi.date);
      y += fontSize;
      addText(x, y, wi.extra);
      y += fontSize * 2;
    }

    renderScrollBars(gl);
    renderButtons(gl);
    renderBars(gl);
    renderText(gl);
  }

  public void resize(GL gl, int width, int height) {
    super.resize(gl, width, height);
  }

  public void mousePressed(int x, int y, int button) {
    super.mousePressed(x, y, button);
    if (x >= 40 && x <= 512-60 && y >= 33 && y <= 336) {
      y -= 33;
      y /= (fontSize * 4);
      selectedWorld = (sb.getPosition() / (4 * fontSize)) + y;
      if (selectedWorld >= worlds.size()) {
        selectedWorld = -1;
      }
    }
  }

  public void mouseReleased(int x, int y, int button) {
  }

  public void mouseMoved(int x, int y, int button) {
    if (button !=0 ) {
      mousePressed(x,y,button);
    }
  }

  public void mouseWheel(int delta) {
  }

  public static class WorldInfo {
    public String name;
    public String folder;
    public String shortFolder;
    public long ms;
    public String date;
    public String extra;
  }

  public ArrayList<WorldInfo> worlds = new ArrayList<WorldInfo>();

  private String long2Date(long time) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(time);
    int m = cal.get(Calendar.MONTH) + 1;
    int d = cal.get(Calendar.DAY_OF_MONTH);
    int y = cal.get(Calendar.YEAR);
    int h = cal.get(Calendar.HOUR) + 1;
    int u = cal.get(Calendar.MINUTE);
    String ampm = cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
    return String.format("(%d/%d/%d %d:%02d%s)", m,d,y,h,u,ampm);
  }

  public void listWorlds() {
    selectedWorld = -1;
    worlds.clear();
    File files[] = new File(Static.getWorldsPath()).listFiles();
    if (files == null) return;
    File world_dat;
    for(int a=0;a<files.length;a++) {
      File file = files[a];
      if (!file.isDirectory()) continue;
      world_dat = new File(file, "world.dat");
      if (!world_dat.exists()) continue;
      World world;
      try {
        world = World.load(file.getAbsolutePath() + "/world.dat", true);
      } catch (Exception e) {
        Static.log(e);
        continue;
      }
      if (world == null) continue;
      WorldInfo wi = new WorldInfo();
      wi.name = world.name;
      wi.folder = file.getAbsolutePath();
      int idx = wi.folder.lastIndexOf(File.separatorChar);
      wi.shortFolder = wi.folder.substring(idx+1);
      wi.ms = world_dat.lastModified();
      wi.date = long2Date(wi.ms);
      wi.extra = "";
      worlds.add(wi);
    }
    worlds.sort(new Comparator<WorldInfo>() {
      public int compare(WorldInfo o1, WorldInfo o2) {
        if (o1.ms > o2.ms) return -1;
        if (o1.ms < o2.ms) return 1;
        return 0;
      }
    });
  }

  public void playWorld() {
    if (selectedWorld == -1) return;
    WorldInfo wi = worlds.get(selectedWorld);
    LocalServerTransport serverTransport = new LocalServerTransport();
    LocalClientTransport clientTransport = new LocalClientTransport();
    Server server = new Server();
    server.startWorld(wi.folder);
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

  private void deleteFolder(File folder) {
    File files[] = folder.listFiles();
    if (files != null) {
      for(int a=0;a<files.length;a++) {
        if (files[a].isDirectory()) {
          deleteFolder(files[a]);
        } else {
          files[a].delete();
        }
      }
    }
    folder.delete();
  }

  public void deleteWorld() {
    if (selectedWorld == -1) return;
    WorldInfo wi = worlds.get(selectedWorld);
    ConfirmMenu confirm = (ConfirmMenu)Static.screens.screens[Client.CONFIRM];
    confirm.setup("Delete World:" + wi.name, "Are you sure?", "Delete",
      new Runnable() {public void run() {
        WorldInfo wi = worlds.get(selectedWorld);
        File world = new File(wi.folder);
        deleteFolder(world);
        listWorlds();
      }}, this
    );
    Static.video.setScreen(confirm);
  }
}
