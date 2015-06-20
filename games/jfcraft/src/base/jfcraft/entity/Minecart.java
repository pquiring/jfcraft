package jfcraft.entity;

/** Minecart entity
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class Minecart extends CreatureBase {
  public float speed;  //current speed
  public int dir;  //current direction (N,E,S,W,NE,NW,SE,SW,A=none)
  public float dist;  //distance travel on current rail in dir

  private static GLModel model;
  private static int initHealth = 5;

  //render assets
  public static Texture texture;
  private static String textureName;

  private static final float pushSpeed = 3.0f;
  private static final float maxSpeed = 10.0f;

  public RenderDest dest;

  public Minecart() {
    id = Entities.MINECART;
    health = initHealth;
  }

  public String getName() {
    return "MINECART";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init() {
    super.init();
    yDrag = Static.dragSpeed;
    xzDrag = 0.1f;
    width = 1.0f;
    width2 = width/2f;
    height = 1.0f;
    height2 = height/2f;
    depth = 1.0f;
    depth2 = depth/2f;
    dest = new RenderDest(parts.length);
  }

  public void initStatic() {
    textureName = "entity/minecart";
    model = Assets.getModel("minecart").model;
  }

  public void initStatic(GL gl) {
    texture = Textures.getTexture(gl, textureName);
  }

  public void initInstance(GL gl) {
    super.initInstance(gl);
  }

  private static final int BASE = 0;
  private static final int _N = 1;
  private static final int _E = 2;
  private static final int _S = 3;
  private static final int _W = 4;

  private static String parts[] = {"BASE", "NORTH", "EAST", "SOUTH", "WEST"};

  public void buildBuffers(RenderDest dest, RenderData data) {
    dest.resetAll();
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = model.getObject(parts[a]);
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        GLUVMap map = obj.getUVMap(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        GLUVMap map1 = obj.getUVMap("normal");
        float uv1[] = map1.uvl.toArray();
        int crack = 10 - (int)(health * 2);
        if (crack > 9) crack = 9;  //it's broken
        if (crack == 0) {
          buf.addTextureCoords(uv1);
        } else {
          GLUVMap map2 = obj.getUVMap("crack");
          float uv2[] = map2.uvl.toArray();
          buf.adjustCrack(uv2, crack);
          buf.addTextureCoords(uv1, uv2);
        }
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
    needCopyBuffers = true;
  }

  public void bindTexture(GL gl) {
    texture.bind(gl);
  }

  public void copyBuffers(GL gl) {
    dest.copyBuffers(gl);
  }

  private void setMatrixModel(GL gl, int bodyPart) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    mat.addTranslate(pos.x, pos.y + 0.5f, pos.z);
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
  }

  public void render(GL gl) {
    int cnt = parts.length;
    for(int a=0;a<cnt;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(gl, a);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, Static.identity.m);  //model matrix
  }

  private void offRail() {
    dist = 0f;
    if (dir != 0) {
      //set xVel/zVel based on current speed/dir
      switch (dir) {
        case N:
          setXVel(0);
          setZVel(-speed / 20f);
          break;
        case E:
          setXVel(speed / 20f);
          setZVel(0);
          break;
        case S:
          setXVel(0);
          setZVel(speed / 20f);
          break;
        case W:
          setXVel(-speed / 20f);
          setZVel(0);
          break;
        case NE:
          setXVel(speed / 20f);
          setZVel(-speed / 20f);
          break;
        case NW:
          setXVel(-speed / 20f);
          setZVel(-speed / 20f);
          break;
        case SE:
          setXVel(speed / 20f);
          setZVel(speed / 20f);
          break;
        case SW:
          setXVel(-speed / 20f);
          setZVel(speed / 20f);
          break;
      }
      dir = 0;
    }
    speed = 0;
  }

  public void tick() {
    super.tick();
    if (occupant != null) return;
    updateFlags(0,0,0);
    boolean fell = false;
    boolean moved;
    Chunk chunk1 = getChunk();
    moved = moveOnRails();
    if (!moved) {
      offRail();
      fell = gravity(-1);
      moved = move(false, false, false, -1, AVOID_NONE);
    }
    Chunk chunk2 = getChunk();
    if (chunk2 != chunk1) {
      chunk1.delEntity(this);
      chunk2.addEntity(this);
    }
    if (fell || moved) {
      Static.server.broadcastEntityMove(this);
    }
  }

  private boolean isN() {return dir == N || dir == NE || dir == NW;}
  private boolean isE() {return dir == E || dir == NE || dir == SE;}
  private boolean isS() {return dir == S || dir == SE || dir == SW;}
  private boolean isW() {return dir == W || dir == NW || dir == SW;}

  private static final float gravity = 0.05f;

  private boolean moveOnRails() {
    Chunk chunk = this.getChunk();
    if (chunk == null) {
      return false;
    }

    int gx = Static.floor(pos.x % 16.0f);
    if (pos.x < 0 && gx != 0) gx = 16 + gx;
    int gy = Static.floor(pos.y);
/*
    if (y > 1f) {
      //a slope up moved into air block just before entering next block
      if (y % 1f < Static._1_32) {
        gy--;
      }
    }
*/
    int gz = Static.floor(pos.z % 16.0f);
    if (pos.z < 0 && gz != 0) gz = 16 + gz;

    char rid = chunk.getID(gx, gy, gz);
    if (!BlockRail.isRail(rid)) {
      return false;
    }

