package jfcraft.opengl;

/**
 *
 * @author pquiring
 *
 * Created : Oct 29, 2014
 */

import javaforce.gl.*;

public interface RenderSource {
  public void buildBuffers(RenderDest dest);
  public void bindTexture();
  public void render();
}
