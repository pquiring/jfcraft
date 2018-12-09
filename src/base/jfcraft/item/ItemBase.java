package jfcraft.item;

/** Base class for all registered Items (and Blocks)
 *
 * @author pquiring
 *
 * Created : May 2, 2014
 */

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.GL_FALSE;
import static javaforce.gl.GL.glUniformMatrix4fv;

import jfcraft.item.Item;
import jfcraft.opengl.*;
import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;
import jfcraft.item.*;
import static jfcraft.data.Types.*;

public class ItemBase implements RenderSource {
  public char id;
  public String blockName;
  public char blockID;  //item id -> block id
  public String seedPlantedName;
  public char seedPlantedID;
  public int maxStack = 64;
  public boolean isDamaged, isTool, isArmor, isFood, isVar, isDir, isDirXZ, isDirFace, isWeapon;
  public boolean isFuel, canBake, isSeeds, isGreen;
  public boolean canPlace, canPlaceInWater;
  public boolean cantGive;  //do not give with /give command
  public int tool, weapon, armor, heat;
  public String bakeName;
  public char bakeID;
  public String name;  //base name
  public String names[], images[];
  public AssetImage ai[];
  public SubTexture textures[];
  public float attackDmg = 1;
  public boolean useRelease;  //activate on release (bow)
  public int material;
  public RenderDest bufs[];  //inventory (vars)
  public boolean renderAsEntity;
  public int entityID;

