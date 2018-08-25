package jfcraft.gen;

/** Chunk generator phase 2 : structures
 *
 * Any structure can only span 8 chunks in any direction,
 *  for a total of 17 chunks span (that's 272 blocks).
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import static javaforce.gl.GL.*;

import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;

public class GeneratorPhase2Mario implements GeneratorPhase2Base {
  private Chunk chunk;
  private Random r = new Random();

  public void getIDs() {}

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    synchronized(chunk.lock) {
      chunk.needPhase2 = false;
      chunk.dirty = true;
      int cnt;

      r.setSeed(chunk.seed);

      if (r.nextInt(30) == 0) {
        addMountian();
      }

      if (r.nextInt(20) == 0) {
        addPipe();
      }
      cnt = r.nextInt(2);
      for(int a=0;a<cnt;a++) {
        addSkyBlock();
      }
      cnt = r.nextInt(4);
      for(int a=0;a<cnt;a++) {
        addBricks();
      }
    }
  }
  private void setBlock(int x, int y, int z, char id, int bits) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    c.setBlock(x, y, z, id, bits);
  }
  private void setBlockIfEmpty(int x, int y, int z, char id, int bits) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    c.setBlockIfEmpty(x, y, z, id, bits);
  }
  private void clearBlock(int x, int y, int z) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    c.clearBlock(x, y, z);
    if (y < 10) {
      c.setBlock(x, y, z, Blocks.LAVA, 0);
    }
  }
  private BlockBase getBlock(int x, int y, int z) {
    if (y < 0) return null;
    if (y > 255) return null;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    return Static.blocks.blocks[c.getID(x,y,z)];
  }

  private void addPipe() {
    //generate pipes
//    Static.log("cave@" + chunk.cx + "," + chunk.cz);
    //start a cave on this chunk, can span max 8 chunks from here in any direction
    int groundelev = (int)chunk.elev[8*16+8];
    int elev = r.nextInt(groundelev - 20);
    int dir = r.nextInt(4) + N;  //n,e,s,w
    int maxelev = groundelev + 2 + r.nextInt(3);  //go a bit above ground (if pipe surfaces)
    doPipe(elev, maxelev, dir, false);
    dir = Direction.opposite(dir);
    if (dir > 180) dir -= 360;
    doPipe(elev, maxelev, dir, false);
  }

  /*
 The Pipe:
    xx
   x  x
  x    x
  x    x
   x  x
    xx
  */

  private static int bits_lime = Chunk.makeBits(0,Blocks.VAR_LIME);
  private static int bits_green = Chunk.makeBits(0,Blocks.VAR_GREEN);
  private static int bits_black = Chunk.makeBits(0,Blocks.VAR_BLACK);

  private void setPipe2(int x,int y,int z,int dx,int dy) {
    float fx = dx - 2.5f;
    float fy = dy - 2.5f;
    float s = Static.abs(fx) + Static.abs(fy) + 0.1f;
    if (s >= 4f) return;  //leave corners
    if (s >= 3f) {
      setBlock(x,y,z,Blocks.TERRACOTA,bits_lime);
    } else {
      clearBlock(x,y,z);
    }
  }

  private void setPipe3(int x,int y,int z,int dx,int dy,int dz) {
    float fx = dx - 2.5f;
    float fy = dy - 2.5f;
    float fz = dz - 2.5f;
    float s = Static.abs(fx) + Static.abs(fy) + Static.abs(fz) + 0.1f;
    if (s >= 14f) return;  //leave corners
    if (s >= 4.5f) {
      setBlock(x,y,z,Blocks.TERRACOTA,bits_lime);
    } else {
      clearBlock(x,y,z);
    }
  }

  private int steps[][] = {
    {2,1},
    {3,1},
    {4,2},
    {4,3},
    {3,4},
    {2,4},
    {1,3},
    {1,2}
  };

  private void doPipe(int elev, int maxelev, int dir, boolean fork) {
    int x = 8;
    int y = elev;
    int z = 8;
    int pos = 0;
    int len = 64 + r.nextInt(256);
    int dx = 0, dy = 0, dz = 0;
    int uponly = maxelev - 10;
    do {
      if (!fork && r.nextInt(150) == 0) {
        //fork
        int forkdir = 0;
        switch (dir) {
          case N: forkdir = E; break;
          case E: forkdir = S; break;
          case S: forkdir = W; break;
          case W: forkdir = N; break;
          case A: forkdir = r.nextInt(4) + N; break;
        }
        if (forkdir > W) forkdir = N;
        doPipe(y, maxelev, forkdir, true);
      }

      switch (dir) {
        case N:
        case S:
          for(int px=0;px<6;px++) {
            for(int py=0;py<6;py++) {
              setPipe2(x+px,y+py,z,px,py);
            }
          }
          break;
        case E:
        case W:
          for(int pz=0;pz<6;pz++) {
            for(int py=0;py<6;py++) {
              setPipe2(x,y+py,z+pz,pz,py);
            }
          }
          break;
        case A:
          for(int px=0;px<6;px++) {
            for(int pz=0;pz<6;pz++) {
              setPipe2(x+px,y,z+pz,px,pz);
            }
          }
          break;
      }

      switch (dir) {
        case N: dx = 0; dy = 0; dz =-1; break;
        case E: dx = 1; dy = 0; dz = 0; break;
        case S: dx = 0; dy = 0; dz = 1; break;
        case W: dx =-1; dy = 0; dz = 0; break;
        case A: dx = 0; dy = 1; dz = 0; break;
      }

      if (dir == A) {
        //put steps in the tube
        int idx = pos % 8;
        setBlock(x+steps[idx][0],y,z+steps[idx][1],Blocks.TERRACOTA,bits_green);
      }

      x += dx;
      y += dy;
      z += dz;
      if (y < 10) y = 10;
      if (y > 128) y = 128;

      if ((pos + 8) % 16 == 0 && y < uponly) {
        if (r.nextInt(1) == 0) {
          //change direction
          dir = 1 + r.nextInt(5);  //n,e,s,w,a (never goes down)
          //create connection
          for(int px=0;px<6;px++) {
            for(int py=0;py<6;py++) {
              for(int pz=0;pz<6;pz++) {
                setPipe3(x+px,y+py,z+pz,px,py,pz);
              }
            }
          }
        }
      }

      pos++;
      //can only go upto 8 chunks beyond start point or if surfaced
      if (x > 110 || x < -110 || z > 110 || z < -110 || (dir == A && y >= maxelev)) {
        break;
      }
    } while (pos < len);
  }

  private void addSkyBlock() {
    int elev = (int)chunk.elev[8*16+8];
    elev = elev + r.nextInt(256 - elev);
    if (elev > 256-16) {
      elev = 256-16;
    }
    int bits = Chunk.makeBits(0, Blocks.VAR_WHITE);
    for(int x = 0;x < 16;x++) {
      for(int y = 0;y < 16;y++) {
        for(int z = 0;z < 16;z++) {
          setBlock(x, elev + y, z, Blocks.SOLID, bits);
        }
      }
    }
    setBlock( 0,elev   , 0,BlockScrew.SCREW,0);
    setBlock( 0,elev+15, 0,BlockScrew.SCREW,0);
    setBlock( 0,elev   ,15,BlockScrew.SCREW,0);
    setBlock( 0,elev+15,15,BlockScrew.SCREW,0);
    setBlock(15,elev   , 0,BlockScrew.SCREW,0);
    setBlock(15,elev+15, 0,BlockScrew.SCREW,0);
    setBlock(15,elev   ,15,BlockScrew.SCREW,0);
    setBlock(15,elev+15,15,BlockScrew.SCREW,0);
  }

  private void addBricks() {
    int elev = (int)chunk.elev[8*16+8];
    elev = elev + r.nextInt(256 - elev);
    if (elev > 256-16) {
      elev = 256-16;
    }
    int var = Blocks.VAR_WHITE;
    for(int x = 0;x < 16;x++) {
      for(int z = 0;z < 16;z++) {
        setBlock(x, elev, z, Blocks.BRICK, var);
      }
    }
    setBlock(0,elev,0, BlockCoinBlock.COIN_BLOCK, 0);
    setBlock(0,elev,15, BlockCoinBlock.COIN_BLOCK, 0);
    setBlock(15,elev,0, BlockCoinBlock.COIN_BLOCK, 0);
    setBlock(15,elev,15, BlockCoinBlock.COIN_BLOCK, 0);
  }

  private void setMountain2(int x,int y,int z,int dx,int dz) {
    float fx = dx - 7.5f;
    float fz = dz - 7.5f;
    float s = Static.abs(fx) + Static.abs(fz) + 0.1f;
    if (s >= 14f) return;
    setBlock(x,y,z,Blocks.TERRACOTA,bits_green);
  }

  private float cap[] = {
    12,10,8,6,2
  };

  private void setCap3(int x,int y,int z,int dx,int dy,int dz) {
    float fx = dx - 7.5f;
    float fz = dz - 7.5f;
    float s = Static.abs(fx) + Static.abs(fz) + 0.1f;
    if (s >= cap[dy]) return;
    setBlock(x,y,z,Blocks.TERRACOTA,bits_green);
  }

  private void addMountian() {
    int elev = (int)chunk.elev[8*16+8];
    int height = r.nextInt(64 + 32 - elev);
    if (height <= 5) return;
    height += elev;
    chunk.elev[8*16+8] = height;
    for(int x = 0;x < 16;x++) {
      for(int z = 0;z < 16;z++) {
        for(int y = elev;y < height;y++) {
          setMountain2(x,y,z,x,z);
        }
      }
    }
    //put on a cap
    for(int x = 0;x < 16;x++) {
      for(int z = 0;z < 16;z++) {
        for(int y = 0;y < 5;y++) {
          setCap3(x,height + y,z,x,y,z);
        }
      }
    }
    //put eyes
    int y = height - 3;
    setBlock(4,y,0,Blocks.SOLID,bits_black);
    setBlock(4,y,15,Blocks.SOLID,bits_black);
    setBlock(11,y,0,Blocks.SOLID,bits_black);
    setBlock(11,y,15,Blocks.SOLID,bits_black);
    setBlock(0,y,4,Blocks.SOLID,bits_black);
    setBlock(15,y,4,Blocks.SOLID,bits_black);
    setBlock(0,y,11,Blocks.SOLID,bits_black);
    setBlock(15,y,11,Blocks.SOLID,bits_black);
  }
}
