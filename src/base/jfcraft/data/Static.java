package jfcraft.data;

/** Static placeholder for some common objects.
 *
 * @author pquiring
 *
 * Created : Mar 28, 2014
 */

import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.audio.*;
import jfcraft.client.*;
import jfcraft.opengl.*;
import jfcraft.plugin.PluginLoader;
import jfcraft.server.*;

public class Static {
  public static String version = "0.22";

//  public static boolean debug;

  public static RenderEngine video;
  public static AudioEngine audio;
  public static Object renderLock = new Object();  //TODO : eliminate this
  public static Object clientMoveLock = new Object();  //TODO : eliminate this
  public static Blocks blocks = new Blocks();
  public static Items items = new Items();
  public static Recipes recipes = new Recipes();
  public static Entities entities = new Entities();  //registered entities
  public static Packets packets = new Packets();
  public static Extras extras = new Extras();
  public static Screens screens = new Screens();
  public static Dims dims = new Dims();
  public static Client client;  //playing client (client side only)
  public static Server server;  //current server (server side only)
  public static Game game;  //main screen for rendering game
  public static int fps;  //current FPS
  public static int tick;  //last server tick duration (ms)
  public static boolean spawn = true;  //spawn entities (monsters)
  public static GLMatrix identity = new GLMatrix();
  public static XYZ camera_pos = new XYZ();  //camera position
  public static XYZ camera_ang = new XYZ();  //camera angle

  public static boolean enablePrebuildItems = false;  //not ready yet (see RenderScreen.renderItemFast())

  public static boolean debugRotate = false;
  public static boolean debugProfile = false;
  public static boolean debugCaves = false;
  public static boolean debugBug = false;
  public static boolean debugDisableRandomTicks = false;
  public static boolean debugPurgeEntities = false;
  public static boolean debugDisableFog = true;

  public static String debugMsg;

  public static boolean doSteps = false;

  public static ServerInterface iface;

  public static boolean mlogs;  //create multi logs (command line option)

  private static final int getLogID() {
    return Thread.currentThread().hashCode();
  }

  public static void log(String msg) {
    if (iface != null) {
      iface.log(msg);
    }
    JFLog.log(getLogID(), msg);
  }

  public static void log(Throwable t) {
    JFLog.log(getLogID(), t);
  }

  public static void logTrace(String msg) {
    JFLog.logTrace(getLogID(), msg);
  }

  private static void initLog(String name, boolean stdout) {
    String path = getBasePath() + "/logs";
    new File(path).mkdirs();
    if (mlogs) {
      path += "/" + JF.getPID();
      new File(path).mkdirs();
    }
    path += "/" + name.replaceAll(" ", "_") + ".log";
    JFLog.init(getLogID(), path, stdout);
  }

  public static void initClientThread(String name, boolean stdout, boolean timer) {
    Thread.currentThread().setName(name);
    initLog(name, stdout);
    if (!timer)
      Static.log("Thread start:" + name);
    else
      Static.log("Timer start:" + name);
  }

  public static void initServerThread(String name, boolean stdout, boolean timer) {
    Thread.currentThread().setName(name);
    initLog(name, stdout);
    if (!timer)
      Static.log("Thread start:" + name);
    else
      Static.log("Timer start:" + name);
  }

  //gui
  public static float width;
  public static float height;
  public static float scale;

  public static boolean inGame;

  public static int max_texture_size;

  //opengl attributes (arrays)
  public static int attribVertex, attribTextureCoords, attribTextureCoords2, attribColor, attribSunLight, attribBlockLight;

  //opengl uniforms
  public static int uniformMatrixPerspective, uniformMatrixModel , uniformMatrixView;
  public static int uniformSunLight;
  public static int uniformAlphaFactor;
  public static int uniformEnableTextures;
  public static int uniformEnableFog;
  public static int uniformEnableHorsePattern;
  public static int uniformEnableHorseArmor;
  public static int uniformFogColor;
  public static int uniformFogNear;
  public static int uniformFogFar;
  public static int uniformEnableTint;
  public static int uniformTintColor;
  public static int uniformTexture;
  public static int uniformCrack;
  public static int uniformHorsePattern;
  public static int uniformHorseArmor;

  //colors : R G B
  public static final float white[] = new float[] {1,1,1};
  public static final float black[] = new float[] {0,0,0};
  public static final float grey[] = new float[] {0.12f,0.12f,0.12f};
  public static final float red[] = new float[] {1,0,0};
  public static final float green[] = new float[] {0,1,0};
  public static final float blue[] = new float[] {0,0,1};
  public static final float yellow[] = new float[] {0.07f,0.20f,0.33f};

  //colors : R G B A
  public static final float white4[] = new float[] {1,1,1,1};
  public static final float black4[] = new float[] {0,0,0,1};
  public static final float grey4[] = new float[] {0.12f,0.12f,0.12f,1};
  public static final float red4[] = new float[] {1,0,0,1};
  public static final float green4[] = new float[] {0,1,0,1};
  public static final float blue4[] = new float[] {0,0,1,1};
  public static final float yellow4[] = new float[] {0.07f,0.20f,0.33f,1};
  public static final float skyblue4[] = new float[] {0.2f,0.2f,0.6f,1};

