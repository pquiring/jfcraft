package jfcraft.opengl;

/**
 *
 * @author pquiring
 *
 * Created : Mar 26, 2014
 */

import jfcraft.data.*;

public class SubTexture {
  public Texture texture;
  public float x1, y1, x2, y2;  //texture coords
  public float fx1, fy1, fx2, fy2;  //texture coords (flowing water on 45 def angle)
  public float fx3, fy3, fx4, fy4;  //texture coords (flowing water on 45 def angle)
  public float width, height;  //texture coords
  public AssetImage ai;
  public boolean isAlpha;
  public boolean isAnimated;  //texture loaded is animated
  public int textureUnit;  //texture unit (GL_TEXTURE0, etc.)
  public int buffersIdx;  //RenderDest.getBuffer(buffersIdx) Chunk.DEST_NORMAL or Chunk.DEST_ALPHA or animated
  public boolean isFullTexture;  //uses a full texture (ie: chest)

  //for items
  public float x1s[], y1s[], x2s[], y2s[];
}
