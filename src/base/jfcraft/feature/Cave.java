package jfcraft.feature;

/**
 * Cave
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Static.*;

public class Cave extends Eraser {
  private float dir, d1, d2;
  private float elev;
  private int len;
  private boolean taper;
  private float factor;
  private float ydir;
  private float profiles[][] = new float[4][8];

  private GLMatrix mat = new GLMatrix();
  private GLVector3 vec = new GLVector3();

  private void setupCaves() {
    elev = data.c1 % chunk.getElev(8, 8);
    d1 = data.cf1 * 180f;
    d2 = d1 + 180f;
    dir = d1;
    len = 100;
    factor = 1f;
    ydir = 0f;
    setSize(8);
    setPos(8,elev,8);
    setFill(Blocks.LAVA, 10);
    buildProfiles();
  }

  private void buildProfiles() {
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    for(int side=0;side<4;side++) {
      float e = 1f;  //test
      float f = 0.5f;
      for(int pos=2;pos<4;pos++) {
        e += Static.abs(Static.noiseFloat(N_RANDOM2, wx + side,wz + pos)) * f;
        f += 1.5f;
        if (e >= 4f) e = 4f;
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

  public boolean setup() {
    setupCaves();
    return true;
  }

  public void move() {
    len--;
    mat.setIdentity();
    mat.addRotate(dir, 0, 1, 0);
    mat.addRotate3(ydir, 1, 0, 0);
    vec.set(0, 0, -1);  //north
    mat.mult(vec);
    addX(vec.v[0]);
    addY(vec.v[1]);
    addZ(vec.v[2]);
    int x = wx + getX();
    int y = getY();
    int z = wz + getZ();
    dir += Static.noiseFloat(N_RANDOM1, x, y, z) * 15f;
    ydir += Static.noiseFloat(N_RANDOM1, x, -y, z) * 5f;
  }

  public boolean endPath() {
    return len == 0;
  }

  public boolean flip() {
    dir = d2;
    setPos(8, elev, 8);
    len = 100;
    factor = 1f;
    ydir = 0f;
    return true;
  }

  public void preErase() {
    buildShape();
  }

  public void postErase() {
  }
}
