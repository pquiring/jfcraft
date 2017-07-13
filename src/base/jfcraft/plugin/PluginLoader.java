package jfcraft.plugin;

/** Loads plugins
 *
 * @author pquiring
 */

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import javaforce.*;

import jfcraft.data.*;

public class PluginLoader {

  public static ArrayList<PluginBase> plugins = new ArrayList<PluginBase>();

  /** Load plugins. */
  public static void loadPlugins() {
    String Dplugins = System.getProperty("java.class.path");
    if (Dplugins == null) return;
    String jars[] = Dplugins.split(File.pathSeparator);
    //load base assets
    if (!Assets.addZip("base.zip")) {  //faithfull texture pack
      JFAWT.showError("Error", "Failed to load base.zip");
      System.exit(0);
    }
    if (!Assets.addZip("jfassets.zip")) {
      JFAWT.showError("Error", "Failed to load jfassets.zip");
      System.exit(0);
    }
    for(int a=0;a<jars.length;a++) {
      if (jars[a].equals("javaforce.jar")) continue;
      if (jars[a].equals("jfcraft.jar")) continue;
      if (jars[a].equals("swt.jar")) continue;
      if (jars[a].equals("lwjgl.jar")) continue;
      try {
        ZipFile zf = new ZipFile(jars[a]);
        ZipEntry ze = zf.getEntry("plugin.properties");
        InputStream is = zf.getInputStream(ze);
        Properties props = new Properties();
        props.load(is);
        String clsName = props.getProperty("Class");
        String name = props.getProperty("Name");
        String ver = props.getProperty("Version");
        String desc = props.getProperty("Desc");
        zf.close();
        Class cls = Class.forName(clsName);
        Constructor c = cls.getConstructor();
        PluginBase plugin = (PluginBase)c.newInstance();
        plugins.add(plugin);
        Assets.addZip(jars[a]);
//        plugin.registerPlugin();
      } catch (Exception e) {
        Static.log(e);
        continue;
      }
    }
  }

  /** Init plugins. */
  public static void registerPlugins() {
    int cnt = plugins.size();
    for(int a=0;a<cnt;a++) {
      PluginBase pb = plugins.get(a);
      Static.log("registerPlugin:" + pb.getName());
      try { plugins.get(a).registerPlugin(); } catch (Exception e) {Static.log(e);}
    }
  }

  /** Lists available plugins.
   * @return jar/class/name/version/desc
   */
  public static String[][] listPlugins() {
    ArrayList<String[]> plugins = new ArrayList<String[]>();

    File path1 = new File("plugins/");
    if (path1.isDirectory()) {
      listPlugins(path1, plugins);
    }

    File path2 = new File(Static.getBasePath() + "plugins/");
    if (path2.isDirectory() && !path1.getAbsoluteFile().equals(path2.getAbsoluteFile())) {
      listPlugins(path2, plugins);
    }

    return plugins.toArray(new String[0][0]);
  }

  private static void listPlugins(File folder, ArrayList<String[]> plugins) {
    try {
      File files[] = folder.listFiles();
      if (files == null) return;
      for(int a=0;a<files.length;a++) {
        if (files[a].isDirectory()) continue;
        String ext = files[a].getName().toLowerCase();
        if (!ext.endsWith(".jar") && !ext.endsWith(".zip")) continue;
        //load plugin.properties from jar file
        ZipFile zf = null;
        try {
          zf = new ZipFile(files[a]);
          ZipEntry ze = zf.getEntry("plugin.properties");
          InputStream is = zf.getInputStream(ze);
          Properties props = new Properties();
          props.load(is);
          String cls = props.getProperty("Class");
          String name = props.getProperty("Name");
          String ver = props.getProperty("Version");
          String desc = props.getProperty("Desc");
          plugins.add(new String[]{files[a].getAbsolutePath(),cls,name,ver,desc});
        } catch (Exception e) {
          Static.log(e);
        }
        try {
          if (zf != null) {
            zf.close();
          }
        } catch (Exception e) {
          Static.log(e);
        }
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  /** Returns a string comma delimited with all loaded plug-ins. */
  public static String getPluginsString() {
    ArrayList<String> names = new ArrayList<String>();
    int size = plugins.size();
    for(int a=0;a<size;a++) {
      names.add(plugins.get(a).getName());
    }
    names.sort(new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    StringBuilder sb = new StringBuilder();
    for(int a=0;a<size;a++) {
      if (a > 0) sb.append(',');
      sb.append(names.get(a));
    }
    return sb.toString();
  }
}
