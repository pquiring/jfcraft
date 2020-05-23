package jfcraft.feature;

/**
 *  Cave
 *
 *  Each cave is connected to two of the four sides of the chunk.
 *  The connection point on the E,S walls is defined by the current chunk.
 *  The N,W walls are defined from the adjacent chunks.
 *  A Bezier curve is used to connect points.
 *
 *  There are 3 levels, each level has 2 caves:
 *
 *  64 = SEALEVEL
 *  60 (4) = gap
 *  44 (16) = level 1
 *  40 (4) = gap
 *  24 (16) = level 2
 *  20 (4) = gap
 *  4 (16) = level 3
 *  0 (4) = gap (BEDROCK)
 *
 *  This creates a true Perlin noise generated cave instead of fragments.
 *
 */

import java.awt.geom.*;
import java.awt.*;

import javaforce.gl.*;
import javaforce.*;

import jfcraft.data.*;
import static jfcraft.data.Static.*;
import static jfcraft.data.Direction.*;

public class Cave extends Eraser {
  private float profiles[][] = new float[4][8];
  private static JFImage imgXZ = new JFImage(16,16);
  private static JFImage imgY = new JFImage(16,256);
  private int dir, dir1, dir2;
  private int path;
  private int level;

  private int nx, ny, nz;
  private int ex, ey, ez;
  private int sx, sy, sz;
  private int wx, wy, wz;
  private int nsy, wey, nwy, ney, swy, sey;  //mid-point elevations

  private boolean eop;  //end of path
  private static final int levelBase[] = new int[] {0, 4, 24, 44};
  private boolean connected[] = new boolean[4];

  private void setupCaves() {
    int _wx = chunk.cx * 16 + level;
    int _wz = chunk.cz * 16 + level;

    sx = Static.noiseInt(N_RANDOM1, 8, _wx, _wz) + 4;
    sz = 15;
    sy = Static.noiseInt(N_RANDOM3, 16, _wx, _wz);
    sy += levelBase[level];

    ex = 15;
    ez = Static.noiseInt(N_RANDOM5, 8, _wx, _wz) + 4;
    ey = Static.noiseInt(N_RANDOM6, 16, _wx, _wz);
    ey += levelBase[level];

    boolean connect = false;

    switch (Static.noiseInt(N_RANDOM7, 3, _wx, _wz)) {  //N -> W/S/E
      case 0: dir = NW; dir2 = SE; connect = false; break;
      case 1: dir = NS; dir2 = WE; connect = Static.noiseInt(N_RANDOM8, 2, _wx, _wz) == 0; break;
      case 2: dir = NE; dir2 = SW; connect = false; break;
    }

    _wz -= 16;

    nx = Static.noiseInt(N_RANDOM1, 8, _wx, _wz) + 4;
    nz = -1;
    ny = Static.noiseInt(N_RANDOM3, 16, _wx, _wz);
    ny += levelBase[level];

    _wz += 16;
    _wx -= 16;

    wx = -1;
    wz = Static.noiseInt(N_RANDOM5, 8, _wx, _wz) + 4;
    wy = Static.noiseInt(N_RANDOM6, 16, _wx, _wz);
    wy += levelBase[level];

    _wx += 16;

    //calc mid-point y
    if (connect) {
      nsy = (ny + sy) / 2;
      wey = (wy + ey) / 2;
      nwy = (ny + wy) / 2;
      ney = (ny + ey) / 2;
      swy = (sy + wy) / 2;
      sey = (sy + ey) / 2;
    } else {
      nsy = levelBase[level];
      wey = levelBase[level];
      nwy = levelBase[level];
      ney = levelBase[level];
      swy = levelBase[level];
      sey = levelBase[level];
      //avoid connections
      switch (dir1) {
        case NW: swy += 12; break;
        case NS: wey += 12; break;
        case NE: swy += 12; break;
      }
    }
    connected[level] = connect;
    if (level > 1) {
      if (connect && connected[level-1]) {
        //connect to lower level
        new CaveElevator().build(chunk, data, levelBase[level] - 16 - 4 - 15, levelBase[level]);
      }
    }

    nsy += Static.noiseInt(N_RANDOM1, 3, _wx, _wz) - 1;
    wey += Static.noiseInt(N_RANDOM2, 3, _wx, _wz) - 1;
    nwy += Static.noiseInt(N_RANDOM3, 3, _wx, _wz) - 1;
    ney += Static.noiseInt(N_RANDOM4, 3, _wx, _wz) - 1;
    swy += Static.noiseInt(N_RANDOM5, 3, _wx, _wz) - 1;
    sey += Static.noiseInt(N_RANDOM6, 3, _wx, _wz) - 1;

    setSize(8);
    setPos(nx,ny,nz);
    setCenter(4,0,4);  //center bottom
    setFill(Blocks.LAVA, 10);  //fill with LAVA when y <= 10
    buildPath();
    buildProfiles();
    path = 1;
    eop = false;
  }

