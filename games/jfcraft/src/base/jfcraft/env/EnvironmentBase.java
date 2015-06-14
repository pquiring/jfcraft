package jfcraft.env;

/**
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.client.*;

public interface EnvironmentBase {
  public void init(GL gl);
  public void render(GL gl, int time, float sunLight, Client client);
}
