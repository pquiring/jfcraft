package jfcraft.data;

/**
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

public class AssetImage extends Asset {
  public JFImage image;
  public int x, y;  //position : used in Blocks.stitchTiles() or Items.stitchTiles()
  public int w, h;  //width, height
  public boolean isAnimated;

  //animation images
  public JFImage images[];
  public int noFrames;

  //block animation
  int curFrame;
  public void nextFrame(GL gl) {
    curFrame++;
    if (curFrame == noFrames) curFrame = 0;
    GL.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, x, y, w, h, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, images[curFrame].getPixels());
  }

  //item animation
  int xs[], ys[];
}
