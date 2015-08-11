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
  }
  public void addFaceInvItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    //calc angle and add to ty1/ty2
    float yAngle = Static.client.player.ang.y;
    yAngle += 180f;
    if (yAngle < 0) yAngle += 360.0f;
    if (yAngle > 360) yAngle -= 360.0f;
    SubTexture st = textures[0];
    AssetImage ai = st.ai;
//    Static.log("w/h=" + w + "," + h);
    float frames = ai.noFrames;
    int frame = Static.floor((360.0f - yAngle) / (360.0f / frames));
//    Static.log("compass=" + frame);
    if (frame >= ai.noFrames) frame = ai.noFrames-1;
    if (frame < 0) frame = 0;
//    Static.log("y=" + yAngle + ",ay=" + ay + ":frames=" + frames);
    tx1 = st.x1s[frame];
    ty1 = st.y1s[frame];
    tx2 = st.x2s[frame];
    ty2 = st.y2s[frame];
    float x1 = 0;
    float y1 = 0;
    float x2 = 1;
    float y2 = 1;
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
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
    //swap x and redo for other side
    x1 = x2;
    x2 = -0.5f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
  }
}
