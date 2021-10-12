package jfcraft.block;

/** Domino
 *
 * @author pquiring
 */

import javaforce.gl.*;

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockDomino extends BlockBase {
  public static char DOMINO;
  public BlockDomino(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isComplex = true;
    isSolid = false;
    canReplace = false;
    isDir = true;  //full 360
    renderAsEntity = true;
    resetBoxes(Type.ENTITY);
  }
  public void getIDs(World world) {
    DOMINO = world.getBlockID("DOMINO");
    super.getIDs(world);
    entityID = Domino.DOMINO;
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    Coords c = new Coords();
    c.setPos(data.x + data.chunk.cx * 16, data.y, data.z + data.chunk.cz * 16);
    Domino domino = (Domino)data.chunk.findBlockEntity(entityID, c);
    if (domino == null) {
      Static.log("BlockDomino.buildBuffers():Can not find entity@" + c);
      return;
    }
    RenderData data2 = new RenderData();
    data2.crack = data.crack;
    data2.var[X] = data.var[X];
    domino.buildBuffers(domino.getDest(), data2);
    domino.needCopyBuffers = true;
  }
  public boolean place(Client client, Coords c) {
    Domino domino = new Domino();
    domino.init(Static.server.world);
    domino.dim = c.chunk.dim;
    domino.pos.x = ((float)c.x) + 0.5f;
    domino.pos.y = ((float)c.y) + 0.5f;
    domino.pos.z = ((float)c.z) + 0.5f;
    domino.gx = c.gx;
    domino.gy = c.gy;
    domino.gz = c.gz;
    domino.ang.y = c.ang.y;
    domino.uid = Static.server.world.generateUID();
    c.chunk.addEntity(domino);
    Static.server.world.addEntity(domino);
    Static.server.broadcastEntitySpawn(domino);
    return super.place(client, c);
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    //find and remove entity
    EntityBase e = c.chunk.findBlockEntity(entityID, c);
    if (e != null) {
      c.chunk.delEntity(e);
      Static.server.world.delEntity(e.uid);
      Static.server.broadcastEntityDespawn(e);
    }
  }

  public void etick(EntityBase e, Coords c) {
    Domino domino = (Domino)c.chunk.findBlockEntity(entityID, c);
    if (e == domino) {
      //block received etick from its own entity
      return;
    }
    if (domino == null) {
      Static.log("Domino.etick():Can not find entity");
      return;
    }
    if (domino.age < 1 * 20) {
      return;
    }

    //calc which way to fall
    Vector3 vD = new Vector3();
    vD.v[0] = 0;
    vD.v[1] = 0;
    vD.v[2] = 1;
    Matrix mat = new Matrix();
    mat.addRotate(domino.ang.y, 0, 1, 0);
    mat.mult(vD);

    Vector3 ve = new Vector3();
    ve.v[0] = e.pos.x - domino.pos.x;
    ve.v[1] = e.pos.y - domino.pos.y;
    ve.v[2] = e.pos.z - domino.pos.z;

    float dir;

    if (vD.dot(ve) > 0) dir = 1.0f; else dir = -1.0f;

    domino.fall(dir);
  }
}
