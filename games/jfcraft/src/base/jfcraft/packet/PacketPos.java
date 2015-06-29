package jfcraft.packet;

/** Packet with 6 Floats and 2 Ints
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.entity.*;
import jfcraft.item.*;
import jfcraft.audio.*;
import jfcraft.block.*;

public class PacketPos extends Packet {
  public float f1, f2, f3, f4, f5, f6;
  public int i1, i2;

  public PacketPos() {};

  public PacketPos(byte cmd) {
    super(cmd);
  }

  public PacketPos(byte cmd, float f1, float f2, float f3, float f4, float f5, float f6, int i1, int i2) {
    super(cmd);
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.f4 = f4;
    this.f5 = f5;
    this.f6 = f6;
    this.i1 = i1;
    this.i2 = i2;
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.player.offline) return;  //player in limbo
    int bits = i1;
    boolean up = (bits & Player.MOVE_UP) != 0;
    boolean dn = (bits & Player.MOVE_DN) != 0;
    boolean lt = (bits & Player.MOVE_LT) != 0;
    boolean rt = (bits & Player.MOVE_RT) != 0;
    boolean moving = up || dn || lt || rt;
    boolean jump = (bits & Player.JUMP) != 0;
    boolean sneak = (bits & Player.SNEAK) != 0;
    boolean run = (bits & Player.RUN) != 0;
    boolean b1 = (bits & Player.LT_BUTTON) != 0;
    boolean b2 = (bits & Player.RT_BUTTON) != 0;
    boolean fup = (bits & Player.FLY_UP) != 0;
    boolean fdn = (bits & Player.FLY_DN) != 0;
    client.player.ang.x = f4;
    client.player.ang.y = f5;
    client.player.ang.z = f6;
    Chunk chunk1 = client.player.getChunk();
    if (client.player.vehicle == null) {
      client.player.move(up, dn, lt, rt, jump, sneak, run, b1, b2, fup, fdn);
      float dx = Math.abs(client.player.pos.x - f1);
      float dy = Math.abs(client.player.pos.y - f2);
      float dz = Math.abs(client.player.pos.z - f3);
      if (dx > 0.1f || dy > 0.1f || dz > 0.1f) {
        Static.log("Error:client moved too far? " + dx + "," + dy + "," + dz + ":" + client.player.vehicle);
        Static.log("C=" + f1 + "," + f2 + "," + f3);
        Static.log("S=" + client.player.pos.x + "," + client.player.pos.y + "," + client.player.pos.z);
        if (false) {
          PacketMoveBack p2 = new PacketMoveBack(Packets.MOVEBACK, client.player.pos.x, client.player.pos.y, client.player.pos.z);
          client.serverTransport.addUpdate(p2);
          client.cheat++;
          if (false && client.cheat > 20) {
            client.serverTransport.close();
            server.removeClient(client);
            return;
          }
        } else {
          client.player.pos.x = f1;
          client.player.pos.y = f2;
          client.player.pos.z = f3;
        }
      } else {
        client.cheat = 0;
        client.player.pos.x = f1;
        client.player.pos.y = f2;
        client.player.pos.z = f3;
      }
    } else {
      VehicleBase veh = client.player.vehicle;
      if (veh != null) {
        veh.up = up;
        veh.dn = dn;
        veh.lt = lt;
        veh.rt = rt;
        veh.run = run;
        veh.sneak = sneak;
        veh.jump = jump;
      }
    }
    if (client.player.underWater) {
      if (client.underwaterCounter < 2 * 20) {
        client.underwaterCounter++;
      } else {
        client.underwaterCounter = 0;
        if (client.player.air > 0) {
          client.player.air--;
          client.serverTransport.sendAir(client.player);
        } else {
          client.player.takeDmg(2.0f, null);  //drowning
        }
      }
    } else {
      client.underwaterCounter = 0;
      if (client.player.air != 20) {
        client.player.air = 20;
        client.serverTransport.sendAir(client.player);
      }
    }
    if (chunk1 == null) return;
    Chunk chunk2 = client.player.getChunk();
    if (chunk2 == null) return;
    if (chunk1 != chunk2) {
      chunk1.delEntity(client.player);
      chunk2.addEntity(client.player);
    }
    if (client.player.vehicle == null) {
      server.broadcastEntityMove(client.player, false);
    }
    if (moving) {
      switch (client.player.mode) {
        case EntityBase.MODE_WALK:
          client.player.exhaustion += 0.01f * client.player.sneakSpeed / 20f;
          break;
        case EntityBase.MODE_SNEAK:
          client.player.exhaustion += 0.01f * client.player.walkSpeed / 20f;
          break;
        case EntityBase.MODE_RUN:
          client.player.exhaustion += 0.1f * client.player.runSpeed / 20f;
          break;
        case EntityBase.MODE_SWIM:
          client.player.exhaustion += 0.15f * client.player.swimSpeed / 20f;
          break;
      }
    }
    if (jump) {
      switch (client.player.mode) {
        case EntityBase.MODE_RUN:
          client.player.exhaustion += 0.8f;
          break;
        default:
          client.player.exhaustion += 0.2f;
          break;
      }
    }
    if (moving && client.player.onGround(0, 0, 0, (char)0)) {
      if (client.soundStep <= 0) {
        if (client.player.vehicle != null) {
          //TODO : play vehicle sound (horse gallops, etc.)
        } else {
          server.broadcastSound(client.player.dim, client.player.pos.x, client.player.pos.y, client.player.pos.z, Sounds.SOUND_STEP, 1);
        }
        client.soundStep = 10;  //do not play again for 1/2 sec
      } else {
        client.soundStep--;
      }
    } else {
      client.soundStep = 0;
    }
    if (b1) {
      client.player.findBlock(-1, BlockHitTest.Type.SELECTION, client.s1);
      if (client.s1 != null) {
        if (client.s1.block != null) {
          //break block
          if (client.s1.block.id == 0) return;
          synchronized(client.s1.chunk.lock) {
            float dmg = client.s1.block.dmg();
            if (client.crack.dmg > 0.0f && client.crack.x != client.s1.gx || client.crack.y != client.s1.gy || client.crack.z != client.s1.gz || client.crack_cx != client.s1.cx || client.crack_cz != client.s1.cz) {
              if (client.crack.dmg > 0.0f) {
                server.broadcastDelExtra(client.player.dim, client.crack_cx * 16 + client.crack.x, client.crack.y, client.crack_cz * 16 + client.crack.z, Extras.CRACK);
              }
              client.crack_cx = client.s1.cx;
              client.crack_cz = client.s1.cz;
              client.crack.x = (short)client.s1.gx;
              client.crack.y = (short)client.s1.gy;
              client.crack.z = (short)client.s1.gz;
              client.crack.dmg = dmg;
            } else {
              client.crack.dmg += dmg;
            }
            if (client.crack.dmg >= 100.0f) {
              client.player.exhaustion += 0.025f;
              client.crack.dmg = 0.0f;
              client.s1.block.destroy(client, client.s1, true);
              //??? might need broadcastDelCrack() if destroy() does not call broadcastSetBlock() ???
              server.broadcastSound(client.player.dim, client.player.pos.x, client.player.pos.y, client.player.pos.z, Sounds.SOUND_BREAK, 1);
            } else {
              server.broadcastAddCrack(client.s1.chunk.dim, client.s1.x, client.s1.y, client.s1.z, client.crack.dmg);
              client.crackTicks = 2;
            }
          }
        } else if (client.s1.entity != null) {
          //attack();
          client.action[0] = Client.ACTION_ATTACK;
          client.player.exhaustion += 0.3f;
          ((CreatureBase)client.s1.entity).takeDmg(client.player.calcDmg(client.player.items[client.activeSlot]), client.player);
        }
      }
    }
    else if (b2) {
      client.player.findBlock(-1, BlockHitTest.Type.SELECTION, client.s1);
      if (client.s1.block != null || client.s1.entity != null) {
        if (client.s1.block != null && client.s1.block.canUse && !sneak && client.action[1] != Client.ACTION_PLACE) {
          //useBlock();
          client.action[1] = Client.ACTION_USE_BLOCK;
          synchronized(client.lock) {
            client.s1.block.useBlock(client, client.s1);
          }
        } else if (client.s1.entity != null && client.s1.entity.canUse()) {
          client.action[1] = Client.ACTION_USE_ENTITY;
          client.s1.entity.useEntity(client, sneak);
        } else if (client.action[1] == Client.ACTION_PLACE || client.action[1] == Client.ACTION_IDLE || client.action[1] == Client.ACTION_USE_TOOL) {
          Item item = client.player.items[client.activeSlot];
          ItemBase itembase = Static.items.items[item.id];
          if (itembase.isTool || itembase.isWeapon || itembase.isFood) {
            //useTool();
            client.action[1] = Client.ACTION_USE_TOOL;
            synchronized(client.lock) {
              boolean used;
              if (client.s1.block != null) {
                used = client.s1.block.useTool(client, client.s1);
              } else if (client.s1.entity != null) {
                used = client.s1.entity.useTool(client, client.s1);
              } else {
                used = false;
              }
              if (!used) {
                itembase.useItem(client);
              }
            }
          } else {
            //placeBlock();
            client.action[1] = Client.ACTION_PLACE;
            if (client.placeCounter > 0) {
              client.placeCounter--;
              return;
            }
            client.placeCounter = 10;
            synchronized(client.lock) {
              if (item.id == Blocks.AIR) return;
              ItemBase itembase2;
              if (Static.isBlock(item.id)) {
                itembase2 = Static.blocks.blocks[item.id];
              } else {
                itembase2 = Static.items.items[item.id];
              }
              if (itembase2.canPlaceInWater) {
                client.player.findBlock(Blocks.WATER, BlockHitTest.Type.SELECTION, client.s2);
                if (client.s2.block != null) {
                  client.s1.copy(client.s2);
                }
              }
              synchronized(client.s1.chunk.lock) {
                if (itembase2.canPlace(client.s1) && server.blockClear(client.s1)) {
                  if (itembase2.isDir) {
                    client.player.getDir(client.s1);
                    client.s1.otherSide();
                  }
                  if (itembase2.isVar) {
                    client.s1.var = item.var;
                  }
                  if (!itembase2.place(client, client.s1)) return;
                  server.world.checkPowered(client.s1.chunk.dim, client.s1.x, client.s1.y, client.s1.z);
                  item.count--;
                  if (item.count == 0) {
                    item.clear();
                  }
                  client.serverTransport.setInvItem((client.activeSlot), item);
                  return;
                }
              }
              //try placing on adjacent block
              if (client.s1.dir == -1) return;  //no side available ???
              client.s1.otherSide();
              client.s1.adjacentBlock();
              client.s1.otherSide();
              synchronized(client.s1.chunk.lock) {
                if (itembase2.canPlace(client.s1) && server.blockClear(client.s1)) {
                  if (itembase2.isDir) {
                    client.player.getDir(client.s1);
                    client.s1.otherSide();
                  }
                  if (itembase2.isVar) {
                    client.s1.var = item.var;
                  }
                  if (!itembase2.place(client, client.s1)) return;
                  server.world.checkPowered(client.s1.chunk.dim, client.s1.x, client.s1.y, client.s1.z);
                  item.count--;
                  if (item.count == 0) {
                    item.clear();
                  }
                  client.serverTransport.setInvItem((client.activeSlot), item);
                }
              }
            }
          }
        }
      } else {
        //use item in the air
        ItemBase itembase3 = Static.items.items[client.player.items[client.activeSlot].id];
        if (itembase3.isTool || itembase3.isWeapon || itembase3.isFood) {
          client.action[1] = Client.ACTION_USE_TOOL;
          itembase3.useItem(client);
        }
      }
    } else {
      if (client.placeCounter > 0) {
        client.placeCounter = 0;
      }
      client.action[0] = Client.ACTION_IDLE;
      client.action[1] = Client.ACTION_IDLE;
      ItemBase item = Static.items.items[client.player.items[client.activeSlot].id];
      item.releaseItem(client);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    buffer.writeFloat(f1);
    buffer.writeFloat(f2);
    buffer.writeFloat(f3);
    buffer.writeFloat(f4);
    buffer.writeFloat(f5);
    buffer.writeFloat(f6);
    buffer.writeInt(i1);
    buffer.writeInt(i2);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    f1 = buffer.readFloat();
    f2 = buffer.readFloat();
    f3 = buffer.readFloat();
    f4 = buffer.readFloat();
    f5 = buffer.readFloat();
    f6 = buffer.readFloat();
    i1 = buffer.readInt();
    i2 = buffer.readInt();
    return true;
  }
}
