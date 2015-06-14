package jfcraft.data;

/** Extra redstone info
 *
 * @author pquiring
 */

public class ExtraRedstone extends ExtraBase {
  public boolean active, powered, extra, busy;
  public int lvl;  //power level 0-15
  public int cnt;  //usage counter
  public byte t1, t2;  //timers

  public String getName() {
    return "redstone";
  }

  public ExtraRedstone() {
    this.id = Extras.REDSTONE;
  }
  public ExtraRedstone(int x,int y,int z) {
    this.id = Extras.REDSTONE;
    this.x = (short)x;
    this.y = (short)y;
    this.z = (short)z;
  }

  private byte getBits() {
    byte bits = 0;
    if (active) bits |= ACTIVE;
    if (powered) bits |= POWERED;
    if (extra) bits |= EXTRA;
    if (busy) bits |= BUSY;
    return bits;
  }
  private void setBits(byte bits) {
    active = (bits & ACTIVE) == ACTIVE;
    powered = (bits & POWERED) == POWERED;
    extra = (bits & EXTRA) == EXTRA;
    busy = (bits & BUSY) == BUSY;
  }

  private int ACTIVE = 0x01;
  private int POWERED = 0x02;
  private int EXTRA = 0x04;
  private int BUSY = 0x08;

  public void update(ExtraBase update) {
    ExtraRedstone redstone = (ExtraRedstone)update;
    this.active = redstone.active;
    this.powered = redstone.powered;
    this.extra = redstone.extra;
    this.busy = redstone.busy;
    this.lvl = redstone.lvl;
    this.cnt = redstone.cnt;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeByte(getBits());
    buffer.writeInt(lvl);
    buffer.writeInt(cnt);
    buffer.writeByte(t1);
    buffer.writeByte(t2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    byte bits = buffer.readByte();
    setBits(bits);
    lvl = buffer.readInt();
    cnt = buffer.readInt();
    t1 = buffer.readByte();
    t2 = buffer.readByte();
    return true;
  }
}
