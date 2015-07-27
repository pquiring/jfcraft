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

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.opengl.*;

public class Player extends HumaniodBase {
  //inventory (first 9 are active items)
  public String name;

  public ExtraChest enderChest = new ExtraChest(0,0,0,3*9);

  public Client client;

  public int gainedLife, tookHungerDmg;

  public float walkAngle;  //angle of legs/arms as walking
  public float walkAngleDelta;

  //would like to move Render Assets to Entity, but it's static!!!
  //render assets
  private static RenderDest dest;
  //texture size
  private static Texture texture;

  public Player() {
    super(4*9, 4);
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
  }

  public void initStatic(GL gl) {
    super.initStatic(gl);  //HumanoidBase
    texture = Textures.getTexture(gl, "entity/steve", 0);
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

  public void buildBuffers(RenderDest dest, RenderData data) {
    super.buildBuffers(super.getDest(), data);  //HumanoidBase
    GLModel mod = loadModel("steve");
    //transfer data into dest
    for(int a=0;a<parts.length;a++) {
      RenderBuffers buf = dest.getBuffers(a);
      GLObject obj = mod.getObject(parts[a]);
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

  public RenderBuffers getRightHand() {
    return dest.getBuffers(R_ARM);
  }

  public void bindTexture(GL gl) {
    texture.bind(gl);
  }

  public void setMatrixModel(GL gl, int bodyPart, RenderBuffers buf) {
    mat.setIdentity();
    mat.addRotate(-ang.y, 0, 1, 0);
    switch (bodyPart) {
      case HEAD:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(-ang.x, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
        break;
      case BODY:
        break;
      case L_ARM:
        mat.addTranslate(0, buf.org.y, 0);
        mat.addRotate(walkAngle, 1, 0, 0);
        mat.addTranslate2(0, -buf.org.y, 0);
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
    mat.addTranslate(pos.x, pos.y, pos.z);
    if (scale != 1.0f) {
      mat.addTranslate2(buf.center.x, buf.center.y, buf.center.z);
      mat.addScale(scale, scale, scale);
      mat.addTranslate2(-buf.center.x, -buf.center.y, -buf.center.z);
    }
    gl.glUniformMatrix4fv(Static.uniformMatrixModel, 1, GL.GL_FALSE, mat.m);  //model matrix
  }

  public void render(GL gl) {
    for(int a=0;a<dest.count();a++) {
      RenderBuffers buf = dest.getBuffers(a);
      setMatrixModel(gl, a, buf);
      buf.bindBuffers(gl);
      buf.render(gl);
    }
    renderArmor(gl);
    renderItemInHand(gl);
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

  public void copyBuffers(GL gl) {
    super.copyBuffers(gl);  //HumaniodBase
    dest.copyBuffers(gl);
  }

  private static final byte ver = 0;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeByte(ver);
    if (file) {
      for(int a=0;a<9*3;a++) {
        enderChest.items[a].write(buffer, file);
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
    }
    byte nameLength = buffer.readByte();
    byte nameBytes[] = new byte[nameLength];
    buffer.readBytes(nameBytes);
    name = new String(nameBytes);
    return true;
  }
}
