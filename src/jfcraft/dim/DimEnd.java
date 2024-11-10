package jfcraft.dim;

/** Dimension : Nether.
 *
 * @author pquiring
 */

import java.util.Random;
import jfcraft.data.*;
import jfcraft.entity.EntityBase;
import jfcraft.env.*;
import jfcraft.light.*;
import jfcraft.gen.*;

public class DimEnd extends DimBase {

  public String getName() {
    return "end";
  }

  private EntityBase mobs[];

  public void init() {
    super.init();
    mobs = Static.entities.listSpawn(id);
  }

  private GeneratorPhase1Base phase1 = new GeneratorPhase1End();

  public GeneratorPhase1Base getGeneratorPhase1() {
    return phase1;
  }

  private GeneratorPhase2Base phase2 = new GeneratorPhase2End();

  public GeneratorPhase2Base getGeneratorPhase2() {
    return phase2;
  }

  private GeneratorPhase3Base phase3 = new GeneratorPhase3End();

  public GeneratorPhase3Base getGeneratorPhase3() {
    return phase3;
  }

  private LightingBase light_client = new LightingAmbient(7);
  private LightingBase light_server = new LightingAmbient(7);

  public LightingBase getLightingServer() {
    return light_server;
  }
  public LightingBase getLightingClient() {
    return light_client;
  }

  private EnvironmentBase env = new EnvironmentEnd();

  public EnvironmentBase getEnvironment() {
    return env;
  }

  private Random r = new Random();

  public void spawnMonsters(Chunk chunk) {
    if (chunk.dim != Dims.END) return;
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
