package jfcraft.feature;

/**
 * Eraser
 *
 * Clears stone thru multiple paths using a shape.
 */

import java.util.*;

import jfcraft.data.*;
import jfcraft.gen.*;
import jfcraft.biome.*;
import static jfcraft.data.Direction.*;

public abstract class Eraser {
  public boolean shape[];
  private boolean edges[];
  private int size;   //shape dimensions (must be odd)
  private int dd;     //dim * dim
  private int half;   //dim/2
  private boolean clearAbove;
  private boolean start;

  public GeneratorChunk chunk;
  public BiomeData data;

  private float px,py,pz;  //center position
  private float ox,oy,oz;  //old position
  public int w_x, w_z;  //world x/y coords of chunk
  private int offx, offy, offz;  //offset to center of shape
  private char fill;
  private int fillElev;

  public void build(GeneratorChunk chunk, BiomeData data) {
    this.chunk = chunk;
    this.data = data;
    w_x = chunk.cx * 16;
    w_z = chunk.cz * 16;
    px = 8;
    py = 0;
    pz = 8;
    if (!setup()) return;
    do {
      start = true;
      followPath();
    } while (nextPath());
  }

  private void followPath() {
    do {
      preErase();
      erase();
      postErase();
      ox = px;
      oy = py;
      oz = pz;
      move();
    } while (!endPath());
  }

  /** Setup Eraser, call setSize() and fill in shape. */
  public abstract boolean setup();
  /** Move center position by one block. */
  public abstract void move();
  /** Check if at end of path. */
  public abstract boolean endPath();
  /** Starts next path to erase. */
  public abstract boolean nextPath();
  /** Called before erasing a point. */
  public abstract void preErase();
  /** Called after erasing a point. */
  public abstract void postErase();

  /** Sets size of shape in all 3 dimensions. Size must be odd. */
  public void setSize(int size) {
    if ((size & 1) == 0) size++;
    this.size = size;
    half = size/2;
    dd = size*size;
    shape = new boolean[size*size*size];
    edges = new boolean[size*size*size];
    offx = half;
    offy = half;
    offz = half;
  }

  public int getSize() {
    return size;
  }

  /** Sets position of shape relative to chunk. */
  public void setPos(float x, float y, float z) {
    px = x;
    py = y;
    pz = z;
  }

  public int getX() {return (int)px;}
  public int getY() {return (int)py;}
  public int getZ() {return (int)pz;}

  public void addX(float delta) {px += delta;}
  public void addY(float delta) {py += delta;}
  public void addZ(float delta) {pz += delta;}

  public void setClearAbove(boolean state) {
    clearAbove = state;
  }

  /** Sets center of shape (default = 1/2 of each dimension) */
  public void setCenter(int x,int y,int z) {
    offx = x;
    offy = y;
    offz = z;
  }

  public void resetShape() {
    Arrays.fill(shape, false);
  }

  public void setShape(int x, int y, int z) {
    shape[y * dd + z * size + x] = true;
  }

  public void clearShape(int x, int y, int z) {
    shape[y * dd + z * size + x] = false;
  }

  /** Replace rock with fill id below or at elevation (WATER or LAVA only). */
  public void setFill(char id, int elev) {
    fill = id;
    fillElev = elev;
  }

  private void eraseShape(boolean shape[]) {
    int ix = (int)px - offx;
    int iy = (int)py - offy;
    int iz = (int)pz - offz;
    for(int y=0;y<size;y++) {
      for(int z=0;z<size;z++) {
        for(int x=0;x<size;x++) {
          if (shape[y * dd + z * size + x]) {
            int xx = ix + x;
            int yy = iy + y;
            int zz = iz + z;
            if (yy == 0) continue;  //do not remove bedrock
            chunk.clearBlock(xx, yy, zz);
            if (fill != 0 && yy <= fillElev) {
              chunk.setBlock2(xx, yy, zz, fill, 0);
            }
            if (yy == size-1 && clearAbove) {
              while (yy < 256) {
                yy++;
                if (chunk.getBlock(xx, yy, zz) == 0) break;
                chunk.clearBlock(xx, yy, zz);
              }
            }
          }
        }
      }
    }
  }

  private void erase() {
    if (start) {
      //erase full shape
      eraseShape(shape);
      start = false;
    } else {
      //calc edge bits
      clearEdges();
      if (px < ox) setEdge(W);
      if (px > ox) setEdge(E);
      if (pz < oz) setEdge(N);
      if (pz > oz) setEdge(S);
      if (py < oy) setEdge(B);
      if (py > oy) setEdge(A);
      eraseShape(edges);
    }
  }

  private void clearEdges() {
    Arrays.fill(edges, false);
  }

  private void setEdge(int side) {
    switch (side) {
      case N:
        for(int y=0;y<size;y++) {
          for(int x=0;x<size;x++) {
            for(int z=0;z<size;z++) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
      case E:
        for(int y=0;y<size;y++) {
          for(int z=0;z<size;z++) {
            for(int x=size-1;x>=0;x--) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
      case S:
        for(int y=0;y<size;y++) {
          for(int x=0;x<size;x++) {
            for(int z=size-1;z>=0;z--) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
      case W:
        for(int y=0;y<size;y++) {
          for(int z=0;z<size;z++) {
            for(int x=0;x<size;x++) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
      case A:
        for(int z=0;z<size;z++) {
          for(int x=0;x<size;x++) {
            for(int y=0;y<size;y++) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
      case B:
        for(int z=0;z<size;z++) {
          for(int x=0;x<size;x++) {
            for(int y=size-1;y>=0;y--) {
              if (shape[y * dd + z * size + x]) {
                edges[y * dd + z * size + x] = true;
                break;
              }
            }
          }
        }
        break;
    }
  }
}
