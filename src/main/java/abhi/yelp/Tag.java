package abhi.yelp;

/**
 * Author : abhishek
 * Created on 3/21/16.
 */
public enum Tag {

   REVIEW_COUNT,
   VOTE_USEFUL,
   VOTE_FUNNY,
   VOTE_COOL,

   STAR_1,
   STAR_2,
   STAR_3,
   STAR_4,
   STAR_5,

   CAT_RES,
   CAT_SHO,
   CAT_SER,
   CAT_MED,
   CAT_ENT,
   CAT_BEA,
   CAT_REC,
   CAT_OTH,
   CAT_AME_RES,
   CAT_BAR_RES,
   CAT_CAF_RES,
   CAT_ETH_RES,
   CAT_FAS_RES,
   CAT_OTH_RES,
   CAT_DRU_SHO,
   CAT_FAS_SHO,
   CAT_FOO_SHO,
   CAT_HOU_SHO,
   CAT_OTH_SHO,
   CAT_AUT_SER,
   CAT_COM_SER,
   CAT_EDU_SER,
   CAT_EST_SER,
   CAT_HOT_SER,
   CAT_HOU_SER,
   CAT_OTH_SER,
   CAT_TRA_SER,
   CAT_DOC_MED,
   CAT_HOS_MED,
   CAT_ART_ENT,
   CAT_EVE_ENT,
   CAT_GAM_ENT,
   CAT_GRO_BEA,
   CAT_SPA_BEA,
   CAT_FIT_REC,
   CAT_SPO_REC,
   CAT_NAT_OTH,
   CAT_REG_OTH,

   COL_BROWN,
   COL_CALTECH,
   COL_CPSU,
   COL_CMU,
   COL_COLUMBIA,
   COL_CORNELL,
   COL_GATECH,
   COL_HARVARD,
   COL_HMU,
   COL_MIT,
   COL_PRINCETON,
   COL_PURDUE,
   COL_RPI,
   COL_RICE,
   COL_STANFORD,
   COL_UCLA,
   COL_UCSD,
   COL_UCBERKELY,
   COL_UIUC,
   COL_UMC,
   COL_UMA,
   COL_UMANN,
   COL_UNCC,
   COL_UPENN,
   COL_USC,
   COL_UTAUS,
   COL_UWASH,
   COL_WATERLOO,
   COL_VTECH;



   public static Tag getValue(String tagStr){
        for(Tag tag : Tag.values()){
            if(tag.toString().equalsIgnoreCase(tagStr)){
                return tag;
            }
        }
        return null;
    }

}
