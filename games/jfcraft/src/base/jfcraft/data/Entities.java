package jfcraft.data;

/** Registered Entities
 *
 * @author pquiring
 *
 * Created : Aug 5, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static jfcraft.data.Extras.MAX_ID;

import jfcraft.entity.*;
import jfcraft.opengl.*;

public class Entities implements SerialCreator {
  public static final short MAX_ID = 8192;

  //players
  public static int PLAYER;  //will be zero

  //monsters
  public static int ZOMBIE;
  public static int SKELETON;
  public static int ENDERMAN;
  public static int ZOMBIE_PIGMAN;
  public static int SLIME;

  //animals (spawn with world generator)
  public static int PIG;
  public static int COW;
  public static int SHEEP;
  public static int HORSE;

  //misc
  public static int MOVINGBLOCK;
  public static int WORLDITEM;

  //items as entities
  public static int END_PORTAL;
  public static int ARROW;
  public static int CHEST;
  public static int BOAT;
  public static int MINECART;
  public static int PISTON;
  public static int PISTON_STICKY;
  public static int LEVER;
  public static int ENDER_CHEST;

  public int entityCount;
  public EntityBase[] regEntities = new EntityBase[MAX_ID];
  public EntityBase[] entities;

  public void registerEntity(EntityBase e) {
    regEntities[entityCount++] = e;
  }

  public void orderEntities() {
    entities = new EntityBase[MAX_ID];
    for(int a=0;a<MAX_ID;a++) {
      EntityBase eb = regEntities[a];
      if (eb == null) continue;
      entities[eb.id] = eb;
    }
  }

  public void registerDefault() {
    registerEntity(new Player());
    registerEntity(new Zombie());
    registerEntity(new Skeleton());
    registerEntity(new Enderman());
    registerEntity(new ZombiePigman());
    registerEntity(new Slime());
    registerEntity(new Chest());
    registerEntity(new EnderChest());
    registerEntity(new Piston());
    registerEntity(new Piston().setSticky());
    registerEntity(new Lever());
    registerEntity(new MovingBlock());
    registerEntity(new WorldItem());
    registerEntity(new Pig());
    registerEntity(new Cow());
    registerEntity(new Horse());
    registerEntity(new Sheep());
    registerEntity(new EndPortal());
    registerEntity(new Arrow());
    registerEntity(new Boat());
    registerEntity(new Minecart());
    registerEntity(new Horse());
  }

  public void initStatic() {
    Static.log("initStatic()");
    for(int a=0;a<MAX_ID;a++) {
      EntityBase e = regEntities[a];
      if (e == null) continue;
      e.initStatic();
    }
  }

  public void initStatic(GL gl) {
    Static.log("initStatic(gl)");
    RenderData data = new RenderData();
    for(int a=0;a<MAX_ID;a++) {
      EntityBase entity = regEntities[a];
      if (entity == null) continue;
      try {
        entity.initStatic(gl);
        entity.init();
        entity.initInstance(gl);
        entity.buildBuffers(entity.getDest(), data);
        entity.copyBuffers(gl);
      } catch (Exception e) {
        Static.log(e);
      }
    }
  }

  /** Returns a list of mobs that spawn in dimension. */
  public EntityBase[] listSpawn(int dimID) {
    ArrayList<EntityBase> mobs = new ArrayList<EntityBase>();
    for(int a=0;a<MAX_ID;a++) {
      EntityBase eb = entities[a];
      if (eb == null) continue;
      int dims[] = eb.getSpawnDims();
      for(int b=0;b<dims.length;b++) {
        if (dims[b] == dimID) {
          mobs.add(eb);
          break;
        }
      }
    }
    return mobs.toArray(new EntityBase[0]);
  }

  /** Returns a list of mobs that generate in dimension. */
  public EntityBase[] listGenerate(int dimID) {
    ArrayList<EntityBase> mobs = new ArrayList<EntityBase>();
    for(int a=0;a<MAX_ID;a++) {
      EntityBase eb = entities[a];
      if (eb == null) continue;
      int dims[] = eb.getGenerateDims();
      for(int b=0;b<dims.length;b++) {
        if (dims[b] == dimID) {
          mobs.add(eb);
          break;
        }
      }
    }
    return mobs.toArray(new EntityBase[0]);
  }

  @Override
  public SerialClass create(SerialBuffer buffer) {
    int type = buffer.peekInt(1);
    EntityBase base = Static.entities.entities[type];
    if (base == null) {
      Static.logTrace("Error:Entity not registered:" + type);
      return null;
    }
    try {
      EntityBase eb = base.getClass().newInstance();
      return eb;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
