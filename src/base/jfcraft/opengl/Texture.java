package jfcraft.opengl;

/**
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.block.*;
import jfcraft.data.Static;

public class Texture {
  private static boolean mipmaps = false;

  public int glid = -1;
  public String name;
  public int unit = 0;

  public int tilesIdx = -1;  //tiles idx

  //stitching data
  public JFImage image, usage;
  public int sx = 0;
  public int sy = 0;
  private int ux = 0;
  private int uy = 0;

  public Texture() {}

  public void initImage(int w,int h) {
    image = new JFImage(w, h);
    image.setResizeOperation(JFImage.ResizeOperation.CHOP);
    sx = w;
    sy = h;
  }

  public void resizeImage() {
    image.setSize(sx, sy);
    if (usage != null) {
      ux = sx / 16;
      uy = sy / 16;
      usage.setSize(ux, uy);
    }
  }

  public boolean expandImage() {
    if (sx > sy) {
      //expand height
      sy <<= 1;
      if (sy > Static.max_texture_size) {
        return false;
      }
      resizeImage();
    } else {
      //expand width
      sx <<= 1;
      if (sx > Static.max_texture_size) {
        return false;
      }
      resizeImage();
    }
    return true;
  }

  public void initUsage() {
    ux = sx / 16;
    uy = sy / 16;
    usage = new JFImage(ux,uy);
    usage.setResizeOperation(JFImage.ResizeOperation.CHOP);
  }

  public int[] placeSubTexture(int px[], int w,int h) {
    while (true) {
      int loc[] = placeSubTexture2(px,w,h);
      if (loc != null) return loc;
      if (!expandImage()) return null;
    }
  }

  private int[] placeSubTexture2(int px[], int w,int h) {
    int xy[] = new int[2];
    int bw = w / 16;
    int bh = h / 16;
    for(int x = 0;x < ux-bw;x++) {
      for(int y = 0;y < uy-bh;y++) {
        boolean fits = true;
        for(int cx = 0;cx < bw;cx++) {
          for(int cy = 0;cy < bh;cy++) {
            if (usage.getPixel(x+cx, y+cy) != 0) {
              fits = false;
              break;
            }
          }
          if (!fits) break;
        }
        if (fits) {
          for(int cx = 0;cx < bw;cx++) {
            for(int cy = 0;cy < bh;cy++) {
              usage.putPixel(x+cx, y+cy, 0xffffff);
            }
          }
          image.putPixels(px, x*16, y*16, w, h, 0);
          xy[0] = x*16;
          xy[1] = y*16;
          return xy;
        }
      }
    }
    return null;  //no place available (call expandImage())
  }

  public void load() {
    load(image);
  }

  public void load(JFImage image) {
    int ids[] = new int[1];
    ids[0] = -1;
    glGenTextures(1, ids);
    if (ids[0] == -1) {
      JFAWT.showError("Error", "glGenTextures failed");
      System.exit(0);
    }
    glid = ids[0];
    glActiveTexture(GL_TEXTURE0 + unit);
    glBindTexture(GL_TEXTURE_2D, glid);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    if (mipmaps) {
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
    } else {
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }
    glTexImage2D(GL_TEXTURE_2D, 0, 4, image.getWidth(), image.getHeight(), 0, GL_BGRA
      , GL_UNSIGNED_BYTE, image.getPixels());
  }

  public void unload() {
    int ids[] = new int[1];
    ids[0] = glid;
    glDeleteTextures(1, ids);
  }

  public void bind() {
    glActiveTexture(GL_TEXTURE0 + unit);
    glBindTexture(GL_TEXTURE_2D, glid);
  }

  /** Returns a SubTexture that is part of this texture. */
  public SubTexture getSubTexture(float x1, float y1, float x2, float y2) {
    SubTexture st = new SubTexture();
    st.texture = this;
    st.x1 = x1;
    st.y1 = y1;
    st.x2 = x2;
    st.y2 = y2;
    st.width = st.x2 - st.x1;
    st.height = st.y2 - st.y1;
    return st;
  }

  /** Returns a SubTexture that is same as this texture. */
  public SubTexture getSubTexture() {
    SubTexture st = new SubTexture();
    st.texture = this;
    st.x1 = 0;
    st.y1 = 0;
    st.x2 = 1;
    st.y2 = 1;
    return st;
  }
}
