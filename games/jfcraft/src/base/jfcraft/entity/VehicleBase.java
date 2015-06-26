package jfcraft.entity;

/**
 *
 * @author pquiring
 */

public abstract class VehicleBase extends CreatureBase {
  public CreatureBase occupant;
  public boolean up, dn, run, sneak;  //occupant controls

  public void resetControls() {
    up = false;
    dn = false;
    run = false;
    sneak = false;
  }
}
