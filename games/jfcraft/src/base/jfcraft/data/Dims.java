package jfcraft.data;

/** Dimensions.
 *
 * @author pquiring
 */

import jfcraft.dim.*;

public class Dims {
  public static final int MAX_ID = 256;
  public int dimCount = 0;

  public DimBase regDims[] = new DimBase[MAX_ID];
  public DimBase dims[];

  public static int EARTH;  //always Zero
  public static int END;
  public static int NETHER;

  public void registerDimension(DimBase dim) {
    regDims[dimCount++] = dim;
  }

  public void orderDims() {
    dims = new DimBase[MAX_ID];
    for(int a=0;a<MAX_ID;a++) {
      DimBase db = regDims[a];
      if (db == null) continue;
      dims[db.id] = db;
    }
  }

  public void registerDefault() {
    registerDimension(new DimEarth());
    registerDimension(new DimEnd());
    registerDimension(new DimNether());
  }

  public void resetAll() {
    int p = 0;
    while (dims[p] != null) {
      dims[p].getGeneratorPhase1().reset();
      dims[p].getGeneratorPhase2().reset();
      dims[p].getGeneratorPhase3().reset();
      p++;
    }
  }
}
