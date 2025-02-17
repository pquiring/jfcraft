package jfcraft.entity;

/** Player state
 *
 * NOTE : The player is removed from the Chunk on the client side and only exists
 *        in the world entities list.
 *
 * @author pquiring
 *
 * Created : Mar 24, 2014
 */

import java.util.*;

import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.extra.*;
import jfcraft.opengl.*;

public class Player extends HumaniodBase {
  //inventory (first 9 are active items)
  public String name;

  public ExtraChest enderChest = new ExtraChest(0,0,0,3*9);

  public Client client;

  public int gainedLife, tookHungerDmg;

  public float walkAngle;  //angle of legs/arms as walking
  public float walkAngleDelta;

  public HashMap<String, Event> events = new HashMap<>();

  //would like to move Render Assets to Entity, but it's static!!!
  //render assets
  private static RenderDest dest;
  //texture size
  private static TextureMap texture;
  private static Model model;

  public Player() {
    super(4*9 + 1, 4);  //+1 for shield
    id = Entities.PLAYER;
  }

  public RenderDest getDest() {
    return dest;
  }

  public String getName() {
    return "player";
  }

  public void init(World world) {
    super.init(world);
    isStatic = true;
    width = 0.6f;
    width2 = width/2;
    height = 1.6f;
    height2 = height/2;
    depth = width;
    depth2 = width2;
    eyeHeight = 1.3f;
    jumpVelocity = 0.58f;  //results in jump of 1.42
    //speeds are blocks per second
    walkSpeed = 4.3f;
    runSpeed = 5.6f;
    sneakSpeed = 1.3f;
    swimSpeed = (walkSpeed / 2.0f);
    reach = 5.0f;
    walkAngleDelta = 5.0f;
    yDrag = Static.dragSpeed;
    xzDrag = yDrag * 4.0f;
    attackDmg = 1.0f;  //base damage (fists)
    attackRange = 5.0f;
    legLength = 0.625f;
    if (Static.debugTest) {
      runSpeed = 25.0f;
      fastSwimSpeed = 25.0f;
    }
  }

  public void initStatic() {
    model = loadModel("steve");
  }

  public void initStaticGL() {
    super.initStaticGL();  //HumanoidBase
    texture = Textures.getTexture("entity/player/slim/steve", 0);
    dest = new RenderDest(parts.length);
  }

  public void tick() {
    if (health > 0 && health < 20 && gainedLife == 0 && hunger >= 18) {
      exhaustion += 3.0f;
      health++;
      gainedLife = 4 * 20;
      client.serverTransport.sendHealth(this);
    } else if (gainedLife > 0) {
      gainedLife--;
    }
    if (exhaustion >= 4) {
      exhaustion -= 4;
      if (saturation > 0) {
        if (saturation < 1)
          saturation = 0;
        else
          saturation -= 1;
      } else if (hunger > 0) {
        if (hunger < 1)
          hunger = 0;
        else
          hunger -= 1;
        client.serverTransport.sendHunger(this);
      }
    }
    if (hunger == 0 && tookHungerDmg == 0) {
      takeDmg(1, null);
      tookHungerDmg = 4 * 20;
    } else if (tookHungerDmg > 0) {
      tookHungerDmg--;
    }
    super.tick();
  }

  private static String parts[] = {"HEAD", "BODY", "L_ARM", "R_ARM", "L_LEG", "R_LEG"};

