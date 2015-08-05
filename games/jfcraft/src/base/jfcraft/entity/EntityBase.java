package jfcraft.entity;

/** Base class for all mobile entities.
 *  Mobs : Players, villagers, monsters, etc.
 *  Special items : Chest, Sign, Book on enchanting table,
 *    World Item, Moving Block
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public abstract class EntityBase implements EntityHitTest, RenderSource, SerialClass {
  public XYZ pos = new XYZ();  //position
  public XYZ ang = new XYZ();  //angle
  public XYZ vel = new XYZ();  //velocity

//  public boolean debug = false;

  //the position represent the entities center position at the bottom (feet)
  // centered on x and z, but bottom on y of their cube
  public int dim;  //dimension
  public int id;  //Entity type : PLAYER, ZOMBIE, etc.
  public int uid;  //unique id (in game only) (not saved to disk)
  public int age;
  public int teleportTimer;
  public int cid;  //id within chunk (saved to disk)
  public int flags;  //generic flags (usage depends on derived entities)

  public float width, width2, height, height2, depth, depth2;
  public float legLength;  //for when in vehicle
  public float eyeHeight, jumpVelocity, reach;
  public float walkSpeed, runSpeed, sneakSpeed, swimSpeed;
  public float yDrag, xzDrag;
  public boolean inWater, inLava;  //whole body
  public boolean inLadder, inVines, inWeb;
  public boolean underWater, underLava;  //camera view
  public boolean creative;
  public float floatRad;
  public float jumpPos, jumpStart;  //for debug only I think (max height of last jump)
  public float attackDmg, attackRange;
  public boolean wasInLiquid;
  public Object lock;
  public boolean offline;  //player only
  public int attackCount, attackDelay;
  public boolean isBlock;
  public boolean instanceInited;
  public int sound;
  public int maxAge;
  public CreatureBase target;
  public boolean onGround, onWater;
  public int mode;  //IDLE, WALK, RUN, SNEAK, BOWCHARGE, etc.
  public float scale;  //scale entity
  public boolean isStatic;  //one static instance for all instances
  public boolean dirty, needCopyBuffers;
  public int path[];

  public float angX;  //default position

  public static Random r = new Random();
  public static GLMatrix mat = new GLMatrix(); //for rendering only (client side render only)

  public World world;

  public void init(World world) {
    //init values
    this.world = world;
    lock = new Object();
    maxAge = -1;
    if (!isStatic) {
      dirty = true;
      needCopyBuffers = true;
    }
  }
  public void initStatic() {}
  public void initStaticGL() {}
  public void initInstance() {
    instanceInited = true;
    scale = 1.0f;
  }
  public void setScale(float scale) {
    this.scale = scale;
  }

  public abstract String getName();

  /** Returns a class to receive the assigned ID.  The class must contain a public static int with the name of the entity (all uppercase). */
  public Class getIDClass() {
    return Entities.class;
  }

  public GLModel loadModel(String fn) {
    return Assets.getModel(fn).model;
  }

  private Coords coords = new Coords();

  /** Tests all blocks. */
  private boolean hitTest(float dx, float dy, float dz) {
    float px = pos.x + dx - width2;
    float py = pos.y + dy;
    float pz = pos.z + dz - depth2;

    float tx = pos.x + dx;
    float ty = pos.y + dy + height2;
    float tz = pos.z + dz;

    int ix = Static.ceil(width);
    int iy = Static.ceil(height);
    int iz = Static.ceil(depth);

    synchronized(coords) {
      for(int x=0;x<=ix;x++) {
        for(int y=0;y<=iy;y++) {
          for(int z=0;z<=iz;z++) {
            float cx = px;
            if (x > width) cx += width; else cx += x;
            float cy = py;
            if (y > height) cy += height; else cy += y;
            float cz = pz;
            if (z > depth) cz += depth; else cz += z;
            world.getBlock(dim,cx,cy,cz,coords);
            if (coords.block.hitBox(tx, ty, tz, width2, height2, depth2, coords, BlockHitTest.Type.ENTITY)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /** Tests if entity is in water/lava/ladder. */
  private void checkBlocks(float dx, float dy, float dz) {
    float px = pos.x + dx - width2;
    float py = pos.y + dy;
    float pz = pos.z + dz - depth2;

    int cx = Static.ceil(width);
    int cy = Static.ceil(height);
    int cz = Static.ceil(depth);

    inWater = false;
    inLava = false;
    inLadder = false;
    inVines = false;
    inWeb = false;

    synchronized(coords) {
      for(int x=0;x<=cx;x++) {
        for(int y=0;y<=cy;y++) {
          for(int z=0;z<=cz;z++) {
            float tx = px;
            if (x > width) tx += width; else tx += x;
            float ty = py;
            if (y > height) ty += height; else ty += y;
            float tz = pz;
            if (z > depth) tz += depth; else tz += z;
            world.getBlock(dim,tx,ty,tz,coords);
            if (coords.block.id == Blocks.WATER) {
              inWater = true;
            }
            if (coords.block.id == Blocks.LAVA) {
              inLava = true;
            }
            if (coords.block.id == Blocks.LADDER) {
              inLadder = true;
            }
            if (coords.block.id == Blocks.VINES) {
              inVines = true;
            }
            if (coords.block.id == Blocks.WEB) {
              inWeb = true;
            }
          }
        }
      }
    }
  }

  /** Tick all blocks within entity. */
  private void tickBlocks(float dx, float dy, float dz) {
    World world = Static.server.world;
    float px = pos.x + dx - width2;
    float py = pos.y + dy;
    float pz = pos.z + dz - depth2;

    int cx = Static.ceil(width);
    int cy = Static.ceil(height);
    int cz = Static.ceil(depth);

    synchronized(coords) {
      for(int x=0;x<=cx;x++) {
        for(int y=0;y<=cy;y++) {
          for(int z=0;z<=cz;z++) {
            float tx = px;
            if (x > width) tx += width; else tx += x;
            float ty = py;
            if (y > height) ty += height; else ty += y;
            float tz = pz;
            if (z > depth) tz += depth; else tz += z;
            world.getBlock(dim,tx,ty,tz,coords);
            coords.block.etick(this, coords);
          }
        }
      }
    }
  }

  public void updateFlags(float dx, float dy, float dz) {
    onGround = onGround(dx,dy,dz, (char)0);
    onWater = onGround(dx,dy,dz, Blocks.WATER);

    checkBlocks(dx,dy,dz);

    //check right at camera position
    char id = world.getID2(dim, pos.x, pos.y + eyeHeight, pos.z);
    underWater = id == Blocks.WATER;
    underLava = id == Blocks.LAVA;
  }

  /** Checks if entity is on ground (tid=0) or on block with id == tid (tid != 0)
   *
   * @param tid = target id (0=check for any solid block to stand on)
   */
  public boolean onGround(float dx, float dy, float dz, char tid) {
    float tx = pos.x + dx;
    float ty = pos.y + dy + height2 - Static._1_16;
    float tz = pos.z + dz;

    float px = pos.x + dx - width2;
    float py = pos.y + dy - Static._1_16;
    float pz = pos.z + dz - depth2;

    int cx = Static.ceil(width);
    int cy = Static.ceil(height);
    int cz = Static.ceil(depth);

    synchronized(coords) {
      for(int x=0;x<=cx;x++) {
        for(int z=0;z<=cz;z++) {
          float sx = px;
          if (x > width) sx += width; else sx += x;
          float sy = py;
          float sz = pz;
          if (z > depth) sz += depth; else sz += z;
          world.getBlock(dim,sx,sy,sz,coords);
          if (tid == 0) {
            if (coords.block.hitBox(tx, ty, tz, width2, height2, depth2, coords, BlockHitTest.Type.ENTITY)) return true;
          } else {
            if (coords.block.id == tid) return true;
          }
        }
      }
    }
    return false;
  }

  public boolean hitHead() {
    float tx = pos.x;
    float ty = pos.y + height2;
    float tz = pos.z;

    float px = pos.x - width2;
    float py = pos.y + height;
    float pz = pos.z - depth2;

    int cx = Static.ceil(width);
    int cy = Static.ceil(height);
    int cz = Static.ceil(depth);

    synchronized(coords) {
      for(int x=0;x<=cx;x++) {
        for(int z=0;z<=cz;z++) {
          float sx = px;
          if (x > width) sx += width; else sx += x;
          float sy = py;
          float sz = pz;
          if (z > depth) sz += depth; else sz += z;
          world.getBlock(dim,sx,sy,sz,coords);
          if (coords.block.hitBox(tx, ty, tz, width2, height2, depth2, coords, BlockHitTest.Type.ENTITY)) return true;
        }
      }
    }
    return false;
  }

  private int canStepUp(float dx,float dy,float dz) {
    int ret = 0;
    for(float step=Static._1_16;step<=1.0f;step+=Static._1_16) {
      if (hitTest(dx,dy+step,dz)) {
        ret++;
        continue;
      }
      return ret;
    }
    return -1;
  }

  /** Tests if delta movement would place entity in a block
   *
   * @param dx,dy,dz = delta movement
   * @param sneak = sneak mode (do not fall off block)
   * @param maxFall = max blocks entity can fall (-1 = ignore)
   * @param avoid = avoid certain blocks (water, lava)
   *
   * @return -5=would move into avoided block type
   * @return -4=would fall too far (maxFall)
   * @return -3=would fall off block (sneak mode)
   * @return -2=can jump up
   * @return -1=inBlock (can not jump up)
   * @return 0=!inBlock
   * @return 0-16=canStepUp(*_1_16)
   */
  public int inBlock(float dx, float dy, float dz, boolean sneak, int maxFall, int avoid) {
    boolean hit = hitTest(dx,dy,dz);

    boolean nowOnGround = onGround(dx,dy,dz, (char)0);

    if (sneak && onGround && !hit && !nowOnGround) {
      return -3;
    }

    if (!hit && onGround && !nowOnGround && maxFall != -1) {
      for(float a=0;a<=maxFall;a+=Static._1_16) {
        if (onGround(dx,dy - a,dz, (char)0)) return 0;
      }
      return -4;
    }

    if (!nowOnGround && avoid != AVOID_NONE && !inWater && !inLava) {
      checkBlocks(dx,dy - 0.5f,dz);
      if ((avoid & AVOID_WATER) == AVOID_WATER && inWater) {
        inWater = false;
        return -5;
      }
      if ((avoid & AVOID_LAVA) == AVOID_LAVA && inLava) {
        inLava = false;
        return -5;
      }
    }

    if (!onGround) {
      if (hit) return -1; else return 0;
    }

    int ret = canStepUp(dx,dy,dz);

    if (ret > 8) return -2;

    return ret;
  }

  private float fallBlocks;  //# blocks entity has fallen (to calc fall dmg)

  /** Applies gravity to entity.
   *
   * @param depth = buoyant depth (floating entity) (0=not buoyant)
   */
  private boolean gravity(float depth) {
    updateFlags(0,depth,0);
    if (mode == MODE_FLYING) {
      fallBlocks = 0;
      vel.y = 0;
      return false;
    }
    if (depth == 0) {
      if (onGround && vel.y == 0.0f) {
        fallBlocks = 0;
        return false;
      }
    } else {
      if (onWater && !inWater && vel.y == 0.0f) {
        fallBlocks = 0;
        return false;
      }
      if (onGround && !inWater && vel.y == 0.0f) {
        fallBlocks = 0;
        return false;
      }
    }
    //apply gravity
    if (depth == 0) {
      vel.y -= Static.gravitySpeed;
    } else {
      if (inWater || onWater) {
        vel.y += Static.gravitySpeed;
      } else {
        vel.y -= Static.gravitySpeed;
      }
    }
    //apply drag
    if (vel.y > 0.0f) vel.y -= yDrag / 20f; else vel.y += yDrag / 20f;
    //apply max
    if (inWeb) {
      fallBlocks = 0;
      if (vel.y < -Static.termVelocityWeb) vel.y = -Static.termVelocityWeb;
      else if (vel.y > Static.termVelocityWeb) vel.y = Static.termVelocityWeb;
    } if (inWater || inLava) {
      fallBlocks = 0;
      if (vel.y < -Static.termVelocityLiquid) vel.y = -Static.termVelocityLiquid;
    } else if (inLadder || inVines) {
      fallBlocks = 0;
      if (vel.y < -Static.termVelocityClimb) vel.y = -Static.termVelocityClimb;
    } else {
      if (vel.y < -Static.termVelocityAir) vel.y = -Static.termVelocityAir;
    }
    if (vel.y == 0.0f) {
      //note:may not happen at top of jump
      fallBlocks = 0;
      return false;
    }
    float stepY, deltaY = vel.y;
    while (deltaY != 0.0f) {
      stepY = deltaY;
      if (stepY > Static._1_16) stepY = Static._1_16;
      if (stepY < -Static._1_16) stepY = -Static._1_16;
      pos.y += stepY;
      deltaY -= stepY;
      if (stepY > 0.0f) {
        //jumping
        jumpPos = pos.y;  //debug info
        if (hitHead()) {
          //bumped head
          pos.y -= stepY;
          vel.y = 0.0f;
          return true;
        }
        if (depth != 0) {
          updateFlags(0,0,0);
          if (onWater && !inWater) {
            vel.y = 0.0f;
            return true;
          }
        }
      } else {
        //falling
        fallBlocks += -stepY;
        if (onGround(0, 0, 0, (char)0)) {
          if (mode == MODE_JUMPING) mode = MODE_IDLE;
          if (!inWater && !inLava && fallBlocks > 3 && this instanceof CreatureBase) {
            if (world.isServer) {
              //deal falling dmg
              Static.log("falling dmg:" + fallBlocks + ":" + this);
              fallBlocks -= 3f;
              CreatureBase cb = (CreatureBase)this;
              cb.takeDmg(fallBlocks, null);
            }
          }
          onGround = true;
          fallBlocks = 0;
          vel.y = 0.0f;
          if (pos.y > 0.0f) {
            pos.y -= pos.y % Static._1_16;
          } else {
            pos.y -= Static._1_16 + (pos.y % Static._1_16);
          }
          return true;
        }
      }
    }
    return true;
  }

  public void setXVel(float xv) {
    //TODO : use coeff of surface (ie: make ice slippery)
    if (xv > 0) {
      if (vel.x < xv) vel.x = xv;
    } else {
      if (vel.x > xv) vel.x = xv;
    }
  }

  public void setZVel(float zv) {
    //TODO : use coeff of surface (ie: make ice slippery)
    if (zv > 0) {
      if (vel.z < zv) vel.z = zv;
    } else {
      if (vel.z > zv) vel.z = zv;
    }
  }

  public boolean jump() {
    if (mode != MODE_FLYING) {
      if (inWater || inLava) {
        vel.y = jumpVelocity / 3.0f;
      } else {
        if (wasInLiquid || onGround) {
          vel.y = jumpVelocity;
          jumpPos = 0.0f;
          jumpStart = pos.y;
          mode = MODE_JUMPING;
          return true;
        }
      }
    }
    return false;
  }

  /** How deep creatures will auto float in water.*/
  public float getBuoyant() {
    if (inWater && mode != MODE_FLYING) {
      floatRad += 0.314f;
      if (floatRad > Static.PIx2) floatRad = 0f;
      return 0.5f + (float)Math.sin(floatRad) * 0.25f;
    } else {
      return 0;
    }
  }

  public static final int AVOID_NONE = 0;
  public static final int AVOID_LAVA = 1;
  public static final int AVOID_WATER = 2;
  public static final int AVOID_LAVA_WATER = 3;

  /** Attempt to walk in x/z (may move up steps)
   * @param sneak : sneak mode (do not fall off block)
   * @param stick : stick into blocks (arrows)
   * @param usePath : use path
   * @param maxFall : max dist to call
   * @param avoid : avoid certain blocks (water, lava, etc.)
   *
   * @return entity moved?
   */

  public boolean move(boolean sneak, boolean stick, boolean usePath, int maxFall, int avoid) {
    boolean server = world.isServer;
    Chunk chunk1 = null, chunk2;
    float ox=0, oy=0, oz=0;  //org pos

    updateFlags(0,0,0);

    if (inWeb) {
      vel.x /= Static.webVelocityScale;
      vel.z /= Static.webVelocityScale;
    }

    if (server) {
      chunk1 = getChunk();
      if (chunk1 == null) return false;
      ox = pos.x;
      oy = pos.y;
      oz = pos.z;
    }

    float step;
    int ret = -1;
    boolean moved = false;

    if ((inLadder || inVines) && sneak) {
      //hold position up/down
    } else {
      if (gravity(getBuoyant())) moved = true;
    }

    if (vel.x == 0.0f && vel.z == 0.0f) return moved;

    boolean climb = false;
    //try to move in one step
    if (vel.x < 1 && vel.z < 1 && mode != MODE_FLYING && !usePath) {
      ret = inBlock(vel.x,0,vel.z, sneak, maxFall, avoid);
    }
    if (mode == MODE_FLYING) {
      //TODO : creative mode
      ret = 0;
    }
    switch (ret) {
      default:
        pos.y += ret * Static._1_16;
        //no break
      case 0:
        pos.x += vel.x;
        pos.z += vel.z;
        moved = true;
        break;
      case -5:  //avoidance
      case -4:  //move closer to edge
      case -3:  //move as close as possible to edge
      case -2:  //do not jump here (get closer first)
      case -1:
        //move in smaller increments
        float xVel = vel.x;
        float zVel = vel.z;
        while (xVel != 0.0f || zVel != 0.0f) {
          if (xVel < -0.01f) {
            //move west
            if (xVel < -Static._1_32) step = -Static._1_32; else step = xVel;
            ret = inBlock(step,0,0, sneak, maxFall, avoid);
            switch (ret) {
              default:
                pos.y += ret * Static._1_16;
                //no break;
              case 0:
                pos.x += step;
                xVel -= step;
                moved = true;
                break;
              case -2:
                if (id != Entities.PLAYER) {
                  jump();
                }
                //no break;
              case -1:
                if (stick) {
                  pos.x += step;  //stick into block (arrow)
                } else {
                  pos.x = Static.floor((pos.x - width2) * 16.0f) / 16.0f + width2 + 0.001f;
                }
                xVel = 0.0f;
                vel.x = 0.0f;
                if (inLadder || inVines) climb = true;
                break;
              case -3:
              case -4:
              case -5:
                //TODO : move close to edge without falling
                xVel = 0.0f;
                vel.x = 0.0f;
                break;
            }
          } else if (xVel > 0.01f) {
            //move east
            if (xVel > Static._1_32) step = Static._1_32; else step = xVel;
            ret = inBlock(step,0,0, sneak, maxFall, avoid);
            switch (ret) {
              default:
                pos.y += ret * Static._1_16;
                //no break;
              case 0:
                pos.x += step;
                xVel -= step;
                moved = true;
                break;
              case -2:
                if (id != Entities.PLAYER) {
                  jump();
                }
                //no break;
              case -1:
                if (stick) {
                  pos.x += step;
                } else {
                  pos.x = Static.ceil((pos.x + width2) * 16.0f) / 16.0f - width2 - 0.001f;
                }
                xVel = 0.0f;
                vel.x = 0.0f;
                if (inLadder || inVines) climb = true;
                break;
              case -3:
              case -4:
              case -5:
                //TODO : move close to edge without falling
                xVel = 0.0f;
                vel.x = 0.0f;
                break;
            }
          } else {
            xVel = 0.0f;
          }
          if (zVel < -0.01f) {
            //move north
            if (zVel < -Static._1_32) step = -Static._1_32; else step = zVel;
            ret = inBlock(0,0,step, sneak, maxFall, avoid);
            switch (ret) {
              default:
                pos.y += ret * Static._1_16;
                //no break;
              case 0:
                pos.z += step;
                zVel -= step;
                moved = true;
                break;
              case -2:
                if (id != Entities.PLAYER) {
                  jump();
                }
                //no break;
              case -1:
                if (stick) {
                  pos.z += step;
                } else {
                  pos.z = Static.floor((pos.z - depth2) * 16.0f) / 16.0f + depth2 + 0.001f;
                }
                zVel = 0.0f;
                vel.z = 0.0f;
                if (inLadder || inVines) climb = true;
                break;
              case -3:
              case -4:
              case -5:
                //TODO : move close to edge without falling
                zVel = 0.0f;
                vel.z = 0.0f;
                break;
            }
          } else if (zVel > 0.01f) {
            //move south
            if (zVel > Static._1_32) step = Static._1_32; else step = zVel;
            ret = inBlock(0,0,step, sneak, maxFall, avoid);
            switch (ret) {
              default:
                pos.y += ret * Static._1_16;
                //no break;
              case 0:
                pos.z += step;
                zVel -= step;
                moved = true;
                break;
              case -2:
                if (id != Entities.PLAYER) {
                  jump();
                }
                //no break;
              case -1:
                if (stick) {
                  pos.z += step;
                } else {
                  pos.z = Static.ceil((pos.z + depth2) * 16.0f) / 16.0f - depth2 - 0.001f;
                }
                zVel = 0.0f;
                vel.z = 0.0f;
                if (inLadder || inVines) climb = true;
                break;
              case -3:
              case -4:
              case -5:
                //TODO : move close to edge without falling
                zVel = 0.0f;
                vel.z = 0.0f;
                break;
            }
          } else {
            zVel = 0.0f;
          }
        }
      break;
    }

    if (climb) {
      //use ladder/vines
      vel.y = Static.climbSpeed;
    }

    if (server) {
      chunk2 = getChunk();
      if (chunk2 == null) {
        //do not move into a chunk that is not loaded
        pos.x = ox;
        pos.y = oy;
        pos.z = oz;
        return moved;  //might have jumped
      }
      if (chunk1 != chunk2) {
        chunk1.delEntity(this);
        chunk2.addEntity(this);
        Static.server.broadcastEntitySpawn(this);  //in case moves into area that is now visible to a client
      }
    }

    //apply drag
    float deltaTime = 1000.0f / 20.0f;
    float drag = xzDrag / 1000.0f * deltaTime;
    float total = Math.abs(vel.x) + Math.abs(vel.z);
    float xp = Math.abs(vel.x) / total;
    float xdrag = drag * xp;
    float zp = Math.abs(vel.z) / total;
    float zdrag = drag * zp;
    if (vel.x > 0.0f) {
      vel.x -= xdrag;
      if (vel.x < 0) vel.x = 0;
    } else if (vel.x < 0.0f) {
      vel.x += xdrag;
      if (vel.x > 0) vel.x = 0;
    }

    if (vel.z > 0.0f) {
      vel.z -= zdrag;
      if (vel.z < 0) vel.z = 0;
    } else if (vel.z < 0.0f) {
      vel.z += zdrag;
      if (vel.z > 0) vel.z = 0;
    }

    if (moved) dirty = true;

    return moved;
  }

  public static Vectors move_vectors = new Vectors();
  /** Player move. */
  public void move(boolean up, boolean dn, boolean lt, boolean rt,
    boolean jump, boolean sneak, boolean run, boolean b1, boolean b2,
    boolean fup, boolean fdn)
  {
    float speed = 0;
    boolean flying = mode == MODE_FLYING;
    if (inWater || inLava) {
      mode = EntityBase.MODE_SWIM;
      speed = swimSpeed;
    }
    else if (sneak || b2) {
      mode = EntityBase.MODE_SNEAK;
      speed = sneakSpeed;
    }
    else if (run) {
      mode = EntityBase.MODE_RUN;
      speed = runSpeed;
    }
    else {
      mode = EntityBase.MODE_WALK;
      speed = walkSpeed;
    }
    if (lt || rt || up || dn) {
      synchronized(move_vectors) {
        calcVectors(speed / 20.0f, move_vectors);
        float x = 0, z = 0;
        if (lt) {
          x += move_vectors.left.v[0];
          z += move_vectors.left.v[2];
        }
        if (rt) {
          x += -move_vectors.left.v[0];
          z += -move_vectors.left.v[2];
        }
        if (up) {
          x += move_vectors.forward.v[0];
          z += move_vectors.forward.v[2];
        }
        if (dn) {
          x += -move_vectors.forward.v[0];
          z += -move_vectors.forward.v[2];
        }
        if (x != 0) setXVel(x);
        if (z != 0) setZVel(z);
      }
    } else {
      mode = EntityBase.MODE_IDLE;
    }
    if (jump) {
      jump();
    }
    if (flying) mode = MODE_FLYING;  //reset flying mode (creative)
    if (mode == MODE_FLYING && fup) {
      pos.y += 1.0f;
    }
    if (mode == MODE_FLYING && fdn) {
      pos.y -= 1.0f;
    }
    move(sneak, false, false, -1, AVOID_NONE);
  }

  public void rotateY(float angle) {
    ang.y += angle;
    //keep between -180 and +180
    while (ang.y < -180.0f) ang.y += 360.0f;
    while (ang.y > 180.0f) ang.y -= 360.0f;
  }

  public void rotateX(float angle) {
    ang.x += angle;
    //keep between -90 and +90
    if (ang.x < -90.0f) ang.x = -90.0f;
    if (ang.x > 90.0f) ang.x = 90.0f;
  }

  private static GLMatrix cvmat = new GLMatrix();
  //this func is called from many threads (client & server side)
  //so it MUST be sync'ed
  public synchronized Vectors calcVectors(float speed, Vectors v) {
    cvmat.setIdentity();
    cvmat.addRotate(ang.y, 0, 1, 0);
    if (mode == MODE_FLYING) speed *= 2.0f;  //test
    v.forward.set(0,0,-speed);
    cvmat.mult(v.forward);
    v.left.set(-speed,0,0);
    cvmat.mult(v.left);

    cvmat.setIdentity();
    cvmat.addRotate(ang.x, 1, 0, 0);
    cvmat.addRotate(ang.y, 0, 1, 0);
    v.facing.set(0,0,-1);
    cvmat.mult(v.facing);
    return v;
  }

  /** Used when placing a block. */
  public void getDir(Coords c) {
    c.dir_xz = -1;
    c.dir_y = -1;
    c.dir = -1;

    //for some custom blocks
    c.ang.copy(ang);

    if (ang.x < -45.0f) {c.dir = A; c.dir_y = A;}  //looking up
    if (ang.x > 45.0f) {c.dir = B; c.dir_y = B;}  //looking down
    if (ang.y >= 135.0f && ang.y <= 225.0f) {if (c.dir == -1) c.dir = S; c.dir_xz = S; return;}  //looking south
    if (ang.y >= 45.0f && ang.y <= 135.0f) {if (c.dir == -1) c.dir = E; c.dir_xz = E; return;}  //looking east
    if (ang.y >= 225.0f && ang.y <= 315.0f) {if (c.dir == -1) c.dir = W; c.dir_xz = W; return;}  //looking west
    //looking north
    if (c.dir == -1) c.dir = N;
    c.dir_xz = N;
  }

  //normal body parts (steve, zombie, etc.)
  public static final int HEAD = 0;
  public static final int BODY = 1;
  public static final int L_ARM = 2;
  public static final int R_ARM = 3;
  public static final int L_LEG = 4;
  public static final int R_LEG = 5;

  //transportation mode (used in CreatureBase for mobs)
  public static final int MODE_IDLE = 0;
  public static final int MODE_WALK = 1;
  public static final int MODE_RUN = 2;
  public static final int MODE_SNEAK = 3;
  public static final int MODE_SWIM = 4;
  public static final int MODE_FLYING = 5;
  public static final int MODE_JUMPING = 6;

  //bit states for player input
  public static final int JUMP = 0x10;
  public static final int LT_BUTTON = 0x20;
  public static final int RT_BUTTON = 0x40;
  public static final int MOVE_UP = 0x80;
  public static final int MOVE_DN = 0x100;
  public static final int MOVE_LT = 0x200;
  public static final int MOVE_RT = 0x400;
  public static final int SNEAK = 0x800;
  public static final int RUN = 0x1000;
  public static final int FLY_UP = 0x2000;
  public static final int FLY_DN = 0x4000;

  /** Returns chunk containing Entity. */
  public Chunk getChunk() {
    int cx = Static.floor(pos.x / 16.0f);
    int cz = Static.floor(pos.z / 16.0f);
    return world.chunks.getChunk(dim, cx, cz);
  }

  public BlockBase getBlock(float dx, float dy, float dz) {
    Chunk chunk = getChunk();

    int gx = Static.floor(pos.x % 16.0f);
    if (pos.x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(pos.y);
    int gz = Static.floor(pos.z % 16.0f);
    if (pos.z < 0 && gz != 0) gz = 16 + gz;

    char id = chunk.getID(gx, gy, gz);
    return Static.blocks.blocks[id];
  }

  public BlockBase getBlock2(float dx, float dy, float dz) {
    Chunk chunk = getChunk();

    int gx = Static.floor(pos.x % 16.0f);
    if (pos.x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(pos.y);
    int gz = Static.floor(pos.z % 16.0f);
    if (pos.z < 0 && gz != 0) gz = 16 + gz;

    char id = chunk.getID2(gx, gy, gz);
    return Static.blocks.blocks[id];
  }

  /** Checks if entity is within valid chunk. */
  public boolean hasChunk() {
    int cx = Static.floor(pos.x / 16.0f);
    int cz = Static.floor(pos.z / 16.0f);
    return world.chunks.hasChunk(dim, cx, cz);
  }

  public Coords getCoords(Coords c) {
    c.chunk = getChunk();
    c.entity = this;
    c.cx = c.chunk.cx;
    c.cz = c.chunk.cz;
    c.x = Static.floor(pos.x);
    c.y = Static.floor(pos.y);
    c.z = Static.floor(pos.z);

    int gx = Static.floor(pos.x % 16.0f);
    if (pos.x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(pos.y);
    int gz = Static.floor(pos.z % 16.0f);
    if (pos.z < 0 && gz != 0) gz = 16 + gz;

    c.gx = gx;
    c.gy = gy;
    c.gz = gz;

    return c;
  }

  /** Find an entity near this one. */
  public EntityBase findEntity(float range, int id) {
    return Static.server.findEntity(pos.x,pos.y,pos.z,range, id);
  }

  public void despawn() {
    Chunk chunk = getChunk();
    if (chunk == null) {
      //this should not happen
      Static.log("Entity can not despawn, chunk not loaded???");
      return;
    }
    chunk.delEntity(this);
    Static.server.world.delEntity(uid);
    Static.server.broadcastEntityDespawn(this);
  }

  /** Server side tick. */
  public void tick() {
    if (sound > 0) {
      sound--;
    }
    age++;
    if ((maxAge != -1 && age >= maxAge && target == null) || (pos.y < -128 && id != Entities.PLAYER)) {
      despawn();
      return;
    }
    if (teleportTimer > 0) {
      teleportTimer--;
    }
    if (this instanceof CreatureBase) {
      tickBlocks(0,0,0);
    }
  }
  /** Client side tick. */
  public void ctick() {}
  public float getSpawnRate() {return 0;}
  /** Returns which dimensions this entity would generate in. */
  public int[] getGenerateDims() {
    return new int[0];
  }
  /** Returns which dimensions this entity would spawn in. */
  public int[] getSpawnDims() {
    return new int[0];
  }
  public EntityBase spawn(Chunk chunk) {return null;}
  public boolean hitPoint(float hx, float hy, float hz) {
    return (
         hx >= pos.x - width2 && hx < pos.x + width2
      && hy >= pos.y && hy < pos.y + height
      && hz >= pos.z - depth2 && hz < pos.z + depth2
    );
  }
  public boolean hitBox(float hx, float hy, float hz, float hwidth2, float hheight2, float hdepth2) {
    //AABB (Axis Aligned Bounding Box)
    //https://gamedev.stackexchange.com/questions/60505/how-to-check-for-cube-collisions

    if (Math.abs((hx) - (pos.x) ) < (width2 + hwidth2) ) {
      if (Math.abs((hy) - (pos.y + height2) ) < (height2 + hheight2) ) {
        if (Math.abs((hz) - (pos.z) ) < (depth2 + hdepth2) ) {
          return true;
        }
      }
    }

    return false;
  }
  public boolean canSelect() {
    return true;
  }
  public boolean canUse() {
    return false;
  }
  /** Use entity. */
  public void useEntity(Client c, boolean sneak) {}
  /** Use tool on entity. */
  public boolean useTool(Client client, Coords c) {
    Static.log("Entity.useTool");
    return false;
  }

  public float getLight(float sunLight) {
    float bl = ((float)world.getBlockLight(dim, pos.x, pos.y + height2, pos.z)) / 15.0f;
    float sl = ((float)world.getSunLight(dim, pos.x, pos.y + height2, pos.z)) / 15.0f * sunLight;
    if (sl > bl)
      return sl;
    else
      return bl;
  }

  /** Returns items entity drops on death. */
  public Item[] drop() {
    return new Item[0];
  }

  public void getIDs() {}

  public boolean isFlagSet(int flag) {
    return (flags & flag) != 0;
  }

  public void setFlag(int flag) {
    flags |= flag;
  }

  public void clrFlag(int flag) {
    flags &= -1 - flag;
  }

  //RenderSource

  public RenderDest getDest() {return null;}

  public void buildBuffers(RenderDest dest, RenderData data) {}

  public void copyBuffers() {}

  public void bindTexture() {}

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {};

  public void render() {}

  /** Setup vehicle/occupant relationships after read() from file/network. */
  public void setupLinks(Chunk chunk, boolean file) {}

  public float getSpeed(int mode) {
    switch (mode) {
      case MODE_JUMPING:
      case MODE_RUN: return runSpeed;
      case MODE_WALK: return walkSpeed;
    }
    return 0f;
  }

  public void convertIDs(char blockIDs[], char itemIDs[]) {}

  private static final byte ver = 0;

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeByte(ver);
    buffer.writeInt(id);
    buffer.writeFloat(pos.x);
    buffer.writeFloat(pos.y);
    buffer.writeFloat(pos.z);
    buffer.writeFloat(ang.x);
    buffer.writeFloat(ang.y);
    buffer.writeFloat(ang.z);
    buffer.writeFloat(vel.x);
    buffer.writeFloat(vel.y);
    buffer.writeFloat(vel.z);
    buffer.writeInt(dim);
    buffer.writeInt(flags);
    if (file) {
      buffer.writeInt(age);
      buffer.writeInt(teleportTimer);
      buffer.writeInt(cid);
    } else {
      buffer.writeInt(uid);
    }
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();
    id = buffer.readInt();
    pos.x = buffer.readFloat();
    pos.y = buffer.readFloat();
    pos.z = buffer.readFloat();
    ang.x = buffer.readFloat();
    ang.y = buffer.readFloat();
    ang.z = buffer.readFloat();
    vel.x = buffer.readFloat();
    vel.y = buffer.readFloat();
    vel.z = buffer.readFloat();
    dim = buffer.readInt();
    flags = buffer.readInt();
    if (file) {
      age = buffer.readInt();
      teleportTimer = buffer.readInt();
      cid = buffer.readInt();
    } else {
      uid = buffer.readInt();
    }
    return true;
  }
}
