package jfcraft.data;

/** Caches textures
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;
import java.io.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.opengl.TextureMap;

public class Textures {
  private static HashMap<String, TextureMap> cache = new HashMap<String, TextureMap>();

  public static TextureMap getTexture(String name, int unit) {
    TextureMap texture = cache.get(name);
    if (texture != null) return texture;
    texture = new TextureMap();
    texture.unit = unit;
    texture.load(Assets.getImage(name).image);
    cache.put(name, texture);
    return texture;
  }
  /** Load directly from file (before assets are loaded) */
  public static TextureMap getTexture2(String name) {
    TextureMap texture = new TextureMap();
    JFImage img = new JFImage();
    if (!img.loadPNG(name)) {
      img.setSize(512, 512);
      img.getGraphics().drawBytes("JFCraft".getBytes(), 0, 7, 200, 200);
    }
    texture.load(img);
    return texture;
  }
}
