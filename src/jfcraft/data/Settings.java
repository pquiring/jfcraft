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
  public boolean fancy = true;  //use fancy graphics (else solid)
  public int tcpPort = 25565 + 1;
  public boolean doSteps = true;  //enable steps land (smooth) (experimental)
  public boolean doViewBobbing = true;  //view bobbing
  public boolean pvp = true;
  public boolean dropItemsOnDeath = true;
  public boolean creativeMode = false;
  //VoIP stuff
  public boolean server_voip = true;
  public boolean client_voip = true;
  public boolean ptt = true;  //push to talk (else phone mode)
  public String mic = "<default>";
  public String spk = "<default>";
  public int FPS = 60;  //-1 = max fps
  public boolean clouds = true;

  public static void load() {
    Static.initBaseFolder();
    try {
      XML xml = new XML();
      FileInputStream fis = new FileInputStream(Static.getBasePath() + "/jfcraft.xml");
      xml.read(fis);
      xml.writeClass(current);
      fis.close();
    } catch (FileNotFoundException e) {
      Static.log("No settings found, using defaults.");
      current.setDefault();
    } catch (Exception e) {
      Static.log(e);
      current.setDefault();
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
  public void setDefault() {
    player = "Player";  //player name
    pass = "";  //password (not used yet)
    loadRange = 6;  //# chunks client loads (radius)
    fancy = true;  //use fancy graphics (else solid)
    tcpPort = 25565 + 1;
    doSteps = true;  //enable steps land (smooth) (experimental)
    doViewBobbing = true;  //view bobbing
    pvp = true;
    dropItemsOnDeath = true;
    creativeMode = false;
    //VoIP stuff
    server_voip = true;
    client_voip = true;
    ptt = true;  //push to talk (else phone mode)
    mic = "<default>";
    spk = "<default>";
    FPS = 60;
    clouds = true;
  }
}
