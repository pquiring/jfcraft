package jfcraft.data;

/** Hopper items
 *
 * @author pquiring
 */

import jfcraft.item.*;

public class ExtraHopper extends ExtraContainer {
  public byte transferCooldown;

  public ExtraHopper() {
    this.id = Extras.HOPPER;
    items = new Item[5];
    for(int a=0;a<5;a++) {
      items[a] = new Item();
    }
  }

  public ExtraHopper(int x,int y,int z) {
    this.id = Extras.HOPPER;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
    items = new Item[5];
    for(int a=0;a<5;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "hopper";
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeByte(transferCooldown);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    transferCooldown = buffer.readByte();
    return true;
  }
}