//    if (Static.isClient()) Static.log("pos=" + x + "," + y + "," + z);

    onGround = true;
    vel.x = 0;
    vel.y = 0;
    vel.z = 0;

    int bits = chunk.getBits(gx, gy, gz);
    int e1 = bits & 0x0f;
    int e2 = (bits & 0xf0) >> 4;
    if (e1 == 0 && e2 != 0) {
      e1 = e2;
      e2 = 0;
    }
    boolean u1 = false;
    if ((e1 & 0x8) == 0x8) {
      u1 = true;
      e1 &= 0x7;
    }
    boolean u2 = false;
    if ((e2 & 0x8) == 0x8) {
      u2 = true;
      e2 &= 0x7;
    }
    switch (e1) {
      default:
      case N:
        switch (e2) {
          case E:  //SE or NW
            if (isS() || isE()) {dir = SE;}
            else if (isN() || isW()) {dir = NW;}
            else dir = 0;
            break;
          default:
          case S:  //N or S
            if (isN()) {dir = N;}
            else if (isS()) {dir = S;}
            else dir = 0;
            break;
          case W:  //SW or NE
            if (isS() || isW()) {dir = SW;}
            else if (isN() || isE()) {dir = NE;}
            else dir = 0;
            break;
        }
        break;
      case E:
        switch (e2) {
          case N:  //NW or SE
            if (isS() || isE()) {dir = SE;}
            else if (isN() || isW()) {dir = NW;}
            else dir = 0;
            break;
          case S:  //NE or SW
            if (isN() || isE()) {dir = NE;}
            else if (isS() || isW()) {dir = SW;}
            else dir = 0;
            break;
          default:
          case W:  //E or W
            if (isE()) {dir = E;}
            else if (isW()) {dir = W;}
            else dir = 0;
            break;
        }
        break;
      case S:
        switch (e2) {
          default:
          case N:  //N or S
            if (isN()) {dir = N;}
            else if (isS()) {dir = S;}
            else dir = 0;
            break;
          case E:  //NE or SW
            if (isN() || isE()) {dir = NE;}
            else if (isS() || isW()) {dir = SW;}
            else dir = 0;
            break;
          case W:  //NW or SE
            if (isN() || isW()) {dir = NW;}
            else if (isS() || isE()) {dir = SE;}
            else dir = 0;
            break;
        }
        break;
      case W:
        switch (e2) {
          case N:  //SW or NE
            if (isS() || isW()) {dir = SW;}
            else if (isN() || isE()) {dir = NE;}
            else dir = 0;
            break;
          default:
          case E:  //E or W
            if (isE()) {dir = E;}
            else if (isW()) {dir = W;}
            else dir = 0;
            break;
          case S:  //NW or SE
            if (isN() || isW()) {dir = NW;}
            else if (isS() || isE()) {dir = SE;}
            else dir = 0;
            break;
        }
        break;
    }
    boolean powered = false;
    if (rid == Blocks.RAIL || rid == Blocks.RAIL_DETECTOR || rid == Blocks.RAIL_ACTIVATOR) {
      //do nothing
    }
    else if (rid == Blocks.RAIL_POWERED) {
      ExtraRedstone er = (ExtraRedstone)chunk.getExtra(gx, gy, gz, Extras.REDSTONE);
      if (er == null) {
        Static.log("no redstone data");
        return false;
      }
      if (er.powered) {
        powered = true;
        speed += 0.1f;
      }
    }
    //apply gravity on slope
    if (!powered && (u1 || u2)) {
      if (u1) {
        if (dir == 0) {
          dir = Direction.opposite(e1);
        }
        switch (e1) {
          case N: if (isN()) speed -= gravity; else speed += gravity; break;
          case E: if (isE()) speed -= gravity; else speed += gravity; break;
          case S: if (isS()) speed -= gravity; else speed += gravity; break;
          case W: if (isW()) speed -= gravity; else speed += gravity; break;
        }
      } else {
        if (dir == 0) {
          dir = Direction.opposite(e2);
        }
        switch (e2) {
          case N: if (isN()) speed -= gravity; else speed += gravity; break;
          case E: if (isE()) speed -= gravity; else speed += gravity; break;
          case S: if (isS()) speed -= gravity; else speed += gravity; break;
          case W: if (isW()) speed -= gravity; else speed += gravity; break;
        }
      }
    }
    if (speed > maxSpeed) speed = maxSpeed;
    //apply drag
    if (false) {
      speed -= xzDrag;
      if (speed <= 0) {
        speed = 0;
      }
    }
    if (dir == 0 || speed == 0f) return true;
    float delta = speed / 20f;
    float step;
    float maxDist;
    if (dir > W) {
      //NE,NW,SE,SW
      maxDist = 0.71f;  //diagonal
    } else {
      //N,E,S,W
      maxDist = 1f;
    }
