package jfcraft.data;

/** Event
 *
 * Stores an event/quest player has completed.
 *
 * key/value must not contain commas, or (brackets).
 *
 * @author pquiring
 */

public class Event implements SerialClass {

  /** Key : should be in dot notation */
  public String key;
  /** Value : any string */
  public String value;

  public static Event[] array = new Event[0];

  public Event() {}

  public Event(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeString(key);
    buffer.writeString(value);
    return true;
  }
  public boolean read(SerialBuffer buffer, boolean file) {
    key = buffer.readString();
    value = buffer.readString();
    return true;
  }
}
