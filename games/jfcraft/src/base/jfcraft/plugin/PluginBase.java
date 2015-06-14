package jfcraft.plugin;

/** Base for all plugins.
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.dim.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.recipe.*;
import jfcraft.packet.*;
import jfcraft.opengl.*;

public abstract class PluginBase {
  public void registerBlock(BlockBase block) {
    Static.blocks.registerBlock(block);
  }
  public void registerItem(ItemBase item) {
    Static.items.registerItem(item);
  }
  public void registerEntity(EntityBase entity) {
    Static.entities.registerEntity(entity);
  }
  public void registerRecipe(Recipe recipe) {
    Static.recipes.registerRecipe(recipe);
  }
  public void registerSound(int id, AssetAudio sound) {
    Static.audio.registerSound(id, sound);
  }
  public void registerPacket(Packet packet, String name, Class cls) {
    Static.packets.registerPacket(packet, name, cls);
  }
  public void registerScreen(RenderScreen screen) {
    Static.screens.registerScreen(screen);
  }
  public void registerDimension(DimBase dim) {
    Static.dims.registerDimension(dim);
  }
  public abstract String getName();
  public abstract String getVersion();
  public abstract String getDesc();
  public abstract void registerPlugin();
}