//    if (Static.isClient()) Static.log("dir=" + dir + ",speed=" + speed);
    float dist_maxDist = 0f;
    boolean hasNorth = false;
    boolean up = false, dn = false;
    if (dir > W) {
      //which diag path is cart on?
      hasNorth = e1 == N || e2 == N;
    } else {
      //is cart moving up/down slope?
      if (u1) {
        up = dir == e1;
        dn = !up;
      } else if (u2) {
        up = dir == e2;
        dn = !up;
      }
    }

    while (delta > 0) {
      if (delta > Static._1_16) {
        step = Static._1_16;
      } else {
        step = delta;
      }
      delta -= step;
      dist += step;
      pos.x = (float)Math.floor(pos.x);
      pos.y = (float)Math.floor(pos.y);
      pos.z = (float)Math.floor(pos.z);
      if (dir > W) {
        dist_maxDist = dist  *  maxDist;
      } else {
        if (up) pos.y += dist;
        else if (dn) pos.y += 1f - dist;
      }
      switch (dir) {
        case N:
          pos.x += 0.5f;
          pos.z += 1.0f - dist;
          ang.y = 0;
          break;
        case E:
          pos.x += dist;
          pos.z += 0.5f;
          ang.y = 90f;
          break;
        case S:
          pos.x += 0.5f;
          pos.z += dist;
          ang.y = 180f;
          break;
        case W:
          pos.x += 1.0f - dist;
          pos.z += 0.5f;
          ang.y = -90f;
          break;
        case NE:
          if (hasNorth) {
            //W -> N
            pos.x += dist_maxDist;
            pos.z += 0.5f - dist_maxDist;
          } else {
            //S -> E
            pos.x += 0.5f + dist_maxDist;
            pos.z += 1f - dist_maxDist;
          }
          ang.y = 45f;
          break;
        case NW:
          if (hasNorth) {
            //E -> N
            pos.x += 1f - dist_maxDist;
            pos.z += 0.5f - dist_maxDist;
          } else {
            //S -> W
            pos.x += 0.5f - dist_maxDist;
            pos.z += 1f - dist_maxDist;
          }
          ang.y = -45f;
          break;
        case SE:
          if (hasNorth) {
            //N -> E
            pos.x += 0.5f + dist_maxDist;
            pos.z += dist_maxDist;
          } else {
            //W -> S
            pos.x += dist_maxDist;
            pos.z += 0.5f + dist_maxDist;
          }
          ang.y = 90f + 45f;
          break;
        case SW:
          if (hasNorth) {
            //N -> W
            pos.x += 0.5f - dist_maxDist;
            pos.z += dist_maxDist;
          } else {
            //E -> S
            pos.x += 1f - dist_maxDist;
            pos.z += 0.5f + dist_maxDist;
          }
          ang.y = -90f - 45f;
          break;
      }
      if (dist > maxDist) {
        switch (dir) {
          case N: gz--; break;
          case E: gx++; break;
          case S: gz++; break;
          case W: gx--; break;
        }
        if (up || dn) {
          //check if rail levels off
          rid = chunk.getID(gx, gy, gz);
          if (BlockRail.isRail(rid)) {
            if (up) {
              pos.y -= Static._1_16;
            } else {
              pos.y += Static._1_16;
            }
          }
        } else {
          //check if next rail slopes down
          if (gy > 0) {
            rid = chunk.getID(gx, gy-1, gz);
            if (BlockRail.isRail(rid)) {
              pos.y -= Static._1_16;
            }
          }
        }
        dist -= maxDist;
//        if (Static.isClient()) Static.log("new pos=" + x + "," + y + "," + z);
        return moveOnRails();
      }
      if (inBlock(0, up | dn ? 1f : 0, 0, false, -1, AVOID_NONE) != 0) {
        dir = Direction.opposite(dir);
      }
    }
    return true;
  }

  public boolean canUse() {
    return true;
  }

  public void use(Client c) {
    if (occupant != null) return;  //in use
    occupant = c.player;
    c.player.vehicle = this;
    c.player.pos.x = pos.x;
    c.player.pos.y = pos.y;
    c.player.pos.z = pos.z;
    c.serverTransport.setRiding(occupant, this);
  }

  /** Player move. */
  public synchronized void move(boolean up, boolean dn, boolean lt, boolean rt,
    boolean jump, boolean sneak, boolean run, boolean b1, boolean b2,
    boolean fup, boolean fdn)
  {
    updateFlags(0,0,0);
    //to keep the minecart and player synced this must be synced with render engine
    synchronized(Static.renderLock) {
      Chunk chunk1 = getChunk();
      if (moveOnRails()) {
        Coords c = new Coords();
        if (up) {
          if (speed < pushSpeed / 2f) {
            speed = pushSpeed;
            occupant.getDir(c);
            dir = c.dir_xz;
          }
        } else if (dn) {
          if (speed < pushSpeed) {
            speed = pushSpeed;
            occupant.getDir(c);
            dir = Direction.opposite(c.dir_xz);
          }
        }
      } else {
        offRail();
        gravity(-1);
        move(false, false, false, -1, AVOID_NONE);
      }
      Chunk chunk2 = getChunk();
      if (chunk2 != chunk1) {
        chunk1.delEntity(this);
        chunk2.addEntity(this);
      }
      occupant.pos.x = pos.x;
      occupant.pos.y = pos.y;
      occupant.pos.z = pos.z;
    }
    if (Static.isServer() && sneak) {
      //eject occupant
      occupant.vehicle = null;
      occupant.client.serverTransport.setRiding(occupant, null);
      occupant = null;
    }
  }
  public boolean canSelect() {
    return true;
  }
  public Item[] drop() {
    return new Item[] {new Item(Items.MINECART)};
  }
  public boolean isVehicle() {
    return true;
  }
  public boolean cracks() {
    return true;
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    buffer.writeFloat(speed);
    buffer.writeInt(dir);
    buffer.writeFloat(dist);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    speed = buffer.readFloat();
    dir = buffer.readInt();
    dist = buffer.readFloat();
    return true;
  }
}
