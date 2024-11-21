package jfcraft.data;

/** 3 floats
 *
 * @author pquiring
 */

public class XYZ implements Cloneable {
  public float x,y,z;
  public XYZ() {}
  public XYZ(float x,float y,float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  public void reset() {
    x = y = z = 0f;
  }
  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  public void copy(XYZ in) {
    this.x = in.x;
    this.y = in.y;
    this.z = in.z;
  }
  public boolean isZero() {
    return x == 0 && y == 0 && z == 0;
  }
  public String toString() {
    return "{" + x + "," + y + "," + z + "}";
  }
  public XYZ clone() {
    try {
      return (XYZ)super.clone();
    } catch (Exception e) {
      Static.log(e);
      return null;
    }
  }
}
