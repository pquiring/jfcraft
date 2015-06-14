package jfcraft.data;

/** Chest items
 *
 * @author pquiring
 */

import jfcraft.item.*;

public class ExtraChest extends ExtraContainer {
  public ExtraChest() {
    this.id = Extras.CHEST;
  }

  public ExtraChest(int x,int y,int z, int cnt) {
    this.id = Extras.CHEST;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
    items = new Item[cnt];
    for(int a=0;a<cnt;a++) {
      items[a] = new Item();
    }
  }

  public String getName() {
    return "chest";
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    return true;
  }
}
