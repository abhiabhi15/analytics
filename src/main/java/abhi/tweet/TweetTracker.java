package abhi.tweet;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.*;


/**
 * Author : abhishek
 * Created on 9/6/15.
 */
public class TweetTracker {

    MongoDatabase db;

    private Set<String> userIds = new HashSet<>();
    private List<Integer> fcounts = new ArrayList<>();
    private final Map<String, long[]> friendMap = new HashMap<>();
    private final Map<String, long[]> followerMap = new HashMap<>();


    public TweetTracker() throws Exception{

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(TweetConstants.GRAPH);
    }


    public void getUserIds(){

        FindIterable<Document> iterable = db.getCollection(TweetConstants.USERS).find();
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                String uid = document.get("user_id").toString();
                userIds.add(uid);
            }
        });
    }

    public void getConnectionCounts(){

        FindIterable<Document> iterable = db.getCollection(TweetConstants.USERS).find();
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                fcounts.add((int)document.get("followers_count"));
            }
        });
    }

    public void getFollowerMap(){

        try{
            Twitter twitter = new MyTwitter().getInstance();
            long id = Long.parseLong("538418266");
            long[] followerIds = twitter.getFollowersIDs(53307270).getIDs();
            for(long l : followerIds){
                System.out.println(l);
            }
            System.out.println("For userID = " + " 538418266" + " , followers = " + followerIds.length);
/*
            for(String uid : userIds){
                long[] followerIds = twitter.getFollowersIDs(Long.parseLong("3161843418")).getIDs();
                System.out.println("For userID = " + uid + " , followers = " + followerIds.length);
                followerMap.put(uid, followerIds);
            }*/
        }catch(TwitterException ex){
            ex.printStackTrace();
        }
    }

    public void getFollowerIds(String userId){
        try {
            Twitter twitter = new MyTwitter().getInstance();
            long cursor = -1;
            IDs ids;
            System.out.println("Listing followers's ids.");
            do {
                if (userId != null) {
                    ids = twitter.getFollowersIDs(userId, cursor);
                } else {
                    ids = twitter.getFollowersIDs(cursor);
                }
                for (long id : ids.getIDs()) {
                    System.out.println(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
            System.exit(-1);
        }
    }

    public void printMap(Map<String, long[]> map){
        for(Map.Entry<String, long[]> entry : map.entrySet()){
            System.out.println("User = " + entry.getKey());
            System.out.println("Followers =  " + Arrays.asList(entry.getValue()));
            System.out.println("----------------------------------------------------------");
        }

    }


    public static void main(String[] args) throws Exception{

        TweetTracker tracker = new TweetTracker();
        //tracker.getFollowerIds("538418266");
        tracker.getConnectionCounts();
        Collections.sort(tracker.fcounts);
        for(int count : tracker.fcounts){
            System.out.println(count);
        }
        /*tracker.getUserIds();
        tracker.getFollowerMap();*/
      //  tracker.printMap(tracker.followerMap);

    }

}
