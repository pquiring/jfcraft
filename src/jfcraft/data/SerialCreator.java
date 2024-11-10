package jfcraft.data;

/** Serialized object creator
 *
 * @author pquiring
 */

public interface SerialCreator {
  public SerialClass create(SerialBuffer buffer);
}
