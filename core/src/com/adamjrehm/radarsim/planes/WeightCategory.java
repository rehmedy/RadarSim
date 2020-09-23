package com.adamjrehm.radarsim.planes;

import com.adamjrehm.radarsim.geography.Intersection;
import com.adamjrehm.radarsim.geography.Runway;

public enum WeightCategory {

    CAT_I, CAT_I_FAST, CAT_II_SLOW, CAT_II, CAT_III, CAT_III_HEAVY;

    public Intersection[] getDefaultLandingPath(Runway rwy) {
        if (this == CAT_I || this == CAT_II_SLOW) {
            if (rwy == Runway.R28L)
                return new Intersection[]{Intersection.R28L_D, Intersection.R28R_HS_SOUTH_D};
            if (rwy == Runway.R28R)
                return new Intersection[]{Intersection.R28R_D, Intersection.R28R_HS_NORTH_D};
        } else if (this == CAT_I_FAST || this == CAT_II || this == CAT_III) {
            if (rwy == Runway.R28L)
                return new Intersection[]{Intersection.R28L_E, Intersection.R28R_HS_SOUTH_E};
            if (rwy == Runway.R28R)
                return new Intersection[]{Intersection.R28R_E, Intersection.R28R_HS_NORTH_E};
        } else if (this == CAT_III_HEAVY) {
            return new Intersection[]{Intersection.R28R_F, Intersection.R28R_HS_NORTH_F};
        }
        return null;
    }
}
