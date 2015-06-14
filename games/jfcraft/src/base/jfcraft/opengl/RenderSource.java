package jfcraft.opengl;

/**
 *
 * @author pquiring
 *
 * Created : Oct 29, 2014
 */

import javaforce.gl.*;

public interface RenderSource {
  public void buildBuffers(RenderDest dest, RenderData data);
  public void bindTexture(GL gl);
  public void render(GL gl);
}
