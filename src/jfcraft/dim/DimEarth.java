package jfcraft.dim;

/** Dimension : Earth.
 *
 * @author pquiring
 */

import java.util.*;

import jfcraft.data.*;
import jfcraft.entity.EntityBase;
import jfcraft.light.*;
import jfcraft.gen.*;
import jfcraft.env.*;

public class DimEarth extends DimBase {
  private EntityBase mobs[];

  public String getName() {
    return "earth";
  }

  public void init() {
    super.init();
    mobs = Static.entities.listSpawn(id);
  }

  private GeneratorPhase1Base phase1 = new GeneratorPhase1Earth();

  public GeneratorPhase1Base getGeneratorPhase1() {
    return phase1;
  }

  private GeneratorPhase2Base phase2 = new GeneratorPhase2Earth();

  public GeneratorPhase2Base getGeneratorPhase2() {
    return phase2;
  }

  private GeneratorPhase3Base phase3 = new GeneratorPhase3Earth();

  public GeneratorPhase3Base getGeneratorPhase3() {
    return phase3;
  }

  private LightingBase light_client = new LightingEarth();
  private LightingBase light_server = new LightingEarth();

  public LightingBase getLightingServer() {
    return light_server;
  }
  public LightingBase getLightingClient() {
    return light_client;
  }

  private EnvironmentBase env = new EnvironmentEarth();

  public EnvironmentBase getEnvironment() {
    return env;
  }

  private Random r = new Random();

  public void spawnMonsters(Chunk chunk) {
    if (chunk.dim != Dims.EARTH) return;
    int idx = r.nextInt(mobs.length);
    EntityBase eb = mobs[idx];
    if (r.nextFloat() * 100.0f > eb.getSpawnRate()) {
      return;
    }
    EntityBase e = eb.spawn(chunk);
    if (e == null) {
      return;
    }
    e.uid = Static.server.world.generateUID();
    Static.log("spawn " + e.getName() + " @dim= " + chunk.dim + ":x=" + e.pos.x + ",z=" + e.pos.z + ":uid=" + e.uid);
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
  }
}
