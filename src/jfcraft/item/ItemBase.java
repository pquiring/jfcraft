package jfcraft.item;

/** Base class for all registered Items (and Blocks)
 *
 * @author pquiring
 *
 * Created : May 2, 2014
 */

import jfcraft.item.Item;
import jfcraft.opengl.*;
import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;
import jfcraft.item.*;
import static jfcraft.data.Types.*;
import static jfcraft.entity.EntityBase.*;

public class ItemBase implements RenderSource {
  public char id;
  public int maxStack = 64;

  //item id -> block id when placed
  public String blockName;
  public char blockID;  //looked up from blockName

  //item id -> block id when planted
  public String seedPlantedName;
  public char seedPlantedID;  //lookup up from seedPlantedName

  public boolean isDamaged;
  public boolean isTool;
  public boolean isArmor;
  public boolean isFood;
  public boolean isWeapon;
  public boolean isVar;  //has variations
  public boolean isFuel;
  public boolean canBake;
  public boolean isSeeds;
  public boolean isGreen;
  public boolean canPlace;
  public boolean canPlaceInWater;
  public boolean canUseLiquids;
  public boolean cantGive;  //do not give with /give command

  public boolean isDir;  //is placed based on angle player is facing (player XYZ) (wood)
  public boolean isDirXZ;  //is placed based on angle player is facing (player XZ only) (furnace)
  public boolean isDirFace;  //is placed based on face (face XYZ) (torch)
  public boolean isDirXZ_FaceY;  //is placed based on angle player is facing (player XZ only) except for A or B (face Y) (stairs)
  public boolean reverseDir;  //placed on opposite side

  public int tool;
  public int weapon;
  public int armor;
  public int heat;
  public String bakeName;
  public char bakeID;
  public String name;  //base name
  public String names[];
  public String images[];
  public AssetImage ai[];
  public SubTexture textures[];
  public float attackDmg = 1;
  public boolean useRelease;  //activate on release (bow)
  public int material;
  public RenderDest bufs[];  //inventory (per variant)
  public boolean renderAsEntity;
  public boolean renderAsArmor;
  public boolean renderAsItem;
  public int entityID;
  public float durability = 0.01f;  //good for 100 uses
  public int varMask = 0xf;  //variant mask (4 bits)
  public Voxel voxel[];  //items only (per variant)

  //armor info
  public TextureMap armorTextures[];
  public String armorTextureNames[];
  public float armorScales[];
  public int armorParts[][];

  public int buffersIdx;  //DEST_NORMAL or DEST_ALPHA

  public float hunger, saturation;

  public ItemBase() {
  }
  public ItemBase(String name) {
    this.name = name;
  }
  public ItemBase(String name, String names[]) {
    this.name = name;
    this.names = names;
  }
  public ItemBase(String name, String names[], String images[]) {
    this.name = name;
    this.names = names;
    this.images = images;
  }

  public ItemBase setMaxStack(int cnt) {
    maxStack = cnt;
    return this;
  }
  public ItemBase setTool(int type) {
    isDamaged = true;
    isTool = true;
    tool = type;
    maxStack = 1;
    return this;
  }
  public ItemBase setUseable() {
    isTool = true;
    tool = TOOL_OTHER;
    return this;
  }
  public ItemBase setWeapon(int type) {
    isDamaged = true;
    isWeapon = true;
    weapon = type;
    maxStack = 1;
    if (type == WEAPON_SWORD)
      setAniStyle(ANI_STYLE_ATTACK, ANI_STYLE_DEFEND);
    else if (type == WEAPON_BOW)
      setAniStyle(ANI_STYLE_ATTACK, ANI_STYLE_BOW);
    return this;
  }
  public ItemBase setArmor(int type) {
    isDamaged = true;
    isArmor = true;
    maxStack = 1;
    armor = type;
    return this;
  }
  public ItemBase setFood(float h, float s) {
    isFood = true;
    hunger = h;
    saturation = s;
    useRelease = true;
    setAniStyle(ANI_STYLE_ATTACK, ANI_STYLE_FOOD);
    return this;
  }
  public ItemBase setVar() {
    isVar = true;
    return this;
  }
  public ItemBase setMaterial(int type) {
    material = type;
    return this;
  }
  public ItemBase setFuel(int heat) {
    isFuel = true;
    this.heat = heat * 20;
    return this;
  }
  public ItemBase setBake(String name) {
    canBake = true;
    bakeName = name;
    return this;
  }
  public ItemBase setCanPlace() {
    canPlace = true;
    return this;
  }
  public ItemBase setBlockID(String id) {
    blockName = id;
    return this;
  }
  public ItemBase setDmg(float dmg) {
    attackDmg = dmg;
    return this;
  }
  public ItemBase setGreen() {
    isGreen = true;
    return this;
  }

