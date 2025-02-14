package jfcraft.entity;

/** NPC Alex
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class NPCAlex extends NPC {

  public String getName() {
    return "NPCAlex";
  }

  public static String event_hello = "npc.alex.hello";

  public Page[] getPages(Player player) {
    if (player.getEvent(event_hello) == null) {
      player.setEvent(event_hello, "true");
      return new Page[] {
        new Page(new String[] {
          "Hi, there!",
          "My name is Alex.",
        })
      };
    } else {
      return new Page[] {
        new Page(new String[] {
          "Welcome back!",
        })
      };
    }
  }
}