  private void buildProfiles() {
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    for(int side=0;side<4;side++) {
      float e = 2f;
      float f = 0.5f;
      for(int pos=2;pos<4;pos++) {
        e += Static.abs(Static.noiseFloat(N_RANDOM2, wx + side, wz + pos)) * f;
        f += 1.5f;
        if (e >= 6f) e = 6f;
        profiles[side][pos] = e;
        profiles[side][7-pos] = e;
      }
    }
  }

  private void buildShape() {
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    int px = wx + getX();
    int pz = wz + getZ();
    resetShape();
    for(int z=2;z<6;z++) {
      for(int x=2;x<6;x++) {
        int dA = (int)(Static.min(profiles[0][z], profiles[1][x]) + (Static.abs(Static.noiseFloat(N_RANDOM2, px + x, pz + z) * 0.5f)));
        int dB = (int)(Static.min(profiles[2][z], profiles[3][x]) + (Static.abs(Static.noiseFloat(N_RANDOM3, px + x, pz + z) * 0.5f)));
        if (dA < 0) dA = 0;
        if (dA > 4) dA = 4;
        if (dB < 0) dB = 0;
        if (dB > 4) dB = 4;
        for(int yA = 1;yA <= dA;yA++) {
          setShape(x,4-yA,z);
        }
        for(int yB = 1;yB <= dB;yB++) {
          setShape(x,3+yB,z);
        }
      }
    }
  }

  private static final int black = 0x00000000;
  private static final int white = 0x00ffffff;
  private static final int red   = 0x00ff0000;

  private void buildPath() {
    //draw bezier curves
    imgXZ.fill(0, 0, 16, 16, white);
    imgXZ.getGraphics2D().setColor(Color.black);
    imgY.fill(0, 0, 16, 256, white);
    imgY.getGraphics2D().setColor(Color.black);
    switch (dir) {
      case NS:  //N->S
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(nx, nz, 8, 8, 8, 8, sx, sz));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, ny, 8, nsy, 8, nsy, 15, sy));
        break;
      case NW:  //N->W
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(nx, nz, 8, 8, 8, 8, wx, wz));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, ny, 8, nwy, 8, nwy, 15, wy));
        break;
      case NE:  //N->E
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(nx, nz, 8, 8, 8, 8, ex, ez));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, ny, 8, ney, 8, ney, 15, ey));
        break;
      case WE:  //W->E
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(wx, wz, 8, 8, 8, 8, ex, ez));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, wy, 8, wey, 8, wey, 15, ey));
        break;
      case SW:  //S->W
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(sx, sz, 8, 8, 8, 8, wx, wz));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, sy, 8, swy, 8, swy, 15, wy));
        break;
      case SE:  //S->E
        imgXZ.getGraphics2D().draw(new CubicCurve2D.Float(sx, sz, 8, 8, 8, 8, ex, ez));
        imgY.getGraphics2D().draw(new CubicCurve2D.Float(0, sy, 8, sey, 8, sey, 15, ey));
        break;
    }
    //imgXZ.putPixel(nx, nz, red);  //off-image
    imgY.putPixel(0, ny, red);
  }

  public boolean setup() {
    level = 1;
    setupCaves();
    return true;
  }

  public void move() {
    //move using image (bezier curved path)
    int x = getX();
    int y = getY();
    int z = getZ();
    boolean moved = false;
    int xz = 0;
    outY:
    for(xz=0;xz<16;xz++) {
      for(int dy=-1;dy<=1;dy++) {
        int new_y = y + dy;
        if (new_y < 0 || new_y > 255) continue;
        if (imgY.getPixel(xz, y + dy) == black) {
          //xz value ignored
          y = new_y;
          imgY.putPixel(xz, y, red);
          moved = true;
          break outY;
        }
      }
    }
    outXZ:
    for(int dx=-1;dx<=1;dx++) {
      for(int dz=-1;dz<=1;dz++) {
        int new_x = x + dx;
        int new_z = z + dz;
        if (new_x < 0 || new_x > 15) continue;
        if (new_z < 0 || new_z > 15) continue;
        if (imgXZ.getPixel(new_x, new_z) == black) {
          x = new_x;
          z = new_z;
          imgXZ.putPixel(x, z, red);
          moved = true;
          break outXZ;
        }
      }
    }

    setPos(x,y,z);
    eop = !moved;
  }

  public boolean endPath() {
    return eop;
  }

  public boolean nextPath() {
    if (path == 2) {
      level++;
      if (level > 3) return false;
      setupCaves();
      return true;
    } else {
      dir = dir2;
      switch (dir) {
        case SE: setPos(sx, sy, sz); break;
        case WE: setPos(wx, wy, wz); break;
        case SW: setPos(sx, sy, sz); break;
      }
      buildPath();
      eop = false;
      path++;
      return true;
    }
  }

  public void preErase() {
    buildShape();
  }

  public void postErase() {
  }
}