  public boolean isItem() {
    return true;
  }

  public boolean isBlock() {
    return false;
  }

  public Item bake() {
    return null;
  }

  public Item toItem(int count) {
    Item item = new Item(id);
    item.count = (byte)count;
    if (isDamaged) item.dmg = 1f;
    return item;
  }

  public char getBlockID() {
    return blockID;
  }

  /** Add face for item on billboard. */
  public void addFaceInvItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    tx1 = textures[var].x1;
    ty1 = textures[var].y1;
    tx2 = textures[var].x2;
    ty2 = textures[var].y2;
    float x1 = 0;
    float y1 = 0;
    float x2 = 1;
    float y2 = 1;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,green ? Static.green : Static.white);
    //add face for viewing in hand
/*
    x1 = -0.5f;
    y1 = 1.0f;
    x2 = 0.5f;
    y2 = 0.0f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,green ? Static.green : Static.white);
*/
  }

  public String getName() {
    return name;
  }

  public String getName(int var) {
    if (isVar) return names[var]; else return names[0];
  }

  public String[] getNames() {
    return names;
  }

  public int getMaxVar() {
    if (isVar) {
      return textures.length / names.length;
    } else {
      return 0;
    }
  }

  public boolean canPlace(Coords c) {
    if (!canPlace) return false;
    BlockBase block1 = Static.blocks.blocks[c.chunk.getBlock(c.gx,c.gy,c.gz)];
    BlockBase block2 = Static.blocks.blocks[c.chunk.getBlock2(c.gx,c.gy,c.gz)];
    return block1.canReplace && block2.canReplace;
  }

  public final boolean equals(Item item) {
    Static.log("BIBase.equals() not allowed");
    return false;
  }
  public final boolean equals(ItemBase item) {
    Static.log("BIBase.equals() not allowed");
    return false;
  }
  public boolean useItem(Client client, Coords c) {
    if (isFood) {
      client.foodCounter++;
      if (client.foodCounter == 2 * 20) {
        client.foodCounter = 0;
        //eat it!
        Item item = client.player.items[client.player.activeSlot];
        item.count--;
        if (item.count == 0) {
          item.clear();
        }
        client.serverTransport.setInvItem((byte)client.player.activeSlot, item);
        client.player.saturation += saturation;
        client.player.hunger += hunger;
        if (client.player.hunger > 20) {
          client.player.hunger = 20;
        }
        if (client.player.saturation > client.player.hunger) {
          client.player.saturation = client.player.hunger;
        }
        client.serverTransport.sendHunger(client.player);
      }
      return true;
    }
    return false;
  }
  public void releaseItem(Client client) {
    if (isFood) {
      client.foodCounter = 0;
    }
  }
  public boolean place(Client client, Coords c) {
    if (blockID == 0) return false;
    BlockBase base = Static.blocks.blocks[blockID];
    return base.place(client, c);
  }

  private static final int ANI_STYLE_ATTACK = 0;
  private static final int ANI_STYLE_DEFEND = 1;
  private static final int ANI_STYLE_PLACE = 2;
  private static final int ANI_STYLE_FOOD = 3;
  private static final int ANI_STYLE_BOW = 4;

  private int aniStyle1 = ANI_STYLE_ATTACK;
  private int aniStyle2 = ANI_STYLE_PLACE;

  //actions
  private static final float ANI_SET = 0f;
  private static final float ANI_ADD = 1f;

  private static final int ANI_ACTION = 0;
  private static final int ANI_ANG_X = 1;
  private static final int ANI_ANG_Y = 2;
  private static final int ANI_ANG_Z = 3;
  private static final int ANI_POS_X = 4;
  private static final int ANI_POS_Y = 5;
  private static final int ANI_POS_Z = 6;
  private static final int ANI_STEPS = 7;
  private static final int ANI_NEXT = 8;
  private static final int ANI_HOLD = 9;

  public ItemBase setAniStyle(int b1, int b2) {
    aniStyle1 = b1;
    aniStyle2 = b2;
    return this;
  }

  private static float[][] aniNull = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //idle
    //hold in front at 45 deg angle
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //b1 (not used)
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //b2 (not used)
  };

  private static float[][] aniAttack = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //idle
    //move forward and tilt forward, then move down a bit and return
    {ANI_ADD ,0,0,15 ,0,0,-0.1f ,3,3,0},  //b1
    {ANI_SET ,0,0,0  ,0,0,0     ,1,0,0},  //b2 (not used)
    {ANI_ADD ,0,0,15 ,0,0,0     ,3,0,0},  //b1 p2
  };

  private static float[][] aniDefend = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0  ,0,0,0 ,1,0,0},  //idle
    //hold in front at 45 deg angle
    {ANI_SET ,0,0,0  ,0,0,0 ,1,0,0},  //b1 (not used)
    {ANI_SET ,0,0,90 ,0,0,0 ,1,2,1},  //b2 : hold until b2 released
  };

  private static float[][] aniPlace = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0},  //idle
    //move left and tilt on z axis to the left 90deg then return
    {ANI_ADD ,0,0,5 ,0.1f,0,0 ,5,3,0},  //b1 -> p2
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //b2 (not used)
    {ANI_ADD ,0,0,-5 ,0,-0.1f,0 ,5,0,0},  //b1 p2
  };

  private static float[][] aniFood = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //idle
    //move in front and then move up/down quickly
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //b1 (not used)
    {ANI_SET ,0,0,0 ,-3f,1f,0 ,1,3,0},  //b2 p1 -> p2
    {ANI_ADD ,0,0,0 ,0,-0.1f,0 ,5,4,0},  //b2 p2 -> p3
    {ANI_ADD ,0,0,0 ,0,0.1f,0 ,5,3,0},  //b2 p3 -> p2
  };

  private static float[][] aniBow = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //idle
    //move up to center of screen and stretch
    {ANI_ADD ,0,0,0 ,0,0.05f,0 ,5,0,0},  //b1 (not used)
    {ANI_ADD ,0,0,0 ,0,0.05f,0 ,5,3,1},  //b2 (hold)
  };

  private int aniIdx, aniStep, aniButton;
  private float aniData[][];

  public void animateReset() {
    aniIdx = 0;
    aniStep = 0;
    aniButton = 0;
  }

  public void animateItem(XYZ handAngle, XYZ handPos, boolean b1, boolean b2, boolean haveTarget, boolean moving) {
    switch (aniStyle1) {
      default:
      case 0: animateItemDefault(handAngle, handPos, b1, b2, haveTarget, moving);
    }
  }

  public void animateItemBobbing(XYZ handAngle, XYZ handPos) {
    //TODO
  }

  public void animateItemDefault(XYZ itemAngle, XYZ itemPos, boolean b1, boolean b2, boolean haveTarget, boolean moving) {
    if (aniIdx == 0) {
      itemAngle.x = 0;
      itemAngle.y = 0;
      itemAngle.z = 0;
      itemPos.x = 0;
      itemPos.y = 0;
      itemPos.z = 0;
      if (moving)  {
        if (Settings.current.doViewBobbing) animateItemBobbing(itemAngle, itemPos);
      }
      if (aniIdx == 0 && b1) {
        aniIdx = 1;
        aniStep = 0;
        aniButton = 1;
        switch (aniStyle1) {
          case ANI_STYLE_ATTACK: aniData = aniAttack; break;
          case ANI_STYLE_DEFEND: aniData = aniDefend; break;
          case ANI_STYLE_PLACE: aniData = aniPlace; break;
          case ANI_STYLE_FOOD: aniData = aniFood; break;
          case ANI_STYLE_BOW: aniData = aniBow; break;
        }
      }
      else if (aniIdx == 0 && b2) {
        aniIdx = 2;
        aniStep = 0;
        aniButton = 2;
        switch (aniStyle2) {
          case ANI_STYLE_ATTACK: aniData = aniAttack; break;
          case ANI_STYLE_DEFEND: {
            if (Static.client.player.hasShieldEquiped()) {
              aniData = aniNull;
            } else {
              aniData = aniDefend;
            }
            break;
          }
          case ANI_STYLE_PLACE: aniData = aniPlace; break;
          case ANI_STYLE_FOOD: aniData = aniFood; break;
          case ANI_STYLE_BOW: aniData = aniBow; break;
        }
      }
    } else {
      float ad[] = aniData[aniIdx];
      float steps = ad[ANI_STEPS];
      aniStep++;
      if (aniStep >= steps) {
        if (ad[ANI_HOLD] == 1f) {
          //hold until button released
          switch (aniButton) {
            case 1: if (!b1) animateReset(); break;
            case 2: if (!b2) animateReset(); break;
          }
          return;
        } else {
          aniIdx = (int)ad[ANI_NEXT];
          aniStep = 0;
        }
      }
      float act = ad[0];
      if (act == ANI_SET) {
        itemAngle.x = ad[1];
        itemAngle.y = ad[2];
        itemAngle.z = ad[3];
        itemPos.x = ad[4];
        itemPos.y = ad[5];
        itemPos.z = ad[6];
      } else if (act == ANI_ADD) {
        itemAngle.x += ad[1];
        itemAngle.y += ad[2];
        itemAngle.z += ad[3];
        itemPos.x += ad[4];
        itemPos.y += ad[5];
        itemPos.z += ad[6];
      }
    }
  }

  public ItemBase setDir() {
    isDir = true;
    return this;
  }
  public ItemBase setDirFace() {
    isDirFace = true;
    return this;
  }
  public ItemBase setCantGive() {
    cantGive = true;
    return this;
  }

  public ItemBase setDurability(float uses) {
    durability = 1.0f / uses;
    return this;
  }

  public void assignID(char id) {
    this.id = id;
  }

  /** Called after all IDs have been assigned, retrieve any IDs needed for logic. (server & client side) */
  public void getIDs(World world) {
    if (bakeName != null) {
      bakeID = world.getBlockID(bakeName);
    }
    if (blockName != null) {
      blockID = world.getBlockID(blockName);
    }
    if (seedPlantedName != null) {
      seedPlantedID = world.getBlockID(seedPlantedName);
    }
  }

  /** Returned preferred dir to show item/block in inventory. */
  public int getPreferredDir() {
    if (isDirXZ || isDirFace) return Direction.S;
    return Direction.A;
  }

  public ItemBase setArmorTextures(String names[], float scales[], int parts[][]) {
    armorTextureNames = names;
    armorScales = scales;
    armorParts = parts;
    return this;
  }

  public int getArmorLayers() {
    return armorParts.length;
  }

  public int getArmorParts(int layer) {
    return armorParts[layer].length;
  }

  public void bindArmorTexture(int layer) {
    if (armorTextures == null) {
      armorTextures = new TextureMap[armorTextureNames.length];
      for(int a=0;a<armorTextureNames.length;a++) {
        armorTextures[a] = Textures.getTexture(armorTextureNames[a], 0);
      }
    }
    armorTextures[layer].bind();
  }

  public float getArmorScale(int layer) {
    return armorScales[layer];
  }

  public int getArmorPart(int layer, int partidx) {
    return armorParts[layer][partidx];
  }

  public RenderDest getDest() {
    int idx = 0;
    if (isVar) {
      idx = Static.data.var[X] & varMask;
    }
    if (idx >= bufs.length) {
      Static.log("Error:idx > bufs.length:" + this);
      idx = bufs.length - 1;
    }
    return bufs[idx];
  }

  public void buildBuffers(RenderDest dest) {
    if (renderAsEntity && !renderAsItem) return;
    addFaceInvItem(dest.getBuffers(0), Static.data.var[X], isGreen);
    buffersIdx = 0;
  }

  public void bindTexture() {
    if (renderAsEntity || (renderAsArmor && Static.data.part != NONE)) {
      EntityBase entity = Static.entities.entities[entityID];
      entity.bindTexture();
    } else {
      if (voxel != null && !Static.data.inventory) {
        voxel[isVar ? Static.data.var[X] & varMask : 0].bindTexture();
      } else {
        if (textures.length == 0) {
          Static.logTrace("Error:ItemBase.bindTexture() : no textures");
          return;
        }
        textures[isVar ? Static.data.var[X] & varMask : 0].texture.bind();
      }
    }
  }

  /*
  There are 4 main areas where items are rendered:
    WorldItem.render()                [voxel or entity rotating]
    HumanoidBase.renderItemInHand()   [voxel or entity in hand]
    HumanoidBase.renderArmor()        [render as entity over body]
    RenderScreen.renderItem()         [flat face or entity (not voxel)]
  */

  public void render() {
    if (renderAsArmor && Static.data.part != NONE) {
      EntityBase entity = Static.entities.entities[entityID];
      entity.pos.copy(Static.data.pos);
      entity.ang.copy(Static.data.ang);
      entity.setScale(Static.data.scale);
      entity.setPart(Static.data.part);
      entity.bindTexture();
      entity.render();
    } else if (renderAsEntity) {
      EntityBase entity = Static.entities.entities[entityID];
      entity.pos.copy(Static.data.pos);
      entity.ang.copy(Static.data.ang);
      entity.setScale(Static.data.scale);
      entity.bindTexture();
      entity.render();
    } else {
      if (bufs == null || bufs.length == 0) {
        Static.log("ERROR:ItemBase.render() no bufs:name=" + getName());
        return;
      }
      if (textures == null || textures.length == 0) {
        Static.log("ERROR:ItemBase.render() no textures:name=" + getName());
        return;
      }
      Static.data.dir[X] = getPreferredDir();
      bindTexture();
      if (voxel != null && !Static.data.inventory) {
        voxel[isVar ? Static.data.var[X] : 0].render();
      } else {
        RenderBuffers buf = getDest().getBuffers(buffersIdx);
        buf.bindBuffers();
        buf.render();
      }
    }
  }

  /** Create Item to render in inventory screen. */
  public void createItem(int var) {
    if (bufs[var] == null) {
      bufs[var] = new RenderDest(1);
    }
    buildBuffers(bufs[var]);
    bufs[var].getBuffers(0).copyBuffers();
  }

  /** Create Item voxel to render in player hand. */
  public void createVoxel(int var) {
    if (voxel[var] == null) {
      voxel[var] = new Voxel(this, var);
    }
    voxel[var].buildBuffers(voxel[var].dest);
    voxel[var].dest.getBuffers(0).copyBuffers();
  }

  public Voxel getVoxel(int var) {
    return voxel[var];
  }

  public String toString() {
    return "Item:" + name;
  }
}
