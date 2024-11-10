package jfcraft.item;

/** Item class holds one stack of items/blocks.
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import javaforce.*;

import jfcraft.data.*;

public class Item implements java.lang.Cloneable, SerialClass, SerialCreator {
  public char id;
  public byte var;   //variation (4bits)
  public byte count; //64 max
  public float dmg;  //damage 0.0f - 1.0f
  public Object attr;   //extended attrs

  public Item() {
    id = 0;
  }
  public Item(char id) {
    this.id = id;
    this.var = 0;
    this.count = 1;
    this.dmg = 0.0f;
  }
  public Item(char id, int var) {
    this.id = id;
    this.var = (byte)var;
    this.count = 1;
    this.dmg = 1.0f;
  }
  public Item(char id, int var, int count) {
    this.id = id;
    this.var = (byte)var;
    this.count = (byte)count;
    this.dmg = 1.0f;
  }
  public Item(char id, float dmg) {
    this.id = id;
    this.dmg = dmg;
    this.count = 1;
  }
  public final boolean equals(Item item) {
    return item.id == id && item.var == var;
  }
  public final void copy(Item item) {
    this.id = item.id;
    this.var = item.var;
    this.count = item.count;
    this.dmg = item.dmg;
  }
  public final void copy(Item item, byte count) {
    this.id = item.id;
    this.var = item.var;
    this.count = count;
    this.dmg = item.dmg;
  }
  public final void clear() {
    id = 0;
    count = 0;
    var = 0;
    dmg = 0.0f;
  }
  public final boolean isEmpty() {
    return id == 0;
  }
  public final String toString() {
    return "item:" + (int)id + "," + var + "," + count + "," + dmg;
  }
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }

  private static final byte ver = 0;

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeChar(id);
    buffer.writeByte(var);
    buffer.writeByte(count);
    buffer.writeFloat(dmg);
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    id = buffer.readChar();
    var = buffer.readByte();
    count = buffer.readByte();
    dmg = buffer.readFloat();
    return true;
  }

  public SerialClass create(SerialBuffer buffer) {
    return new Item();
  }
}
