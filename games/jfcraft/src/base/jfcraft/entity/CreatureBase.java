package jfcraft.entity;

/** Entity base for all creature types (Player, monsters, animals, etc.)
 *
 * Plan to add path finding, etc. in here.
 *
 * @author pquiring
 */

import jfcraft.packet.Packet;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.packet.*;
import jfcraft.item.*;
import static jfcraft.data.Direction.*;

public abstract class CreatureBase extends EntityBase {
  public float health;  //20=full
  public float ar;  //armor rating (20=max)
  public float air;  //20=full
  public float hunger;  //20 = full
  public float saturation;  //20 = full
  public float exhaustion;  //when >= 4.0 : -1 from saturation or food

  public VehicleBase vehicle;
  public int vcid, vuid;  //vehicle id (vcid=in chunk on disk ,vuid=in game)

  public void hit() {
    runCount = 7;
  }

  public void takeDmg(float amt, CreatureBase from) {
    synchronized(lock) {
      if (health == 0) {
        Static.log("already dead");
        despawn();
        return;
      }
      if (!Settings.current.pvp) {
        if (id == 0 && from.id == 0) return;  //PvP disabled
      }
      //TODO : reduce amt by armor rating
      exhaustion += 0.3f;
      hit();
      if (amt >= health) {
        health = 0;
        //entity is dead
        Static.log("Entity killed:" + this);
        if (id != Entities.PLAYER) {
          despawn();

          Item items[] = drop();
          Chunk chunk = getChunk();
          for(int a=0;a<items.length;a++) {
            Item item = items[a];
            if (item.id == Blocks.AIR) continue;
            WorldItem.create(item, dim, pos.x, pos.y, pos.z, chunk, -1);
          }
        }
      } else {
        health -= amt;
        target = from;  //switch target (if aggresive)
        if (from != null) {
          //do knockback
          GLMatrix mat = new GLMatrix();  //TODO : make this static??? (minor)
          float ang = from.ang.y + 180.0f;
          if (ang > 180.0f) ang -= 360.0f;
          mat.addRotate(ang, 0, 1, 0);
          GLVector3 vec = new GLVector3();
          vec.v[0] = 0;
          vec.v[1] = 0;
          vec.v[2] = 1;
          mat.mult(vec);
          vel.x = vec.v[0] * walkSpeed / 20f;
          vel.y = jumpVelocity / 3.0f;
          vel.z = vec.v[2] * walkSpeed / 20f;
          if (id == Entities.PLAYER) {
            Player player = (Player)this;
            player.client.serverTransport.addUpdate(new PacketKnockBack(Packets.KNOCKBACK, vel.x, vel.y ,vel.z));
          }
        }
      }
//      if (id == Entities.PLAYER) {
        Static.server.broadcastEntityHealth(this);
//      }
    }
  }

  public float calcDmg(Item weapon) {
    //calc damage entity can deal with weapon
    ItemBase base = Static.items.items[weapon.id];
    float dmg = attackDmg;
    if (base.isWeapon || base.isTool) {
      dmg = base.attackDmg;
    }
    //TODO : check for enchantments, etc.
    return dmg;
  }

  public static Vectors find_vectors = new Vectors();

  /** Find block or entity in this entities line of sight (ray tracing)
   *
   * @param tid = block id to find (-1 any solid block)
   * @param type = Entity or Selection
   * @param veh = Entity to NOT select
   * @param c = coords to fill in
   */
  public void findBlock(int tid, BlockHitTest.Type type, VehicleBase veh, Coords c) {
    float dx,dy,dz;
    synchronized(find_vectors) {
      calcVectors(Static._1_16, find_vectors);
      dx = find_vectors.facing.v[0];
      dy = find_vectors.facing.v[1];
      dz = find_vectors.facing.v[2];
    }
    findBlock(tid,
      pos.x,pos.y + eyeHeight,pos.z,
      dx * Static._1_16,
      dy * Static._1_16,
      dz * Static._1_16,
      type, veh, c);
  }

