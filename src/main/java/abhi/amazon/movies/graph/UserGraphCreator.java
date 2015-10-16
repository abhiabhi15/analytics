package abhi.amazon.movies.graph;

import abhi.amazon.movies.MovieConstants;
import abhi.amazon.movies.MovieUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.List;

/**
 * Author : abhishek
 * Created on 10/15/15.
 */
public class UserGraphCreator {

    private static final String SRC_COLLECTION = "q1_2004";
    private static final String DEST_COLLECTION = "g_q1_2004";

    private static final String FILE_PATH = "/home/abhishek/dstools/analytics/src/main/resources/";
    private static final String GENRE_LOOKUP_FILE_NAME = FILE_PATH + "movies/genre.json";


    MongoDatabase db;
    JSONParser parser = new JSONParser();

    public void createGraphNodes() throws Exception {

        Object obj = parser.parse(new FileReader(GENRE_LOOKUP_FILE_NAME));

        JSONObject lookupObj =  (JSONObject) obj;
        JSONObject lookup = (JSONObject)lookupObj.get(MovieConstants.GENRE);

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(MovieConstants.MOVIE);
        MongoCollection srcCollection = db.getCollection(SRC_COLLECTION);
        MongoCollection destCollection = db.getCollection(DEST_COLLECTION);
        destCollection.createIndex(new Document(MovieConstants.UID, 1));

        MongoCursor<Document> cursor = srcCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document reviewer = cursor.next();
                Document node = createReviewerNode(reviewer, lookup);
                destCollection.insertOne(node);
            }
        } finally {
            cursor.close();
        }
    }

    private Document createReviewerNode(Document reviewer, JSONObject lookup) {

        Document reviewerNode = new Document();
        reviewerNode.append(MovieConstants.UID, reviewer.get(MovieConstants.UID));
        reviewerNode.append(MovieConstants.PROFILE_NAME, reviewer.get(MovieConstants.PROFILE_NAME));

        List<Document> reviews = (List<Document>) reviewer.get(MovieConstants.REVIEWS);
        int numReviews = reviews.size();
        float sumScore = 0, sumHelpScore = 0;

        Document genreDoc = new Document();
        for(Document reviewDoc : reviews){
            sumScore += Float.parseFloat((String) reviewDoc.get(MovieConstants.SCORE));
            sumHelpScore += getHelpScore((String) reviewDoc.get(MovieConstants.HELPFULNESS));
            updateGenreDoc(genreDoc, lookup, reviewDoc);
        }

        reviewerNode.append(MovieConstants.AVG_REVIEW_SCORE, sumScore/numReviews);
        reviewerNode.append(MovieConstants.AVG_HELP_SCORE, sumHelpScore/numReviews);
        reviewerNode.append(MovieConstants.GENRE, genreDoc);

        return reviewerNode;
    }

    private void updateGenreDoc(Document genreDoc, JSONObject lookup, Document reviewDoc) {

        JSONArray genreArr = (JSONArray)lookup.get(reviewDoc.get(MovieConstants.PRODUCT_ID));
        if(genreArr != null){
            for(int j=0; j < genreArr.size(); j++){
                String genre = (String) genreArr.get(j);
                if(genreDoc.containsKey(genre)){
                    genreDoc.put(genre, (int)genreDoc.get(genre) +1);
                }else{
                    genreDoc.put(genre, 1);
                }
            }
        }
    }


    private float getHelpScore(String helpScore) {

        String[] scrs = helpScore.split("/");
        return Float.parseFloat(scrs[0])/Float.parseFloat(scrs[1]);
    }


    public static void main(String[] args) {

        UserGraphCreator userGraphCreator = new UserGraphCreator();
        try {

            userGraphCreator.createGraphNodes();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
