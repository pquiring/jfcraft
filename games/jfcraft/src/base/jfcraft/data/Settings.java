package jfcraft.data;

/** Settings
 *
 * @author pquiring
 *
 * Created : Aug 2, 2014
 */

import javaforce.*;

import java.io.*;

public class Settings {
  public static Settings current = new Settings();

  public String player = "Player";  //player name
  public String pass = "";  //password (not used yet)
  public int loadRange = 6;  //# chunks client loads (radius)
  public boolean isFancy = true;  //use fancy graphics (else solid)
  public int tcpPort = 25565 + 1;
  public boolean doSteps = true;  //enable steps land (smooth) (experimental)
  public boolean doViewBobbing = true;  //view bobbing
  public boolean pvp = true;
  public boolean dropItemsOnDeath = true;
  //VoIP stuff
  public boolean server_voip = true;
  public boolean client_voip = true;
  public boolean ptt = true;  //push to talk (else phone mode)
  public String mic = "<default>";
  public String spk = "<default>";

  public static void load() {
    try {
      XML xml = new XML();
      FileInputStream fis = new FileInputStream(Static.getBasePath() + "/jfcraft.xml");
      xml.read(fis);
      xml.writeClass(current);
      fis.close();
    } catch (FileNotFoundException e) {
      Static.log("No settings found, using defaults.");
    } catch (Exception e) {
      Static.log(e);
    }
  }
  public static void save() {
    try {
      File folder = new File(Static.getBasePath());
      if (!folder.exists()) folder.mkdir();
      XML xml = new XML();
      xml.readClass("jfcraft", current);
      FileOutputStream fos = new FileOutputStream(Static.getBasePath() + "/jfcraft.xml");
      xml.write(fos);
      fos.close();
    } catch (Exception e) {
      Static.log(e);
    }
  }
}
