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
    Map<String, Integer> userIdMap = new HashMap<>();

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
            br = new BufferedReader(new FileReader(YelpConstants.FILE_PATH + "yelp_tags_new.tsv"));
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

                    String userId = (String) jsonObject.get("user_id");
                    String businessId = (String) jsonObject.get("business_id");
                    JSONObject votes = (JSONObject)jsonObject.get("votes");
                    Integer useful = (int) (long) votes.get("useful");
                    Integer funny = (int) (long) votes.get("funny");
                    Integer cool = (int) (long) votes.get("cool");
                    List<String> businessCategories = businessCategory.get(businessId);
                    List<String> businessColgs = businessSchools.get(businessId);


                    Map<Tag, Integer> attrMap = null;
                    if (userCumDataMap.containsKey(userId)){

                        attrMap = userCumDataMap.get(userId);
                        attrMap.put(Tag.REVIEW_COUNT, attrMap.get(Tag.REVIEW_COUNT) + 1);
                        attrMap.put(Tag.VOTE_FUNNY, attrMap.get(Tag.VOTE_FUNNY) + funny);
                        attrMap.put(Tag.VOTE_USEFUL, attrMap.get(Tag.VOTE_USEFUL) + useful);
                        attrMap.put(Tag.VOTE_COOL, attrMap.get(Tag.VOTE_COOL) + cool);

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
                                Tag catTag = Tag.getValue("TAG_" + category);
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
                        for( String colg : businessColgs){
                            colg = universityTagMap.get(colg);
                            attrMap.put( Tag.getValue("COL_" +colg), 1);
                        }
                        for( String bcategory : businessCategories){
                            List<String> categories = categoryTagMap.get(bcategory);
                            for(String category : categories){
                                attrMap.put( Tag.getValue("TAG_" + category), 1);
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeAttrFiles() {
        System.out.println("Writing Files");
        FileWriter writer = null;
        String append = "";
        if (cumulative){
            append = ".cum";
        }
        try{
            writer = new FileWriter(YelpConstants.FILE_PATH + "attr/yelp_" + currYear + append + ".attrlist");
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
            }
            writer.flush();
            writer.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AttrListMaker maker = new AttrListMaker();
        maker.cumulative = Boolean.FALSE;
        maker.currYear = 2004;
        maker.init();
        maker.loadBusinessTags();
        maker.populateUserMaps();
        maker.writeAttrFiles();
    }

}
