package jfcraft.data;

/** Dropper/dispenser items
 *
 * @author pquiring
 */

import jfcraft.item.*;

public class ExtraDropper extends ExtraContainer {
  public byte dropCooldown;

  public ExtraDropper() {
    this.id = Extras.DROPPER;
    items = new Item[9];
    for(int a=0;a<9;a++) {
      items[a] = new Item();
    }
  }

  public ExtraDropper(int x,int y,int z) {
    this.id = Extras.DROPPER;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
    items = new Item[9];
    for(int a=0;a<9;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "dropper";
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeByte(dropCooldown);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    dropCooldown = buffer.readByte();
    return true;
  }
}