  private void findBlock(int tid, float ix, float iy, float iz, float dx, float dy, float dz, BlockHitTest.Type type, VehicleBase veh, Coords c) {
    //current position in space
    float px = ix;
    float py = iy;
    float pz = iz;

    c.block = null;
    c.entity = null;
    c.chunk = null;

//    Static.log("find:" + dx +"," +dy +","+ dz );

    int side = -1;

    float sx = 0, sy = 0, sz = 0;  //last AIR position in space
    boolean haveSide = false;
    int vid = -1;
    if (veh != null) vid = veh.uid;

    World world = Static.world();

    EntityBase elist[] = world.getEntities();

    int cnt = (int)(reach / Static._1_16);
    boolean ok = false;
    for(int a=0;a<cnt;a++) {
      for(int e=0;e<elist.length;e++) {
        EntityBase entity = elist[e];
        if (entity.uid == this.uid) continue;
        if (entity.uid == vid) continue;  //do not select vehicle
        if (!entity.canSelect()) continue;
        if (entity.isBlock) continue;
        if (entity.hitPoint(px, py, pz)) {
          c.block = null;
          entity.getCoords(c);
          return;
        }
      }
      world.getBlock(dim,px,py,pz,c);
      if (c.block.id == tid) {
        ok = true;
        break;
      } else if (!c.block.canSelect) {
        //do not select it (WATER, LAVA, FIRE, etc.)
        sx = px;
        sy = py;
        sz = pz;
        haveSide = true;
      } else if (c.block.id == Blocks.AIR) {
        sx = px;
        sy = py;
        sz = pz;
        haveSide = true;
      } else if (c.block.hitPoint(px,py,pz,c,type)) {
        ok = true;
        break;
      }
      //add vector
      px += dx;
      py += dy;
      pz += dz;
    }
    if (!ok) {
      //nothing within reach
      c.block = null;
      c.entity = null;
      c.chunk = null;
      return;
    }

    if (haveSide) {
      //compare px,py,pz with sx,sy,sz to find side
      //convert all to block coords
      int ipx = Static.floor(px);
      int ipy = Static.floor(py);
      int ipz = Static.floor(pz);
      int isx = Static.floor(sx);
      int isy = Static.floor(sy);
      int isz = Static.floor(sz);

      if (isx < ipx) {
        side = E;
      } else if (isx > ipx) {
        side = W;
      } else if (isy < ipy) {
        side = A;
      } else if (isy > ipy) {
        side = B;
      } else if (isz < ipz) {
        side = S;
      } else if (isz > ipz) {
        side = N;
      }
      c.sx = sx;  //used with slabs to know where to place them exactly
      c.sy = sy;
      c.sz = sz;
    } else {
      c.sx = 0;
      c.sy = 0;
      c.sz = 0;
    }

    c.dir = side;
  }

  public void getTarget() {
    target = (CreatureBase)findEntity(16.0f, Entities.PLAYER);
  }

