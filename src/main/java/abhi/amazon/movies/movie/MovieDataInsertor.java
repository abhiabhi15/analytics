package abhi.amazon.movies.movie;

import abhi.amazon.movies.MovieConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 11/22/15.
 */
public class MovieDataInsertor {


    private MongoDatabase db;
    private MongoCollection mInfo;
    private MongoCollection mData;
    private Map<String, Integer> productMap;

    public void getProductIds(){

        productMap = new LinkedHashMap<>();
        String fileName = "/home/abhishek/dstools/datamining/independent-study/amazon-movie-reviews/data/movies_300_plus_reviews.csv";
        try{

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split(",");
                productMap.put(tokens[0], Integer.parseInt(tokens[1]));
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void createSummary(){

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(MovieConstants.MOVIE);
        mData = db.getCollection(MovieConstants.MOVIE_DATA);
        mData.createIndex(new Document(MovieConstants.PRODUCT_ID, 1));

        mInfo = db.getCollection(MovieConstants.MOVIE_INFO);

        BasicDBObject whereQuery = new BasicDBObject();


        String fileName = "/home/abhishek/dstools/datamining/independent-study/amazon-movie-reviews/data/movie_final.tsv";
        try{

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split("\t");
                String prodId = tokens[0];
                whereQuery.put(MovieConstants.PRODUCT_ID, prodId);
                Document record = (Document) mInfo.find(whereQuery).first();
                if(record != null){
                    Document doc = new Document(MovieConstants.PRODUCT_ID, prodId).
                            append(MovieConstants.REVIEWS, record.get(MovieConstants.REVIEWS)).
                            append(MovieConstants.TITLE, tokens[1]);

                    mData.insertOne(doc);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void insertProdIds(){

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(MovieConstants.MOVIE);
        mInfo = db.getCollection(MovieConstants.MOVIE_INFO);

        mInfo.createIndex(new Document(MovieConstants.PRODUCT_ID, 1));

        for(Map.Entry<String, Integer> entry : productMap.entrySet()){
            Document doc = new Document(MovieConstants.PRODUCT_ID, entry.getKey()).
                                  append(MovieConstants.REVIEWS, entry.getValue());

            mInfo.insertOne(doc);        }
    }

    public static void main(String[] args) {
        MovieDataInsertor insertor = new MovieDataInsertor();
        insertor.createSummary();
    }
}
