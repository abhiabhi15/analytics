package abhi.dblp;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 9/10/15.
 */
public class DblpParser {

    MongoDatabase db;
    Map<String, Long> journalMap = new HashMap<>();

    public static void main(String[] args) {
        DblpParser parser = new DblpParser();
        parser.getJournalMap();
    }

    public void getJournalMap(){
        BufferedReader br = null;
        JSONParser parser = new JSONParser();

         String sCurrentLine = null;
        try {

            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/dblp_scripts/dblp_article.json"));
            long paperCount = 0;
            while ((sCurrentLine = br.readLine()) != null) {

                if(sCurrentLine.length() < 2){
                    continue;
                }
                if(sCurrentLine.charAt(sCurrentLine.length()-1) == ','){
                    sCurrentLine = sCurrentLine.substring(0, sCurrentLine.length()-1);
                }
                Object obj;
                try {
                    obj = parser.parse(sCurrentLine);
                    JSONArray jsonArray = (JSONArray) obj;
                    String journal =  null;
                    paperCount++;
                    if((journal = (String)jsonArray.get(5)) != null){

                        if(journalMap.containsKey(journal)){
                            journalMap.put(journal, journalMap.get(journal)+1l);
                        }else {
                            journalMap.put(journal, 1l);
                        }
                    }

                    if(paperCount% 10000 == 0){
                        System.out.println("Parsed Articles = " + paperCount);
                    }
                }catch (Exception ex){
                    System.out.println(sCurrentLine);
                    ex.printStackTrace();
                }
           }
           for(Map.Entry<String, Long> entry : journalMap.entrySet()){
               System.out.println(entry.getKey() + "|" + entry.getValue());
           }
        } catch (IOException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void readJsonFile() {

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(DblpConstants.DBLP);
        MongoCollection collection = db.getCollection(DblpConstants.AUTHOR_2014);
        collection.createIndex(new Document(DblpConstants.UID, 1));

        BufferedReader br = null;
        JSONParser parser = new JSONParser();

        int counter = 0;
        int reportFreq = 2000;

        String sCurrentLine = null;
        try {

            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/dblp_scripts/dblp_article.json"));
            BasicDBObject whereQuery = null;
            int q_paper = 0;
            while ((sCurrentLine = br.readLine()) != null) {

                counter++;
                if(sCurrentLine.length() < 2){
                    continue;
                }
                if(sCurrentLine.charAt(sCurrentLine.length()-1) == ','){
                    sCurrentLine = sCurrentLine.substring(0, sCurrentLine.length()-1);
                }
                Object obj;
                try {
                    obj = parser.parse(sCurrentLine);
                    JSONArray jsonArray = (JSONArray) obj;
                    long year = (long)jsonArray.get(3);

                    if(jsonArray.get(3) == null){
                        System.out.println("Year Null " + sCurrentLine);
                    }
                    if(year != 2014){
                        continue;
                    }

                   // System.out.println("Paper Q " + year);
                    q_paper++;
                    if(q_paper % reportFreq == 0){
                        System.out.println("[Qualified Parsed Papers ] = " + q_paper);
                    }
                    if(counter % 500 == 0){
                        System.out.println("[Parsed Papers ] = " + counter);
                    }
                    List<Author> authors = DblpUtils.parseAuthors(jsonArray);
                    for(Author author : authors){

                        whereQuery = new BasicDBObject();
                        whereQuery.put(DblpConstants.UID, author.uid);
                        Document record = (Document) collection.find(whereQuery).first();
                        if(record != null){
                         //   System.out.println("Updating Author Data : " + author.name  );
                            collection.updateOne(new Document(DblpConstants.UID, author.uid),
                                    author.update(record));
                        }else {
                            collection.insertOne(author.getMongoDoc());
                        }
                    }
                }catch (Exception ex){
                    System.out.println(sCurrentLine);
                    ex.printStackTrace();

                }

            }
        } catch (IOException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