  public void moveToTarget() {
    //TODO find path to target
    float dx,dy,dz,ax,ay,az,dist;
    dx = target.pos.x - pos.x;
    dy = target.pos.y - pos.y;
    dz = target.pos.z - pos.z;
    ax = Static.abs(dx);
    ay = Static.abs(dy);
    az = Static.abs(dz);
    dist = (float)Math.sqrt(ax * ax + ay * ay + az * az);
    if (dist < 0.1f) dist = 0.1f;
    if (dist > 64.0f) {
      target = null;
    }
    //calc angle to face target
    //    z-
    //  q1|q2
    //-x--|--x+
    //  q3|q4
    //    z+
    if (dx == 0) {
      if (dz < 0) {
        ang.y = 0.0f;
      } else {
        ang.y = 180.0f;
      }
    } else if (dz == 0) {
      if (dx < 0) {
        ang.y = -90.0f;
      } else {
        ang.y = 90.0f;
      }
    } else if (dx > 0) {
      if (dz > 0) {
        //q4
        ang.y = 180.0f - (float)Math.toDegrees(Math.atan(ax / az));
      } else {
        //q2
        ang.y = (float)Math.toDegrees(Math.atan(ax / az));
      }
    } else {
      if (dz > 0) {
        //q3
        ang.y = -180.0f + (float)Math.toDegrees(Math.atan(ax / az));
      } else {
        //q1
        ang.y = - (float)Math.toDegrees(Math.atan(ax / az));
      }
    }
    if (dist > attackRange) {
      mode = MODE_WALK;
    } else {
      mode = MODE_IDLE;
      //attack!!!
      if (attackCount == 0) {
        target.takeDmg(attackDmg, this);
        attackCount = attackDelay;
      } else {
        attackCount--;
      }
    }
    //find angle(x) between target.y and this.y
    ang.x = (float)-Math.toDegrees(Math.atan(dy / dist));
  }

  public int walkLength, runCount;
  public int walkDutyCycle = 10;  //walk 5% of the time

  public void randomWalking() {
    //random walking
    walkLength--;
    if (walkLength < 0) {
      walkLength = r.nextInt(5 * 20);  //walk upto 5 secs
      if (runCount > 0 || r.nextInt(walkDutyCycle) == 0) {
        ang.y += (r.nextFloat() - 0.5f) * 90.0f;
        if (ang.y > 180.0f) ang.y -= 180.0f;
        else if (ang.y < -180.0f) ang.y += 180.0f;
        if (runCount > 0) {
          runCount--;
          mode = MODE_RUN;
        } else {
          mode = MODE_WALK;
        }
      } else {
        mode = MODE_IDLE;
      }
    }
    ang.x = angX;
  }

  private static GLMatrix mMat = new GLMatrix();
  private static GLVector3 mVec = new GLVector3();

  /** Moves a creature (animal or monster)
   *
   */
  public void moveEntity() {
  //  if (!onGround && !inWater && mode != MODE_JUMPING) return;  //horse can jump and move
    float speed = getSpeed(mode);
    if (speed == 0f) return;
    speed /= 20f;
    mMat.setIdentity();
    mMat.addRotate(ang.y, 0, 1, 0);
    mVec.set(0, 0, -speed);
    mMat.mult(mVec);
    vel.x = mVec.v[0];
    vel.z = mVec.v[2];
    if (!move(false, false, false,
      mode == MODE_RUN ? -1 : 2,
      target == null ? (mode == MODE_RUN ? AVOID_NONE : AVOID_LAVA_WATER) : AVOID_LAVA
    )) {
      mode = MODE_IDLE;
    }
  }

  public boolean cracks() {
    return false;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeFloat(health);
    buffer.writeFloat(ar);
    buffer.writeFloat(air);
    buffer.writeFloat(hunger);
    buffer.writeFloat(saturation);
    buffer.writeFloat(exhaustion);
    if (file) {
      if (vehicle != null) {
        buffer.writeInt(vehicle.cid);
      } else {
        buffer.writeInt(0);
      }
    } else {
      if (vehicle != null) {
        buffer.writeInt(vehicle.uid);
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
    health = buffer.readFloat();
    ar = buffer.readFloat();
    air = buffer.readFloat();
    hunger = buffer.readFloat();
    saturation = buffer.readFloat();
    exhaustion = buffer.readFloat();
    if (file) {
      vcid = buffer.readInt();
    } else {
      vuid = buffer.readInt();
    }
    return true;
  }

  public void setupLinks(Chunk chunk, boolean file) {
    if (file) {
      if (vcid != 0) {
        vehicle = (VehicleBase)chunk.getEntity2(vcid);
      }
    } else {
      if (vuid != 0) {
        vehicle = (VehicleBase)chunk.getEntity(vuid);
      }
    }
  }
}
