package jfcraft.entity;

/** Horse entity.
 *
 * @author pquiring
 *
 * Created : Jun 26, 2015
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.audio.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.move.*;
import jfcraft.opengl.*;

public class Horse extends VehicleBase {
  public float walkAngle;  //angle of legs/arms as walking
  public float walkAngleDelta;
  public static RenderDest dest;

  public int type;
  public int pattern;
  public int tameCounter;  //not saved to disk
  public static final int tameCounterMax = 20 * 15;
  public ExtraHorse inventory;

  private static Model model;

  public int hearts;

  public static final int FLAG_TAMED = 1;
  public static final int FLAG_SADDLE = 2;
  public static final int FLAG_CHEST = 4;
  public static final int FLAG_ARMOR_IRON = 8;
  public static final int FLAG_ARMOR_GOLD = 16;
  public static final int FLAG_ARMOR_DIAMOND = 32;

  public boolean isTamed() {
    return (flags & FLAG_TAMED) != 0;
  }

  public void setTamed(boolean tamed) {
    if (tamed) {
      flags |= FLAG_TAMED;
    } else {
      flags &= -1 - FLAG_TAMED;
    }
  }

  public boolean haveSaddle() {
    return (flags & FLAG_SADDLE) != 0;
  }

  public void setHaveSaddle(boolean saddle) {
    if (saddle) {
      flags |= FLAG_SADDLE;
    } else {
      flags &= -1 - FLAG_SADDLE;
    }
  }

  public boolean haveChest() {
    return (flags & FLAG_CHEST) != 0;
  }

  public void setHaveChest(boolean flag) {
    if (flag) {
      flags |= FLAG_CHEST;
    } else {
      flags &= -1 - FLAG_CHEST;
    }
  }

  public boolean haveArmor(int flag) {
    return (flags & flag) != 0;
  }

  public static final int TYPE_DONKEY = 0;
  public static final int TYPE_MULE = 1;
  public static final int TYPE_SKELETON = 2;
  public static final int TYPE_ZOMBIE = 3;
  //pattern applies to following only
  public static final int TYPE_BLACK = 4;
  public static final int TYPE_BROWN = 5;
  public static final int TYPE_CHESTNUT = 6;
  public static final int TYPE_CREAMY = 7;
  public static final int TYPE_DARKBROWN = 8;
  public static final int TYPE_GRAY = 9;
  public static final int TYPE_WHITE = 10;

  public static final int PATTERN_NONE = 0;
  public static final int PATTERN_BLACKDOTS = 11;
  public static final int PATTERN_WHITE = 12;
  public static final int PATTERN_WHITEDOTS = 13;
  public static final int PATTERN_WHITEFIELD = 14;

  public static final int ARMOR_IRON = 15;
  public static final int ARMOR_GOLD = 16;
  public static final int ARMOR_DIAMOND = 17;

  //render assets
  public static TextureMap textures[];
  public static Vertex3 org[];

  public static int initHealth = 10;

  public Horse() {
    id = Entities.HORSE;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "horse";
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.0f;
    height2 = height/2;
    depth = 1.5f;
    depth2 = depth/2;
    walkAngleDelta = 5.0f;
    angX = 45f;
    if (world.isServer) {
      eyeHeight = 0.5f;
      jumpVelocity = 0.58f;  //results in jump of 1.42
      //speeds are blocks per second
      walkSpeed = 3.0f;
      runSpeed = 4.0f;
      walkSpeedOccupant = 11.0f;
      runSpeedOccupant = 14.0f;
      sneakSpeed = 1.3f;
      swimSpeed = (walkSpeed / 2.0f);
    }
    setMove(new MoveHorse());
  }

  public void initStatic() {
    super.initStatic();
    dest = new RenderDest(parts.length);
    model = loadModel("horse");
  }

  public void initStaticGL() {
    super.initStaticGL();
    textures = new TextureMap[textureNames.length];
    int unit = 0;
    for(int a=0;a<textureNames.length;a++) {
      if (a == PATTERN_BLACKDOTS) unit = 2;  //markings
      if (a == ARMOR_IRON) unit = 3;  //armor
      textures[a] = Textures.getTexture(textureNames[a], unit);
    }
  }

  private static final String textureNames[] = {
    "entity/horse/donkey",
    "entity/horse/mule",
    "entity/horse/horse_skeleton",
    "entity/horse/horse_zombie",
    //these can have markings
    "entity/horse/horse_black",
    "entity/horse/horse_brown",
    "entity/horse/horse_chestnut",
    "entity/horse/horse_creamy",
    "entity/horse/horse_darkbrown",
    "entity/horse/horse_gray",
    "entity/horse/horse_white",
    //markings (texture unit #2) (overlay)
    "entity/horse/horse_markings_blackdots",
    "entity/horse/horse_markings_whitedots",  //should be just "white" but missing texture
    "entity/horse/horse_markings_whitedots",
    "entity/horse/horse_markings_whitefield",
    //armor (texture unit #3) (overlay)
    "entity/horse/armor/horse_armor_iron",
    "entity/horse/armor/horse_armor_gold",
    "entity/horse/armor/horse_armor_diamond",
  };

  private static String parts[] = {
    "HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG"
    , "L_ARM_LOWER", "R_ARM_LOWER", "L_ARM_HOOVE", "R_ARM_HOOVE"
    , "L_LEG_LOWER", "R_LEG_LOWER", "L_LEG_HOOVE", "R_LEG_HOOVE"
    , "L_EAR_SHORT", "R_EAR_SHORT", "L_EAR_LONG", "R_EAR_LONG"
    , "TAIL_1", "TAIL_2", "TAIL_3"
    , "MANE", "NECK", "JAW_LOWER", "JAW_UPPER"
    , "CHEST", "SADDLE_SEAT", "SADDLE_HEAD"
  };

  //0-5 = HEAD, BODY, ARMs, LEGs
  private static final int L_ARM_LOWER = 6;
  private static final int R_ARM_LOWER = 7;
  private static final int L_ARM_HOOVE = 8;
  private static final int R_ARM_HOOVE = 9;
  private static final int L_LEG_LOWER = 10;
  private static final int R_LEG_LOWER = 11;
  private static final int L_LEG_HOOVE = 12;
  private static final int R_LEG_HOOVE = 13;
  private static final int L_EAR_SHORT = 14;
  private static final int R_EAR_SHORT = 15;
  private static final int L_EAR_LONG = 16;
  private static final int R_EAR_LONG = 17;
  private static final int TAIL_1 = 18;
  private static final int TAIL_2 = 19;
  private static final int TAIL_3 = 20;
  private static final int MANE = 21;
  private static final int NECK = 22;
  private static final int JAW_LOWER = 23;
  private static final int JAW_UPPER = 24;
  private static final int CHEST = 25;
  private static final int SADDLE_SEAT = 26;
  private static final int SADDLE_HEAD = 27;

  private static int commonParts[] = {
    HEAD,BODY,NECK,JAW_LOWER,JAW_UPPER
    , L_ARM,R_ARM,L_LEG,R_LEG,L_ARM_LOWER,R_ARM_LOWER,L_LEG_LOWER,R_LEG_LOWER
    , L_ARM_HOOVE,R_ARM_HOOVE,L_LEG_HOOVE,R_LEG_HOOVE,
  };

  private static int extraParts[][] = {
    {L_EAR_LONG, R_EAR_LONG, TAIL_1, TAIL_2, TAIL_3},  //donkey
    {L_EAR_LONG, R_EAR_LONG, TAIL_1, TAIL_2, TAIL_3},  //mule
    {TAIL_1},  //skeleton
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //zombie
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //black
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //brown
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //chestnet
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //creamy
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //darkbrown
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //gray
    {L_EAR_SHORT, R_EAR_SHORT, MANE, TAIL_1, TAIL_2, TAIL_3},  //white
  };

  public void buildBuffers(RenderDest dest, RenderData data) {
    org = new Vertex3[parts.length];
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
      org[a] = obj.org;
    }
  }

  public void bindTexture() {
    textures[type].bind();
    if (pattern != PATTERN_NONE) {
      textures[pattern].bind();
      glUniform1i(Static.uniformEnableHorsePattern, 1);
    }
    if (haveArmor(FLAG_ARMOR_IRON)) {
      textures[ARMOR_IRON].bind();
      glUniform1i(Static.uniformEnableHorseArmor, 1);
    }
    else if (haveArmor(FLAG_ARMOR_GOLD)) {
      textures[ARMOR_GOLD].bind();
      glUniform1i(Static.uniformEnableHorseArmor, 1);
    }
    else if (haveArmor(FLAG_ARMOR_DIAMOND)) {
      textures[ARMOR_DIAMOND].bind();
      glUniform1i(Static.uniformEnableHorseArmor, 1);
    }
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  //transforms are applied in reverse
  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case JAW_LOWER:
        //must rotate parent part first
        mat.addTranslate2(org[NECK].x, org[NECK].y, org[NECK].z);
        mat.addRotate2(-ang.x, 1, 0, 0);
        mat.addTranslate2(-org[NECK].x, -org[NECK].y, -org[NECK].z);
        mat.addTranslate2(org[JAW_LOWER].x, org[JAW_LOWER].y, org[JAW_LOWER].z);
        mat.addRotate2(-10f, 1, 0, 0);
        mat.addTranslate2(-org[JAW_LOWER].x, -org[JAW_LOWER].y, -org[JAW_LOWER].z);
        break;
      case JAW_UPPER:
        //must rotate parent part first
        mat.addTranslate2(org[NECK].x, org[NECK].y, org[NECK].z);
        mat.addRotate2(-ang.x, 1, 0, 0);
        mat.addTranslate2(-org[NECK].x, -org[NECK].y, -org[NECK].z);
        mat.addTranslate2(org[JAW_UPPER].x, org[JAW_UPPER].y, org[JAW_UPPER].z);
        mat.addRotate2(+10f, 1, 0, 0);
        mat.addTranslate2(-org[JAW_UPPER].x, -org[JAW_UPPER].y, -org[JAW_UPPER].z);
        break;
      case L_EAR_SHORT:
      case R_EAR_SHORT:
      case L_EAR_LONG:
      case R_EAR_LONG:
      case HEAD:
      case NECK:
      case MANE:
      case SADDLE_HEAD:
        mat.addTranslate2(org[NECK].x, org[NECK].y, org[NECK].z);
        mat.addRotate2(-ang.x, 1, 0, 0);
        mat.addTranslate2(-org[NECK].x, -org[NECK].y, -org[NECK].z);
        break;
      case BODY:
      case SADDLE_SEAT:
        break;
      case L_ARM:
      case L_ARM_LOWER:
      case L_ARM_HOOVE:
        mat.addTranslate2(org[L_ARM].x, org[L_ARM].y, org[L_ARM].z);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(-org[L_ARM].x, -org[L_ARM].y, -org[L_ARM].z);
        break;
      case R_LEG:
      case R_LEG_LOWER:
      case R_LEG_HOOVE:
        mat.addTranslate2(org[R_LEG].x, org[R_LEG].y, org[R_LEG].z);
        mat.addRotate2(-walkAngle, 1, 0, 0);
        mat.addTranslate2(-org[R_LEG].x, -org[R_LEG].y, -org[R_LEG].z);
        break;
      case R_ARM:
      case R_ARM_LOWER:
      case R_ARM_HOOVE:
        mat.addTranslate2(org[R_ARM].x, org[R_ARM].y, org[R_ARM].z);
        mat.addRotate2(walkAngle, 1, 0, 0);
        mat.addTranslate2(-org[R_ARM].x, -org[R_ARM].y, -org[R_ARM].z);
        break;
      case L_LEG:
      case L_LEG_LOWER:
      case L_LEG_HOOVE:
        mat.addTranslate2(org[L_LEG].x, org[L_LEG].y, org[L_LEG].z);
        mat.addRotate2(walkAngle, 1, 0, 0);
        mat.addTranslate2(-org[L_LEG].x, -org[L_LEG].y, -org[L_LEG].z);
        break;
      case TAIL_1:
        mat.addTranslate2(org[TAIL_1].x, org[TAIL_1].y, org[TAIL_1].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_1].x, -org[TAIL_1].y, -org[TAIL_1].z);
        break;
      case TAIL_2:
        mat.addTranslate2(org[TAIL_1].x, org[TAIL_1].y, org[TAIL_1].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_1].x, -org[TAIL_1].y, -org[TAIL_1].z);
        mat.addTranslate2(org[TAIL_2].x, org[TAIL_2].y, org[TAIL_2].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_2].x, -org[TAIL_2].y, -org[TAIL_2].z);
        break;
      case TAIL_3:
        mat.addTranslate2(org[TAIL_1].x, org[TAIL_1].y, org[TAIL_1].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_1].x, -org[TAIL_1].y, -org[TAIL_1].z);
        mat.addTranslate2(org[TAIL_2].x, org[TAIL_2].y, org[TAIL_2].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_2].x, -org[TAIL_2].y, -org[TAIL_2].z);
        mat.addTranslate2(org[TAIL_3].x, org[TAIL_3].y, org[TAIL_3].z);
        mat.addRotate2(+30, 1, 0, 0);
        mat.addTranslate2(-org[TAIL_3].x, -org[TAIL_3].y, -org[TAIL_3].z);
        break;
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public void render() {
    for(int a=0;a<commonParts.length;a++) {
      int part = commonParts[a];
      RenderBuffers buf = dest.getBuffers(part);
      setMatrixModel(part, buf);
      buf.bindBuffers();
      buf.render();
    }
    int ep[] = extraParts[type];
    int cnt = ep.length;
    for(int a=0;a<cnt;a++) {
      int part = ep[a];
      RenderBuffers buf = dest.getBuffers(part);
      setMatrixModel(part, buf);
      buf.bindBuffers();
      buf.render();
    }
    glUniform1i(Static.uniformEnableHorsePattern, 0);
    glUniform1i(Static.uniformEnableHorseArmor, 0);
    if (haveChest()) {
      int part = CHEST;
      RenderBuffers buf = dest.getBuffers(part);
      setMatrixModel(part, buf);
      buf.bindBuffers();
      buf.render();
    }
    if (haveSaddle()) {
      int part = SADDLE_SEAT;
      RenderBuffers buf = dest.getBuffers(part);
      setMatrixModel(part, buf);
      buf.bindBuffers();
      buf.render();
      part = SADDLE_HEAD;
      buf = dest.getBuffers(part);
      setMatrixModel(part, buf);
      buf.bindBuffers();
      buf.render();
    }
  }

  private float nextFloat() {
    return r.nextFloat() * 3f - 1.5f;
  }

  public void ctick() {
    float delta = 0;
    switch (mode) {
      case MODE_IDLE:
        walkAngle = 0.0f;
        break;
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
    if (mode != MODE_IDLE) {
      walkAngle += delta;
      if ((walkAngle < -45.0) || (walkAngle > 45.0)) {
        walkAngleDelta *= -1;
      }
    }
    if (hearts > 0) {
      hearts--;
      Chunk chunk = getChunk();
      Particle p = new Particle(pos.x + nextFloat(), pos.y, pos.z + nextFloat(), Particle.t_heart, true);
      p.init(chunk.world);
      p.maxAge = r.nextInt(20) + 20 * 2;
      p.scale = r.nextFloat() / 20f + 0.3f;
      p.setMove(new MoveFloat(0,0.1f,0));
      chunk.addEntity(p);
    }
  }

  public int getMenu() {
    if (!isTamed()) return Client.INVENTORY;
    return Client.HORSE;
  }

  private static Random r = new Random();
  public EntityBase spawn(Chunk chunk) {
    World world = Static.server.world;
    float px = r.nextInt(16) + chunk.cx * 16.0f + 0.5f;
    float pz = r.nextInt(16) + chunk.cz * 16.0f + 0.5f;
    for(float gy = 255;gy>0;gy--) {
      float py = gy;
      if (world.isEmpty(chunk.dim,px,py,pz)
        && world.isEmpty(chunk.dim,px,py-1,pz)
        && world.canSpawnOn(chunk.dim,px,py-2,pz))
      {
        py -= 1.0f;
        Horse e = new Horse();
        e.init(world);
        e.type = r.nextInt(11);  //0-10
        if (e.type == TYPE_SKELETON || e.type == TYPE_ZOMBIE) {
          e.type = TYPE_BLACK;
        }
        if (e.type > 3) {
          //pattern = 0,11,12,13,14
          e.pattern = r.nextInt(5);
          if (e.pattern > 0) {
            e.pattern += 10;
          }
        }
        e.inventory = new ExtraHorse(false);
        e.inventory.horse = e;
        if (e.type < 2) {
          //donkey and mule can not use armor, place OBSIDIAN there
          e.inventory.items[ExtraHorse.ARMOR].id = Blocks.OBSIDIAN;
        }
        e.dim = chunk.dim;
        e.health = initHealth;
        e.pos.x = px;
        e.pos.y = py;
        e.pos.z = pz;
        e.ang.x = 45f;
        e.ang.y = r.nextInt(360);
        return e;
      }
    }
    return null;
  }

  public float getSpawnRate() {
    return 2.0f;
  }

  public boolean canUse() {
    return true;
  }

  public void useEntity(Client client, boolean sneak) {
    synchronized(this) {
      Item item = client.player.items[client.player.activeSlot];
      if (item.id != 0) {
        if (item.id == Blocks.CHEST) {
          //add chest to mule/donkey
          if (type > 1) return;  //not a mule/donkey
          synchronized(this) {
            if (haveChest()) return;  //already have a chest
            Item saddle = inventory.items[ExtraHorse.SADDLE];
            Item armor = inventory.items[ExtraHorse.ARMOR];
            inventory = new ExtraHorse(true);
            inventory.horse = this;
            inventory.items[ExtraHorse.SADDLE] = saddle;
            inventory.items[ExtraHorse.ARMOR] = armor;
            setHaveChest(true);
            item.count--;
            if (item.count == 0) item.clear();
            client.serverTransport.setInvItem((byte)client.player.activeSlot, item);
          }
          Static.server.broadcastEntityFlags(this);
        }
        //TODO : apple, carrot, etc.
      }
      if (sneak) {
        //access inventory directly
        if (!isTamed()) return;
        synchronized(client.lock) {
          client.container = inventory;
          client.menu = Client.HORSE;
          client.serverTransport.setContainer(client.container);
          client.serverTransport.enterMenu(client.menu);
        }
        return;
      }
      //else try to mount horse
      if (occupant != null) return;  //in use
      resetControls();
      occupant = client.player;
      Chunk chunk1 = occupant.getChunk();
      client.player.vehicle = this;
      Static.server.broadcastRiding(this, occupant, true);
      client.player.pos.x = pos.x;
      client.player.pos.y = pos.y - occupant.legLength + 1.5f;
      client.player.pos.z = pos.z;
      Static.server.broadcastEntityMove(client.player, true);
      Chunk chunk2 = occupant.getChunk();
      if (chunk2 != chunk1) {
        chunk1.delEntity(occupant);
        chunk2.addEntity(occupant);
      }
    }
  }

  public boolean canSelect() {
    return true;
  }

  public void despawn() {
    if (occupant != null) {
      occupant.vehicle = null;
      Static.server.broadcastRiding(this, occupant, false);
      occupant = null;
    }
    super.despawn();
  }

  public Item[] drop() {
    int cnt = 0;
    for(int a=0;a<inventory.items.length;a++) {
      if (a == ExtraHorse.ARMOR && inventory.items[a].id == Blocks.OBSIDIAN) {
        continue;
      }
      if (inventory.items[a].id != 0) cnt++;
    }
    if (haveChest()) cnt++;
    Item items[] = new Item[cnt];
    int idx = 0;
    if (haveChest()) {
      items[idx] = new Item();
      items[idx].id = Blocks.CHEST;
      items[idx].count = 1;
      idx++;
    }
    for(int a=0;a<inventory.items.length;a++) {
      if (a == ExtraHorse.ARMOR && inventory.items[a].id == Blocks.OBSIDIAN) {
        continue;
      }
      if (inventory.items[a].id == 0) continue;
      items[idx] = new Item();
      items[idx].copy(inventory.items[a]);
      idx++;
    }
    return items;
  }

  public void hit() {
    super.hit();
    if (sound == 0) {
      sound = 2 * 20;
//      Static.server.broadcastSound(dim, pos.x, pos.y, pos.z, Sounds.SOUND_HORSE, 1);
    }
  }

  public int[] getGenerateDims() {
    return new int[] {Dims.EARTH};
  }

  public void checkFlags() {
    int newFlags = 0;
    if (isTamed()) newFlags |= FLAG_TAMED;
    if (haveChest()) newFlags |= FLAG_CHEST;
    Static.log("inv=" + inventory);
    Static.log("inv.items=" + inventory.items);
    if (inventory.items[ExtraHorse.SADDLE].id == Items.SADDLE) {
      newFlags |= FLAG_SADDLE;
    }
    char aid = inventory.items[ExtraHorse.ARMOR].id;
    if (aid == Items.HORSE_ARMOR_IRON) {
      newFlags |= FLAG_ARMOR_IRON;
    }
    else if (aid == Items.HORSE_ARMOR_GOLD) {
      newFlags |= FLAG_ARMOR_GOLD;
    }
    else if (aid == Items.HORSE_ARMOR_DIAMOND) {
      newFlags |= FLAG_ARMOR_DIAMOND;
    }
    if (newFlags != flags) {
      flags = newFlags;
      dirty = true;
      Static.server.broadcastEntityFlags(this);
    }
  }

  public void convertIDs(char blockIDs[], char itemIDs[]) {
    int cnt = inventory.items.length;
    for(int a=0;a<cnt;a++) {
      char id = inventory.items[a].id;
      if (Static.isBlock(id)) {
        id = blockIDs[id];
      } else {
        id = (char)(itemIDs[id - Items.FIRST_ID] + Items.FIRST_ID);
      }
      inventory.items[a].id = id;
    }
  }

  public void setFlags(int newFlags) {
    if (((newFlags & FLAG_TAMED) != 0) && ((flags & FLAG_TAMED) == 0)) {
      //just tamed : show heart particles
      hearts = 3 * 20;
    }
    super.setFlags(newFlags);
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeByte((byte)type);
    buffer.writeByte((byte)pattern);
    inventory.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    type = buffer.readByte();
    pattern = buffer.readByte();
    inventory = new ExtraHorse();
    inventory.horse = this;
    inventory.read(buffer, file);
    return true;
  }
}
