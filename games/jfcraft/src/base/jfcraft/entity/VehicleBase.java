package jfcraft.entity;

import jfcraft.client.*;
import jfcraft.data.*;

/**
 *
 * @author pquiring
 */

public abstract class VehicleBase extends CreatureBase {
  public CreatureBase occupant;
  public int ocid, ouid;  //occupant id (ocid=in chunk on disk ,ouid=in game)

  public boolean up, dn, run, sneak;  //occupant controls
  public boolean lt, rt, jump;

  public void resetControls() {
    up = false;
    dn = false;
    run = false;
    sneak = false;
  }

  public int getMenu() {
    return Client.INVENTORY;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer,file);
    buffer.writeByte(ver);
    if (file) {
      if (occupant != null) {
        buffer.writeInt(occupant.cid);
      } else {
        buffer.writeInt(0);
      }
    } else {
      if (occupant != null) {
        buffer.writeInt(occupant.uid);
      } else {
        buffer.writeInt(0);
      }
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    if (file) {
      ocid = buffer.readInt();
    } else {
      ouid = buffer.readInt();
    }
    return true;
  }

  public void setupLinks(Chunk chunk, boolean file) {
    if (file) {
      if (ocid != 0) {
        occupant = (VehicleBase)chunk.getEntity2(ocid);
      }
    } else {
      if (ouid != 0) {
        occupant = (VehicleBase)chunk.getEntity(ouid);
      }
    }
  }
}
