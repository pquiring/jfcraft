package jfcraft.data;

/** Page - a page of text (dialog) with NPC.
 *
 * max 6 lines.
 *
 * Special tokens:
 *
 * #choice(action,action,...) text  //offer dialog choice (will be tabbed over)
 * #reload  //invokes NPC.getPages() again and go to page 0 after Page is clicked (may only be used if no choices are available)
 * #exit  //exit dialog after Page is clicked (default after last Page)
 *
 * Special actions:
 *
 *  event:key=value  //set event if choice selected
 *  goto:index  //go to page (must be last action per choice)
 *  exit //exit dialog if choice selected (must be last action per choice)
 *  reload  //invoke NPC.getPages() again again and go to page 0 if choice selected (must be last action per choice)
 *
 * @author pquiring
 */

public class Page implements SerialClass {
  public String[] text;

  //0-5 = action : select choice
  public static final byte ACTION_EXIT = 10;
  public static final byte ACTION_NEXT = 11;

  public Page() {}

  public Page(String[] text) {
    this.text = text;
  }

  public int choices() {
    int cnt = 0;
    for(String txt : text) {
      if (txt.startsWith("#choice")) cnt++;
    }
    return cnt;
  }

  public String toString() {
    return "Page:" + text.length;
  }

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeInt(text.length);
    for(String txt : text) {
      buffer.writeString(txt);
    }
    return true;
  }
  public boolean read(SerialBuffer buffer, boolean file) {
    int cnt = buffer.readInt();
    text = new String[cnt];
    for(int i=0;i<cnt;i++) {
      text[i] = buffer.readString();
    }
    return true;
  }
}
