package jfcraft.data;

/** Registered Entities
 *
 * @author pquiring
 *
 * Created : Aug 5, 2014
 */

import java.util.*;
import java.lang.reflect.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.entity.*;
import jfcraft.extra.*;
import jfcraft.opengl.*;
import static jfcraft.data.Extras.MAX_ID;

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

  //npcs
  public static int VILLAGER;
  public static int NPC;
  public static int NPCALEX;

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
  public static int SHIELD;

  public static void getIDs(World world) {
    PLAYER = world.getEntityID("player");
    ZOMBIE = world.getEntityID("ZOMBIE");
    SKELETON = world.getEntityID("SKELETON");
    ENDERMAN = world.getEntityID("ENDERMAN");
    ZOMBIE_PIGMAN = world.getEntityID("ZOMBIE_PIGMAN");
    SLIME = world.getEntityID("SLIME");
    VILLAGER = world.getEntityID("VILLAGER");
    PIG = world.getEntityID("PIG");
    COW = world.getEntityID("COW");
    SHEEP = world.getEntityID("SHEEP");
    HORSE = world.getEntityID("HORSE");
    MOVINGBLOCK = world.getEntityID("MOVINGBLOCK");
    WORLDITEM = world.getEntityID("WORLDITEM");
    END_PORTAL = world.getEntityID("END_PORTAL");
    ARROW = world.getEntityID("ARROW");
    CHEST = world.getEntityID("CHEST");
    BOAT = world.getEntityID("BOAT");
    MINECART = world.getEntityID("MINECART");
    PISTON = world.getEntityID("PISTON");
    PISTON_STICKY = world.getEntityID("PISTON_STICKY");
    LEVER = world.getEntityID("LEVER");
    ENDER_CHEST = world.getEntityID("ENDER_CHEST");
    SHIELD = world.getEntityID("SHIELD");
    NPC = world.getEntityID("NPC");
    NPCALEX = world.getEntityID("NPCALEX");
  }

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
    registerEntity(new Villager());
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
    registerEntity(new Shield());
    registerEntity(new NPC());
    registerEntity(new NPCAlex());
  }

  public void initStatic() {
    Static.log("initStatic()");
    for(int a=0;a<MAX_ID;a++) {
      EntityBase e = regEntities[a];
      if (e == null) continue;
      e.initStatic();
    }
  }

  public void initStaticGL() {
    Static.log("initStatic(gl)");
    World world = new World(true);
    for(int a=0;a<MAX_ID;a++) {
      EntityBase entity = regEntities[a];
      if (entity == null) continue;
      try {
        entity.initStaticGL();
        entity.init(world);
        entity.initInstance();
        entity.buildBuffers(entity.getDest());
        entity.copyBuffers();
      } catch (Exception e) {
        Static.log(e);
      }
    }
    //particle is a client side only entity and is not registered
    new Particle().initStaticGL();
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

  public SerialClass create(SerialBuffer buffer, EntityBase[] entities) {
    int type = buffer.peekInt(1);
    if (type >= entities.length) return null;
    EntityBase base = entities[type];
    if (base == null) {
      Static.logTrace("Error:Entity not registered:" + type);
      return null;
    }
    try {
      Class<?> cls = base.getClass();
      Constructor ctor = cls.getConstructor();
      EntityBase eb = (EntityBase)ctor.newInstance();
      return eb;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public SerialClass create(SerialBuffer buffer) {
    return create(buffer, Static.entities.entities);
  }

  public EntityBase getEntity(int id) {
    for(int a=0;a<entities.length;a++) {
      if (entities[a].id == id) {
        return entities[a];
      }
    }
    Static.log("Error:Entity ID not found:" + id);
    return null;
  }

  public EntityBase getEntity(String name) {
    name = name.toLowerCase();
    for(int a=0;a<regEntities.length;a++) {
      EntityBase entity = regEntities[a];
      if (entity == null) continue;
      String ename = entity.getName().toLowerCase();
      if (ename.equals(name)) {
        return entity;
      }
    }
    Static.log("Error:Entity not found:" + name);
    return null;
  }

  public EntityBase cloneEntity(String name) {
    EntityBase eb = getEntity(name);
    if (eb == null) return null;
    return eb.clone();
  }
}
