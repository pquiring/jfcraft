  package jfcraft.data;

/**
 * Exported Region (BluePrints).
 *   Similar to .schematic for MC.
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;

import javaforce.JF;

import jfcraft.entity.EntityBase;

public class BluePrint implements SerialClass, SerialCreator {
  private char blocks[][];  //type:16
  private byte bits[][];  //dir:4 var:4
  private char blocks2[][];  //type:16
  private byte bits2[][];  //dir:4 var:4

  public int X, Y, Z;

  private static SerialBuffer buffer = new SerialBuffer();
  private static SerialCoder coder = new SerialCoder();
  private static SerialCreator creator = new SerialCreator() {
      public SerialClass create(SerialBuffer buffer) {
        return new BluePrint();
      }
    };

  public ArrayList<EntityBase> entities = new ArrayList<EntityBase>();
  public ArrayList<ExtraBase> extras = new ArrayList<ExtraBase>();

  //id mapping
  public ArrayList<String> blockMap = new ArrayList<String>();  //0-32767
  public ArrayList<String> itemMap = new ArrayList<String>();  //32768-65535
  public ArrayList<String> entityMap = new ArrayList<String>();
  public ArrayList<String> extraMap = new ArrayList<String>();

  public String missingID;

  public boolean convertIDs(World world) {
    char blockIDs[] = new char[blockMap.size()];
    char itemIDs[] = new char[itemMap.size()];
    int entityIDs[] = new int[entityMap.size()];
    byte extraIDs[] = new byte[extraMap.size()];

    int bcnt1 = blockIDs.length;
    int bcnt2 = world.blockMap.size();
    for(int a=0;a<bcnt1;a++) {
      String name = blockMap.get(a);
      boolean ok = false;
      for(int b=0;b<bcnt2;b++) {
        if (world.blockMap.get(b).equals(name)) {
          blockIDs[a] = (char)b;
          ok = true;
          break;
        }
      }
      if (!ok) {
        missingID = name;
        return false;
      }
    }

    int icnt1 = itemIDs.length;
    int icnt2 = world.itemMap.size();
    for(int a=0;a<icnt1;a++) {
      String name = itemMap.get(a);
      boolean ok = false;
      for(int b=0;b<icnt2;b++) {
        if (world.itemMap.get(b).equals(name)) {
          itemIDs[a] = (char)b;
          ok = true;
          break;
        }
      }
      if (!ok) {
        missingID = name;
        return false;
      }
    }

    int ecnt1 = entityIDs.length;
    int ecnt2 = world.entityMap.size();
    for(int a=0;a<ecnt1;a++) {
      String name = entityMap.get(a);
      boolean ok = false;
      for(int b=0;b<ecnt2;b++) {
        if (world.entityMap.get(b).equals(name)) {
          entityIDs[a] = b;
          ok = true;
          break;
        }
      }
      if (!ok) {
        missingID = name;
        return false;
      }
    }

    int xcnt1 = extraIDs.length;
    int xcnt2 = world.extraMap.size();
    for(int a=0;a<xcnt1;a++) {
      String name = extraMap.get(a);
      boolean ok = false;
      for(int b=0;b<xcnt2;b++) {
        if (world.extraMap.get(b).equals(name)) {
          extraIDs[a] = (byte)b;
          ok = true;
          break;
        }
      }
      if (!ok) {
        missingID = name;
        return false;
      }
    }

    //convert block IDs
    for(int y=0;y<Y;y++) {
      if (blocks[y] != null) {
        int idx = 0;
        char b[] = blocks[y];
        for(int z=0;z<Z;z++) {
          for(int x=0;x<X;x++) {
            b[idx] = blockIDs[b[idx]];
            idx++;
          }
        }
      }
      if (blocks2[y] != null) {
        int idx = 0;
        char b[] = blocks2[y];
        for(int z=0;z<Z;z++) {
          for(int x=0;x<X;x++) {
            b[idx] = blockIDs[b[idx]];
            idx++;
          }
        }
      }
    }

    //convert extra IDs (and their contents if instanceof ExtraContainer)
    int xcnt = extras.size();
    for(int a=0;a<xcnt;a++) {
      ExtraBase extra = extras.get(a);
      extra.id = extraIDs[extra.id];
      extra.convertIDs(blockIDs, itemIDs);
    }
    //convert entities (and any items they are carrying)
    int ecnt = entities.size();
    for(int a=0;a<ecnt;a++) {
      EntityBase entity = entities.get(a);
      entity.id = extraIDs[entity.id];
      entity.convertIDs(blockIDs, itemIDs);
    }

    return true;
  }

  public void readInit(int w,int h,int d, World world) {
    Static.log("initRead:" + w + "," + h + "," + d);
    X = w;
    Y = h;
    Z = d;
    blocks = new char[h][];
    bits = new byte[h][];
    blocks2 = new char[h][];
    bits2 = new byte[h][];
    //copy world id mapping
    blockMap.addAll(world.blockMap);
    itemMap.addAll(world.itemMap);
    entityMap.addAll(world.entityMap);
    extraMap.addAll(world.extraMap);
  }

  public void setID(int x,int y,int z, char id) {
    if (blocks[y] == null) {
      blocks[y] = new char[X * Z];
    }
    blocks[y][z * X + x] = id;
  }

  public void setBits(int x,int y,int z, int val) {
    if (bits[y] == null) {
      bits[y] = new byte[X * Z];
    }
    bits[y][z * X + x] = (byte)val;
  }

  public void setID2(int x,int y,int z, char id) {
    if (blocks2[y] == null) {
      blocks2[y] = new char[X * Z];
    }
    blocks2[y][z * X + x] = id;
  }

  public void setBits2(int x,int y,int z, int val) {
    if (bits2[y] == null) {
      bits2[y] = new byte[X * Z];
    }
    bits2[y][z * X + x] = (byte)val;
  }

  public void readChunk(Chunk chunk, int sx, int sy, int sz, int dx, int dy, int dz, int w, int h, int d) {
    int cnt = 0;
    Static.log("readChunk ?:" + w + "," + h + "," + d);
    Static.log("readChunk s:" + sx + "," + sy + "," + sz);
    Static.log("readChunk d:" + dx + "," + dy + "," + dz);
    synchronized(chunk.lock) {
      for(int y=0;y<h;y++) {
        for(int z=0;z<d;z++) {
          for(int x=0;x<w;x++) {
            char id = chunk.getID(x+sx,y+sy,z+sz);
            if (id != 0) {
              setID(x+dx,y+dy,z+dz, id);
              setBits(x+dx,y+dy,z+dz, chunk.getBits(x+sx,y+sy,z+sz));
              cnt++;
            }
            char id2 = chunk.getID2(x+sx,y+sy,z+sz);
            if (id2 != 0) {
              setID2(x+dx,y+dy,z+dz, id2);
              setBits2(x+dx,y+dy,z+dz, chunk.getBits2(x+sx,y+sy,z+sz));
              cnt++;
            }
          }
        }
      }
      Static.log("read blocks:" + cnt);
      float cx = chunk.cx * 16f;
      float cz = chunk.cz * 16f;
      float sx1 = sx + cx;
      float sy1 = sy;
      float sz1 = sz + cz;
      float sx2 = sx + w - 1 + cx;
      float sy2 = sy + h - 1;
      float sz2 = sz + d - 1 + cz;
      synchronized(buffer) {
        //copy entities in range
        int ecnt = chunk.entities.size();
        for(int a=0;a<ecnt;a++) {
          EntityBase e = chunk.entities.get(a);
          float x = e.pos.x;
          float y = e.pos.y;
          float z = e.pos.z;
          if (x >= sx1 && x <= sx2) {
            if (y >= sy1 && x <= sy2) {
              if (z >= sz1 && x <= sz2) {
                buffer.reset();
                e.write(buffer, true);
                buffer.rewind();
                e = (EntityBase)Static.entities.create(buffer);
                if (e != null) {
                  e.read(buffer, true);
                  e.pos.x -= sx + cx;
                  e.pos.y -= sy;
                  e.pos.z -= sz + cz;
                  e.pos.x += dx;
                  e.pos.y += dy;
                  e.pos.z += dz;
                  entities.add(e);
                }
              }
            }
          }
        }
        //copy extras in range
        int xcnt = chunk.extras.size();
        for(int a=0;a<xcnt;a++) {
          ExtraBase e = chunk.extras.get(a);
          short x = e.x;
          short y = e.y;
          short z = e.z;
          if (x >= sx1 && x <= sx2) {
            if (y >= sy1 && x <= sy2) {
              if (z >= sz1 && x <= sz2) {
                buffer.reset();
                e.write(buffer, true);
                buffer.rewind();
                e = (ExtraBase)Static.extras.create(buffer);
                if (e != null) {
                  e.read(buffer, true);
                  e.x -= sx;
                  e.y -= sy;
                  e.z -= sz;
                  e.x += sx;
                  e.y += sy;
                  e.z += sz;
                  extras.add(e);
                }
              }
            }
          }
        }
      }
    }
  }

  public char getID(int x,int y,int z) {
    if (blocks[y] == null) return 0;
    return blocks[y][z * X + x];
  }

  public byte getBits(int x,int y,int z) {
    if (bits[y] == null) return 0;
    return bits[y][z * X + x];
  }

  public char getID2(int x,int y,int z) {
    if (blocks2[y] == null) return 0;
    return blocks2[y][z * X + x];
  }

  public byte getBits2(int x,int y,int z) {
    if (bits2[y] == null) return 0;
    return bits2[y][z * X + x];
  }

  public void writeChunk(Chunk chunk, int sx, int sy, int sz, int dx, int dy, int dz, int w, int h, int d) {
    int cnt = 0;
    Static.log("writeChunk ?:" + w + "," + h + "," + d);
    Static.log("writeChunk s:" + sx + "," + sy + "," + sz);
    Static.log("writeChunk d:" + dx + "," + dy + "," + dz);
    synchronized(chunk.lock) {
      for(int y=0;y<h;y++) {
        for(int z=0;z<d;z++) {
          for(int x=0;x<w;x++) {
            char id = getID(x+sx,y+sy,z+sz);
            if (id != 0) {
              chunk.setBlock(x+dx,y+dy,z+dz, id, getBits(x+sx,y+sy,z+sz));
              cnt++;
            }
            char id2 = getID2(x+sx,y+sy,z+sz);
            if (id2 != 0) {
              chunk.setBlock(x+dx,y+dy,z+dz, id2, getBits2(x+sx,y+sy,z+sz));
              cnt++;
            }
          }
        }
      }
      float cx = chunk.cx * 16f;
      float cz = chunk.cz * 16f;
      float sx1 = sx;
      float sy1 = sy;
      float sz1 = sz;
      float sx2 = sx + w - 1;
      float sy2 = sy + h - 1;
      float sz2 = sz + d - 1;
      //copy entities in range
      int ecnt = entities.size();
      for(int a=0;a<ecnt;a++) {
        EntityBase e = entities.get(a);
        float x = e.pos.x;
        float y = e.pos.y;
        float z = e.pos.z;
        if (x >= sx1 && x <= sx2) {
          if (y >= sy1 && x <= sy2) {
            if (z >= sz1 && x <= sz2) {
              e.pos.x -= sx;
              e.pos.y -= sy;
              e.pos.z -= sz;
              e.pos.x += dx + cx;
              e.pos.y += dy;
              e.pos.z += dz + cz;
              chunk.entities.add(e);
            }
          }
        }
      }
      //copy extras in range
      int xcnt = extras.size();
      for(int a=0;a<xcnt;a++) {
        ExtraBase e = extras.get(a);
        short x = e.x;
        short y = e.y;
        short z = e.z;
        if (x >= sx1 && x <= sx2) {
          if (y >= sy1 && x <= sy2) {
            if (z >= sz1 && x <= sz2) {
              e.x -= sx;
              e.y -= sy;
              e.z -= sz;
              e.x += dx;
              e.y += dy;
              e.z += dz;
              chunk.extras.add(e);
            }
          }
        }
      }
    }
    Static.log("write to chunk:" + cnt);
  }

  public static BluePrint read(String filename) {
    synchronized(coder) {
      try {
        FileInputStream fis = new FileInputStream(filename);
        byte data[] = JF.readAll(fis);
        fis.close();
        BluePrint xchunk = (BluePrint)coder.decodeObject(data, creator, true);
        return xchunk;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  public boolean save(String filename) {
    synchronized(coder) {
      byte data[] = coder.encodeObject(this, true);
      try {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(data);
        fos.close();
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }
  }

  public void mirrorX() {}

  public void mirrorZ() {}

  /** Rotates blueprints.
   * @param ang must be multiple of 90 degrees [90, 180, 270].
   */
  public void rotateY(float ang) {
    if (ang == 0f) return;
    switch ((int)ang) {
      case 90:
      case 180:
      case 270:
        break;
    }
  }

  private static final byte ver = 0;

  private static final int magic = 0x12345678;

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    {
      buffer.writeByte(ver);

      buffer.writeInt(X);
      buffer.writeInt(Y);
      buffer.writeInt(Z);

      //write ID assignments
      //blocks
      int blockSize = blockMap.size();
      buffer.writeInt(blockSize);
      for(int a=0;a<blockSize;a++) {
        String name = blockMap.get(a);
        buffer.writeByte((byte)name.length());
        buffer.writeBytes(name.getBytes());
      }
      //items
      int itemSize = itemMap.size();
      buffer.writeInt(itemSize);
      for(int a=0;a<itemSize;a++) {
        String name = itemMap.get(a);
        buffer.writeByte((byte)name.length());
        buffer.writeBytes(name.getBytes());
      }
      //entities
      int entitySize = entityMap.size();
      buffer.writeInt(entitySize);
      for(int a=0;a<entitySize;a++) {
        String name = entityMap.get(a);
        buffer.writeByte((byte)name.length());
        buffer.writeBytes(name.getBytes());
      }
      //extras
      int extraSize = extraMap.size();
      buffer.writeInt(extraSize);
      for(int a=0;a<extraSize;a++) {
        String name = extraMap.get(a);
        buffer.writeByte((byte)name.length());
        buffer.writeBytes(name.getBytes());
      }

      //blocks
      int cnt1 = 0;
      for(int a=0;a<Y;a++) {
        if (blocks[a] == null) continue;
        cnt1++;
      }
      buffer.writeShort((short)cnt1);
      for(int a=0;a<Y;a++) {
        if (blocks[a] == null) continue;
        buffer.writeByte((byte)a);
        buffer.writeChars(blocks[a]);
      }
      //bits
      int cnt3 = 0;
      for(int a=0;a<Y;a++) {
        if (bits[a] == null) continue;
        cnt3++;
      }
      buffer.writeShort((short)cnt3);
      for(int a=0;a<Y;a++) {
        if (bits[a] == null) continue;
        buffer.writeByte((byte)a);
        buffer.writeBytes(bits[a]);
      }
      //blocks2
      int cnt5 = 0;
      for(int a=0;a<Y;a++) {
        if (blocks2[a] == null) continue;
        cnt5++;
      }
      buffer.writeShort((short)cnt5);
      for(int a=0;a<Y;a++) {
        if (blocks2[a] == null) continue;
        buffer.writeByte((byte)a);
        buffer.writeChars(blocks2[a]);
      }
      //bits2
      int cnt7 = 0;
      for(int a=0;a<Y;a++) {
        if (bits2[a] == null) continue;
        cnt7++;
      }
      buffer.writeShort((short)cnt7);
      for(int a=0;a<Y;a++) {
        if (bits2[a] == null) continue;
        buffer.writeByte((byte)a);
        buffer.writeBytes(bits2[a]);
      }

      //entities
      int entity_size = entities.size();
      buffer.writeInt(entity_size);
      int cid = 1;
      for(int a=0;a<entity_size;a++) {
        entities.get(a).cid = cid++;
      }
      for(int a=0;a<entity_size;a++) {
        entities.get(a).write(buffer, file);
      }
      //extra data
      int extra_size = extras.size();
      buffer.writeInt(extra_size);
      for(int a=0;a<extra_size;a++) {
        extras.get(a).write(buffer, file);
      }

      //future stuff here

      buffer.writeInt(magic);
    }
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    byte ver = buffer.readByte();

    X = buffer.readInt();
    Y = buffer.readInt();
    Z = buffer.readInt();

    blocks = new char[Y][];
    bits = new byte[Y][];
    blocks2 = new char[Y][];
    bits2 = new byte[Y][];

    //read ID assignments
    //blocks
    int blockCnt = buffer.readInt();
    if (blockCnt > 32767) return false;
    for(int a=0;a<blockCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      blockMap.add(new String(name));
    }
    //items
    int itemCnt = buffer.readInt();
    if (itemCnt > 32767) return false;
    for(int a=0;a<itemCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      itemMap.add(new String(name));
    }
    //entities
    int entityCnt = buffer.readInt();
    if (entityCnt > 65535) return false;
    for(int a=0;a<entityCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      entityMap.add(new String(name));
    }
    //extras
    int extraCnt = buffer.readInt();
    if (extraCnt > 128) return false;
    for(int a=0;a<extraCnt;a++) {
      int sl = buffer.readByte() & 0xff;
      byte name[] = new byte[sl];
      buffer.readBytes(name);
      extraMap.add(new String(name));
    }

    //blocks
    int idx;
    int cnt1 = buffer.readShort();
    for(int a=0;a<cnt1;a++) {
      idx = buffer.readByte() & 0xff;
      blocks[idx] = new char[X*Z];
      buffer.readChars(blocks[idx]);
    }
    //bits
    int cnt3 = buffer.readShort();
    for(int a=0;a<cnt3;a++) {
      idx = buffer.readByte() & 0xff;
      bits[idx] = new byte[X*Z];
      buffer.readBytes(bits[idx]);
    }
    //blocks2
    int cnt5 = buffer.readShort();
    for(int a=0;a<cnt5;a++) {
      idx = buffer.readByte() & 0xff;
      blocks2[idx] = new char[X*Z];
      buffer.readChars(blocks2[idx]);
    }
    //bits2
    int cnt7 = buffer.readShort();
    for(int a=0;a<cnt7;a++) {
      idx = buffer.readByte() & 0xff;
      bits2[idx] = new byte[X*Z];
      buffer.readBytes(bits2[idx]);
    }

    //entities
    int entity_size;
    entity_size = buffer.readInt();
    for(int a=0;a<entity_size;a++) {
      EntityBase eb = (EntityBase)Static.entities.create(buffer);
      if (eb != null) {
        eb.read(buffer, file);
        entities.add(eb);
      }
    }
    entity_size = entities.size();
    for(int a=0;a<entity_size;a++) {
      EntityBase eb = entities.get(a);
//      eb.setupLinks(this, file);
    }
    //extra data
    int extra_size = buffer.readInt();
    for(int a=0;a<extra_size;a++) {
      ExtraBase eb = (ExtraBase)Static.extras.create(buffer);
      if (eb != null) {
        eb.read(buffer, file);
        extras.add(eb);
      }
    }

    if (ver > 0) {
      //future stuff
    }

    int test = buffer.readInt();
    if (test != magic) {
      Static.log("BluePrint corrupt !!!");
      return false;
    }
    return true;
  }

  @Override
  public SerialClass create(SerialBuffer buffer) {
    return new BluePrint();
  }
}
