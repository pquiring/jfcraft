package jfcraft.data;

/** Furnace items
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.item.*;
import jfcraft.server.Server;

public class ExtraFurnace extends ExtraContainer {
  public static final int INPUT = 0;
  public static final int FUEL = 1;
  public static final int OUTPUT = 2;

  public int timer;  //0-200 (decreases to zero)
  public int heat;   //decreases to zero
  public int heatMax;  //heat starting point

  public ExtraFurnace() {
    this.id = Extras.FURNACE;
    items = new Item[3];
    for(int a=0;a<3;a++) {
      items[a] = new Item();
    }
  }

  public ExtraFurnace(int x,int y,int z) {
    this.id = Extras.FURNACE;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
    items = new Item[3];
    for(int a=0;a<3;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "furnace";
  }

  //the furnace has some extra criteria for containment

  public void put(Server server, Client client, byte idx, byte count) {
    if (idx == OUTPUT) {
      Static.log("can not place on output");
      return;
    }
    ItemBase itembase = Static.items.items[client.hand.id];
    if (idx == ExtraFurnace.FUEL && !itembase.isFuel) return;
    if (idx == ExtraFurnace.INPUT && !itembase.canBake) return;
    super.put(server, client, idx, count);
  }

  public void exchange(Server server, Client client, byte idx) {
    if (idx == ExtraFurnace.OUTPUT) return;
    super.exchange(server, client, idx);
  }

  public void update(ExtraBase update) {
    super.update(update);
    ExtraFurnace furnace = (ExtraFurnace)update;
    this.timer = furnace.timer;
    this.heat = furnace.heat;
    this.heatMax = furnace.heatMax;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeInt(timer);
    buffer.writeInt(heat);
    buffer.writeInt(heatMax);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    timer = buffer.readInt();
    heat = buffer.readInt();
    heatMax = buffer.readInt();
    return true;
  }
}
