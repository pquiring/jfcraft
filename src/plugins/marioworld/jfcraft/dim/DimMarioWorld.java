package jfcraft.dim;

/**
 *
 * @author pquiring
 */

import java.util.*;

import jfcraft.light.*;
import jfcraft.entity.*;
import jfcraft.data.*;
import jfcraft.gen.*;
import jfcraft.env.*;

public class DimMarioWorld extends DimBase {
  private EntityBase mobs[];

  /** Mario World ID (assigned) */
  public static int MARIO_WORLD;

  public String getName() {
    return "Mario World";
  }

  public void getIDs(World world) {
    MARIO_WORLD = world.getEntityID("MARIO_WORLD");
  }

  public void init() {
    super.init();
    mobs = Static.entities.listSpawn(id);
  }

  private GeneratorPhase1Base phase1 = new GeneratorPhase1Mario();

  public GeneratorPhase1Base getGeneratorPhase1() {
    return phase1;
  }

  private GeneratorPhase2Base phase2 = new GeneratorPhase2Mario();

  public GeneratorPhase2Base getGeneratorPhase2() {
    return phase2;
  }

  private GeneratorPhase3Base phase3 = new GeneratorPhase3Mario();

  public GeneratorPhase3Base getGeneratorPhase3() {
    return phase3;
  }

  private LightingBase light_client = new LightingAmbient(15);
  private LightingBase light_server = new LightingAmbient(15);

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
    if (chunk.dim != MARIO_WORLD) return;
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
