package jfcraft.entity;

/** HitTest interface (collision detection)
 *
 * @author pquiring
 */

public interface EntityHitTest {
  /** Collision test with a point
   * hx,hy,hz = point
   *
   */
  public boolean hitPoint(float hx, float hy, float hz);
  /** Collision test with a box (cube)
   * hx1,hy1,hz1 = center point of cube (on all 3 axis)
   * hwidth2,hheight2,hdepth2 = half extents of cube
   *
   */
  public boolean hitBox(float hx1, float hy1, float hz1, float hwidth2, float hheight2, float hdepth2);
}
