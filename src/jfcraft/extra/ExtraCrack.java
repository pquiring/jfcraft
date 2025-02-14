package jfcraft.extra;

/** Crack
 *
 * - type is not used, stored in separate array which is transient.
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class ExtraCrack extends ExtraBase {
  public float dmg;  //percentage

  public String getName() {
    return "crack";
  }

  public void update(ExtraBase update) {
    ExtraCrack crack = (ExtraCrack)update;
    this.dmg = crack.dmg;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeFloat(dmg);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    dmg = buffer.readFloat();
    return true;
  }
}
