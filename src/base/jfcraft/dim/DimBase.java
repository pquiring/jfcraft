package jfcraft.dim;

/** Dimension base.
 *
 * Defines how to generate a dimension
 *
 * @author pquiring
 */

import jfcraft.light.*;
import jfcraft.env.*;
import jfcraft.gen.*;
import jfcraft.data.*;

public abstract class DimBase {
  public int id;
  public abstract String getName();
  public Class getIDClass() {
    return Dims.class;
  }
  public void getIDs() {};
  public void init() {
    getGeneratorPhase1().getIDs();
    getGeneratorPhase2().getIDs();
    getGeneratorPhase3().getIDs();
  }
  public abstract GeneratorPhase1Base getGeneratorPhase1();
  public abstract GeneratorPhase2Base getGeneratorPhase2();
  public abstract GeneratorPhase3Base getGeneratorPhase3();
  public abstract LightingBase getLightingServer();
  public abstract LightingBase getLightingClient();
  public abstract EnvironmentBase getEnvironment();
  public abstract void spawnMonsters(Chunk chunk);
}
