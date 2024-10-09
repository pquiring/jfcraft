package jfcraft.opengl;

/** Render Data
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

import jfcraft.block.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class RenderData {
  public float x,y,z;
  public int side;  //side we are rendering
  public int dirSide;  //side after rotating
  public int hand;  //LEFT or RIGHT

  public boolean isDir, isDirXZ;
  public boolean isRed, isGreen, isBlue;
  public float clr[];  //custom color

  public boolean active;

  public Chunk chunk;

  public int crack;

  public int bits;

  public char[] id = new char[11];  //IDs of adjacent sides
//  public int bits[] = new int[11];
    public int[] var = new int[11];  //from bits
    public int[] dir = new int[11];  //from bits

  //block2 data
    public char[] id2 = new char[11];
    public int[] var2 = new int[11];  //from bits2
    public int[] dir2 = new int[11];  //from bits2

  public float[] sl = new float[32];  //sun light levels
  public float[] bl = new float[32];  //blk light levels
  public boolean adjLight;

  public boolean[] opaque = new boolean[11];  //is side opaque (therefore do not need to render)
  public float temp, rain;
  public int animation;

  //call resetRotate() if you change these values
  public float[] translate_pre;  //translate pre rotate
  public float[] translate_pst;  //translate post rotate
  public boolean norotate;
  public float rotate;
  public float rotate2;  //this fixes some objects upright
  public boolean yrotate;  //rotate on y-axis by rotate ang instead (ignores isDir)
  public boolean doubleSided;

  public RenderData() {
    reset();
  }

  public void reset() {
    crack = -1;
    sl[X] = 1;
    side = 0;
    x = y = z = 0;
    isDir = false;
    isDirXZ = false;
    resetRotate();
  }

  public void resetRotate() {
    translate_pre = null;
    translate_pst = null;
    norotate = false;
    rotate = 90f;
    rotate2 = 90f;
    yrotate = false;
  }

  public String toString() {
    return "RenderData:" + x + "," + y + "," + z;
  }
}
