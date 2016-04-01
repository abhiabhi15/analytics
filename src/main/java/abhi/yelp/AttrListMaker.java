package abhi.yelp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Author : abhishek
 * Created on 3/19/16.
 */
public class AttrListMaker {

    Map<String, List<String>> categoryTagMap = new HashMap<>();
    Map<String, String> universityTagMap = new HashMap<>();
    Map<String, Integer> userIdMap = new LinkedHashMap<>();

    Map<String, List<String>> businessCategory = new HashMap<>();
    Map<String, List<String>> businessSchools = new HashMap<>();
    Map<Integer, Map<Tag, Integer>> userCumDataMap = new HashMap<>();

    Boolean cumulative = false;
    int currYear;
    static int count = 0;

    private void init() {
        System.out.println("Init Fetching User Ids and Category Tags");
        BufferedReader br = null;
        try{
            String sCurrentLine = null;
            // Read UserID
            br = new BufferedReader(new FileReader(YelpConstants.FILE_PATH + "yelp_user.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] uids = sCurrentLine.split("\t");
                userIdMap.put(uids[0], Integer.valueOf(uids[1]));
            }

            // Read Categories
            br = new BufferedReader(new FileReader(YelpConstants.FILE_PATH + "yelp_tags.tsv"));
            br.readLine();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] catTokens = sCurrentLine.split("\t");
                List<String> categories = new ArrayList<>();
                categories.add(catTokens[2]);
                if(catTokens.length > 3){
                    categories.add(catTokens[3]);
                }
                categoryTagMap.put(catTokens[0], categories);
            }

            // Read Universities
            br = new BufferedReader(new FileReader(YelpConstants.FILE_PATH + "yelp_university.tsv"));
            br.readLine();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] uniTokens = sCurrentLine.split("\t");
                universityTagMap.put(uniTokens[0], uniTokens[1]);
            }

            //Load Business Tags
            loadBusinessTags();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadBusinessTags() {
        System.out.println("Loading Business Tags");
        BufferedReader br = null;
        JSONParser parser = new JSONParser();
        try{
            String sCurrentLine = null;
            br = new BufferedReader(new FileReader(YelpConstants.FILENAME));
            while ((sCurrentLine = br.readLine()) != null) {
                Object obj = parser.parse(sCurrentLine);
                JSONObject jsonObject = (JSONObject) obj;
                String type = (String) jsonObject.get("type");
                if (type.equalsIgnoreCase("business")){

                    count++;
                    if (count % 1000 == 0){
                        System.out.println("Business Processed = " + count);
                    }
                    String businessId = (String) jsonObject.get("business_id");
                    JSONArray categories = (JSONArray) jsonObject.get("categories");
                    Iterator itr = categories.iterator();
                    List<String> categoryList = new ArrayList<>();
                    while (itr.hasNext()) {
                        categoryList.add((String) itr.next());
                    }
                    businessCategory.put(businessId, categoryList);

                    JSONArray varsities = (JSONArray) jsonObject.get("schools");
                    itr = varsities.iterator();
                    List<String> varsityList = new ArrayList<>();
                    while (itr.hasNext()) {
                        varsityList.add((String) itr.next());
                    }
                    businessSchools.put(businessId, varsityList);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void populateNonCumUserMaps(){
        System.out.println("Populating Non cumulative User Maps");
        JSONParser parser = new JSONParser();
        BufferedReader br = null;
        count = 0;
        JSONObject jsonObject = null;
        try{
            String sCurrentLine = null;
            br = new BufferedReader(new FileReader(YelpConstants.FILENAME));
            while ((sCurrentLine = br.readLine()) != null) {
                Object obj = parser.parse(sCurrentLine);
                jsonObject = (JSONObject) obj;
                String type = (String) jsonObject.get("type");
                if (type.equalsIgnoreCase("review")){
                    count++;
                    if (count % 20000 == 0){
                        System.out.println("Review Processed = " + count);
                    }

                    String date = (String) jsonObject.get("date");
                    Integer year = Integer.parseInt(date.split("-")[0]);
                    if (year > currYear){
                        continue;
                    }

                    String uId = (String) jsonObject.get("user_id");
                    String businessId = (String) jsonObject.get("business_id");
                    JSONObject votes = (JSONObject)jsonObject.get("votes");
                    Integer useful = (int) (long) votes.get("useful");
                    Integer funny = (int) (long) votes.get("funny");
                    Integer cool = (int) (long) votes.get("cool");
                    List<String> businessCategories = businessCategory.get(businessId);
                    List<String> businessColgs = businessSchools.get(businessId);
                    Long star = (Long) jsonObject.get("stars");

                    Map<Tag, Integer> attrMap = null;
                    Integer userId = userIdMap.get(uId);
                    if (userCumDataMap.containsKey(userId)){
                        attrMap = userCumDataMap.get(userId);
                        if (year == currYear){
                            if(!attrMap.containsKey(Tag.REVIEW_COUNT)){
                                attrMap.put(Tag.REVIEW_COUNT,1);
                            }else{
                                attrMap.put(Tag.REVIEW_COUNT, attrMap.get(Tag.REVIEW_COUNT) + 1);
                            }

                            if(!attrMap.containsKey(Tag.VOTE_FUNNY)){
                                attrMap.put(Tag.VOTE_FUNNY, 1);
                            }else{
                                attrMap.put(Tag.VOTE_FUNNY, attrMap.get(Tag.VOTE_FUNNY) + funny);
                            }

                            if(!attrMap.containsKey(Tag.VOTE_USEFUL)){
                                attrMap.put(Tag.VOTE_USEFUL, 1);
                            }else{
                                attrMap.put(Tag.VOTE_USEFUL, attrMap.get(Tag.VOTE_USEFUL) + useful);
                            }


                            if(!attrMap.containsKey(Tag.VOTE_COOL)){
                                attrMap.put(Tag.VOTE_COOL, 1);
                            }else{
                                attrMap.put(Tag.VOTE_COOL, attrMap.get(Tag.VOTE_COOL) + cool);
                            }

                            Tag starTag = Tag.getValue("STAR_" + star);
                            if (attrMap.containsKey(starTag)){
                                attrMap.put( starTag , attrMap.get(starTag) + 1);
                            }else{
                                attrMap.put( starTag, 1);
                            }

                            for( String colg : businessColgs){
                                colg = universityTagMap.get(colg);
                                Tag colgTag = Tag.getValue("COL_" +colg);
                                if (attrMap.containsKey(colgTag)){
                                    attrMap.put( colgTag , attrMap.get(colgTag) + 1);
                                }else{
                                    attrMap.put( colgTag, 1);
                                }
                            }

                            for( String bcategory : businessCategories){
                                List<String> categories = categoryTagMap.get(bcategory);
                                for(String category : categories){
                                    Tag catTag = Tag.getValue("CAT_" + category);
                                    if (attrMap.containsKey(catTag)){
                                        attrMap.put( catTag , attrMap.get(catTag) + 1);
                                    }else{
                                        attrMap.put( catTag, 1);
                                    }
                                }
                            }
                        }else{
                           // System.out.println(jsonObject);
                        }
                    }else{
                        int rcount = 1;
                        if (year != currYear){
                            rcount = 0;
                            funny = 0;
                            useful = 0;
                            cool = 0;
                        }

                        attrMap = new HashMap<>();
                        attrMap.put(Tag.REVIEW_COUNT, rcount);
                        attrMap.put(Tag.VOTE_FUNNY, funny);
                        attrMap.put(Tag.VOTE_USEFUL, useful);
                        attrMap.put(Tag.VOTE_COOL, cool);
                        attrMap.put(Tag.getValue("STAR_" + star), rcount);

                        for( String colg : businessColgs){
                            colg = universityTagMap.get(colg);
                            attrMap.put( Tag.getValue("COL_" +colg), rcount);
                        }
                        for( String bcategory : businessCategories){
                            List<String> categories = categoryTagMap.get(bcategory);
                            for(String category : categories){
                                attrMap.put( Tag.getValue("CAT_" + category), rcount);
                            }
                        }
                    }
                    userCumDataMap.put(userId, attrMap);
                }
            }
        }catch (Exception ex){
            System.err.println(jsonObject);
            ex.printStackTrace();
        }
    }

    private void populateUserMaps() {
        System.out.println("Populating User Maps");
        JSONParser parser = new JSONParser();
        BufferedReader br = null;
        count = 0;
        try{
            String sCurrentLine = null;
            br = new BufferedReader(new FileReader(YelpConstants.FILENAME));
            while ((sCurrentLine = br.readLine()) != null) {
                Object obj = parser.parse(sCurrentLine);
                JSONObject jsonObject = (JSONObject) obj;
                String type = (String) jsonObject.get("type");
                if (type.equalsIgnoreCase("review")){
                    count++;
                    if (count % 20000 == 0){
                        System.out.println("Review Processed = " + count);
                    }

                    String date = (String) jsonObject.get("date");
                    Integer year = Integer.parseInt(date.split("-")[0]);
                    if (year > currYear){
                        continue;
                    }

                    String uId = (String) jsonObject.get("user_id");
                    String businessId = (String) jsonObject.get("business_id");
                    JSONObject votes = (JSONObject)jsonObject.get("votes");
                    Integer useful = (int) (long) votes.get("useful");
                    Integer funny = (int) (long) votes.get("funny");
                    Integer cool = (int) (long) votes.get("cool");
                    List<String> businessCategories = businessCategory.get(businessId);
                    List<String> businessColgs = businessSchools.get(businessId);
                    Long star = (Long) jsonObject.get("stars");

                    Map<Tag, Integer> attrMap = null;
                    Integer userId = userIdMap.get(uId);
                    if (userCumDataMap.containsKey(userId)){

                        attrMap = userCumDataMap.get(userId);
                        attrMap.put(Tag.REVIEW_COUNT, attrMap.get(Tag.REVIEW_COUNT) + 1);
                        attrMap.put(Tag.VOTE_FUNNY, attrMap.get(Tag.VOTE_FUNNY) + funny);
                        attrMap.put(Tag.VOTE_USEFUL, attrMap.get(Tag.VOTE_USEFUL) + useful);
                        attrMap.put(Tag.VOTE_COOL, attrMap.get(Tag.VOTE_COOL) + cool);

                        Tag starTag = Tag.getValue("STAR_" + star);
                        if (attrMap.containsKey(starTag)){
                            attrMap.put( starTag , attrMap.get(starTag) + 1);
                        }else{
                            attrMap.put( starTag, 1);
                        }


                        for( String colg : businessColgs){
                            colg = universityTagMap.get(colg);
                            Tag colgTag = Tag.getValue("COL_" +colg);
                            if (attrMap.containsKey(colgTag)){
                                attrMap.put( colgTag , attrMap.get(colgTag) + 1);
                            }else{
                                attrMap.put( colgTag, 1);
                            }
                        }

                        for( String bcategory : businessCategories){
                            List<String> categories = categoryTagMap.get(bcategory);
                            for(String category : categories){
                                Tag catTag = Tag.getValue("CAT_" + category);
                                if (attrMap.containsKey(catTag)){
                                    attrMap.put( catTag , attrMap.get(catTag) + 1);
                                }else{
                                    attrMap.put( catTag, 1);
                                }
                            }
                        }

                    }else{

                        attrMap = new HashMap<>();
                        attrMap.put(Tag.REVIEW_COUNT, 1);
                        attrMap.put(Tag.VOTE_FUNNY, funny);
                        attrMap.put(Tag.VOTE_USEFUL, useful);
                        attrMap.put(Tag.VOTE_COOL, cool);
                        attrMap.put(Tag.getValue("STAR_" + star), 1);

                        for( String colg : businessColgs){
                            colg = universityTagMap.get(colg);
                            attrMap.put( Tag.getValue("COL_" +colg), 1);
                        }
                        for( String bcategory : businessCategories){
                            List<String> categories = categoryTagMap.get(bcategory);
                            for(String category : categories){
                                attrMap.put( Tag.getValue("CAT_" + category), 1);
                            }
                        }
                    }
                    userCumDataMap.put(userId, attrMap);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeAttrFiles() {
        System.out.println("Writing Files = " + currYear);
        FileWriter writer = null;
        String append = "";
        if (cumulative){
            append = ".cum";
        }
        try{
            writer = new FileWriter(YelpConstants.FILE_PATH + "graph/yelp_" + currYear + append + ".attrlist");
            writer.append("USERID");writer.append("\t");
            for(Tag tag : Tag.values()){
                writer.append(tag.name());
                writer.append("\t");
            }
            writer.append("\n");
            for(Map.Entry<Integer, Map<Tag, Integer>> entry : userCumDataMap.entrySet()){
                writer.append(String.valueOf(entry.getKey())); writer.append("\t");
                for(Tag tag : Tag.values()){
                    if (entry.getValue().containsKey(tag)){
                        writer.append(String.valueOf(entry.getValue().get(tag)));
                    }else{
                        writer.append(String.valueOf(0));
                    }
                    writer.append("\t");
                }
                writer.append("\n");
                entry.getValue().clear();
            }
            writer.flush();
            writer.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        AttrListMaker maker = new AttrListMaker();
        maker.init();
        maker.cumulative = Boolean.FALSE;
        for(Integer year : Arrays.asList(2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012)){
            System.out.println("Processing data for year = " + year);
            maker.currYear = year;
            //maker.populateUserMaps();
            maker.populateNonCumUserMaps();
            maker.writeAttrFiles();
        }

    }

}
