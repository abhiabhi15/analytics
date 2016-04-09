package abhi.tripadvisor;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.io.*;
import java.util.*;

/**
 * Author : abhishek
 * Created on 4/1/16.
 */
public class AttrlistMaker {

    Map<String, Integer> userIdMap = new LinkedHashMap<>();
    Map<Integer, Map<String, Integer>> userDataMap = new LinkedHashMap<>();
    MongoDatabase db;
    Set<String> linkPairs = new HashSet<>();

    private MongoCollection getCollection(String year){
        return db.getCollection("ta_" + year);
    }

    private void init(){
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(TripAdvisorConstants.TRIPADVSIOR_DB);

        // Read  User Map
        BufferedReader br = null;
        try{
            String sCurrentLine = null;
            // Read UserID
            br = new BufferedReader(new FileReader(TripAdvisorConstants.DIR_PATH + "tripadvisor_author.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] uids = sCurrentLine.split("\t");
                userIdMap.put(uids[0], Integer.valueOf(uids[1]));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void processData() {

        for( int yr = 2002; yr < 2013; yr++){
            MongoCollection collection = getCollection(String.valueOf(yr));
            MongoCursor<Document> cursor = collection.find().iterator();
            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    List<Document> reviews = (List<Document> )doc.get(TripAdvisorConstants.REVIEWS);
                    for(Document review : reviews){
                        String author = (String) review.get(TripAdvisorConstants.AUTHOR);

                        String overall = (String) review.get(TripAdvisorConstants.R_OVERALL);
                        String clean = (String) review.get(TripAdvisorConstants.R_CLEANLINESS);
                        String rooms = (String) review.get(TripAdvisorConstants.R_ROOMS);
                        String service = (String) review.get(TripAdvisorConstants.R_SERVICE);
                        String location = (String) review.get(TripAdvisorConstants.R_LOCATION);
                        String value = (String) review.get(TripAdvisorConstants.R_VALUE);

                        Integer uid = userIdMap.get(author);
                        Map<String, Integer> attrData = null;
                        if(userDataMap.containsKey(uid)){

                            attrData = userDataMap.get(uid);
                            attrData.put(TripAdvisorConstants.R_OVERALL, attrData.get(TripAdvisorConstants.R_OVERALL) + getVal(overall));
                            attrData.put(TripAdvisorConstants.R_CLEANLINESS, attrData.get(TripAdvisorConstants.R_CLEANLINESS) + getVal(clean));
                            attrData.put(TripAdvisorConstants.R_ROOMS, attrData.get(TripAdvisorConstants.R_ROOMS) + getVal(rooms));
                            attrData.put(TripAdvisorConstants.R_SERVICE, attrData.get(TripAdvisorConstants.R_SERVICE) + getVal(service));
                            attrData.put(TripAdvisorConstants.R_LOCATION, attrData.get(TripAdvisorConstants.R_LOCATION) + getVal(location));
                            attrData.put(TripAdvisorConstants.R_VALUE, attrData.get(TripAdvisorConstants.R_VALUE) + getVal(value));
                            attrData.put(TripAdvisorConstants.REVIEWS, attrData.get(TripAdvisorConstants.REVIEWS) + 1);
                        }else{

                            attrData = new HashMap<>();
                            attrData.put(TripAdvisorConstants.R_OVERALL, getVal(overall));
                            attrData.put(TripAdvisorConstants.R_CLEANLINESS, getVal(clean));
                            attrData.put(TripAdvisorConstants.R_ROOMS, getVal(rooms));
                            attrData.put(TripAdvisorConstants.R_SERVICE, getVal(service));
                            attrData.put(TripAdvisorConstants.R_LOCATION, getVal(location));
                            attrData.put(TripAdvisorConstants.R_VALUE, getVal(value));
                            attrData.put(TripAdvisorConstants.REVIEWS, 1);
                        }
                        userDataMap.put(uid, attrData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

            writeAttrlist(userDataMap, yr);
        }
    }

    private void writeAttrlist(Map<Integer, Map<String, Integer>> userDataMap, int year) {

        FileWriter writer;
        String append = ".cum";
        int count = 0;
        System.out.println("Writing file for year = " + year);
        StringBuffer bf = new StringBuffer(10000000);
        try{
            writer = new FileWriter(new File(TripAdvisorConstants.DIR_PATH + "attrlist/ta_" + year + append + ".attrlist").getAbsoluteFile(), Boolean.TRUE);
            BufferedWriter bw = new BufferedWriter(writer);
            bf.append("USER_ID").append("\t")
            .append("REVIEW_COUNT").append("\t")
            .append("AVG_OVERALL").append("\t")
            .append("AVG_CLEANLINESS").append("\t")
            .append("AVG_ROOMS").append("\t")
            .append("AVG_SERVICE").append("\t")
            .append("AVG_LOCATION").append("\t")
            .append("AVG_VALUE").append("\n");


            for(Map.Entry<Integer, Map<String, Integer>> entry : userDataMap.entrySet() ){
                bf.append(entry.getKey()).append("\t");
                Map<String, Integer> attrMap = entry.getValue();
                bf.append(attrMap.get(TripAdvisorConstants.REVIEWS)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_OVERALL)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_CLEANLINESS)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_ROOMS)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_SERVICE)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_LOCATION)).append("\t");
                bf.append(attrMap.get(TripAdvisorConstants.R_VALUE)).append("\n");
            }

            count++;
            if(count % 1000 == 0){
                bw.write(bf.toString());
                bf.setLength(0);
            }
            bw.write(bf.toString());
            bw.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private Integer getVal(String value) {
        double d = Double.parseDouble(value);
        Integer val = (int)d;
        if(val < 0){
            return 0;
        }else{
            return val;
        }
    }

    public static void main(String[] args) {
        AttrlistMaker maker = new AttrlistMaker();
        maker.init();
        maker.processData();
    }
}
