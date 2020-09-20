package com.adamjrehm.radarsim.planes;

public final class FPLType {

    public static final FPLType VFR = new FPLType(0);
    public static final FPLType IFR = new FPLType(1);

    public final int type;

    public FPLType(int type) {
        this.type = type;
    }

    public boolean equals(FPLType t) {
        if (type == t.type)
            return true;
        return false;
    }
}
