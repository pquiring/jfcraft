package jfcraft.entity;

/** Base class for all Humaniod entities that can equip armor and carry items.
 * Basically anything with the same shape as Player.
 *
 * @author pquiring
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.block.*;
import jfcraft.opengl.*;
import jfcraft.packet.*;
import static jfcraft.data.Direction.*;

public abstract class HumaniodBase extends CreatureBase {
  public Item items[];
  public int activeSlot;  //0-8
  public Item armors[];

  public static final byte items_active_slots = 9;  //0-8
  public static final byte items_inventory = 3*9;  //9-35
  public static final byte shield_idx = 4*9;  //36

  private static Model body;
  private static RenderDest body_dest;
  private static String body_parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG"};
  private static final int part_head = 0;
  private static final int part_body = 1;
  private static final int part_left_arm = 2;
  private static final int part_right_arm = 3;
  private static final int part_left_leg = 4;
  private static final int part_right_leg = 5;

  public boolean disable_cull_face = false;

  public void initStatic() {
    super.initStatic();
    body = loadModel("armor");
    body_dest = new RenderDest(body_parts.length);
  }

  public RenderDest getDest() {
    return body_dest;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    //BUG : some derived classes to DOT call this base method
    //transfer data into dest
    for(int a=0;a<body_parts.length;a++) {
      RenderBuffers buf = body_dest.getBuffers(a);
      Object3 obj = body.getObject(body_parts[a]);
      if (obj == null) {
        System.out.println("Warning:Couldn't find part:" + body_parts[a]);
      }
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
      buf.calcCenter();
    }
  }

  public void copyBuffers() {
    body_dest.copyBuffers();
  }

  public HumaniodBase(int itemCnt, int armorCnt) {
    items = new Item[itemCnt];
    for(int a=0;a<itemCnt;a++) {
      items[a] = new Item();
    }
    armors = new Item[armorCnt];
    for(int a=0;a<armorCnt;a++) {
      armors[a] = new Item();
    }
  }

  public Item getRightItem() {
    return items[activeSlot];
  }

  public Item getLeftItem() {
    if (items == null || items.length <= shield_idx) return null;
    return items[shield_idx];
  }

  public void render() {
    if (disable_cull_face) glDisable(GL_CULL_FACE);
    RenderDest dest = getDest();
    int cnt = dest.count();
    for(int a=0;a<cnt;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(a, buf);
      buf.bindBuffers();
      buf.render();
    }
    if (disable_cull_face) glEnable(GL_CULL_FACE);
    renderArmor();
    renderItemInHand(getRightItem(), 1.0f, false);
    renderItemInHand(getLeftItem(), 1.0f, true);
  }

  public void renderArmor() {
    int cnt = armors.length;
    for(int a=0;a<cnt;a++) {
      char id = armors[a].id;
      if (id == 0) continue;
      ItemBase item = Static.items.items[id];
      int layers = item.getArmorLayers();
      for(int layer=0;layer<layers;layer++) {
        int parts = item.getArmorParts(layer);
        item.bindArmorTexture(layer);
        scale = item.getArmorScale(layer);
        for(int partidx=0;partidx<parts;partidx++) {
          int part = item.getArmorPart(layer, partidx);
          RenderBuffers buf = body_dest.getBuffers(part);
          setMatrixModel(part, buf);
          buf.bindBuffers();
          buf.render();
        }
      }
    }
    scale = 1.0f;
  }

  private static RenderData render_data = new RenderData();
  private static RenderDest render_dest = new RenderDest(Chunk.DEST_COUNT);

  public static final XYZ blockRightHandAngle = new XYZ(0.0f, -45.0f, 25f);
  public static final XYZ blockRightHandPos = new XYZ(3f, -4f, -5.0f);
  public static final XYZ blockLeftHandAngle = new XYZ(0.0f, -45.0f, -25f);
  public static final XYZ blockLeftHandPos = new XYZ(-3f, -4f, -5.0f);

  public static final XYZ itemRightHandAngle = new XYZ(-35.0f, -45.0f, 25f);
  public static final XYZ itemRightHandPos = new XYZ(3f, -3.5f, -5.0f);
  public static final XYZ itemLeftHandAngle = new XYZ(90f, -90.0f, 0f);
  public static final XYZ itemLeftHandPos = new XYZ(6f, -10f, -5.0f);

  public void renderItemInHand(Item item, float light, boolean left) {
    if (item == null || item.count == 0) return;
    ItemBase itembase = Static.items.items[item.id];
    if (Static.isBlock(item.id)) {
      BlockBase block = Static.blocks.blocks[item.id];
      if (block.isDirXZ) {
        render_data.dir[X] = S;
      } else {
        render_data.dir[X] = A;
      }
    }
    itembase.buildBuffers(render_dest, render_data);

    itembase.setViewMatrix(left);

    itembase.bindTexture();

    itembase.render();
  }

  public Item getRightArmItem() {
    return null;
  }

  public Item getLeftArmItem() {
    return null;
  }

  public void convertIDs(char blockIDs[], char itemIDs[]) {
    int cnt = items.length;
    for(int a=0;a<cnt;a++) {
      char id = items[a].id;
      if (Static.isBlock(id)) {
        id = blockIDs[id];
      } else {
        id = (char)(itemIDs[id - Items.FIRST_ID] + Items.FIRST_ID);
      }
      items[a].id = id;
    }
    cnt = armors.length;
    for(int a=0;a<cnt;a++) {
      char id = armors[a].id;
      if (Static.isBlock(id)) {
        id = blockIDs[id];
      } else {
        id = (char)(itemIDs[id - Items.FIRST_ID] + Items.FIRST_ID);
      }
      armors[a].id = id;
    }
  }

  public Item[] drop() {
    boolean player = id == Entities.PLAYER;
    if (player && !Settings.current.dropItemsOnDeath) return new Item[0];
    Item allItems[] = new Item[items.length + armors.length];
    int p = 0;
    for(int a=0;a<items.length;a++) {
      allItems[p] = new Item();
      allItems[p++].copy(items[a]);
      if (player) items[a].clear();
    }
    for(int a=0;a<armors.length;a++) {
      allItems[p] = new Item();
      allItems[p++].copy(armors[a]);
      if (player) armors[a].clear();
    }
    return allItems;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    if (file) {
      byte cnt = (byte)items.length;
      buffer.writeByte(cnt);
      for(int a=0;a<cnt;a++) {
        items[a].write(buffer, file);
      }
    } else {
      //only send entity active slot
      items[activeSlot].write(buffer, file);
    }
    byte cnt = (byte)armors.length;
    buffer.writeByte(cnt);
    for(int a=0;a<armors.length;a++) {
      armors[a].write(buffer, file);
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    if (file) {
      int cnt = buffer.readByte();
      items = new Item[cnt];
      for(int a=0;a<cnt;a++) {
        items[a] = new Item();
        items[a].read(buffer, file);
      }
    } else {
      //only read activeSlot
      items = new Item[1];
      items[0] = new Item();
      items[0].read(buffer, file);
    }
    int cnt = buffer.readByte();
    armors = new Item[cnt];
    for(int a=0;a<cnt;a++) {
      armors[a] = new Item();
      armors[a].read(buffer, file);
    }
    return true;
  }

  public boolean hasShieldEquiped() {
    return items[items.length-1].id == Items.SHIELD;
  }

  public void useItem(Client client) {
    //dmg item and destroy if dmg == 0.0f
    Item item = items[activeSlot];
    if (item.id == 0) return;  //no item
    ItemBase itembase = Static.items.items[item.id];
    if (!itembase.isDamaged) return;  //not useable item
    item.dmg -= itembase.durability;
    if (item.dmg <= 0.0f) {
      item.clear();
      client.serverTransport.setInvItem((byte)client.player.activeSlot, item);
    } else {
      client.serverTransport.addUpdate(new PacketSetInvDmg(Packets.SETINVDMG, item.dmg));
    }
  }
}
