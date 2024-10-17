package jfcraft.block;

/** Water, Lava, etc.
 *
 * dir = flowing direction
 * var = inverse depth (0=full ... 15=near empty)
 *
 * NOTE : if depth == full and dir == 0 then block is static
 * NOTE : if depth == full and dir == 1 then block is flowing
 *
 * @author pquiring
 *
 * Created : Jun 22, 2014
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockLiquid extends BlockAlpha {
  private static Face face = new Face();
  private float flowRate;  //1 for water, 3 for lava
  private boolean canRenew;  //true for water, false for lava
  public BlockLiquid(String id, String names[], String images[]) {
    super(id,names,images);
    canReplace = true;
    canSelect = false;
    canSpawnOn = false;
    isBlocks2 = true;
    isSolid = false;
    isLiquid = true;
    absorbLight = 1;
    resetBoxes(Type.BOTH);  //no boxes
  }
  private float var2depth(int var) {
    return (16 - var) / 16f;
  }
  private int depth2var(float depth) {
    return 16 - (int)(depth * 16f);
  }
  private boolean isStatic(Chunk chunk, int x, int y, int z) {
    if (x < 0) return isStatic(chunk.W ,x+16,y,z);
    if (x > 15) return isStatic(chunk.E,x-16,y,z);
    if (z < 0) return isStatic(chunk.N ,x,y,z+16);
    if (z > 15) return isStatic(chunk.S,x,y,z-16);
    if (chunk.getBlock2(x,y,z) != id) return false;
    return chunk.getBits2(x, y, z) == 0;  //dir == 0 && var == 0(full)
  }
  /** Returns depth of liquid */
  private float getDepth(Chunk chunk, int x, int y, int z) {
    if (x < 0) return getDepth(chunk.W ,x+16,y,z);
    if (x > 15) return getDepth(chunk.E,x-16,y,z);
    if (z < 0) return getDepth(chunk.N ,x,y,z+16);
    if (z > 15) return getDepth(chunk.S,x,y,z-16);
    char adj_id = chunk.getBlock2(x,y,z);
    if (adj_id != id) return 0f;
    return var2depth(Chunk.getVar(chunk.getBits2(x,y,z)));
  }
  private boolean canFill(Chunk chunk, int x, int y,int z) {
    BlockBase base1 = chunk.getBlockType1(x, y, z);
    char id2 = chunk.getBlock2(x, y, z);
    return base1.isComplex && id2 == 0;
  }
  private int getDir(Chunk chunk, int x,int y,int z) {
    if (x < 0) return getDir(chunk.W ,x+16,y,z);
    if (x > 15) return getDir(chunk.E,x-16,y,z);
    if (z < 0) return getDir(chunk.N ,x,y,z+16);
    if (z > 15) return getDir(chunk.S,x,y,z-16);
    char adj_id = chunk.getBlock2(x,y,z);
    if (adj_id != id) return 0;
    return Chunk.getDir(chunk.getBits(x, y, z));
  }
  private int getDesiredDir(Chunk chunk, int x,int y,int z) {
    float dn = getDepth(chunk,x,y,z-1);
    float de = getDepth(chunk,x+1,y,z);
    float ds = getDepth(chunk,x,y,z+1);
    float dw = getDepth(chunk,x-1,y,z);
    int dir = A;
    if (dn > ds) {
      dir = S;
      if (de > dw) {
        dir = SW;
      } else if (dw > de) {
        dir = SE;
      }
    } else if (ds > dn) {
      dir = N;
      if (de > dw) {
        dir = NW;
      } else if (dw > de) {
        dir = NE;
      }
    } else if (de > dw) {
      dir = W;
      if (dn > ds) {
        dir = SW;
      } else if (ds > dn) {
        dir = NW;
      }
    } else if (dw > de) {
      dir = E;
      if (dn > ds) {
        dir = SE;
      } else if (ds > dn) {
        dir = NE;
      }
    }
    return dir;
  }
  public void buildBuffers(RenderDest dest) {
    Static.data.doubleSided = true;
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st = textures[0];
    int x = (int)Static.data.x;
    int y = (int)Static.data.y;
    int z = (int)Static.data.z;
    Chunk chunk = Static.data.chunk;
    float depth = 1;
    if (chunk != null) {
      depth = getDepth(chunk,x,y,z);
    }
    int dir = Static.data.dir2[X];
    Static.data.isDir = false;
    Static.data.isDirXZ = false;
    Static.data.isGreen = isGreen;
    Static.data.isRed = isRed;
    Static.data.isBlue = isBlue;
    Static.data.crack = -1;
    if (depth == 1f) {
      for(int a=0;a<6;a++) {
        if (Static.data.opaque[a]) continue;
        if (Static.data.id2[a] == id) continue;
        Static.data.side = a;
        Static.data.dirSide = a;
        Static.data.isDir = isDir;
        Static.data.isDirXZ = isDirXZ;
        if (Static.data.id[a] == Blocks.ICEBLOCK) continue;
        addFace(buf,st);
      }
    } else {
      float y1,y2,y3,y4;
/*
View from above:
1n2
wxe
4s3
*/
      //calc depth of four corners
      float nw = getDepth(chunk, x-1,y,z-1);
      float n  = getDepth(chunk, x  ,y,z-1);
      float ne = getDepth(chunk, x+1,y,z-1);
      float w  = getDepth(chunk, x-1,y,z);
      //float depth = getDepth(chunk, ix,iy,iz);
      float e  = getDepth(chunk, x+1,y,z);
      float sw = getDepth(chunk, x-1,y,z+1);
      float s  = getDepth(chunk, x  ,y,z+1);
      float se = getDepth(chunk, x+1,y,z+1);
      y1 = depth;
      if (nw > y1) y1 = nw;
      if (n > y1) y1 = n;
      if (w > y1) y1 = w;
      y2 = depth;
      if (ne > y2) y2 = ne;
      if (n > y2) y2 = n;
      if (e > y2) y2 = e;
      y3 = depth;
      if (se > y3) y3 = se;
      if (s > y3) y3 = s;
      if (e > y3) y3 = e;
      y4 = depth;
      if (sw > y4) y4 = sw;
      if (s > y4) y4 = s;
      if (w > y4) y4 = w;
      //top
      face.x[0] = 0; face.y[0] = y1; face.z[0] = 0;
      face.x[1] = 1; face.y[1] = y2; face.z[1] = 0;
      face.x[2] = 1; face.y[2] = y3; face.z[2] = 1;
      face.x[3] = 0; face.y[3] = y4; face.z[3] = 1;
      if (dir != A) {
        st = textures[1];  //flowing
      }
      switch (dir) {
        default:
          Static.log("Liquid with invalid dir:" + dir + "@" + Static.data.x + "," + Static.data.y + "," + Static.data.z);
          //no break
        case A:
          face.u1[0] = st.x1; face.v1[0] = st.y1;
          face.u1[1] = st.x2; face.v1[1] = st.y1;
          face.u1[2] = st.x2; face.v1[2] = st.y2;
          face.u1[3] = st.x1; face.v1[3] = st.y2;
          break;
        case NW:
          face.u1[0] = st.fx3; face.v1[0] = st.fy3;
          face.u1[1] = st.fx2; face.v1[1] = st.fy2;
          face.u1[2] = st.fx1; face.v1[2] = st.fy1;
          face.u1[3] = st.fx4; face.v1[3] = st.fy4;
          break;
        case N:
          face.u1[0] = st.x1; face.v1[0] = st.y2;
          face.u1[1] = st.x2; face.v1[1] = st.y2;
          face.u1[2] = st.x2; face.v1[2] = st.y1;
          face.u1[3] = st.x1; face.v1[3] = st.y1;
          break;
        case NE:
          face.u1[0] = st.fx4; face.v1[0] = st.fy4;
          face.u1[1] = st.fx3; face.v1[1] = st.fy3;
          face.u1[2] = st.fx2; face.v1[2] = st.fy2;
          face.u1[3] = st.fx1; face.v1[3] = st.fy1;
          break;
        case E:
          face.u1[0] = st.x2; face.v1[0] = st.y1;
          face.u1[1] = st.x2; face.v1[1] = st.y2;
          face.u1[2] = st.x1; face.v1[2] = st.y2;
          face.u1[3] = st.x1; face.v1[3] = st.y1;
          break;
        case SE:
          face.u1[0] = st.fx1; face.v1[0] = st.fy1;
          face.u1[1] = st.fx2; face.v1[1] = st.fy2;
          face.u1[2] = st.fx3; face.v1[2] = st.fy3;
          face.u1[3] = st.fx4; face.v1[3] = st.fy4;
          break;
        case S:
          face.u1[0] = st.x1; face.v1[0] = st.y1;
          face.u1[1] = st.x2; face.v1[1] = st.y1;
          face.u1[2] = st.x2; face.v1[2] = st.y2;
          face.u1[3] = st.x1; face.v1[3] = st.y2;
          break;
        case SW:
          face.u1[0] = st.fx2; face.v1[0] = st.fy2;
          face.u1[1] = st.fx1; face.v1[1] = st.fy1;
          face.u1[2] = st.fx4; face.v1[2] = st.fy4;
          face.u1[3] = st.fx3; face.v1[3] = st.fy3;
          break;
        case W:
          face.u1[0] = st.x1; face.v1[0] = st.y2;
          face.u1[1] = st.x1; face.v1[1] = st.y1;
          face.u1[2] = st.x2; face.v1[2] = st.y1;
          face.u1[3] = st.x2; face.v1[3] = st.y2;
          break;
      }
      Static.data.side = A;
      Static.data.dirSide = A;
      buf.addFace(face);
      st = textures[0];
      face.u1[0] = st.x1; face.v1[0] = st.y1;
      face.u1[1] = st.x2; face.v1[1] = st.y1;
      face.u1[2] = st.x2; face.v1[2] = st.y2;
      face.u1[3] = st.x1; face.v1[3] = st.y2;
      //bottom
      if ((!Static.data.opaque[B]) && (Static.data.id2[B] != id)) {
        Static.data.side = B;
        Static.data.dirSide = B;
        Static.data.isDir = isDir;
        Static.data.isDirXZ = isDirXZ;
        addFace(buf,st);
      }
      st = textures[1];  //sides are always flowing down (should never see it though)
      face.u1[0] = st.x1; face.v1[0] = st.y1;
      face.u1[1] = st.x2; face.v1[1] = st.y1;
      face.u1[2] = st.x2; face.v1[2] = st.y2;
      face.u1[3] = st.x1; face.v1[3] = st.y2;
      //n
      if ((!Static.data.opaque[N]) && (Static.data.id2[N] != id)) {
        face.x[0] = 1; face.y[0] = y2; face.z[0] = 0;
        face.x[1] = 0; face.y[1] = y1; face.z[1] = 0;
        face.x[2] = 0; face.y[2] = 0; face.z[2] = 0;
        face.x[3] = 1; face.y[3] = 0; face.z[3] = 0;

        face.v1[0] = st.y1 + ((1f - y2) * st.height);
        face.v1[1] = st.y1 + ((1f - y1) * st.height);

        Static.data.side = N;
        Static.data.dirSide = N;
        buf.addFace(face);
      }
      //e
      if ((!Static.data.opaque[E]) && (Static.data.id2[E] != id)) {
        face.x[0] = 1; face.y[0] = y3; face.z[0] = 1;
        face.x[1] = 1; face.y[1] = y2; face.z[1] = 0;
        face.x[2] = 1; face.y[2] = 0; face.z[2] = 0;
        face.x[3] = 1; face.y[3] = 0; face.z[3] = 1;

        face.v1[0] = st.y1 + ((1f - y3) * st.height);
        face.v1[1] = st.y1 + ((1f - y2) * st.height);

        Static.data.side = E;
        Static.data.dirSide = E;
        buf.addFace(face);
      }
      //s
      if ((!Static.data.opaque[S]) && (Static.data.id2[S] != id)) {
        face.x[0] = 0; face.y[0] = y4; face.z[0] = 1;
        face.x[1] = 1; face.y[1] = y3; face.z[1] = 1;
        face.x[2] = 1; face.y[2] = 0; face.z[2] = 1;
        face.x[3] = 0; face.y[3] = 0; face.z[3] = 1;

        face.v1[0] = st.y1 + ((1f - y4) * st.height);
        face.v1[1] = st.y1 + ((1f - y3) * st.height);

        Static.data.side = S;
        Static.data.dirSide = S;
        buf.addFace(face);
      }
      //w
      if ((!Static.data.opaque[W]) && (Static.data.id2[W] != id)) {
        face.x[0] = 0; face.y[0] = y1; face.z[0] = 0;
        face.x[1] = 0; face.y[1] = y4; face.z[1] = 1;
        face.x[2] = 0; face.y[2] = 0; face.z[2] = 1;
        face.x[3] = 0; face.y[3] = 0; face.z[3] = 0;

        face.v1[0] = st.y1 + ((1f - y1) * st.height);
        face.v1[1] = st.y1 + ((1f - y4) * st.height);

        Static.data.side = W;
        Static.data.dirSide = W;
        buf.addFace(face);
      }
    }
    Static.data.doubleSided = false;
  }
  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    //move water
    tick.t1++;
    if (tick.t1 < 5) return;
    tick.t1 = 0;
    tick.toWorldCoords(chunk, c);
    //get depth
    int x = tick.x;
    int y = tick.y;
    int z = tick.z;
    float depth = getDepth(chunk,x,y,z);
    int bits = chunk.getBits2(x,y,z);
    int dir = Chunk.getDir(bits);
    float da = 0;
    if (y < 255) {
      da = getDepth(chunk, x,y+1,z);
    }
    float dn = getDepth(chunk, x,y,z-1);
    float de = getDepth(chunk, x+1,y,z);
    float ds = getDepth(chunk, x,y,z+1);
    float dw = getDepth(chunk, x-1,y,z);
    float db;
    if (y > 0)
      db = getDepth(chunk, x,y-1,z);
    else
      db = 0f;
    if (depth < 1f || (depth == 1f && dir != 0)) {
      //check if source is gone/lower
      float newDepth = 0;
      if (da > newDepth) {newDepth = 1f; dir = A;}
      if (dn - Static._1_16 * flowRate > newDepth) newDepth = dn - Static._1_16 * flowRate;
      if (de - Static._1_16 * flowRate > newDepth) newDepth = de - Static._1_16 * flowRate;
      if (ds - Static._1_16 * flowRate > newDepth) newDepth = ds - Static._1_16 * flowRate;
      if (dw - Static._1_16 * flowRate > newDepth) newDepth = dw - Static._1_16 * flowRate;
      int cnt = 0;
      if (dn == 1f && isStatic(chunk,x,y,z-1)) cnt++;
      if (de == 1f && isStatic(chunk,x+1,y,z)) cnt++;
      if (ds == 1f && isStatic(chunk,x,y,z+1)) cnt++;
      if (dw == 1f && isStatic(chunk,x-1,y,z)) cnt++;
      //check if flowing -> static (requires 2 static around it)
      if (canRenew && cnt > 1) {
        //convert this block to static liquid
        chunk.setBits2(x, y, z, 0);
        Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z, id, 0);
        depth = 1f;
      } else if (newDepth == 0) {
        //gone!
        chunk.clearBlock2(x, y, z);
        Static.server.broadcastClearBlock2(chunk.dim, c.x, c.y, c.z);
        chunk.delTick(tick);
        return;
      } else if (newDepth != depth) {
        depth = newDepth;
        int xbits = Chunk.makeBits(dir, depth2var(newDepth));
        chunk.setBits2(x, y, z, xbits);
        Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z, id, xbits);
      }
    }
    //push liquid to adj blocks
    depth -= Static._1_16 * flowRate;  //set adj blocks to this depth
    if (depth <= 0.001f) {  //depth == 0f (rounding error?)
      chunk.delTick(tick);
      return;
    }
    if (y > 0) {
      if (Static.blocks.blocks[chunk.getBlock(x, y-1, z)].isSolid) {
        if (dn < depth && canFill(chunk,x,y,z-1)) {
          int xbits = Chunk.makeBits(getDesiredDir(chunk, x, y, z-1), depth2var(depth));
          chunk.setBlock(x, y, z-1, id, xbits);
          Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z-1, id, xbits);
        }
        if (de < depth && canFill(chunk,x+1,y,z)) {
          int xbits = Chunk.makeBits(getDesiredDir(chunk, x+1, y, z), depth2var(depth));
          chunk.setBlock(x+1, y, z, id, xbits);
          Static.server.broadcastSetBlock(chunk.dim, c.x+1, c.y, c.z, id, xbits);
        }
        if (ds < depth && canFill(chunk,x,y,z+1)) {
          int xbits = Chunk.makeBits(getDesiredDir(chunk, x, y, z+1), depth2var(depth));
          chunk.setBlock(x, y, z+1, id, xbits);
          Static.server.broadcastSetBlock(chunk.dim, c.x, c.y, c.z+1, id, xbits);
        }
        if (dw < depth && canFill(chunk,x-1,y,z)) {
          int xbits = Chunk.makeBits(getDesiredDir(chunk, x-1, y, z), depth2var(depth));
          chunk.setBlock(x-1, y, z, id, xbits);
          Static.server.broadcastSetBlock(chunk.dim, c.x-1, c.y, c.z, id, xbits);
        }
      }
      if (db < 1f && canFill(chunk,x,y-1,z)) {
        int xbits = Chunk.makeBits(A, depth2var(1f));  //dir=A means full flowing (not static)
        chunk.setBlock(x, y-1, z, id, xbits);
        Static.server.broadcastSetBlock(chunk.dim, c.x, c.y-1, c.z, id, xbits);
      }
    }
    chunk.delTick(tick);
  }

  public BlockLiquid setFlowRate(float rate) {
    flowRate = rate;
    return this;
  }

  public BlockLiquid setRenews(boolean state) {
    canRenew = state;
    return this;
  }

  public SubTexture getDestroyTexture(int var) {
    return null;
  }
}