  //armor info
  public Texture armorTextures[];
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
  public ItemBase(String name, String names[], String textures[]) {
    this.name = name;
    this.names = names;
    this.images = textures;
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
  public Item bake() {
    return null;
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
  }

  /** Add face for item as WorldItem. */
  public void addFaceWorldItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    tx1 = textures[var].x1;
    ty1 = textures[var].y1;
    tx2 = textures[var].x2;
    ty2 = textures[var].y2;
    float x1 = -0.5f;
    float y1 = 1.0f;
    float x2 = 0.5f;
    float y2 = 0.0f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,green ? Static.green : Static.white);
    //swap x and redo for other side
    x1 = x2;
    x2 = -0.5f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,green ? Static.green : Static.white);
  }

  public String getName() {
    return name;
  }

  /** Returns a class to receive the assigned ID.  The class must contain a public static char with the name of the item/block (all uppercase) */
  public Class getIDClass() {
    return Items.class;
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
    BlockBase block1 = Static.blocks.blocks[c.chunk.getID(c.gx,c.gy,c.gz)];
    BlockBase block2 = Static.blocks.blocks[c.chunk.getID2(c.gx,c.gy,c.gz)];
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
    {ANI_ADD ,-10,0,0 ,0,0,-0.5f ,5,3,0},  //b1
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //b2 (not used)
    {ANI_ADD ,10,0,0 ,0,0,0.5f ,5,0,0},  //b1 p2
  };

  private static float[][] aniDefend = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0  ,0,0,0 ,1,0,0},  //idle
    //hold in front at 45 deg angle
    {ANI_SET ,0,0,0  ,0,0,0 ,1,0,0},  //b1 (not used)
    {ANI_SET ,0,0,90 ,0,0,0 ,1,2,1},  //b2 : -1 steps means hold until b2 released
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
    {ANI_ADD ,0,0,0 ,0,-0.1f,0 ,5,4,1},  //b2 p2 -> p3
    {ANI_ADD ,0,0,0 ,0,0.1f,0 ,5,3,1},  //b2 p3 -> p2
  };

  private static float[][] aniBow = {
    //action, xAngle, yAngle, zAngle, xPos, yPos, zPos, steps, nextIdx, hold
    {ANI_SET ,0,0,0 ,0,0,0 ,1,0,0},  //idle
    //move up to center of screen and stretch
    {ANI_ADD ,-5,0,0 ,0,0,-0.1f ,10,0,0},  //b1
    {ANI_ADD ,-5,0,0 ,0,0,-0.1f ,10,0,0},  //b2
  };

  private int aniIdx, aniStep;
  private float aniData[][];

  public void animateReset() {
    aniIdx = 0;
    aniStep = 0;
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
      float steps = ad[7];
      if (ad[9] == 1f && !b2) {
        aniIdx = 0;
        aniStep = 0;
      } else {
        aniStep++;
        if (aniStep >= steps) {
          aniIdx = (int)ad[8];
          aniStep = 0;
        }
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
    if (isDirXZ)
      return Direction.S;
    else
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
      armorTextures = new Texture[armorTextureNames.length];
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

  private static RenderDest renderDest;
  public static boolean isBlock;
  public static boolean isEntity;
  public static Texture texture;
  public static EntityBase entity;

  public void buildBuffers(RenderDest dest, RenderData data) {
    renderDest = dest;
    isBlock = false;
    isEntity = false;
    ItemBase itembase = Static.items.items[id];
    if (itembase.renderAsEntity) {
      isEntity = true;
      entity = Static.entities.entities[itembase.entityID];
      entity.pos.x = 0;
      entity.pos.y = 0;
      entity.pos.z = 0;
      entity.ang.y = 180;
      entity.setScale(1.0f);
      if (data.hand == LEFT) {
        entity.setPart(EntityBase.L_ITEM);
      } else {
        entity.setPart(EntityBase.R_ITEM);
      }
    } else {
      texture = itembase.textures[0].texture;  //BUG : zero?
      itembase.addFaceWorldItem(renderDest.getBuffers(0), data.var[X], itembase.isGreen);
      buffersIdx = 0;
    }
  }

  public void bindTexture() {
    if (isEntity) {
      entity.bindTexture();
    } else {
      texture.bind();
    }
  }

  public void render() {
    if (isEntity) {
      entity.render();
    } else {
      RenderBuffers buf = renderDest.getBuffers(buffersIdx);
      buf.copyBuffers();
      buf.bindBuffers();
      buf.render();
    }
  }

  private static GLMatrix itemView = new GLMatrix();

  public void setViewMatrix(boolean left) {
    itemView.setIdentity();

    itemView.addRotate(90, 0, 1, 0);
    itemView.addRotate(90, 1, 0, 0);
    itemView.addScale(10, 10, 10);

    if (ItemBase.isBlock) {
      if (left) {
        itemView.addRotate(HumaniodBase.leftHandAngle.x, 1, 0, 0);
        itemView.addRotate(HumaniodBase.leftHandAngle.y, 0, 1, 0);
      } else {
        itemView.addRotate(HumaniodBase.rightHandAngle.x, 1, 0, 0);
        itemView.addRotate(HumaniodBase.rightHandAngle.y, 0, 1, 0);
      }
//      handMat.addRotate(baseHandAngle.z, 0, 0, 1);
      itemView.addTranslate(-0.5f, -0.5f, 0);
    }
    if (left) {
      itemView.addTranslate(HumaniodBase.leftHandPos.x, HumaniodBase.leftHandPos.y, HumaniodBase.leftHandPos.z);
    } else {
      itemView.addTranslate(HumaniodBase.rightHandPos.x, HumaniodBase.rightHandPos.y, HumaniodBase.rightHandPos.z);
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, itemView.m);  //view matrix
  }

  public void setViewMatrixSelf(boolean left, GLVector3 l3) {
    itemView.setIdentity();

    if (left) {
      itemView.addRotate(-90, 0, 1, 0);
      itemView.addRotate(90, 1, 0, 0);
      itemView.addScale(10, 10, 10);
    } else {
      //TODO : move these into HumaniodBase entity instance (arg) (amimated ang)
      itemView.addRotate(Static.client.handAngle.x, 1, 0, 0);
      itemView.addRotate(Static.client.handAngle.y, 0, 1, 0);
      itemView.addRotate(Static.client.handAngle.z, 0, 0, 1);
    }

    if (isBlock) {
      if (left) {
        itemView.addRotate(HumaniodBase.leftHandAngle.x, 1, 0, 0);
        itemView.addRotate(HumaniodBase.leftHandAngle.y, 0, 1, 0);
      } else {
        itemView.addRotate(HumaniodBase.rightHandAngle.x, 1, 0, 0);
        itemView.addRotate(HumaniodBase.rightHandAngle.y, 0, 1, 0);
      }
//      handMat.addRotate(baseHandAngle.z, 0, 0, 1);
      itemView.addTranslate(-0.5f, -0.5f, 0);
    }
    if (left) {
      itemView.addTranslate(HumaniodBase.leftHandPos.x, HumaniodBase.leftHandPos.y, HumaniodBase.leftHandPos.z);
    } else {
      itemView.addTranslate(HumaniodBase.rightHandPos.x, HumaniodBase.rightHandPos.y, HumaniodBase.rightHandPos.z);
    }
    //now apply hand animation
    if (left) {
      float raise = 0.2f * Static.client.player.blockCount;
      itemView.addTranslate(raise, raise, 0f);
    } else {
      //TODO : move these into HumaniodBase entity too (animated pos)
      itemView.addTranslate(Static.client.handPos.x, Static.client.handPos.y, Static.client.handPos.z);
    }

    glUniformMatrix4fv(Static.uniformMatrixView, 1, GL_FALSE, itemView.m);  //view matrix
  }

  public String toString() {
    return "Item:" + name;
  }
}
