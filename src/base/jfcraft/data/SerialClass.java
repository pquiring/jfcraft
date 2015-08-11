package jfcraft.data;

/**
 *
 * @author pquiring
 */

public interface SerialClass {
  public boolean write(SerialBuffer buffer, boolean file);
  public boolean read(SerialBuffer buffer, boolean file);
}
