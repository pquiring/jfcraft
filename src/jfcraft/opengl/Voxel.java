package jfcraft.opengl;

/**
 * Voxel - converts an image into a 3d pixelated object.
 *
 * dims : 0,0 -> 1,1
 *    z : -1/32 -> 1/32
 *
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class Voxel implements RenderSource {
  private ItemBase item;
  private int var;
  public RenderDest dest;

  public Voxel(ItemBase item, int var) {
    this.item = item;
    this.var = var;
    dest = new RenderDest(1);
  }

  public void setVar(int var) {
    this.var = var;
  }

  private static final float dark = 0.1f;

  public void buildBuffers(RenderDest dest) {
    SubTexture st = item.textures[var];
    int w = st.ai.w;
    int h = st.ai.h;
    int size = w * h;
    int px;
    int[] spxs = st.ai.getPixels();
    int[] pxs;
    if (item.isTool || item.isWeapon) {
      //copy pixels mirrored on both axis
      pxs = new int[size];
      int src = 0;
      int dst = 0;
      int last = w * (h-1);
      for(int y=0;y<h;y++) {
        dst = (w-1) - y + last;
        for(int x=0;x<w;x++) {
          px = spxs[src++];
          pxs[dst] = px;
          dst -= w;
        }
      }
      //swap w, h
      int tw = w;
      int th = h;
      w = th;
      h = tw;
    } else {
      //use pixels as is
      pxs = spxs;
    }
    RenderBuffers bufs = dest.getBuffers(0);
    bufs.reset();
    float d = 1.0f / ((float)w);  //size of each voxel
    float d2 = d / 2.0f;
    //bottom to top, left to right
    float x1 = 0f;
    float x2 = 1f;
    float y1 = 0f;
    float y2 = y1 + h * d;
    int off = w * (h-1);
    int x = 0;
    int y = h-1;
    float z1 = 0.5f + d2;
    float z2 = 0.5f - d2;
    SubTexture solidTexture = Static.blocks.solid.textures[0];
    float u1 = solidTexture.x1;
    float v1 = solidTexture.y1;
    float u2 = solidTexture.x2;
    float v2 = solidTexture.y2;
    float clr[] = new float[3];
    Static.data.reset();
    Static.data.clr = clr;
    for(float fy = y1; fy < y2; fy+=d) {
      x = 0;
      for(float fx = x1; fx < x2; fx+=d) {
        px = pxs[off];
        if ((px & 0xff000000) == 0) {  //transparent pixel?
          x++;
          off++;
          continue;
        }
        clr[0] = (float)((px & 0xff0000) >> 16) / 255.0f;
        clr[1] = (float)((px & 0xff00) >> 8) / 255.0f;
        clr[2] = (float)((px & 0xff)) / 255.0f;
        //see bufs.addBox()
        //S
        bufs.addFace(fx,fy,z2, fx+d,fy+d,z2, u1,v1, u2,v2);
        //N
        bufs.addFace(fx+d,fy,z1, fx,fy+d,z1, u1,v1, u2,v2);
        //adjust clr for sides (darker)
        clr[0] = (clr[0] > dark) ? clr[0] - dark : 0f;
        clr[1] = (clr[1] > dark) ? clr[1] - dark : 0f;
        clr[2] = (clr[2] > dark) ? clr[2] - dark : 0f;
        //B
        if (y == h-1 || (pxs[off + w] & 0xff000000) == 0) {
          bufs.addFaceAB(fx,fy,z1, fx+d,fy,z2, u1,v1, u2,v2);
        }
        //A
        if (y == 0 || (pxs[off - w] & 0xff000000) == 0) {
          bufs.addFaceAB(fx,fy+d,z2, fx+d,fy+d,z1, u1,v1, u2,v2);
        }
        //E
        if (x == w-1 || (pxs[off + 1] & 0xff000000) == 0) {
          bufs.addFace(fx+d,fy,z2, fx+d,fy+d,z1, u1,v1, u2,v2);
        }
        //W
        if (x == 0 || (pxs[off - 1] & 0xff000000) == 0) {
          bufs.addFace(fx,fy,z1, fx,fy+d,z2, u1,v1, u2,v2);
        }
        x++;
        off++;
      }
      off -= w * 2;
      y--;
    }
  }

  public void bindTexture() {
    Static.blocks.blocks[Blocks.SOLID].bindTexture();
  }

  public void render() {
    RenderBuffers buf = dest.getBuffers(0);
    buf.bindBuffers();
    buf.render();
  }
}
