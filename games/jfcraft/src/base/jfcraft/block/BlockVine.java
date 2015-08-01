package jfcraft.block;

/**
 *
 * @author pquiring
 */

import java.util.ArrayList;

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockVine extends BlockFace {
  private static GLModel model;
  public BlockVine(String id, String names[], String images[]) {
    super(id, names, images);
    isDirFace = true;
    isSupported = true;
  }

  public void rtick(Chunk chunk, int x,int y,int z) {
    int bits = chunk.getBits(x, y, z);
    int dir = Chunk.getDir(bits);
    if (dir == A || dir == B) return;
    if (y == 0) return;
    y--;
    synchronized(chunk) {
      if (chunk.isEmpty(x, y, z)) {
        chunk.setBlock(x, y, z, id, bits);
        x += chunk.cx * 16;
        z += chunk.cz * 16;
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, bits);
      }
    }
  }

  private static Coords supportingBlock = new Coords();

  public boolean checkSupported(Coords thisBlock) {
    //get supporting block
    supportingBlock.copy(thisBlock);
    supportingBlock.adjacentBlock();
    Static.server.world.getBlock(thisBlock.chunk.dim, supportingBlock.x, supportingBlock.y, supportingBlock.z, supportingBlock);
    if (supportingBlock.block.canSupportBlock(thisBlock)) return true;
    //check if vines above
    if (Static.server.world.getID(thisBlock.chunk.dim, thisBlock.x, thisBlock.y+1, thisBlock.z) == id) return true;
    return false;
  }

  public boolean place(Client client, Coords c) {
    if (c.y == 0 || c.y == 255) return false;
    if (c.dir == A || c.dir == B) return false;  //can only place on walls
    Coords s = c.clone();
    s.adjacentBlock();
    Static.server.world.getBlock(c.chunk.dim, s.x, s.y, s.z, s);
    if (!s.block.isSolid) {
      return false;
    }
    return super.place(client, c);
  }

  private static ArrayList<Box> empty = new ArrayList<Box>();

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    if (type == Type.ENTITY) return empty;
    return super.getBoxes(c, type);
  }
}
