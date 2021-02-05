package com.example.juggler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Charts {

    //CONSTANTS

    //Ambient pressure, MPa
    private static final double P_ATM = 0.101325;
    //Universal gas constant, J/mol-K
    private static final int UNI_R = 8314;
    //Combustion efficiency
    private static final double ETA_C = 0.95;
    //Nozzle efficiency
    private static final double ETA_NOZZLE = 0.85;
    //Density ratio(actual/ideal)
    private static final double RAT_DENS = 0.95;

    //PROPELLANT OPTIONS
    //Described in Constructor
    private static final Map<String, List<Double>> PROP_DATA = new HashMap<String, List<Double>>();
    //Propellant errosive burning area ratio threshold
    private static final int G_STAR = 6;
    //Ratio of burning area / throat area(max) for 6.5MPa
    private static final Map<String, Integer> KN_MAX = new HashMap<String, Integer>();

    //ANALYSI'S PARAMS

    private static final int INTERVAL = 800;
    private static final double STEP = 0.0294;

    //TEXTFIELDS

    ////Chamber diameter(inside), mm
    private static double dc;
    //Chamber length(inside), mm
    private static double lc;
    //Number of propellant segments
    private static int numberOfSegments;
    //Core diameter(initial), mm
    private static double d0;
    //Outer diameter(initial), mm
    private static double Do;
    //Segment length(initial), mm
    private static double Lo;

    //Constructor1
    public Charts(double dc, double lc, int n, double d0, double _Do, double _Lo){
        this.dc = dc;
        this.lc = lc;
        this.numberOfSegments = n;
        this.d0 = d0;
        this.Do = _Do;
        this.Lo = _Lo;
		/*
			[0] - Grain ideal density, g/cm3,
			[1] - Ratio of specific heats, 2-ph,
			[2]  - Ratio of specific heat, mix,
			[3] - Effective molecular wt. of products, kg/kmol,
			[4] - Ideal combustion temperature, K
		*/
        this.PROP_DATA.put("KNDX", Arrays.asList(1.879, 1.043, 1.131, 42.39, 1710.0));
        this.PROP_DATA.put("KNSB", Arrays.asList(1.841, 1.042, 1.136, 39.9, 1600.0));
        this.PROP_DATA.put("KNSU", Arrays.asList(1.889, 1.044, 1.133, 41.98, 1720.0));
        this.PROP_DATA.put("KNER_coarse", Arrays.asList(1.82, 1.043, 1.139, 38.78, 1608.0));
        this.PROP_DATA.put("KNMN_coarse", Arrays.asList(1.854, 1.042, 1.136, 39.83, 1616.0));
        this.KN_MAX.put("KNDX", 320);
        this.KN_MAX.put("KNSB_fine", 395);
        this.KN_MAX.put("KNSB_coarse", 552);
        this.KN_MAX.put("KNSU", 273);
        this.KN_MAX.put("KNER_coarse", 687);
        this.KN_MAX.put("KNMN_coarse", 552);

    }
    //Constructor2
    public Charts(double dc){

        this.dc = dc;
    }


    public static Map<String, Object> pressureCalculating(){
        //Chamber volume, mm3
        double _Vc;
        //Actual chamber temperature, K
        double t0_act;
        //Grain actual density, g/cm3
        double rograin_actual;
        //Specific gas constant, J/kg-K
        double rat;
        //Characteristic exhaust velocity, m/s
        double cstar;
        //Grain length(initial), mm
        double _Lgo;
        //Grain volume(initial), mm3
        double _Vg;
        //Grain mass(initial), kg
        double mgrain;
        //End burning area(initial), mm2
        double _Abeo;
        //Core burning area, mm2
        double _Abco;
        //Total burning area, mm2
        double _Abo;
        //Throat cross-section area(initial), mm2
        double _At;
        double n, a;
        double c1, c2, c3, c4;
        double max = Double.MIN_VALUE;

        double[] x = new double[INTERVAL];
        double[] d = new double[INTERVAL];
        //List of Grain Web Thickness, mm
        double[] tweb = new double[INTERVAL];
        double[] _l = new double[INTERVAL];
        double[] abe = new double[INTERVAL];
        double[] abc = new double[INTERVAL];
        double[] ab_total = new double[INTERVAL];
        double[] p = new double[INTERVAL];
        double[] kn = new double[INTERVAL];
        //Propellant burn, mm/s
        double[] u = new double[INTERVAL];
        //Time since start of burning, s
        double[] t = new double[INTERVAL];
        Map<String, Object> results = new HashMap<String, Object>();

        _Vc = Math.PI / 4 * Math.pow(dc, 2) * lc;
        t0_act = ETA_C * PROP_DATA.get("KNDX").get(4);
        rograin_actual = PROP_DATA.get("KNDX").get(0) * RAT_DENS;
        rat = UNI_R / PROP_DATA.get("KNDX").get(3);
        cstar = Math.sqrt(rat * t0_act / (PROP_DATA.get("KNDX").get(2)
                * Math.pow((2 / (PROP_DATA.get("KNDX").get(2) + 1)),
                (PROP_DATA.get("KNDX").get(2) + 1) / (PROP_DATA.get("KNDX").get(2) - 1))));
        _Lgo = numberOfSegments * Lo;
        _Vg = Math.PI / 4 * (Math.pow(Do, 2) - Math.pow(d0, 2)) * _Lgo;
        mgrain = rograin_actual * _Vg / Math.pow(1000, 2);
        _Abeo = 2 * numberOfSegments * Math.PI / 4 * (Math.pow(Do, 2) - Math.pow(d0, 2));
        _Abco = numberOfSegments * Math.PI * d0 * Lo;
        _Abo = _Abeo + _Abco;
        d[0] = d0;
        _l[0] = _Lgo;
        for (int i = 1; i < x.length; i++){
            x[i] = x[i - 1] + STEP;
            d[i] = d[i - 1] + 2 * STEP;
            _l[i] = _l[i - 1] - 2 * numberOfSegments * STEP;
        }
        for (int i = 0; i < x.length; i++){
            tweb[i] = (Do - d[i]) / 2;
            abe[i] = 2 * numberOfSegments * Math.PI / 4 * (Math.pow(Do, 2) - Math.pow(d[i], 2));
            abc[i] = Math.PI * d[i] * _l[i];
            ab_total[i] = abe[i] + abc[i];
            max = Math.max(max, ab_total[i]);
        }
        _At = max / KN_MAX.get("KNDX");
        c1 = (PROP_DATA.get("KNDX").get(2) + 1 / PROP_DATA.get("KNDX").get(2) - 1);
        c3 = PROP_DATA.get("KNDX").get(2) / rat / t0_act;
        c4 = Math.pow(2 / (PROP_DATA.get("KNDX").get(2) + 1), c1);
        //p[0] = P_ATM;
        for (int i = 0; i < x.length; i++){
            if (p[i] <= 0.8){
                n = 0.619;
                a = 8.875;
            }else if (p[i] > 0.8 & p[i] <= 2.6){
                n = -0.009;
                a = 7.553;
            }else if (p[i] > 2.6 & p[i] <= 6){
                n = 0.688;
                a = 3.841;
            }else if (p[i] > 6 & p[i] <= 8.5){
                n = -0.148;
                a = 17.2;
            }else{
                n = 0.442;
                a = 4.775;
            }
            c2 = 1 / (1 - n);
            kn[i] = ab_total[i] / _At;
            u[i] = a * Math.pow(p[i], n);
            p[i] = Math.pow(kn[i] * a / Math.pow(1e6, n) * rograin_actual /
                    Math.sqrt(c3 * c4), c2) * 1e-6;
            System.out.println(u[i]);
        }
        for (int i = 1; i < x.length; i++){
            t[i] = STEP / u[i] + t[i - 1];
        }
        results.put("range", x);
        results.put("at", _At);
        results.put("pressure", p);
        results.put("time", t);

        return results;
    }
    public double tester(){
        return this.dc;
    }

}
