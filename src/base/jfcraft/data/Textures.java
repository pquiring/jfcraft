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

import jfcraft.opengl.Texture;

public class Textures {
  private static HashMap<String, Texture> cache = new HashMap<String, Texture>();

  public static Texture getTexture(String name, int unit) {
    Texture texture = cache.get(name);
    if (texture != null) return texture;
    texture = new Texture();
    texture.unit = unit;
    texture.load(Assets.getImage(name).image);
    cache.put(name, texture);
    return texture;
  }
  /** Load directly from file (before assets are loaded) */
  public static Texture getTexture2(String name) {
    Texture texture = new Texture();
    JFImage img = new JFImage();
    if (!img.loadPNG(name)) {
      img.setSize(512, 512);
      img.getGraphics().drawBytes("JFCraft".getBytes(), 0, 7, 200, 200);
    }
    texture.load(img);
    return texture;
  }
}
