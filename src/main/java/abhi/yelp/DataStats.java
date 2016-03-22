package abhi.yelp;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author : abhishek
 * Created on 3/17/16.
 */
public class DataStats {

    static int reviewCount = 0;
    Map<String, Integer> businessReviewMap = new HashMap<>();
    Map<String, Integer> userReviewMap = new HashMap<>();
    Map<String, Integer> yearReviewMap = new TreeMap<>();
    Map<String, Integer> categoryMap = new TreeMap<>();
    Map<String, Integer> varsityMap = new TreeMap<>();
    Map<Long, Integer> starMap = new TreeMap<>();
    Map<String, Integer> voteTypeMap = new TreeMap<>();


    public  void getDataStats() {

         BufferedReader br = null;
         try{
             String sCurrentLine = null;
             br = new BufferedReader(new FileReader(YelpConstants.FILENAME));
             while ((sCurrentLine = br.readLine()) != null) {
                 parseAndAnalyzeJson(sCurrentLine);
             }

             System.out.println("======================================================");
//             System.out.println("Review Count = " + reviewCount);
//             System.out.println("Business Places = " + businessReviewMap.size());
//             System.out.println("Users = " + userReviewMap.size());
//
//             for( Map.Entry<String, Integer> entry: yearReviewMap.entrySet()){
//                 System.out.println(entry.getKey() + " === " + entry.getValue());
//             }
//             System.out.println("Total Categories = " + categoryMap.size());
//             for( Map.Entry<String, Integer> entry: categoryMap.entrySet()){
//                 System.out.println(entry.getKey() + "\t" + entry.getValue());
//             }

             System.out.println("======================================================");
             System.out.println("Total Varsities = " + varsityMap.size());
             for( Map.Entry<String, Integer> entry: varsityMap.entrySet()){
                 System.out.println(entry.getKey());
             }

//             System.out.println("======================================================");
//             System.out.println("Total Star Types = " + starMap.size());
//             for( Map.Entry<Long, Integer> entry: starMap.entrySet()){
//                 System.out.println(entry.getKey() + " === " + entry.getValue());
//             }
//
//             System.out.println("======================================================");
//             System.out.println("Total Vote Types = " + voteTypeMap.size());
//             for( Map.Entry<String, Integer> entry: voteTypeMap.entrySet()){
//                 System.out.println(entry.getKey() + " === " + entry.getValue());
//             }

         }catch (Exception ex){
             ex.printStackTrace();
         }
    }

    private void parseAndAnalyzeJson(String sCurrentLine) {

            JSONParser parser = new JSONParser();

            try{
                Object obj = parser.parse(sCurrentLine);
                JSONObject jsonObject = (JSONObject) obj;
                String type = (String) jsonObject.get("type");
//
//                if (type.equalsIgnoreCase("review")){
//                    reviewCount++;
//                    if (reviewCount % 20000 == 0){
//                        System.out.println("Reviews Processed = " + reviewCount);
//                    }
//
////                    String businessId = (String) jsonObject.get("business_id");
////                    if (businessReviewMap.containsKey(businessId)){
////                        businessReviewMap.put(businessId, businessReviewMap.get(businessId) + 1);
////                    }else{
////                        businessReviewMap.put(businessId, 1);
////                    }
////
////
////                    String userId = (String) jsonObject.get("user_id");
////                    if (userReviewMap.containsKey(userId)){
////                        userReviewMap.put(userId, userReviewMap.get(userId) + 1);
////                    }else{
////                        userReviewMap.put(userId, 1);
////                    }
////
////                    String date = (String) jsonObject.get("date");
////                    String[] dateStr = date.split("-");
////                    if (yearReviewMap.containsKey(dateStr[0])){
////                        yearReviewMap.put(dateStr[0], yearReviewMap.get(dateStr[0]) + 1);
////                    }else{
////                        yearReviewMap.put(dateStr[0], 1);
////                    }
//                      Long star = (Long) jsonObject.get("stars");
//                      if(starMap.containsKey(star)){
//                          starMap.put(star, starMap.get(star) + 1);
//                      }else{
//                          starMap.put(star, 1);
//                      }
//
//                     JSONObject votes = (JSONObject)jsonObject.get("votes");
//                    System.out.println(votes);
////                      while (itr.hasNext()) {
////                          String vote = (String) itr.next();
////                          if (voteTypeMap.containsKey(vote)){
////                              voteTypeMap.put(vote, voteTypeMap.get(vote) +1);
////                          }else{
////                              voteTypeMap.put(vote, 1);
////                          }
////                      }
//                }

                  if (type.equalsIgnoreCase("business")){
//
//                      JSONArray categories = (JSONArray) jsonObject.get("categories");
//                      Iterator itr = categories.iterator();
//                      while (itr.hasNext()) {
//                          String category = (String) itr.next();
//                          if (categoryMap.containsKey(category)){
//                              categoryMap.put(category, categoryMap.get(category) +1);
//                          }else{
//                              categoryMap.put(category, 1);
//                          }
//                      }
//
                      JSONArray varsities = (JSONArray) jsonObject.get("schools");
                      Iterator itr = varsities.iterator();
                      while (itr.hasNext()) {
                          String varsity = (String) itr.next();
                          if (varsityMap.containsKey(varsity)){
                              varsityMap.put(varsity, varsityMap.get(varsity) +1);
                          }else{
                              varsityMap.put(varsity, 1);
                          }
                      }
                  }
            }catch (Exception ex){
                ex.printStackTrace();
            }
    }

    public static void main(String[] args) {
        DataStats dataStats = new DataStats();
        dataStats.getDataStats();
    }

}
