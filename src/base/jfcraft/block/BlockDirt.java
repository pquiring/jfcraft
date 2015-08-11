package jfcraft.block;

/** Block dirt
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;
import static jfcraft.data.Types.*;

public class BlockDirt extends BlockOpaque {
  //var are for dirt only
  public final static int VAR_DIRT = 0;
  public final static int VAR_PODZOL = 1;
  public final static int VAR_FARM_DRY = 2;
  public final static int VAR_FARM_WET = 3;
  public BlockDirt(String id, String names[], String images[]) {
    super(id, names, images);
  }
  public boolean useTool(Client client, Coords c) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    if (var == VAR_FARM_DRY) return super.useTool(client, c);
    if (var == VAR_FARM_WET) return super.useTool(client, c);
    if (var == VAR_PODZOL) return super.useTool(client, c);
    synchronized(client.lock) {
      char toolid = client.player.items[client.player.activeSlot].id;
      ItemBase item = Static.items.items[toolid];
      if (item.isTool && item.tool == TOOL_HOE) {
        //change to farm soil
        char newid = id;
        if (id == Blocks.GRASS) newid = Blocks.DIRT;  //change grass to dirt
        c.chunk.setBlock(c.gx,c.gy,c.gz,newid,Chunk.makeBits(0,VAR_FARM_DRY));
        Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,newid,Chunk.makeBits(0,VAR_FARM_DRY));
        c.chunk.addTick(c, false);
        return true;
      }
    }
    return super.useTool(client, c);
  }
  public Item[] drop(Coords c, int var) {
    switch (var) {
      case VAR_DIRT: var = VAR_DIRT; break;
      case VAR_PODZOL: var = VAR_PODZOL; break;
      case VAR_FARM_DRY: var = VAR_DIRT; break;
      case VAR_FARM_WET: var = VAR_DIRT; break;
    }
    return new Item[] {new Item(dropID, var)};
  }
  public void rtick(Chunk chunk, int gx,int gy,int gz) {
    int x = chunk.cx * 16 + gx;
    int y = gy;
    int z = chunk.cz * 16 + gz;
    int var = Chunk.getVar(chunk.getBits(gx, gy, gz));
    switch (var) {
      case VAR_FARM_DRY: {
        //check for nearby water and convert to wet farmland
        for(int dx=-4;dx<=4;dx++) {
          for(int dy=-1;dy<=1;dy++) {
            for(int dz=-4;dz<=4;dz++) {
              if (chunk.getID2(dx + gx, dy + gy, dz + gz) == Blocks.WATER) {
                chunk.setBlock(gx,gy,gz,id,Chunk.makeBits(0,VAR_FARM_WET));
                Static.server.broadcastSetBlock(chunk.dim,x,y,z,id,Chunk.makeBits(0,VAR_FARM_WET));
                return;
              }
            }
          }
        }
        break;
      }
      case VAR_DIRT: {
        //grow grass
        if (chunk.getID(gx, gy+1, gz) != 0) {
          if (chunk.getBlock(gx, gy+1, gz).isSolid) return;
        }
        if (chunk.getID2(gx, gy+1, gz) != 0) return;
        if (chunk.getSunLight(gx, gy+1, gz) == 0) return;
        for(int dx=-1;dx<=1;dx++) {
          for(int dy=-1;dy<=1;dy++) {
            for(int dz=-1;dz<=1;dz++) {
              if (chunk.getID(dx + gx, dy + gy, dz + gz) == Blocks.GRASS) {
                chunk.setBlock(gx, gy, gz, Blocks.GRASS, 0);
                Static.server.broadcastSetBlock(chunk.dim, x, y, z, Blocks.GRASS, 0);
                return;
              }
            }
          }
        }
        break;
      }
    }
  }
}
