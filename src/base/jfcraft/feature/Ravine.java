package jfcraft.feature;

/**
 * Ravine
 */

import java.util.*;

import javaforce.gl.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Static.*;

public class Ravine extends Eraser {
  private float dir, d1, d2;
  private float elev;
  private int halflen;
  private int len;
  private boolean taper;
  private float factor;
  private float profiles[][] = new float[2][50];
  private int height;
  private int width;

  private GLMatrix mat = new GLMatrix();
  private GLVector3 vec = new GLVector3();

  private static boolean once;

  private boolean setupRavine() {
    int celev = (int)chunk.getElev(8, 8);
//    if (celev < 50) return false;
    if (once) return false;
    once = true;
    elev = data.c1 % celev;
    if (elev < 25) elev = 25;

    elev = 30;  //test

    height = 30 + (data.c2 % 20);  //30-50
    d1 = data.cf1 * 180f;
    d2 = d1 + 180f;
    dir = d1;
    halflen = 40 + (data.c3 % 25);  //40-65
    len = halflen;
    width = 10 + ((data.c1 ^ data.c2) % 5);  //10-15
    factor = 1f;
    setSize(50);
    setPos(8,elev,8);
    setFill(Blocks.LAVA, 10);
    buildProfiles();
    return true;
  }

  private void buildProfiles() {
    int wx = chunk.cx * 16;
    int wz = chunk.cz * 16;
    int fwidth = (int)(width * factor)/2;
    int pos1 = 25 - fwidth;
    int pos2 = 25 - fwidth + 5;
    int pos3 = 25 + fwidth - 5;
    int pos4 = 25 + fwidth;
    //top (25%)
    float t = height * 0.25f;
    float ft = t * 0.10f;
    int side = 0;
    for(int pos=25;pos>=pos1;pos--) {
//      if (pos <= pos2) {
        ft *= 1.1f;
        t -= Static.abs(Static.noiseFloat(N_RANDOM2, wx + side,wz + pos)) * ft;
        if (t < 0) t = 0;
//      }
      profiles[side][pos] = t;
      profiles[side][25 + (25-pos)] = t;
    }
    //bottom (75%)
    side = 1;
    float b = height * 0.75f;
    float fb = b * 0.15f;
    for(int pos=25;pos>=pos1;pos--) {
      if (pos <= pos2) {
        fb *= 2.5f;
        b -= Static.abs(Static.noiseFloat(N_RANDOM2, wx + side,wz + pos)) * fb;
        if (b < 0) b = 0;
      }
      profiles[side][pos] = b;
      profiles[side][25 + (25-pos)] = b;
    }
  }

  private void buildShape() {
    int fwidth = (int)(width * factor)/2;
    int pos1 = 25 - fwidth;
    int pos2 = 25 - fwidth + 5;
    int pos3 = 25 + fwidth - 5;
    int pos4 = 25 + fwidth;
    resetShape();
    int y_A = (int)(height * 0.25f);
    int y_B = (int)(height * 0.75f);
    for(int z=pos1;z<=pos4;z++) {
      for(int x=pos1;x<=pos4;x++) {
        int dA = (int)Static.min(profiles[0][x],profiles[0][z]);
        int dB = (int)Static.min(profiles[1][x],profiles[1][z]);
        if (dA >= y_A) dA = y_A-1;
        if (dB >= y_B) dB = y_B-1;
        for(int yA = 0;yA <= dA;yA++) {
          setShape(x,y_B + yA,z);
        }
        for(int yB = 1;yB <= dB;yB++) {
          setShape(x,y_B - yB,z);
        }
      }
    }
  }

  public boolean setup() {
    return setupRavine();
  }

  public void move() {
    len--;
    if (len < 16) {
      factor -= Static._1_32;
    }
    mat.setIdentity();
    mat.addRotate(dir, 0, 1, 0);
    vec.set(0, 0, -1);  //north
    mat.mult(vec);
    addX(vec.v[0]);
    addY(vec.v[1]);
    addZ(vec.v[2]);
  }

  public boolean endPath() {
    return len == 0;
  }

  public boolean flip() {
    dir = d2;
    setPos(8, elev, 8);
    len = halflen;
    factor = 1f;
    return true;
  }

  public void preErase() {
    buildProfiles();
    buildShape();
  }

  public void postErase() {
  }
}
