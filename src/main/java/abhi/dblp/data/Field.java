package abhi.dblp.data;

/**
 * Author : abhishek
 * Created on 3/10/16.
 */
public enum Field {

    CS,
    BIO,
    MECH,
    MATH,
    GEO,
    CHEM,
    ECE,
    SIM,
    STATS,
    OTH,
    ERP,
    AI,
    PHY,
    ICS,
    OR,
    HRS,
    SS,
    DSM,
    BS;

    public static Field getValue(String fieldStr){
        for(Field field : Field.values()){
            if(field.toString().equalsIgnoreCase(fieldStr)){
                return field;
            }
        }
        return null;
    }
}
