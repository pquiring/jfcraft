package jfcraft.data;

/** Cache raw assets (images, audio, models)
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl.*;
import javaforce.gl.model.*;
import javaforce.media.*;

import jfcraft.audio.*;

public class Assets {
  private static ArrayList<Asset> images = new ArrayList<Asset>();
  private static ArrayList<Asset> audios = new ArrayList<Asset>();
  private static ArrayList<Asset> models = new ArrayList<Asset>();
  private static ArrayList<Asset> prints = new ArrayList<Asset>();
  private static ArrayList<Asset> songs = new ArrayList<Asset>();
  private static ArrayList<ZipFile> zips = new ArrayList<ZipFile>();

  private enum Type {IMAGE, AUDIO, MODEL, BLUEPRINT, MUSIC};

  public static void reset() {
    images.clear();
    audios.clear();
    models.clear();
    prints.clear();
    songs.clear();
    zips.clear();
  }

  public static boolean addZip(String filename) {
    if (!new File(filename).exists()) {
      Static.log("Plugin not found:" + filename);
      return false;
    }
    Static.log("addZIP:" + filename);
    try {
      ZipFile zf = new ZipFile(filename);
      zips.add(zf);
      return true;
    } catch (Exception e) {
      Static.log(e);
      return false;
    }
  }

  private static JFImage makeImage() {
    JFImage image = new JFImage(16,16);
    image.fill(0, 0, 16, 16, 0xffffff);
    image.fill(0, 0, 8, 8, 0xff0000);
    image.fill(8, 8, 8, 8, 0xff0000);
    return image;
  }

  private static Asset getAsset(String name, Type type, String filename) {
    if (name == null) {
      Static.logTrace("getAsset:name=null");
    }
    ArrayList<Asset> assets;
    switch (type) {
      case IMAGE: assets = images; break;
      case AUDIO: assets = audios; break;
      case MODEL: assets = models; break;
      case BLUEPRINT: assets = prints; break;
      case MUSIC: assets = songs; break;
      default: return null;
    }

    for(int a=0;a<assets.size();a++) {
      Asset asset = assets.get(a);
      if (asset.name == null) {
        Static.log("null asset name???");
        continue;
      }
      if (asset.name.equals(name)) return asset;
    }
    ZipEntry ze = null;
    InputStream is = null;
    String file = Static.getBasePath() + filename;
    //check %APPDATA%\.jfcraft\assets
    if (new File(file).exists()) {
      try {
        is = new FileInputStream(file);
      } catch (Exception e) {
        Static.log(e);
        is = null;
      }
    }
    if (is == null) {
      //search zip files
      for(int a=0;a<zips.size();a++) {
        ze = zips.get(a).getEntry(filename);
        if (ze != null) {
          try {
            is = zips.get(a).getInputStream(ze);
          } catch (Exception e) {
            Static.log(e);
            is = null;
            break;
          }
          break;
        }
      }
    }
    if (is == null) {
      //should not happen
      Static.log("Asset not found:" + filename);
      switch (type) {
        case IMAGE:
          AssetImage png = new AssetImage();
          png.name = name;
          png.image = makeImage();
          return png;
        case AUDIO:
          AssetAudio wav = new AssetAudio();
          wav.name = name;
          wav.samples = new short[0];
          return wav;
        case MODEL:
          AssetModel model = new AssetModel();
          model.name = name;
          model.model = new Model();
          return model;
        case BLUEPRINT:
          if (true) return null;  //do not return empty blueprint
          AssetBluePrint blueprint = new AssetBluePrint();
          blueprint.name = name;
          blueprint.blueprint = new BluePrint(name);
          return blueprint;
        case MUSIC:
          AssetMusic song = new AssetMusic();
          song.song = new Music.Song();
          return song;
      }
      return null;
    }
    switch (type) {
      case IMAGE:
        AssetImage png = new AssetImage();
        png.name = name;
        png.image = new JFImage();
        if (!png.image.loadPNG(is)) {
          Static.log("Asset load failed:" + filename);
          png.image = makeImage();
        }
        if (png.name == null) {
          Static.logTrace("getAsset:add name=null");
        }
        assets.add(png);
        return png;
      case AUDIO:
        AssetAudio audio = new AssetAudio();
        audio.name = name;
        if (filename.endsWith(".wav")) {
          Wav wav = new Wav();
          if (!wav.load(is)) {
            Static.log("Asset load failed:" + filename);
            audio.samples = new short[0];
          } else {
            wav.readAllSamples();
            audio.samples = wav.samples16;
          }
        } else if (filename.endsWith(".mp3")) {
          if (!Static.optionMedia) return null;
          MediaReader reader = new MediaReader();
          audio.samples = reader.read(is);
        }
        if (audio.name == null) {
          Static.logTrace("getAsset:add name=null");
        }
        assets.add(audio);
        return audio;
      case MODEL:
        AssetModel model = new AssetModel();
        model.name = name;
        if (filename.endsWith(".json")) {
          ModelJSON loader = new ModelJSON(16.0f);
          model.model = loader.load(is);
        } else {
          ModelJF3D loader = new ModelJF3D();
          model.model = loader.load(is);
        }
        assets.add(model);
        return model;
      case BLUEPRINT:
        AssetBluePrint print = new AssetBluePrint();
        print.name = name;
        print.blueprint = BluePrint.read(is);
        assets.add(print);
        return print;
      case MUSIC:
        AssetMusic music = new AssetMusic();
        music.song = Music.load(is);
        return music;
    }
    //TODO more formats (ie: ogg, mp3, etc.)
    return null;
  }

  private static boolean exists(String filename) {
    ZipEntry ze = null;
    String file = Static.getBasePath() + filename;
    //check %APPDATA%\.jfcraft\assets
    if (new File(file).exists()) {
      return true;
    }
    //search zip files
    for(int a=0;a<zips.size();a++) {
      ze = zips.get(a).getEntry(filename);
      if (ze != null) {
        return true;
      }
    }
    return false;
  }

  public static AssetImage getImage(String name) {
    String filename;
    filename = "assets/minecraft/textures/" + name + ".png";
    AssetImage asset = (AssetImage)getAsset(name, Type.IMAGE, filename);
    return asset;
  }

  public static AssetAudio getAudio(String name) {
    String filename;
    filename = "assets/minecraft/audio/" + name + ".mp3";
    if (!exists(filename)) {
      filename = "assets/minecraft/audio/" + name + ".wav";
    }
    AssetAudio asset = (AssetAudio)getAsset(name, Type.AUDIO, filename);
    return asset;
  }

  public static AssetModel getModel(String name) {
    String filename;
    filename = "assets/minecraft/models/" + name + ".json";
    if (!exists(filename)) {
      filename = "assets/minecraft/models/" + name + ".jf3d";
    }
    AssetModel asset = (AssetModel)getAsset(name, Type.MODEL, filename);
    return asset;
  }

  public static AssetBluePrint getBluePrint(String name) {
    String filename;
    filename = "assets/minecraft/blueprints/" + name + ".blueprint";
    AssetBluePrint asset = (AssetBluePrint)getAsset(name, Type.BLUEPRINT, filename);
    return asset;
  }

  public static AssetMusic getMusic(String name) {
    String filename;
    filename = "assets/minecraft/music/" + name + ".mproj";
    AssetMusic asset = (AssetMusic)getAsset(name, Type.MUSIC, filename);
    return asset;
  }
}
