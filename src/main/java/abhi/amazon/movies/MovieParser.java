package abhi.amazon.movies;

import abhi.utils.CalendarUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Author : abhishek
 * Created on 9/18/15.
 */
public class MovieParser {

    MongoDatabase db;
    MongoCollection col_q1;
    MongoCollection col_q2;
    MongoCollection col_q3;
    MongoCollection col_q4;

    Map<String, Long> reviewMap;
    long counter = 0l;
    int errorReviews = 1;
    final String yearOfQuery = "2000";

    public void addUsers() {

        BufferedReader br = null;
        String sCurrentLine = null;
        //MongoClient mongoClient = new MongoClient("192.168.2.100",27017);
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(MovieConstants.MOVIE);

        col_q1 = db.getCollection("q1_" + yearOfQuery);
        col_q2 = db.getCollection("q2_" + yearOfQuery);
        col_q3 = db.getCollection("q3_" + yearOfQuery);
        col_q4 = db.getCollection("q4_" + yearOfQuery);

        //collection.createIndex(new Document(MovieConstants.UID, 1));
        col_q1.createIndex(new Document(MovieConstants.UID, 1));
        col_q2.createIndex(new Document(MovieConstants.UID, 1));
        col_q3.createIndex(new Document(MovieConstants.UID, 1));
        col_q4.createIndex(new Document(MovieConstants.UID, 1));

        try {
            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/review_" + yearOfQuery + ".csv"));
            List<String> lines = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.length() > 2) {
                    lines.add(sCurrentLine);
                } else {
                    counter++;
                    upsertUser(lines);
                    lines.clear();

                    if (counter % 10000 == 0) {
                        System.out.println("Processed Reviews == " + counter);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(sCurrentLine);
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void upsertUser(List<String> lines) throws Exception {

        if (lines.size() == 8) {
            User user = MovieUtils.createUser(lines);
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put(MovieConstants.UID, user.getUserId());
            MongoCollection collection = getProperCollection(user.getPeriod());
            Document record = (Document) collection.find(whereQuery).first();
            if (record != null) {
                //   System.out.println("Updating Author Data : " + author.name  );
                collection.updateOne(new Document(MovieConstants.UID, user.getUserId()),
                        user.update(record));
            } else {
                collection.insertOne(user.getMongoDoc());
            }
        } else {
            System.out.println("------------------ Error Reviews = " + errorReviews++ + " ------------------ ");
            System.out.println("------------------------------------------\n Line Number Exception at Counter = " + counter + "\n");
            int i = 1;
            for (String line : lines) {
                System.out.println("[" + i++ + "] " + line);
            }
        }
    }

    private boolean checkQuarter(String period) {

        String[] qTags = period.trim().split("_");
        if (qTags[1].equalsIgnoreCase(yearOfQuery)) {
            return true;
        }
        return false;
    }

    private MongoCollection getProperCollection(String period) {

        String[] qTags = period.trim().split("_");

        switch (qTags[0]) {
            case "q1":
                return col_q1;
            case "q2":
                return col_q2;
            case "q3":
                return col_q3;
            case "q4":
                return col_q4;
        }
        return null;
    }


    public void parseMovieForStats() {

        reviewMap = new HashMap<>();
        BufferedReader br = null;
        String sCurrentLine = null;
        Calendar calendar = Calendar.getInstance();
        FileWriter writer = null;

        try {
            writer = new FileWriter("/home/abhishek/Downloads/ncsu/istudy/data/review_time.csv");
            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/movies.txt"));

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.length() > 2) {
                    timeStatistics(sCurrentLine, calendar);
                }
            }
            writeTimeStatistics(writer);
            System.out.println("Total Reviews  = " + counter);

        } catch (IOException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void writeTimeStatistics(FileWriter writer) throws Exception {

        writer.append("month");
        writer.append(",");
        writer.append("quarter");
        writer.append(",");
        writer.append("year");
        writer.append(",");
        writer.append("reviews");
        writer.append("\n");
        for (Map.Entry<String, Long> entry : reviewMap.entrySet()) {
            String[] calTime = entry.getKey().split("_");

            writer.append(CalendarUtils.getMonth(calTime[0]));
            writer.append(",");
            writer.append(CalendarUtils.getQuarter(calTime[0]));
            writer.append(",");
            writer.append(calTime[1]);
            writer.append(",");
            writer.append(entry.getValue() + "");
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }

    private void timeStatistics(String sCurrentLine, Calendar calendar) {

        String[] tags = sCurrentLine.split(":");
        String[] headers = tags[0].split("/");
        if (headers.length > 1 && headers[1].equalsIgnoreCase("time")) {
            long ts = Long.parseLong(tags[1].trim());
            calendar.setTimeInMillis(ts * 1000l);

            String time = (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.YEAR);
            if (reviewMap.containsKey(time)) {
                reviewMap.put(time, reviewMap.get(time) + 1l);
            } else {
                reviewMap.put(time, 1l);
            }
            counter++;
            if (counter % 500 == 0) {
                System.out.println("Reviews Processed = " + counter);
            }
        }
    }


    public static void main(String[] args) {
        MovieParser parser = new MovieParser();
        parser.addUsers();
    }
}
