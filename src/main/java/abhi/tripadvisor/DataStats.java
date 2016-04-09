package abhi.tripadvisor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

/**
 * Author : abhishek
 * Created on 3/31/16.
 */
public class DataStats {

    static final String DIR_PATH = "/home/abhishek/Downloads/ncsu/istudy/data/tripadvisor/json";
    Map<String, Integer> dateMap = new TreeMap<>();
    Map<String, Integer> ratingMap = new TreeMap<>();

    private void processData() {

        BufferedReader br = null;
        File dir = new File(DIR_PATH);
        File[] directoryListing = dir.listFiles();
        JSONParser parser = new JSONParser();
        int fcount = 0;
        if (directoryListing != null) {
            for (File file : directoryListing) {
                try {
                    Object obj = parser.parse(new FileReader(file));

                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray reviews = (JSONArray) jsonObject.get("Reviews");
                    Iterator itr = reviews.iterator();

                    while (itr.hasNext()) {
                          JSONObject review = (JSONObject) itr.next();

                          JSONObject ratings = (JSONObject) review.get("Ratings");
                          Set<String> keys = ratings.keySet();
                          if(!isKeysPresent(keys)){
                              continue;
                          }
                          for(String key: keys){
                              if(ratingMap.containsKey(key)){
                                   ratingMap.put(key, ratingMap.get(key)+1);
                              }else{
                                   ratingMap.put(key, 1);
                              }
                          }
                          String date = (String) review.get("Date");
                          String year = date.split(",")[1].trim();
                          if(dateMap.containsKey(year)){
                              dateMap.put(year, dateMap.get(year) +1);
                          }else{
                              dateMap.put(year, 1);
                          }
                    }

                    if(fcount % 100 == 0){
                        System.out.println("File Processed = " + fcount);
                    }
                    fcount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("Total Hotels = " +  fcount);
        System.out.println("Date Map Size  = " + dateMap.size());
        for(Map.Entry<String, Integer> entry: dateMap.entrySet()){
            System.out.println(entry.getKey() + "," + entry.getValue());
        }

        System.out.println("Ratings Map Size  = " + ratingMap.size());
        for(Map.Entry<String, Integer> entry: ratingMap.entrySet()){
            System.out.println(entry.getKey() + "," + entry.getValue());
        }

    }

    private boolean isKeysPresent(Set<String> keys) {

        List<String> wantKeys = Arrays.asList("Overall", "Value", "Service", "Cleanliness", "Rooms" , "Location");
        if(keys.size() < 6){
            return false;
        }
        int count = 0;
        for(String key : keys){
            if(wantKeys.contains(key)){
                count++;
            }
        }
        if (count == 6){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        DataStats stats = new DataStats();
        stats.processData();
    }


}
