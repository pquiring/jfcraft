package jfcraft.opengl;

/** An array of RenderBuffers
 *
 * @author pquiring
 */

import javaforce.gl.*;

public class RenderDest {
  private RenderBuffers buffers[];

  public int preferedIdx;

  public RenderDest(int cnt) {
    buffers = new RenderBuffers[cnt];
  }

  public int count() {
    return buffers.length;
  }

  public RenderBuffers getBuffers(int idx) {
    if (buffers[idx] == null) {
      buffers[idx] = new RenderBuffers();
    }
    return buffers[idx];
  }

  public boolean exists(int idx) {
    if (idx >= buffers.length) return false;
    return buffers[idx] != null;
  }

  public boolean allEmpty() {
    for(int a=0;a<buffers.length;a++) {
      if (buffers[a] != null) {
        if (!buffers[a].isArrayEmpty()) return false;
      }
    }
    return true;
  }

  public void resetAll() {
    for(int a=0;a<buffers.length;a++) {
      if (buffers[a] != null) {
        buffers[a].reset();
      }
    }
  }

  public void copyBuffers() {
    for(int a=0;a<buffers.length;a++) {
      if (buffers[a] != null) {
        buffers[a].copyBuffers();
      }
    }
  }
}
