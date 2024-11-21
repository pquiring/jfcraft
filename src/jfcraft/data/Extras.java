package jfcraft.data;

/**
 *
 * @author pquiring
 */

import java.lang.reflect.*;

public class Extras implements SerialCreator {
  public static final int MAX_ID = 128;
  public int extraCount;
  public ExtraBase extras[];
  public ExtraBase regExtras[] = new ExtraBase[MAX_ID];

  //extra IDs (dynamically assigned)
  public static byte DROPPER; //dispenser too
  public static byte CHEST;
  public static byte FURNACE;
  public static byte HOPPER;
  public static byte CRACK;  //not saved to disk
  public static byte REDSTONE;
  public static byte SIGN;
  public static byte HORSE;

  public static void getIDs(World world) {
    DROPPER = world.getExtraID("dropper");
    CHEST = world.getExtraID("chest");
    FURNACE = world.getExtraID("furnace");
    HOPPER = world.getExtraID("hopper");
    CRACK = world.getExtraID("crack");
    REDSTONE = world.getExtraID("redstone");
    SIGN = world.getExtraID("sign");
    HORSE = world.getExtraID("horse");
  }

  public void registerExtra(ExtraBase eb) {
    regExtras[extraCount++] = eb;
  }

  public void orderExtras() {
    extras = new ExtraBase[MAX_ID];
    for(int a=0;a<MAX_ID;a++) {
      ExtraBase eb = regExtras[a];
      if (eb == null) continue;
      extras[eb.id] = eb;
    }
  }

  public void registerDefault() {
    registerExtra(new ExtraCrack());
    registerExtra(new ExtraRedstone());
    registerExtra(new ExtraFurnace());
    registerExtra(new ExtraHopper());
    registerExtra(new ExtraChest());
    registerExtra(new ExtraDropper());
    registerExtra(new ExtraSign());
    registerExtra(new ExtraHorse());
  }

  public SerialClass create(SerialBuffer buffer, ExtraBase[] extras) {
    int type = buffer.peekByte(1);
    ExtraBase base = extras[type];
    if (base == null) {
      Static.log("Error:Extra not registered:" + type);
      return null;
    }
    try {
      Class<?> cls = base.getClass();
      Constructor ctor = cls.getConstructor();
      ExtraBase eb = (ExtraBase)ctor.newInstance();
      return eb;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public SerialClass create(SerialBuffer buffer) {
    return create(buffer, Static.extras.extras);
  }

  public ExtraBase getExtra(int id) {
    for(int a=0;a<extras.length;a++) {
      if (extras[a].id == id) {
        return extras[a];
      }
    }
    Static.log("Error:Extra ID not found:" + id);
    return null;
  }

  public ExtraBase getExtra(String name) {
    name = name.toLowerCase();
    for(int a=0;a<extras.length;a++) {
      ExtraBase extra = extras[a];
      if (extra == null) continue;
      String ename = extra.getName().toLowerCase();
      if (ename.equals(name)) {
        return extra;
      }
    }
    Static.log("Error:Extra not found:" + name);
    return null;
  }
}
