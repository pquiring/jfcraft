package jfcraft.data;

/** Cache raw assets (images, audio, models)
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javaforce.*;
import javaforce.gl.*;
import javaforce.media.*;

public class Assets {
  private static ArrayList<Asset> pngs = new ArrayList<Asset>();
  private static ArrayList<Asset> wavs = new ArrayList<Asset>();
  private static ArrayList<Asset> models = new ArrayList<Asset>();
  private static ArrayList<Asset> prints = new ArrayList<Asset>();
  private static ArrayList<ZipFile> zips = new ArrayList<ZipFile>();

  private enum Type {PNG, WAV, MODEL, BLUEPRINT};

  public static void reset() {
    pngs.clear();
    wavs.clear();
    models.clear();
    prints.clear();
    zips.clear();
  }

  public static boolean addZip(String filename) {
    if (!new File(filename).exists()) return false;
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
      case PNG: assets = pngs; break;
      case WAV: assets = wavs; break;
      case MODEL: assets = models; break;
      case BLUEPRINT: assets = prints; break;
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
    if (is == null) {
      //should not happen
      Static.log("Asset not found:" + filename);
      switch (type) {
        case PNG:
          AssetImage png = new AssetImage();
          png.name = name;
          png.image = makeImage();
          return png;
        case WAV:
          AssetAudio wav = new AssetAudio();
          wav.name = name;
          wav.wav = new Wav();
          wav.wav.samples16 = new short[0];
          return wav;
        case MODEL:
          AssetModel model = new AssetModel();
          model.name = name;
          model.model = new GLModel();
          return model;
        case BLUEPRINT:
          AssetBluePrint blueprint = new AssetBluePrint();
          blueprint.name = name;
          blueprint.blueprint = new BluePrint();
          return blueprint;
      }
      return null;
    }
    switch (type) {
      case PNG:
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
      case WAV:
        AssetAudio wav = new AssetAudio();
        wav.name = name;
        wav.wav = new Wav();
        if (!wav.wav.load(is)) {
          Static.log("Asset load failed:" + filename);
          wav.wav.samples16 = new short[0];
        } else {
          wav.wav.readAllSamples();
        }
        if (wav.name == null) {
          Static.logTrace("getAsset:add name=null");
        }
        assets.add(wav);
        return wav;
      case MODEL:
        AssetModel model = new AssetModel();
        model.name = name;
        GL_JF3D loader = new GL_JF3D();
        model.model = loader.load(is);
        assets.add(model);
        return model;
      case BLUEPRINT:
        AssetBluePrint print = new AssetBluePrint();
        print.name = name;
        print.blueprint = BluePrint.read(is);
        assets.add(print);
        return print;
    }
    //TODO more formats (ie: ogg, mp3, etc.)
    return null;
  }

  public static AssetImage getImage(String name) {
    String filename;
    filename = "assets/minecraft/textures/" + name + ".png";
    AssetImage asset = (AssetImage)getAsset(name, Type.PNG, filename);
    return asset;
  }

  public static AssetAudio getAudio(String name) {
    String filename;
    filename = "assets/minecraft/audio/" + name + ".wav";
    AssetAudio asset = (AssetAudio)getAsset(name, Type.WAV, filename);
    return asset;
  }

  public static AssetModel getModel(String name) {
    String filename;
    filename = "assets/minecraft/models/" + name + ".jf3d";
    AssetModel asset = (AssetModel)getAsset(name, Type.MODEL, filename);
    return asset;
  }

  public static AssetBluePrint getBluePrint(String name) {
    String filename;
    filename = "assets/minecraft/blueprints/" + name + ".blueprint";
    AssetBluePrint asset = (AssetBluePrint)getAsset(name, Type.BLUEPRINT, filename);
    return asset;
  }
}
