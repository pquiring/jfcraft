package jfcraft.item;

/** ItemCompass
 *
 * @author pquiring
 *
 * Created : July 23, 2014
 *
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class ItemCompass extends ItemBase {
  public ItemCompass(String id, String names[], String texture[]) {
    super(id, names, texture);
    varMask = 0;
  }
  public int getAngle(int cnt) {
    float yAngle = 0;
    if (Static.client != null) yAngle = Static.client.player.ang.y;
    yAngle += 180f;
    if (yAngle < 0) yAngle += 360.0f;
    if (yAngle > 360) yAngle -= 360.0f;
    int ang = (int)(((360f - yAngle) / 360.0f) * cnt);
    if (ang < 0) ang = 0;
    if (ang >= cnt) ang = cnt - 1;
    return ang;
  }
  public void addFaceInvItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    //calc angle and add to ty1/ty2
    SubTexture st = textures[0];
    AssetImage ai = st.ai;
    if (st.isAnimated) {
      int frame = getAngle(ai.noFrames);
      tx1 = st.x1s[frame];
      ty1 = st.y1s[frame];
      tx2 = st.x2s[frame];
      ty2 = st.y2s[frame];
    } else {
      int ang = getAngle(32);
      //32 images
      tx1 = textures[ang].x1;
      ty1 = textures[ang].y1;
      tx2 = textures[ang].x2;
      ty2 = textures[ang].y2;
    }
    float x1 = 0;
    float y1 = 0;
    float x2 = 1;
    float y2 = 1;
    obj.reset();
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
  }
  public void addFaceWorldItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    float ay = 0.0f;
    tx1 = textures[var].x1;
    ty1 = textures[var].y1 + ay;
    tx2 = textures[var].x2;
    ty2 = textures[var].y2 + ay;
    float x1 = -0.5f;
    float y1 = 1;
    float x2 = 0.5f;
    float y2 = 0;
    obj.reset();
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
    //swap x and redo for other side
    x1 = x2;
    x2 = -0.5f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
  }
  public void render() {
    Static.data.reset();
    buildBuffers(bufs[0]);
    bufs[0].copyBuffers();
    super.render();
  }
  public Voxel getVoxel(int var) {
    Voxel voxel = super.getVoxel(var);
    voxel.setVar(getAngle(32));
    createVoxel(0);
    return voxel;
  }
}
