package jfcraft.env;

/**
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.client.*;

public interface EnvironmentBase {
  public void init();
  public void render(int time, float sunLight, Client client);
}