  public void buildBuffers(RenderDest dest) {
    super.buildBuffers(super.getDest());  //HumanoidBase
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      Object3 obj = model.getObject(parts[a]);
      if (obj == null) {
        System.out.println("Warning:Couldn't find part:" + parts[a]);
      }
      buf.addVertex(obj.vpl.toArray());
      buf.addPoly(obj.vil.toArray());
      int cnt = obj.vpl.size();
      for(int b=0;b<cnt;b++) {
        buf.addDefault();
      }
      if (obj.maps.size() == 1) {
        UVMap map = obj.maps.get(0);
        buf.addTextureCoords(map.uvl.toArray());
      } else {
        UVMap map1 = obj.maps.get(0);
        UVMap map2 = obj.maps.get(1);
        buf.addTextureCoords(map1.uvl.toArray(), map2.uvl.toArray());
      }
      buf.org = obj.org;
      buf.type = obj.type;
    }
  }

  public RenderBuffers getRightHand() {
    return dest.getBuffers(R_ARM);
  }

  public void bindTexture() {
    texture.bind();
  }

  public void setMatrixModel(int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);  //rotation to face direction
    scale = Static.data.scale;
    boolean is_item = false;
    switch (bodyPart) {
      case HEAD:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(-ang.x, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case BODY:
        break;
      case L_ITEM:
        //shield
        is_item = true;
        break;
      case L_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case R_ITEM:
        is_item = true;
        if (Static.data.isRenderAsEntity) {
          mat.addScale(scale, scale, scale);
        } else if (Static.data.isBlock) {
          //blocks are centered on 0.5,0.5,0.5 but need to be scaled
          mat.addTranslate(-0.5f, -0.5f, -0.5f);
          mat.addScale(scale, scale, scale);
          mat.addTranslate(0.5f, 0.5f, 0.5f);
        } else {
          //item (voxel)
          mat.addRotate(90, 0, 1, 0);
          mat.addScale(scale, scale, scale);
        }
        break;
      case R_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(-walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case L_LEG:
        if (vehicle == null) {
          mat.addTranslate(0, buf.org.y, 0);
          mat.addRotate(-walkAngle, 1, 0, 0);
          mat.addTranslate2(0, -buf.org.y, 0);
        } else {
          mat.addTranslate(0, buf.org.y, 0);
          mat.addRotate(90, 1, 0, 0);
          mat.addTranslate2(0, -buf.org.y, 0);
        }
        break;
      case R_LEG:
        if (vehicle == null) {
          mat.addTranslate(0, buf.org.y, 0);
          mat.addRotate(walkAngle, 1, 0, 0);
          mat.addTranslate2(0, -buf.org.y, 0);
        } else {
          mat.addTranslate(0, buf.org.y, 0);
          mat.addRotate(90, 1, 0, 0);
          mat.addTranslate2(0, -buf.org.y, 0);
        }
        break;
    }
    switch (bodyPart) {
      case L_ITEM: {
        //shield
        if (Static.data.isPlayerView) {
          mat.addTranslate(0, eyeHeight, 0);  //move center to hips and rotate up/down with camera view
          mat.addRotate2(-ang.x, 1, 0, 0);
          mat.addTranslate2(0, -eyeHeight, 0);  //move back
          //TODO : keep shield on side of screen : this may need tweaking for screen aspect ratio
          mat.addTranslate2(-Static._1_16 * 1, 0, 0);
        }
        break;
      }
      case R_ITEM: {
        if (Static.data.isPlayerView) {
          mat.addTranslate(0, eyeHeight, 0);  //move center to eyeLevel and rotate up/down with camera view
          if (Static.data.isBlock || Static.data.isRenderAsEntity) {
            mat.addRotate2(-ang.x, 1, 0, 0);  //block / entity
          } else {
            mat.addRotate2(-ang.x + 25, 0, 0, 1);  //item (voxel) : on z axis because of 90 deg rotation
          }
          mat.addTranslate2(0, -eyeHeight, 0);  //move back
          //TODO : keep item on side of screen : this may need tweaking for screen aspect ratio
          float tx = 0;
          float ty = 0;
          float tz = 0;
          if (Static.data.isRenderAsEntity) {
            //adjust tx, ty, tz
            tx = Static._1_16 * 12;
            ty = -Static._1_16 * 6;
            tz = 0;
          } else if (Static.data.isRenderAsItem) {
            //adjust tx, ty, tz
            tx = Static._1_16 * 1;
            ty = -Static._1_16 * 4;
            tz = 0;
          } else if (Static.data.isBlock) {
            //adjust tx, ty, tz
            tx = Static._1_16 * 12;
            ty = -Static._1_16 * 6;
            tz = 0;
          } else {
            //adjust tx, ty, tz
            tx = Static._1_16 * 1;
            ty = -Static._1_16 * 16;
            tz = 0;
          }
          if (Static.data.isRenderAsEntity || Static.data.isBlock || Static.data.isRenderAsItem) {
            mat.addTranslate2(tx, ty, tz);
          } else {
            mat.addTranslate2(tz, ty, tx);
          }
        }
        if (Static.data.isRenderAsEntity) {
          //entity (+0.5f)
          float tx = Static._1_16 * 4 + 0.5f;
          float ty = Static._1_16 * 16 + 0.5f;
          float tz = Static._1_16 * -20 + 0.5f;
          mat.addTranslate2(tx, ty, tz);
        } else if (Static.data.isBlock) {
          //block
          float tx = Static._1_16 * 4;
          float ty = Static._1_16 * 16;
          float tz = Static._1_16 * -20;
          mat.addTranslate2(tx, ty, tz);
        } else {
          //item (voxel) [swap x,z because of 90 deg rotation]
          float tx = Static._1_16 * 4;
          float ty = Static._1_16 * (16+8);
          float tz = Static._1_16 * 2;
          mat.addTranslate2(tz, ty, tx);
        }
        break;
      }
    }
    mat.addTranslate(pos.x, pos.y, pos.z);
    if (!is_item && scale != 1.0f && buf.center != null) {
      mat.addTranslate2(buf.center.x, buf.center.y, buf.center.z);
      mat.addScale(scale, scale, scale);
      mat.addTranslate2(-buf.center.x, -buf.center.y, -buf.center.z);
    }
    glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL_FALSE, mat.m);  //model matrix
  }

  public float getBuoyant() {
    return 0f;
  }

  public void ctick() {
    float delta = 0;
    switch (mode) {
      case MODE_IDLE:
        walkAngle = 0.0f;
        return;
      case MODE_RUN:
        delta = walkAngleDelta * 2f;
        break;
      case MODE_SWIM:
      case MODE_WALK:
        delta = walkAngleDelta;
        break;
      case MODE_SNEAK:
        delta = walkAngleDelta / 2f;
        break;
    }
    walkAngle += delta;
    if ((walkAngle < -45.0) || (walkAngle > 45.0)) {
      walkAngleDelta *= -1;
    }
  }

  public void copyBuffers() {
    super.copyBuffers();  //HumaniodBase
    dest.copyBuffers();
  }

  public void adjustSpawnPosition() {
    Chunk chunk = getChunk();
    if (chunk == null) return;
    //TODO : move to safe location
    pos.y = chunk.elev[8 * 16 + 8];
  }

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

  public String getEvent(String key) {
    Event event = events.get(key);
    if (event == null) return "";
    return event.value;
  }

  public void setEvent(String key, String value) {
    events.put(key, new Event(key, value));
  }

  private static final byte ver = 1;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    if (file) {
      for(int a=0;a<9*3;a++) {
        enderChest.items[a].write(buffer, file);
      }
      if (ver >= 1) {
        Event[] ea = events.values().toArray(Event.array);
        buffer.writeInt(ea.length);
        for(Event e : ea) {
          e.write(buffer, file);
        }
      }
    }
    byte nameBytes[] = name.getBytes();
    byte nameLength = (byte)nameBytes.length;
    buffer.writeByte(nameLength);
    buffer.writeBytes(nameBytes);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    byte ver = buffer.readByte();
    if (file) {
      for(int a=0;a<9*3;a++) {
        enderChest.items[a].read(buffer, file);
      }
      events.clear();
      if (ver >= 1) {
        int cnt = buffer.readInt();
        for(int a=0;a<cnt;a++) {
          Event event = new Event();
          event.read(buffer, file);
          events.put(event.key, event);
        }
      }
    }
    byte nameLength = buffer.readByte();
    byte nameBytes[] = new byte[nameLength];
    buffer.readBytes(nameBytes);
    name = new String(nameBytes);
    return true;
  }
}
