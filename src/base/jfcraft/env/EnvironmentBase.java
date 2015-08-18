package jfcraft.env;

/**
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;

public interface EnvironmentBase {
  public void init();
  public void preRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks);
  public void postRender(int time, float sunLight, Client client, XYZ camera, Chunk[] chunks);
  public void tick();
}
