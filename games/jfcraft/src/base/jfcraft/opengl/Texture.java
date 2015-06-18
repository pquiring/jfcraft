package jfcraft.opengl;

/**
 *
 * @author pquiring
 *
 * Created : Mar 22, 2014
 */

import javaforce.*;
import javaforce.gl.*;

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

  public void load(GL gl) {
    load(gl, image);
  }

  public void load(GL gl, JFImage image) {
    int ids[] = new int[1];
    ids[0] = -1;
    gl.glGenTextures(1, ids);
    if (ids[0] == -1) {
      JF.showError("Error", "glGenTextures failed");
      System.exit(0);
    }
    glid = ids[0];
    gl.glActiveTexture(GL.GL_TEXTURE0 + unit);
    gl.glBindTexture(GL.GL_TEXTURE_2D, glid);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    if (mipmaps) {
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
    } else {
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    }
    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, image.getWidth(), image.getHeight(), 0, GL.GL_BGRA
      , GL.GL_UNSIGNED_BYTE, image.getPixels());
  }

  public void unload(GL gl) {
    int ids[] = new int[1];
    ids[0] = glid;
    gl.glDeleteTextures(1, ids, 0);
  }

  public void bind(GL gl) {
    gl.glBindTexture(GL.GL_TEXTURE_2D, glid);
  }

  /** Returns a SubTexture that is part of this texture. */
  public SubTexture getSubTexture(float x1, float y1, float x2, float y2) {
    SubTexture st = new SubTexture();
    st.texture = this;
    st.x1 = x1;
    st.y1 = y1;
    st.x2 = x2;
    st.y2 = y2;
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
