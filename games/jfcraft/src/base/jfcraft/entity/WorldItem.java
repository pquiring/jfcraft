package jfcraft.entity;

/** ground block
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class WorldItem extends EntityBase {
  public Item item;

  public RenderDest obj;
  public int buffersIdx;
  public Texture texture;
  public EntityBase entity;

  public WorldItem() {
    id = Entities.WORLDITEM;
  }

  public WorldItem setItem(Item item) {
    if (item != null) {
      this.item = (Item)item.clone();
    }
    return this;
  }

  public String getName() {
    return "WorldItem";
  }

  public void init() {
    super.init();
    width = 0.5f;
    width2 = width/2;
    height = width;
    height2 = width2;
    depth = width;
    depth2 = width2;
    yDrag = Static.dragSpeed;
    xzDrag = 0.5f;
    maxAge = 20 * 60 * 5;  //5 mins
  }
  public void initInstance(GL gl) {
    super.initInstance(gl);
    if (item == null) return;
    obj = new RenderDest(2);  //DEST_...
    if (Static.isBlock(item.id)) {
      BlockBase block = Static.blocks.blocks[item.id];
      if (block.renderAsEntity) {
        entity = Static.entities.entities[block.entityID];
      } else if (block.renderAsItem) {
        buffersIdx = 0;
        block.addFaceWorldItem(obj.getBuffers(0), item.var, block.isGreen);
        texture = block.textures[0].texture;
      } else {
        RenderData data = new RenderData();
        data.x = -0.5f;
        data.y = 0f;
        data.z = -0.5f;
        data.sl[X] = 1.0f;
        data.bl[X] = 0.0f;
        data.crack = -1;
        if (block.isVar) {
          data.var[X] = item.var;
        }
        block.buildBuffers(obj, data);
        buffersIdx = block.buffersIdx;
        texture = block.textures[0].texture;
      }
    } else {
      buffersIdx = 0;
      ItemBase baseitem = Static.items.items[item.id];
      baseitem.addFaceWorldItem(obj.getBuffers(0), item.var, baseitem.isGreen);
      texture = baseitem.textures[0].texture;
    }
    RenderBuffers buf = obj.getBuffers(buffersIdx);
    buf.copyBuffers(gl);
  }
  public void bindTexture(GL gl) {
    if (texture != null) {
      texture.bind(gl);
    } else if (entity != null) {
      entity.bindTexture(gl);
    }
  }
  public void render(GL gl) {
    if (entity != null) {
      entity.pos.x = pos.x;
      entity.pos.y = pos.y + 0.125f;  //0.5f * 0.25f = 0.125f
      entity.pos.z = pos.z;
      entity.ang.y = ang.y;
      entity.setScale(0.25f);
      entity.render(gl);
    } else {
      mat.setIdentity();
      mat.addRotate(ang.y, 0, 1, 0);
      mat.addTranslate(pos.x, pos.y, pos.z);
      float scale = 0.25f;
      mat.addScale(scale, scale, scale);
      gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);
      RenderBuffers buf = obj.getBuffers(buffersIdx);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
  }
  public void tick() {
    ctick();
    boolean moved = false;
    if (gravity(0)) moved = true;
    if (move(false, false, false, -1, AVOID_NONE)) moved = true;
    if (moved) Static.server.broadcastEntityMove(this);
    if (age > 3 * 20) {
      //check if player is overlapping me
      EntityBase e[] = Static.server.world.getEntities();
      for(int a=0;a<e.length;a++) {
        if (e[a].id != Entities.PLAYER) continue;
        if (e[a].hitBox(pos.x, pos.y, pos.z, width2, height2, depth2)) {
          //player gets this item
          Client c = Static.server.getClient((Player)e[a]);
          c.addItem(item, true);
          Chunk chunk = getChunk();
          chunk.delEntity(this);
          Static.server.world.delEntity(uid);
          Static.server.broadcastEntityDespawn(this);
          return;
        }
      }
    }
    super.tick();
  }
  public void ctick() {
    ang.y += 5.0f;
    if (ang.y > 180.0f) {
      ang.y = -180.0f;
    }
  }
  public boolean canSelect() {
    return false;
  }

  private static float nextFloat5() {
    return r.nextFloat() - 0.5f;
  }

  private static float nextFloat() {
    return 1.0f + r.nextFloat();
  }

  public static void create(Item item, int dim, float x, float y, float z, Chunk chunk, int dir) {
    WorldItem e = new WorldItem();
    e.setItem(item);
    e.init();
    e.dim = dim;
    e.uid = Static.world().generateUID();
    e.pos.x = x;
    e.pos.y = y;
    e.pos.z = z;
    switch (dir) {
      case -1:
      case A:
        e.vel.x = nextFloat5() / 5.0f;
        e.vel.y = nextFloat() / 5.0f;
        e.vel.z = nextFloat5() / 5.0f;
        break;
      case N:
        e.vel.x = nextFloat5() / 5.0f;
        e.vel.y = nextFloat() / 5.0f;
        e.vel.z = -nextFloat() / 5.0f;
        break;
      case E:
        e.vel.x = nextFloat() / 5.0f;
        e.vel.y = nextFloat() / 5.0f;
        e.vel.z = nextFloat5() / 5.0f;
        break;
      case S:
        e.vel.x = nextFloat5() / 5.0f;
        e.vel.y = nextFloat() / 5.0f;
        e.vel.z = nextFloat() / 5.0f;
        break;
      case W:
        e.vel.x = -nextFloat() / 5.0f;
        e.vel.y = nextFloat() / 5.0f;
        e.vel.z = nextFloat5() / 5.0f;
        break;
      case B:
        e.vel.x = nextFloat5() / 5.0f;
        e.vel.y = 0;
        e.vel.z = nextFloat5() / 5.0f;
        break;
    }
    e.age = 3 * 20;  //can pick up right away
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    item.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    item = new Item();
    item.read(buffer, file);
    return true;
  }
}
