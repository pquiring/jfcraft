package jfcraft.entity;

/** Villager entity
 *
 * http://minecraft.fandom.com/wiki/Villager
 * http://minecraft.fandom.com/wiki/Trading
 *
 * @author pquiring
 *
 * Created : Nov 10, 2024
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import jfcraft.move.*;

public class Villager extends HumaniodBase {
  private float walkAngle;  //angle of legs/arms as walking
  private float walkAngleDelta;
  
  private static boolean debug = true;

  //render assets
  public static RenderDest dest;
  private static TextureMap texture;
  private static String textureName;
  private static Model model;

  private static int initHealth = 20;
  private static int initArmor = 2;

  public int job;  //profession
  public int level;  //profession level
  public int biome;  //variant of trade : see Biomes.{type}
  //biomes : DESERT, JUNGLE, PLAINS, SAVANNA, SNOW, SWAMP, TAIGA
  
  public int trade_index;

  public static final int JOB_NONE = 0;
  public static final int JOB_NITWIT = 1;
  public static final int JOB_ARMORER = 2;
  public static final int JOB_BITCHER = 3;
  public static final int JOB_CARTOGRAPHER = 4;
  public static final int JOB_CLERIC = 5;
  public static final int JOB_FARMER = 6;
  public static final int JOB_FISHERMAN = 7;
  public static final int JOB_FLETCHER = 8;
  public static final int JOB_LEATHERWORKER = 9;
  public static final int JOB_LIBRARIAN = 10;
  public static final int JOB_MASON = 11;
  public static final int JOB_SHEPHERD = 12;
  public static final int JOB_TOOLSMITH = 13;
  public static final int JOB_WEAPONSMITH = 14;
  public static final int JOB_MAX = 14;

  public static final int LEVEL_NOVICE = 0;
  public static final int LEVEL_APPRENTICE = 1;
  public static final int LEVEL_JOURNEYMAN = 2;
  public static final int LEVEL_EXPERT = 3;
  public static final int LEVEL_MASTER = 4;
  public static final int LEVEL_MAX = 4;

  public Villager() {
    super(1, 4);
    id = Entities.VILLAGER;
    if (debug) {
      job = JOB_MASON;
    }
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "Villager";
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.6f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    walkAngleDelta = 5.0f;
    if (world.isServer) {
      ar = initArmor;
      eyeHeight = 1.3f;
      jumpVelocity = 0.58f;  //results in jump of 1.42
      //speeds are blocks per second
      walkSpeed = 4.3f;
      runSpeed = 5.6f;
      sneakSpeed = 1.3f;
      swimSpeed = (walkSpeed / 2.0f);
      reach = 5.0f;
      attackRange = 2.0f;
      attackDelay = 30;  //1.5 sec per attack
      attackDmg = 1.0f;
      maxAge = 20 * 60 * 15;  //15 mins
    }
    setMove(new MoveCreature());
  }

  public void initStatic() {
    super.initStatic();
    textureName = "entity/villager/villager";
    dest = new RenderDest(parts.length);
    model = loadModel("villager");
  }

  public void initStaticGL() {
    super.initStaticGL();
    texture = Textures.getTexture(textureName, 0);
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "C_ARM", "NOSE", "BRIM"};

  private static final int C_ARM = 6;
  private static final int NOSE = 7;
  private static final int BRIM = 8;

  public void buildBuffers(RenderDest dest) {
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      Object3 obj = model.getObject(parts[a]);
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        UVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        UVMap map1 = obj.maps.get(0);
        UVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  public void bindTexture() {
    texture.bind();
  }

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(-ang.x, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case BODY:
        break;
      case L_ARM:
      case R_ARM:
      case C_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(45.0f, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case L_LEG:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case R_LEG:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate2(walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public void ctick() {
    float delta = 0;
    switch (mode) {
      case MODE_IDLE:
        walkAngle = 0.0f;
        return;
      case MODE_RUN:
        delta = walkAngleDelta * 2f;
        break;
      case MODE_SWIM:
      case MODE_WALK:
        delta = walkAngleDelta;
        break;
      case MODE_SNEAK:
        delta = walkAngleDelta / 2f;
        break;
    }
    walkAngle += delta;
    if ((walkAngle < -45.0) || (walkAngle > 45.0)) {
      walkAngleDelta *= -1;
    }
  }

  public void render() {
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      if (buf.isBufferEmpty()) continue;
      setMatrixModel(a, buf);
      buf.bindBuffers();
      buf.render();
    }
  }

  public EntityBase spawn(Chunk chunk) {
    World world = Static.server.world;
    float px = r.nextInt(16) + chunk.cx * 16.0f + 0.5f;
    int y = r.nextInt(256);
    float pz = r.nextInt(16) + chunk.cz * 16.0f + 0.5f;
    for(float gy = y;gy>0;gy--) {
      float py = gy;
      if (world.isEmpty(chunk.dim,px,py,pz)
        && world.isEmpty(chunk.dim,px,py-1,pz)
        && (world.canSpawnOn(chunk.dim,px,py-2,pz)))
      {
        py -= 1.0f;
        Villager e = new Villager();
        e.init(world);
        e.dim = chunk.dim;
        e.health = initHealth;
        e.pos.x = px;
        e.pos.y = py;
        e.pos.z = pz;
        e.ang.y = r.nextInt(360);
        e.job = r.nextInt(JOB_MAX + 1);
        e.level = r.nextInt(LEVEL_MAX + 1);
        e.biome = r.nextInt(5);
        return e;
      }
    }
    return null;
  }
  public float getSpawnRate() {
    return 5.0f;
  }
  public Item[] drop() {
    Random r = new Random();
    Item items[] = new Item[1];
    items[0] = new Item(Items.APPLE, 0, r.nextInt(2)+1);
    return items;
  }
  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
//      Static.server.broadcastSound(dim, pos.x, pos.y, pos.z, Sounds.SOUND_VILLAGER, 1);
    }
  }
  public int[] getSpawnDims() {
    return new int[] {Dims.EARTH};
  }
  public void useEntity(Client client, boolean sneak) {
    if (job == JOB_NONE || job == JOB_NITWIT) {
      //nope - shake head
      Static.log("Villager.useEntity():nope");
      return;
    }
    Static.log("Villager.useEntity():trade");
    //open villager menu
    client.villager = this;
    client.serverTransport.sendVillager(this);
    client.serverTransport.enterMenu(Client.VILLAGER);
    client.menu = Client.VILLAGER;
  }
  public boolean useTool(Client client, Coords c) {
    useEntity(client, false);
    return true;
  }

  private static ItemRef[][] noOffers = new ItemRef[0][0];

  //TODO : this will be a massive table
  private static ItemRef[][][][] offers = new ItemRef[][][][] {
    {  //UNEMPLOYED
      {}
    },
    {  //NITWIT
      {}
    },
    {  //ARMORER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //BUTCHER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //CARTOGRAPHER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //CLERIC
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //FARMER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //FISHERMAN
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //FLETCHER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //LEATHERWORKER
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //LIBRARIAN
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //MASON
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //SHEPARD
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //TOOLSMITH
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }, {  //WEAPONSMITH
      {  //NOVICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //APPRENTICE
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //JOURNEYMAN
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //EXPERT
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }, {  //MASTER
          {new ItemRef("COAL", 15), null, new ItemRef("EMERALD", 1)}
        , {new ItemRef("EMERAL", 5), null, new ItemRef("IRON_HELMET", 1)}
      }
    }
  };

  //0-1 = input : 2 = output
  public Item[][] getOfferings() {
    if (job == JOB_NONE || job == JOB_NITWIT) return ItemRef.getItems(noOffers);
    if (debug) {
      Static.log("Villager.job=" + job);
      Static.log("Villager.level=" + level);
    }
    return ItemRef.getItems(offers[job][level]);
  }

  public Item getOffer(Item[] items) {
    if (trade_index == -1) return null;
    Item[][] trade_offers = getOfferings();
    if (trade_offers == null) return null;
    if (trade_index >= trade_offers.length) return null;
    char give0 = items[0].id;
    char give1 = items[1].id;
    Item item0 = trade_offers[trade_index][0];
    Item item1 = trade_offers[trade_index][1];
    char offer0 = item0 != null ? item0.id : 0;
    char offer1 = item1 != null ? item1.id : 0;
    if ((give0 == offer0 || give0 == offer1) && (give1 == offer0 || give1 == offer1)) {
      return trade_offers[trade_index][2];
    }
    return null;
  }

  private static final byte ver = 0;

  /** Method can be over written to write extra data. */
  public void writeExtra(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeInt(job);
    buffer.writeInt(biome);
  }

  /** Method can be over written to read extra data. */
  public void readExtra(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    job = buffer.readInt();
    biome = buffer.readInt();
  }
}
