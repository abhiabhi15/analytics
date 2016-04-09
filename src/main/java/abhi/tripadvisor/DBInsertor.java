package abhi.tripadvisor;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Author : abhishek
 * Created on 4/2/16.
 */
public class DBInsertor {

    MongoDatabase db;
    List<String> ignoreList = Arrays.asList("A TripAdvisor Member", "lass=");
    int fcount = 0;

    private void init(){
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(TripAdvisorConstants.TRIPADVSIOR_DB);
    }

    private MongoCollection getCollection(String year){
          return db.getCollection("ta_" + year);
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
        if (count == 6) {
            return true;
        }
        return false;
    }


    private void insertIntoDB() {

        File dir = new File(TripAdvisorConstants.DIR_PATH + "json");
        File[] directoryListing = dir.listFiles();
        JSONParser parser = new JSONParser();

        if (directoryListing != null) {
            for (File file : directoryListing) {
                fcount++;
                if( fcount % 100 == 0){
                    System.out.println("File Processed = " + fcount);
                }

                try {
                    Object obj = parser.parse(new FileReader(file));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONObject hotelInfo = (JSONObject) jsonObject.get(TripAdvisorConstants.HOTEL_INFO);
                    String hotelId = (String) hotelInfo.get(TripAdvisorConstants.HOTEL_ID);

                    JSONArray reviews = (JSONArray) jsonObject.get("Reviews");
                    Iterator itr = reviews.iterator();

                    Map<String, List<Document>> hotelReviewsMap = new HashMap<>();
                    while (itr.hasNext()) {

                        JSONObject reviewJson = (JSONObject) itr.next();
                        JSONObject ratings = (JSONObject) reviewJson.get("Ratings");
                        Set<String> keys = ratings.keySet();
                        if(!isKeysPresent(keys)){
                            continue;
                        }

                        String author = (String) reviewJson.get(TripAdvisorConstants.AUTHOR);
                        if(ignoreList.contains(author.trim())){
                            continue;
                        }

                        String date = (String) reviewJson.get(TripAdvisorConstants.DATE);
                        String year = date.split(",")[1].trim();

                        Document review = new Document();
                        review.append(TripAdvisorConstants.REVIEW_ID, reviewJson.get(TripAdvisorConstants.REVIEW_ID));
                        review.append(TripAdvisorConstants.AUTHOR, author);
                        review.append(TripAdvisorConstants.HOTEL_ID, hotelId);
                        review.append(TripAdvisorConstants.YEAR, year);
                        review.append(TripAdvisorConstants.R_OVERALL,ratings.get(TripAdvisorConstants.R_OVERALL));
                        review.append(TripAdvisorConstants.R_VALUE, ratings.get(TripAdvisorConstants.R_VALUE));
                        review.append(TripAdvisorConstants.R_LOCATION, ratings.get(TripAdvisorConstants.R_LOCATION));
                        review.append(TripAdvisorConstants.R_SERVICE,  ratings.get(TripAdvisorConstants.R_SERVICE));
                        review.append(TripAdvisorConstants.R_CLEANLINESS,  ratings.get(TripAdvisorConstants.R_CLEANLINESS));
                        review.append(TripAdvisorConstants.R_ROOMS,  ratings.get(TripAdvisorConstants.R_ROOMS));

                        updateHotelReviews(hotelReviewsMap, review, year);
                    }

                    if(hotelReviewsMap.size() > 0){
                        insertReviewsInDB(hotelReviewsMap);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insertReviewsInDB(Map<String, List<Document>> hotelReviewsMap) {

        for(Map.Entry<String, List<Document>> entry : hotelReviewsMap.entrySet()){
            String year = entry.getKey();
            MongoCollection collection = getCollection(year);
            Document hotelBSON = new Document();
            hotelBSON.append(TripAdvisorConstants.REVIEWS, entry.getValue());
            collection.insertOne(hotelBSON);
        }
    }

    private void updateHotelReviews(Map<String, List<Document>> hotelReviewMap, Document review, String year) {

        List<Document> reviewList = null;
        if(hotelReviewMap.containsKey(year)){
            reviewList = hotelReviewMap.get(year);
        }else{
            reviewList = new ArrayList<>();
        }
        reviewList.add(review);
        hotelReviewMap.put(year, reviewList);
    }

    public static void main(String[] args) {
        DBInsertor insertor = new DBInsertor();
        insertor.init();
        insertor.insertIntoDB();
    }
}