  public static final float _1_8 = 1.0f/8.0f;
  public static final float _1_16 = 1.0f/16.0f;
  public static final float _1_32 = 1.0f/32.0f;
  public static final float _1_48 = 1.0f/48.0f;
  public static final float _1_64 = 1.0f/64.0f;
  public static final float _0_75_15 = 0.75f/15.0f;
  public static final float _0_5_15 = 0.5f/15.0f;
  public static final float _0_25_15 = 0.25f/15.0f;
  public static final float PIx2 = (float)Math.PI * 2f;

  public static int glver[]; //OpenGL Version : ie:3.3.0
  public static boolean keys[] = new boolean[1024];
  public static boolean button[] = new boolean[4];
  public static boolean buttonClick[] = new boolean[4];

  public static final int maxLoadRange = 16;

  public static float gravitySpeed = 1.6f / 20f;
  public static float climbSpeed = 5.0f / 20f;
  public static float dragSpeed = 0.4f;
  public static float termVelocityClimb = 5.0f / 20f;
  public static float termVelocityWeb = 1.0f / 20f;
  public static float webVelocityScale = 5.0f;
  public static float termVelocityLiquid = 5.0f / 20f;
  public static float termVelocityAir = 78.4f / 20f;
  public static Noise noises[];
  public static float noiseParams[][] = {
    //octaves, persistence, scale
    {1, 0.0f, 0.002f},  //temp : low octave for smooth changes
    {1, 0.0f, 0.002f},  //rain : low octave for smooth changes
    {3, 0.7f, 0.007f},  //random1 : high octave for greater complexity
    {3, 0.7f, 0.007f},  //random2 : high octave for greater complexity

    //plains
    {3, 0.5f, 0.01f},   //elev1 : waveform1 : high octave for greater complexity
    {2, 0.5f, 0.002f},  //elev2 : scale1 : low octave for smooth changes

    //mountains
    {2, 0.5f, 0.01f},   //elev3 : waveform2 : high octave for greater complexity
    {2, 0.5f, 0.002f},  //elev4 : scale2 : low octave for smooth changes

    //oceans
    {2, 0.5f, 0.01f},   //elev5 : waveform3 : high octave for greater complexity
    {1, 0.0f, 0.001f},  //elev6 : scale3 : low octave for smooth changes

    //soil/gravel deposites (3d)
    {1, 0.0f, 0.1f},   //soil : 3d

    {1, 0.0f, 0.007f},  //nether
    {2, 0.5f, 0.005f},  //end_top
    {2, 0.5f, 0.005f},  //end_bottom
  };
  public static int N_TEMP = 0;
  public static int N_RAIN = 1;
  public static int N_RANDOM1 = 2;
  public static int N_RANDOM2 = 3;
  public static int N_ELEV1 = 4;
  public static int N_ELEV2 = 5;
  public static int N_ELEV3 = 6;
  public static int N_ELEV4 = 7;
  public static int N_ELEV5 = 8;
  public static int N_ELEV6 = 9;
  public static int N_SOIL = 10;
  public static int N_NETHER = 11;
  public static int N_END_1 = 12;
  public static int N_END_2 = 13;
  //...
  public static void initNoises(World world) {
    int cnt = noiseParams.length;
    noises = new Noise[cnt];
    Random r = new Random();
    r.setSeed(world.seed);
    for(int a=0;a<cnt;a++) {
      noises[a] = new Noise();
      noises[a].init(r, (int)noiseParams[a][0], noiseParams[a][1], noiseParams[a][2]);
    }
  }

  public static boolean isBlock(char id) {
    return id < 32768;
  }

  public static boolean isItem(char id) {
    return id >= 32768;
  }

  public static int floor( float x ) { return (int)Math.floor(x); }
  public static int ceil( float x ) { return (int)Math.ceil(x); }
  public static float abs( float x ) { return x >= 0.0f ? x : -x; }
  public static int abs(int x) { return x >= 0 ? x : -x; }
  public static int min(int a, int b) { return a < b ? a : b; }
  public static int max(int a, int b) { return a > b ? a : b; }

  public static String getBasePath() {
    if (JF.isWindows()) {
      return System.getenv("APPDATA") + "/.jfcraft/";
    } else {
      return JF.getUserPath() + "/.jfcraft/";
    }
  }

  public static String getWorldsPath() {
    return getBasePath() + "saves/";
  }

  public static void registerAll(boolean client) {
    PluginLoader.loadPlugins();
    Static.registerDefault();
    PluginLoader.registerPlugins();
    if (client) {
      Static.audio = new AudioEngine();
      Static.audio.start();
      Static.audio.registerDefault();
      Static.screens.registerDefault();
      Static.blocks.stitchTiles();
      Static.items.stitchTiles();
      Static.blocks.initPerf();  //must redo this if fancy/fast is toggled
    }
    packets.registerDefault();
    extras.registerDefault();
  }

  public static void registerDefault() {
    Static.blocks.registerDefault();
    Static.items.registerDefault();
    Static.dims.registerDefault();
    Static.recipes.registerDefault();
    Static.entities.registerDefault();
  }

  public static void clearButtons() {
    for(int a=0;a<button.length;a++) {
      button[a] = false;
    }
    for(int a=0;a<buttonClick.length;a++) {
      buttonClick[a] = false;
    }
  }

  public static void setInterface(ServerInterface iface) {
    Static.iface = iface;
  }
}
