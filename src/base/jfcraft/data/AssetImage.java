package jfcraft.data;

/**
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

public class AssetImage extends Asset {
  public JFImage image, image2;
  public int x, y;  //position : used in Blocks.stitchTiles() or Items.stitchTiles()
  public int w, h;  //width, height
  public boolean isAnimated;
  public boolean isCrack;
  public boolean isPerf;

  //animation images
  public JFImage images[];
  public int noFrames;

  //block animation
  int curFrame;
  public void nextFrame() {
    curFrame++;
    if (curFrame == noFrames) curFrame = 0;
    glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA, GL_UNSIGNED_BYTE, images[curFrame].getBuffer());
  }

  private void init2() {
    if (image2 == null) {
      image2 = image.getJFImage(0, 0, w, h);
      image2.fillAlpha(0, 0, w, h, 0xff);
    }
  }

  public void reload(boolean alpha) {
    if (alpha) {
      glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA, GL_UNSIGNED_BYTE, image.getBuffer());
    } else {
      init2();
      glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA, GL_UNSIGNED_BYTE, image2.getBuffer());
    }
  }

  public int[] getPixels() {
    if (isPerf && !Settings.current.fancy) {
      init2();
      return image2.getBuffer();
    } else {
      return image.getBuffer();
    }
  }

  public int[] getPixels(int frame) {
    return images[frame].getBuffer();
  }

  //item animation
  int xs[], ys[];
}
