package jfcraft.block;

/** Step blocks
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.ArrayList;

import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockStep extends BlockBase {
  public BlockStep(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
//    hasShape = true;  //TODO
    isComplex = true;
    isSolid = false;
    dropBlock = "AIR";
    canPlace = false;  //too complex
  }

  private static boolean q[] = new boolean[8];

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    /*
      -z
     0|1    y+
    --|--x
     2|3    y-

     4|5    y+
    -----x
     6|7    y-
      +z
    */
    int bits = data.bits;
    q[0] = (bits & QUNW) != 0;
    q[1] = (bits & QUNE) != 0;
    q[2] = (bits & QLNW) != 0;
    q[3] = (bits & QLNE) != 0;
    q[4] = (bits & QUSW) != 0;
    q[5] = (bits & QUSE) != 0;
    q[6] = (bits & QLSW) != 0;
    q[7] = (bits & QLSE) != 0;

    SubTexture st = getTexture(data);
    for(int a=0;a<8;a++) {
      if (q[a]) {
        addQuad(buf, data, a, st);
      }
    }
  }

  public void setShape(Chunk chunk, int gx, int gy, int gz, boolean live, Coords c) {
    int bits = chunk.getBits(gx,gy,gz);
    int old = bits;
    int x = gx + chunk.cx * 16;
    int y = gy;
    int z = gz + chunk.cz * 16;
    World world = Static.server.world;
    //TODO!!!
    if (old != bits) {
      Static.log("setShape:old=" + old + ",new=" + bits);
      chunk.setBits(gx,gy,gz, bits);
      if (live) {
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, bits);
      }
    }
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();

    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    q[0] = (bits & QUNW) != 0;
    q[1] = (bits & QUNE) != 0;
    q[2] = (bits & QLNW) != 0;
    q[3] = (bits & QLNE) != 0;
    q[4] = (bits & QUSW) != 0;
    q[5] = (bits & QUSE) != 0;
    q[6] = (bits & QLSW) != 0;
    q[7] = (bits & QLSE) != 0;

    if (q[0]) list.add(new Box( 0, 8, 0,  8,16, 8));
    if (q[1]) list.add(new Box( 8, 8, 0, 16,16, 8));
    if (q[2]) list.add(new Box( 0, 0, 0,  8, 8, 8));
    if (q[3]) list.add(new Box( 8, 0, 0, 16, 8, 8));
    if (q[4]) list.add(new Box( 0, 8, 8,  8,16,16));
    if (q[5]) list.add(new Box( 8, 8, 8, 16,16,16));
    if (q[6]) list.add(new Box( 0, 0, 8,  8, 8,16));
    if (q[7]) list.add(new Box( 8, 0, 8, 16, 8,16));

    return list;
  }
}
