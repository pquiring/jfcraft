package jfcraft.opengl;

/**
 * Voxel - converts an image into a 3d pixelated object.
 */

import jfcraft.item.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;
import static jfcraft.item.ItemBase.data;

public class Voxel implements RenderSource {
  private ItemBase item;
  private int var;
  public RenderDest dest;

  public Voxel(ItemBase item, int var) {
    this.item = item;
    this.var = var;
    dest = new RenderDest(1);
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    SubTexture st = item.textures[var];
    int w = st.ai.w;
    int h = st.ai.h;
    int px[] = st.ai.getPixels();
    RenderBuffers bufs = dest.getBuffers(0);
    float d = 1.0f / ((float)w);  //size of each voxel
    //bottom to top, left to right
    float x1 = 0f;
    float x2 = 1f;
    float y1 = 0f;
    float y2 = y1 + h * d;
    int off = w * (h-1);
    int x = 0, y = h-1;
    float zz = 0.5f;
    float d2 = Static._1_32;  //depth of voxels
    SubTexture solidTexture = Static.blocks.solid.textures[0];
    float u1 = solidTexture.x1;
    float v1 = solidTexture.y1;
    float u2 = solidTexture.x2;
    float v2 = solidTexture.y2;
    float clr[] = new float[3];
    float clr2[] = new float[3];
    for(float fy = y1; fy < y2; fy+=d) {
      x = 0;
      for(float fx = x1; fx < x2; fx+=d) {
        if ((px[off] & 0xff000000) == 0) {  //transparent pixel?
          x++;
          off++;
          continue;
        }
        data.clr = clr;
        clr[0] = (float)((px[off] & 0xff0000) >> 16) / 255.0f;
        clr[1] = (float)((px[off] & 0xff00) >> 8) / 255.0f;
        clr[2] = (float)((px[off] & 0xff)) / 255.0f;
        clr2[0] = (clr[0] > 5) ? clr[0] - 5f : 0f;
        clr2[1] = (clr[1] > 5) ? clr[1] - 5f : 0f;
        clr2[2] = (clr[2] > 5) ? clr[2] - 5f : 0f;
        //see bufs.addBox()
        //S
        bufs.addFace(fx,fy,zz-d2, fx+d,fy+d,zz-d2, u1,v1, u2,v2, data);
        //N
        bufs.addFace(fx+d,fy,zz+d2, fx,fy+d,zz+d2, u1,v1, u2,v2, data);
        data.clr = clr2;
        //B
        if (y < h-1 && (px[off + w] & 0xff000000) == 0) {
          bufs.addFaceAB(fx,fy+d,zz+d2, fx+d,fy+d,zz-d2, u1,v1, u2,v2, data);
        }
        //A
        if (y > 0 && (px[off - w] & 0xff000000) == 0) {
          bufs.addFaceAB(fx,fy,zz-d2, fx+d,fy,zz+d2, u1,v1, u2,v2, data);
        }
        //E
        if (x < w-1 && (px[off + 1] & 0xff000000) == 0) {
          bufs.addFace(fx,fy,zz-d2, fx,fy+d,zz+d2, u1,v1, u2,v2, data);
        }
        //W
        if (x > 0 && (px[off - 1] & 0xff000000) == 0) {
          bufs.addFace(fx+d,fy,zz+d2, fx+d,fy+d,zz-d2, u1,v1, u2,v2, data);
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
