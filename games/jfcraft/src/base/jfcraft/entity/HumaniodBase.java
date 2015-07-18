package jfcraft.entity;

/** Base class for all Humaniod entities that can equip armor and carry items.
 * Basically anything with the same shape as Player.
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public abstract class HumaniodBase extends CreatureBase {
  public Item items[];
  public int activeSlot;  //0-8
  public Item armors[];

  private static RenderDest dest;

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG"};

  public void initStatic(GL gl) {
    dest = new RenderDest(parts.length);
  }

  public RenderDest getDest() {
    return dest;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    GLModel mod = loadModel("armor");
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = mod.getObject(parts[a]);
      if (obj == null) {
        System.out.println("Warning:Couldn't find part:" + parts[a]);
      }
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        GLUVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        GLUVMap map1 = obj.maps.get(0);
        GLUVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
      }
      buf.org = obj.org;
      buf.type = obj.type;
      buf.calcCenter();
    }
  }

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
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

  public void renderArmor(GL gl) {
    int cnt = armors.length;
    for(int a=0;a<cnt;a++) {
      char id = armors[a].id;
      if (id == 0) continue;
      ItemBase item = Static.items.items[id];
      int layers = item.getArmorLayers();
      for(int layer=0;layer<layers;layer++) {
        int parts = item.getArmorParts(layer);
        item.bindArmorTexture(gl, layer);
        scale = item.getArmorScale(layer);
        for(int partidx=0;partidx<parts;partidx++) {
          int part = item.getArmorPart(layer, partidx);
          RenderBuffers buf = dest.getBuffers(part);
          setMatrixModel(gl, part, buf);
          buf.bindBuffers(gl);
          buf.render(gl);
        }
      }
    }
    scale = 1.0f;
  }

  public void renderItemInHand(GL gl) {
    //TODO
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
}
