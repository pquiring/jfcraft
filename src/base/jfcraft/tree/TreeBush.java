package jfcraft.tree;

/**
 * TreeBush
 */

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class TreeBush extends TreeBase {
  public byte var;
  public boolean snow;
  public TreeBush setVar(byte var) {
    this.var = var;
    return this;
  }
  public TreeBush setSnow(boolean snow) {
    this.snow = snow;
    return this;
  }
  public void plant(int x,int y, int z, Chunk chunk) {
    this.chunk = chunk;
    setBlock(x  ,y  ,z  ,Blocks.WOOD, 0, var);

    setBlock(x  ,y+1,z  ,Blocks.LEAVES, 0, Chunk.makeBits(B,var));
    setBlock(x  ,y+0,z-1,Blocks.LEAVES, 0, Chunk.makeBits(S,var));
    setBlock(x  ,y+0,z+1,Blocks.LEAVES, 0, Chunk.makeBits(N,var));
    setBlock(x-1,y+0,z  ,Blocks.LEAVES, 0, Chunk.makeBits(E,var));
    setBlock(x-1,y+0,z-1,Blocks.LEAVES, 0, Chunk.makeBits(E,var));  //SE
    setBlock(x-1,y+0,z+1,Blocks.LEAVES, 0, Chunk.makeBits(E,var));  //NW
    setBlock(x+1,y+0,z  ,Blocks.LEAVES, 0, Chunk.makeBits(W,var));
    setBlock(x+1,y+0,z-1,Blocks.LEAVES, 0, Chunk.makeBits(W,var));  //SW
    setBlock(x+1,y+0,z+1,Blocks.LEAVES, 0, Chunk.makeBits(W,var));  //NW
    //place snow on top of tree
    if (!snow) return;
    setBlock(x  ,y+2,z  ,Blocks.SNOW, 0, 0);
    setBlock(x  ,y+1,z-1,Blocks.SNOW, 0, 0);
    setBlock(x  ,y+1,z+1,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+1,z  ,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+1,z-1,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+1,z+1,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+1,z  ,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+1,z-1,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+1,z+1,Blocks.SNOW, 0, 0);
  }
}
