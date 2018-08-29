package jfcraft.entity;

/** Arrow entity
 *
 * @author pquiring
 */

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;

public class Arrow extends EntityBase {
  public CreatureBase owner;
  public boolean armed;

  //render assets
  public static RenderDest dest;
  public static Texture texture;
  protected static String textureName;
  private static GLModel model;

  public Arrow() {
    id = Entities.ARROW;
  }

  public Arrow setOwner(CreatureBase owner) {
    this.owner = owner;
    return this;
  }

  public String getName() {
    return "arrow";
  }

  public RenderDest getDest() {
    return dest;
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.5f;
    width2 = width/2f;
    height = 0.5f;
    height2 = height/2f;
    depth = 0.5f;
    depth2 = depth/2f;
    maxAge = 1 * 60 * 20;  //1 min
    armed = true;
    yDrag = Static.dragSpeed;
    xzDrag = yDrag * 4.0f;
  }

  public void initStatic() {
    super.initStatic();
    dest = new RenderDest(parts.length);
    textureName = "entity/arrow";
    model = loadModel("arrow");
  }

  public void initStaticGL() {
    super.initStaticGL();
    texture = Textures.getTexture(textureName, 0);
  }

  public void initInstance() {
    super.initInstance();
  }

  private static String parts[] = {"ARROW"};

  public void buildBuffers(RenderDest dest, RenderData data) {
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
        GLUVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        GLUVMap map1 = obj.maps.get(0);
        GLUVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
  }

  public void bindTexture() {
    texture.bind();
  }

  public void copyBuffers() {
    dest.copyBuffers();
  }

  private void setMatrixModel() {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    mat.addRotate(-ang.x, 1, 0, 0);
    mat.addTranslate(pos.x, pos.y, pos.z);
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public void render() {
    setMatrixModel();
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      buf.bindBuffers();
      buf.render();
    }
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, Static.identity.m);  //model matrix
  }

  public void tick() {
    super.tick();
//    Static.log("arrow tick:" + x + "," + y + "," + z + ":" + xVelocity + "," + yVelocity + "," + zVelocity);
    EntityBase list[] = Static.server.world.getEntities();
    for(int a=0;a<list.length;a++) {
      EntityBase e = list[a];
      if (e.hitBox(pos.x, pos.y + height2, pos.z, width2, height2, depth2)) {
        if (e instanceof CreatureBase) {
          CreatureBase cb = (CreatureBase)e;
          boolean deflected = false;
          if (cb.blocking) {
            //check if shield is facing arrow (45 deg cover)
            float aa = ang.y;
            float ca = cb.ang.y;
            ca += 180.0f;
            if (ca > 360.0f) ca -= 360.0f;
            if (ca < 180f && aa > 180.0f) aa -= 360.0f;  //move aa to -180 to 180 range
            float min = ca - 80f;
            float max = ca + 80f;
            if (aa > min && aa < max) {
              //arrow deflected by shield
              ang.y += 180.0f;
              if (ang.y > 360.0f) ang.y -= 360.0f;
              vel.x *= -1f;
              vel.z *= -1f;
              move(false, true, false, -1, AVOID_NONE);  //move once backwards to avoid hitting target again
              vel.x *= 0.2f;
              vel.z *= 0.2f;
              deflected = true;
              System.out.println("deflected");
            }
          }
          if (!deflected) {
            System.out.println("!deflected");
            if (!armed) {
              //give to entity
              if (e instanceof Player) {
                Player p = (Player)e;
                if (!p.client.addItem(new Item(Items.ARROW), true)) {
                  return;  //can not give so leave it there
                }
              }
            } else {
              float dmg = (float)Math.sqrt(vel.x * vel.x + vel.y * vel.y + vel.z * vel.z) * 2f;
              cb.takeDmg(dmg, owner);
            }
            Chunk c = getChunk();
            c.delEntity(this);
            Static.server.world.delEntity(uid);
            Static.server.broadcastEntityDespawn(this);
          }
        }
      }
    }
    if (inBlock(0,0,0,false,-1, AVOID_NONE) == -1) {
      armed = false;
      vel.x = 0;
      vel.y = 0;
      vel.z = 0;
//      Static.log("Arrow stuck on block");
      return;  //stuck on block
    }
    //TODO : adjust xAngle based on gravity to create ballistic arc
    boolean moved = move(false, true, false, -1, AVOID_NONE);
    if (moved) {
      Static.server.broadcastEntityMove(this, false);
    }
  }

  public boolean canSelect() {
    return false;
  }
}
