package jfcraft.item;

/** ItemClock
 *
 * @author pquiring
 *
 * Created : July 23, 2014
 *
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class ItemClock extends ItemBase {
  public ItemClock(String id, String names[], String texture[]) {
    super(id, names, texture);
  }
  public void addFaceInvItem(RenderBuffers obj, int var, boolean green) {
    float tx1, ty1, tx2, ty2;
    if (!isVar) var = 0;
    //calc angle and add to ty1/ty2
    float time = Static.client.world.time;  //24000 ticks / day
    //the clock is based on 6am
    time += 12000;
    if (time < 0) time += 24000;
    if (time > 24000) time -= 24000;
    SubTexture st = textures[0];
    AssetImage ai = st.ai;
//    Static.log("w/h=" + w + "," + h);
    float frames = ai.noFrames;
    int frame = Static.floor(time / (24000.0f / frames));
//    Static.log("clock=" + frame);
    if (frame >= ai.noFrames) frame = ai.noFrames-1;
    if (frame < 0) frame = 0;
//    Static.log("time=" + time + ",ay=" + ay + ":frames=" + frames);
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
    //calc angle and add to ty1/ty2
    float ay = 0.0f;
    float time = Static.client.world.time;  //24000 ticks / day
    float w = textures[var].ai.image.getWidth();
    float h = textures[var].ai.image.getHeight();
//    Static.log("w/h=" + w + "," + h);
    float frames = h / w;
    ay = Static.floor(time / (24000.0f / frames)) / frames;
//    Static.log("y=" + yAngle + ",ay=" + ay + ":frames=" + frames);
    tx1 = textures[var].x1;
    ty1 = textures[var].y1 + ay;
    tx2 = textures[var].x2;
    ty2 = textures[var].y2 + ay;
    float x1 = -0.5f;
    float y1 = 0;
    float x2 = 0.5f;
    float y2 = 1;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
    //swap x and redo for other side
    x1 = x2;
    x2 = -0.5f;
    obj.addFace2D(x1,y1,x2,y2,tx1,ty1,tx2,ty2,Static.white);
  }
}
