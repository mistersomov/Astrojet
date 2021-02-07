package com.example.astrojet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartsConstants {

    //Ambient pressure, MPa
    public static final double P_ATM = 0.101325;
    //Universal gas constant, J/mol-K
    public static final int UNI_R = 8314;
    //Combustion efficiency
    public static final double ETA_C = 0.95;
    //Nozzle efficiency
    public static final double ETA_NOZZLE = 0.85;
    //Density ratio(actual/ideal)
    public static final double RAT_DENS = 0.95;
    //Propellant errosive burning area ratio threshold
    public static final int G_STAR = 6;

    public static final Map<String, List<Double>> PROP_DATA;
    static {
        Map<String, List<Double>> map = new HashMap<String, List<Double>>();
        map.put("KNDX", Arrays.asList(1.879, 1.043, 1.131, 42.39, 1710.0));
        map.put("KNSB", Arrays.asList(1.841, 1.042, 1.136, 39.9, 1600.0));
        map.put("KNSU", Arrays.asList(1.889, 1.044, 1.133, 41.98, 1720.0));
        map.put("KNER_coarse", Arrays.asList(1.82, 1.043, 1.139, 38.78, 1608.0));
        map.put("KNMN_coarse", Arrays.asList(1.854, 1.042, 1.136, 39.83, 1616.0));

        PROP_DATA = Collections.unmodifiableMap(map);
    }
    public static final Map<String, Integer> KN_MAX;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("KNDX", 320);
        map.put("KNSB_fine", 395);
        map.put("KNSB_coarse", 552);
        map.put("KNSU", 273);
        map.put("KNER_coarse", 687);
        map.put("KNMN_coarse", 552);

        KN_MAX = Collections.unmodifiableMap(map);
    }
    

}
