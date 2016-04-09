package abhi.tripadvisor;

import abhi.yelp.YelpConstants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.util.*;

/**
 * Author : abhishek
 * Created on 4/1/16.
 */
public class EdgelistMaker {

    Map<String, Integer> userIdMap = new LinkedHashMap<>();
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
                    for(int i =0; i < reviews.size() -1; i++){
                        for(int j= i+1; j < reviews.size(); j++ ){
                            int author_i = userIdMap.get((String) (reviews.get(i).get(TripAdvisorConstants.AUTHOR)));
                            int author_j = userIdMap.get((String) (reviews.get(j).get(TripAdvisorConstants.AUTHOR)));
                            if(author_i != author_j){
                                String pair1 = author_i + "_" + author_j;
                                String pair2 = author_j + "_" + author_i;
                                if(!linkPairs.contains(pair1) && !linkPairs.contains(pair2)){
                                    linkPairs.add(pair1);
                                }
                            }
                        }
                    }
                }
                writeEdgelist(linkPairs, yr);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
    }

    private void writeEdgelist(Set<String> userIdList, int currYear) {

        FileWriter writer;
        String append = ".cum";
        int count = 0;
        System.out.println("Writing file for year = " + currYear);
        StringBuffer bf = new StringBuffer(10000000);
        try{
            writer = new FileWriter(new File(TripAdvisorConstants.DIR_PATH + "edgelist/ta_" + currYear + append + ".edgelist").getAbsoluteFile(), Boolean.TRUE);
            BufferedWriter bw = new BufferedWriter(writer);
            for( String pair : userIdList){
                String[] pairs = pair.split("_");

                bf.append(pairs[0]);
                bf.append("\t");
                bf.append(pairs[1]);
                bf.append("\n");
                count++;
                if(count % 100000 == 0){
                    bw.write(bf.toString());
                    bf.setLength(0);
                }
            }
            bw.write(bf.toString());
            bw.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeUserId() {

        System.out.println("Writing User Id Map in File");
        System.out.println("Map Size = "  + userIdMap.size());
        FileWriter writer;
        try{
            writer = new FileWriter(TripAdvisorConstants.DIR_PATH + "tripadvisor_author" + ".txt");
            for (Map.Entry<String, Integer> entry : userIdMap.entrySet()){
                writer.append(entry.getKey());
                writer.append("\t");
                writer.append(String.valueOf(entry.getValue()));
                writer.append("\n");
            }
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void writeCumEdgelist(){

        BufferedReader br = null;
        try{
            for(int i =2002; i < 2013; i++){
                    System.out.println("Processing file for year = " + i);
                    String sCurrentLine = null;
                    br = new BufferedReader(new FileReader(TripAdvisorConstants.DIR_PATH + "edgelist/ta_" + i + ".edgelist"));
                    while ((sCurrentLine = br.readLine()) != null) {
                        String[] pairs = sCurrentLine.split("\t");
                        int author_i = Integer.parseInt(pairs[0]);
                        int author_j = Integer.parseInt(pairs[1]);
                        if(author_i != author_j){
                            String pair1 = author_i + "_" + author_j;
                            String pair2 = author_j + "_" + author_i;
                            if(!linkPairs.contains(pair1) && !linkPairs.contains(pair2)){
                                linkPairs.add(pair1);
                            }
                        }
                    }
                    writeEdgelist(linkPairs, i);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EdgelistMaker edgelist = new EdgelistMaker();
        edgelist.init();
        edgelist.writeCumEdgelist();
    }
}
