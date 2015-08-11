package jfcraft.data;

/** Chunk Storage on disk
 *
 * @author pquiring
 *
 * Created : Jul 25, 2014
 */

import java.io.*;
import java.util.*;

public class ChunkStore {
  public RandomAccessFile raf;

  public ArrayList<Header> list = new ArrayList<Header>();
  public long fileSize;

  public int ix,iz;

  //types
  public static final byte FREE = 0;
  public static final byte CHUNK = 1;
  public static final byte FREEBYTE = -1; //if file chunk is < 5 bytes

  public static class Header {
    public byte type;
    public int size;  //FREE or CHUNK type only
    public int cx,cz;  //CHUNK type only
    public long pos;  //not written to disk
  }

  public void open(String fileName, int ix,int iz) {
//    Static.log("openStore:" + fileName);
    this.ix = ix;
    this.iz = iz;
    try {
      raf = new RandomAccessFile(fileName, "rw");
      //walk file and build chunk list
      long pos = 0;
      fileSize = raf.length();
      while (pos < fileSize) {
        Header header = new Header();
        header.pos = pos;
        raf.seek(pos);
        header.type = raf.readByte();
        pos++;
        switch (header.type) {
          case CHUNK:
            header.size = raf.readInt();
            header.cx = raf.readInt();
            header.cz = raf.readInt();
            pos += 4 + header.size;
            break;
          case FREEBYTE:
            break;
          default:
//            log("unknown chunk id:" + Integer.toString(header.type, 16));
            //no break;
          case FREE:
            header.size = raf.readInt();
            pos += 4 + header.size;
            break;
        }
        if (header.size < 0 || header.size > 1024 * 1024) {
          Static.logTrace("Illegal file chunk size=" + header.size + " @ " + header.pos);
          System.exit(0);
        }
        list.add(header);
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void create(String fileName, int ix,int iz) {
//    Static.log("createStore:" + fileName);
    this.ix = ix;
    this.iz = iz;
    try {
      raf = new RandomAccessFile(fileName, "rw");
      if (raf.length() != 0) {
        Static.log("Warning : Creating chunkstore but file already exists, truncating file!");
        raf.setLength(0);
      }
      fileSize = 0;
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public void saveChunk(byte data[], int cx, int cz) {
    int chunkSize = data.length + 2 * 4;
    try {
      int cnt = list.size();
      for(int a=0;a<cnt;a++) {
        Header h = list.get(a);
        if (h.type == CHUNK) {
          if (h.cx == cx && h.cz == cz) {
            if (h.size >= chunkSize) {
              //can place back in same place
//              log("replaced chunk @ " + Long.toString(h.pos, 16) + ",oldSize=" + h.size + ",newSize=" + chunkSize);
              raf.seek(h.pos + 1);
              int left = h.size - chunkSize;
              h.size = chunkSize;
              raf.writeInt(h.size);
              raf.seek(h.pos + 1 + 3 * 4);
              raf.write(data);
              if (left == 0) return;
              Header h2;
              while (a < cnt-1) {
                h2 = list.get(a+1);
                boolean free = false;
                switch (h2.type) {
                  case FREE:
                    left += 1 + 4 + h2.size;
                    free = true;
                    break;
                  case FREEBYTE:
                    left++;
                    free = true;
                    break;
                }
                if (!free) break;
                list.remove(a+1);
                cnt--;
              }
              long pos = h.pos + 1 + 4 + h.size;
              if (left < 5) {
                //pad left with FREEBYTEs
                for(int i=0;i<left;i++) {
                  raf.writeByte(FREEBYTE);
                  Header h3 = new Header();
                  h3.type = FREEBYTE;
                  h3.size = 1;
                  h3.pos = pos++;
                  a++;
                  list.add(a, h3);
                }
              } else {
                //create a FREE chunk
                Header h3 = new Header();
                h3.type = FREE;
                h3.size = left - 5;
                h3.pos = pos;
                raf.writeByte(h3.type);
                raf.writeInt(h3.size);
                list.add(a+1, h3);
              }
              return;
            } else {
              //mark this file chunk as free and save else where
              h.type = FREE;
              //check if above is free and merge
              while (a > 0) {
                Header above = list.get(a-1);
                int size = -1;
                switch (above.type) {
                  case FREE:
                    size = 1 + 4 + above.size;
                    break;
                  case FREEBYTE:
                    size = 1;
                    break;
                }
                if (size == -1) break;
                h.size += size;
                h.pos -= size;
                list.remove(a-1);
                cnt--;
                a--;
              }
              //check if below is free and merge
              while (a < cnt-1) {
                Header below = list.get(a+1);
                int size = -1;
                switch (below.type) {
                  case FREE:
                    size = 1 + 4 + below.size;
                    break;
                  case FREEBYTE:
                    size = 1;
                    break;
                }
                if (size == -1) break;
                h.size += size;
                list.remove(a+1);
                cnt--;
              }
              raf.seek(h.pos);
              raf.writeByte(h.type);  //FREE
              raf.writeInt(h.size);
              break;
            }
          }
        }
      }
      //not found in file (or chunk has grown and area was released)
      //check if empty space somewhere that can be used
      for(int a=0;a<cnt;a++) {
        Header h = list.get(a);
        if (h.type != FREE) continue;
        if (h.size >= chunkSize) {
          //found a free place with enough space
          int left = h.size - chunkSize;
//          log("save old chunk @ " + Long.toString(h.pos, 16) + ",oldSize=" + h.size + ",newSize=" + chunkSize + ",left=" + left);
          h.type = CHUNK;
          h.size = chunkSize;
          h.cx = cx;
          h.cz = cz;
          raf.seek(h.pos);
          raf.writeByte(h.type);
          raf.writeInt(h.size);
          raf.writeInt(h.cx);
          raf.writeInt(h.cz);
          raf.write(data);
          if (left == 0) return;
          long pos = h.pos + 1 + 4 + chunkSize;
          if (left < 5) {
            //pad left with FREEBYTEs
//            log("writting freebytes:" + left + "@" + Long.toString(raf.getFilePointer(), 16));
            for(int i=0;i<left;i++) {
              raf.writeByte(FREEBYTE);
              Header h3 = new Header();
              h3.type = FREEBYTE;
              h3.size = 1;
              h3.pos = pos++;
              a++;
              list.add(a, h3);
            }
          } else {
//            log("writting free@" + Long.toString(raf.getFilePointer(), 16));
            //create a FREE chunk
            Header h3 = new Header();
            h3.type = FREE;
            h3.size = left - 1 - 4;
            h3.pos = pos;
            raf.writeByte(h3.type);
            raf.writeInt(h3.size);
            list.add(a+1, h3);
          }
          return;
        }
      }
      //no free space, add a new chunk
//      log("save new chunk @ " + Long.toString(fileSize, 16));
      Header h = new Header();
      h.type = CHUNK;
      h.size = chunkSize;
      h.pos = fileSize;
      h.cx = cx;
      h.cz = cz;
      list.add(h);
      raf.seek(fileSize);
      raf.writeByte(h.type);
      raf.writeInt(h.size);
      raf.writeInt(h.cx);
      raf.writeInt(h.cz);
      raf.write(data);
      fileSize += 1 + 4 + chunkSize;
    } catch (Exception e) {
      Static.log(e);
    }
  }

  private static SerialCoder coder = new SerialCoder();  //used to read chunks only

  private static Chunk serialChunk = new Chunk(null);  //just for Chunk.create()

  public Chunk loadChunk(int cx, int cz) {
    try {
      int cnt = list.size();
      for(int a=0;a<cnt;a++) {
        Header h = list.get(a);
        if (h.type == CHUNK) {
          if (h.cx == cx && h.cz == cz) {
            //load chunk
            byte data[] = new byte[h.size - 2 * 4];
            raf.seek(h.pos + 1 + 3 * 4);
            raf.readFully(data);
            Chunk chunk = (Chunk)coder.decodeObject(data, serialChunk, true);
            //delete any players in chunk (should not happen)
            cnt = chunk.entities.size();
            for(a=0;a<cnt;) {
              if (chunk.entities.get(a).id == Entities.PLAYER) {
                chunk.entities.remove(a);
                cnt--;
              } else {
                a++;
              }
            }
            return chunk;
          }
        }
      }
    } catch (Exception e) {
      Static.log(e);
    }
    return null;
  }

  public void verify() {
    int cnt = list.size();
    try {
      for(int a=0;a<cnt;a++) {
        Header h = list.get(a);
        raf.seek(h.pos);
        byte type = raf.readByte();
        if (type != h.type) {
          Static.log("verify failed:type mismatch @ " + Long.toString(h.pos, 16));
          System.exit(0);
        }
        int size, cx, cz;
        switch (h.type) {
          case FREE: {
            size = raf.readInt();
            if (size != h.size) {
              Static.log("verify failed:size mismatch @ " + Long.toString(h.pos, 16));
              System.exit(0);
            }
            break;
          }
          case CHUNK: {
            size = raf.readInt();
            if (size != h.size) {
              Static.log("verify failed:size mismatch @ " + Long.toString(h.pos, 16));
              System.exit(0);
            }
            cx = raf.readInt();
            if (cx != h.cx) {
              Static.log("verify failed:cx mismatch @ " + Long.toString(h.pos, 16));
              System.exit(0);
            }
            cz = raf.readInt();
            if (cz != h.cz) {
              Static.log("verify failed:cz mismatch @ " + Long.toString(h.pos, 16));
              System.exit(0);
            }
            break;
          }
        }
      }
    } catch (Exception e) {
      Static.log(e);
    }
  }
}
