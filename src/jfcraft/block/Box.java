package jfcraft.block;

/** Bounding Box represent solid area of block.
 *
 * Some blocks may contain multiple boxes.
 *
 * @author pquiring
 */

public class Box {
  public final float x1,y1,z1;
  public final float x2,y2,z2;

  public Box(float x1, float y1, float z1, float x2, float y2, float z2) {
    this.x1 = x1 / 16.0f;
    this.y1 = y1 / 16.0f;
    this.z1 = z1 / 16.0f;
    this.x2 = x2 / 16.0f;
    this.y2 = y2 / 16.0f;
    this.z2 = z2 / 16.0f;
  }
}
