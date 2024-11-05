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
import static jfcraft.entity.EntityBase.*;

public class RenderData {
  public float x,y,z;
  public int side;  //side we are rendering
  public int dirSide;  //side after rotating
  public int part;  //body part (-1 = none) EntityBase.*
  public int context;  //rendering context

  public XYZ pos = new XYZ();  //position
  public XYZ ang = new XYZ();  //angle
  public float scale;
  public int count;  //WorldItem only
  public boolean inventory;  //render in inventory screen (no voxel)

  public boolean isDir, isDirXZ;
  public boolean isRed, isGreen, isBlue;
  public boolean isBlock;
  public boolean isRenderAsEntity;
  public boolean isItem;
  public boolean isRenderAsItem;
  public boolean isPlayerView;
  public float clr[];  //custom color

  public boolean active;

  public Chunk chunk;

  public int crack;

  public int bits;

  //block1 layer data
    public char[] id = new char[11];  //IDs of adjacent sides
    public int[] var = new int[11];  //from bits
    public int[] dir = new int[11];  //from bits

  //block2 layer data
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
    bl[X] = 0;
    var[X] = 0;
    clr = null;
    side = 0;
    x = y = z = 0;
    isDir = false;
    isDirXZ = false;
    isRed = isGreen = isBlue = false;
    isBlock = false;
    isRenderAsEntity = false;
    isItem = false;
    part = NONE;
    pos.reset();
    ang.reset();
    count = 1;
    scale = 1;
    inventory = false;
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
