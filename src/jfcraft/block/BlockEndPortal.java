package jfcraft.block;

/** End Portal
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class BlockEndPortal extends BlockBase {
  public BlockEndPortal(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isComplex = true;
    isSolid = false;
    emitLight = 15;
    dropBlock = "AIR";
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,4,16,Type.BOTH);
  }

  public void buildBuffers(RenderDest dest) {
    //it's an entity that can not be selected
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    //can not destroy
  }

  public void etick(EntityBase e, Coords c) {
    if (e.teleportTimer > 0) {
      e.teleportTimer = 20;
    } else {
      Static.server.teleport(e, c, 1);
    }
  }

  /** Find or create portal in current dim. */
  public void teleport(EntityBase e, Coords c) {
    if (e.dim == 0) {
      //just move to spawn point
      if (e instanceof Player) {
        //TODO : find current spawn point
      }
      //else use world spawn point
      Static.server.spawnPlayer(e);
    } else {
      //create platform in end world
      Coords p = new Coords();
      //TODO : randomize position
      p.setPos(96,64,96);
      p.chunk = Static.server.world.chunks.getChunk2(e.dim, p.cx, p.cz, true, true, true);
      for(int x=0;x<8;x++) {
        for(int z=0;z<8;z++) {
          p.chunk.setBlock(p.gx + x, 64, p.gz + z, Blocks.OBSIDIAN, 0);
          Static.server.broadcastSetBlock(1, p.x + x, 64, p.z + z, Blocks.OBSIDIAN, 0);  //multiplayer
        }
      }
      e.pos.y++;
      e.pos.x += 4;
      e.pos.z += 4;
    }
  }
}
