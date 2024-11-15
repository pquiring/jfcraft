package jfcraft.data;

/**
 * WorldOptions
 */

public class WorldOptions implements SerialClass, SerialCreator {
  public long seed;
  public boolean doSteps;
  public boolean doGrassBank;
  public boolean doFlatWorld;

  private static final int SEED = 1;
  private static final int STEPS = 2;
  private static final int GRASSBANK = 3;
  private static final int FLATWORLD = 4;
  private static final int END = 0;

  public boolean write(SerialBuffer buffer, boolean file) {
    buffer.writeInt(SEED);
    buffer.writeInt(8);
    buffer.writeLong(seed);
    buffer.writeInt(STEPS);
    buffer.writeInt(1);
    buffer.writeBoolean(doSteps);
    buffer.writeInt(GRASSBANK);
    buffer.writeInt(1);
    buffer.writeBoolean(doGrassBank);
    buffer.writeInt(FLATWORLD);
    buffer.writeInt(1);
    buffer.writeBoolean(doFlatWorld);
    buffer.writeInt(END);
    buffer.writeInt(0);
    return true;
  }

  public boolean read(SerialBuffer buffer, boolean file) {
    out:
    do {
      int type = buffer.readInt();
      int length = buffer.readInt();
      switch (type) {
        case SEED: seed = buffer.readLong(); break;
        case STEPS: doSteps = buffer.readBoolean(); break;
        case GRASSBANK: doGrassBank = buffer.readBoolean(); break;
        case FLATWORLD: doFlatWorld = buffer.readBoolean(); break;
        case END: break out;
        default: buffer.readBytes(new byte[length]); break;
      }
    } while (true);
    return true;
  }

  public SerialClass create(SerialBuffer buffer) {
    return new WorldOptions();
  }

}
