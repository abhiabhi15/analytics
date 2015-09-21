package abhi.amazon.movies;


import abhi.utils.CalendarUtils;
import com.mongodb.client.MongoDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 9/18/15.
 */
public class MovieParser {

    MongoDatabase db;
    Map<String, Long> reviewMap;
    long counter = 0l;

    public void parseMovie(){

        reviewMap = new HashMap<>();
        BufferedReader br = null;
        String sCurrentLine = null;
        Calendar calendar = Calendar.getInstance();
        FileWriter writer = null;

        try {
            writer = new FileWriter("/home/abhishek/Downloads/ncsu/istudy/data/review_time.csv");
            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/movies.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                if(sCurrentLine.length() > 2){
                    timeStatistics(sCurrentLine, calendar);
                }
            }
            writeTimeStatistics(writer);
            System.out.println("Total Reviews  = " + counter);
        } catch (IOException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void writeTimeStatistics(FileWriter writer) throws Exception{

        writer.append("month"); writer.append(",");
        writer.append("quarter"); writer.append(",");
        writer.append("year"); writer.append(",");
        writer.append("reviews");
        writer.append("\n");
        for(Map.Entry<String, Long> entry : reviewMap.entrySet()){
            String[] calTime = entry.getKey().split("_");

            writer.append(CalendarUtils.getMonth(calTime[0]));writer.append(",");
            writer.append(CalendarUtils.getQuarter(calTime[0]));writer.append(",");
            writer.append(calTime[1]);writer.append(",");
            writer.append(entry.getValue()+"");
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }

    private void timeStatistics(String sCurrentLine, Calendar calendar) {

        String[] tags = sCurrentLine.split(":");
        String[] headers = tags[0].split("/");
        if(headers.length > 1 && headers[1].equalsIgnoreCase("time")){
            long ts = Long.parseLong(tags[1].trim());
            calendar.setTimeInMillis(ts * 1000l);

            String time = (calendar.get(Calendar.MONTH)+1) + "_"+calendar.get(Calendar.YEAR);
            if(reviewMap.containsKey(time)){
                reviewMap.put(time, reviewMap.get(time)+1l);
            }else{
                reviewMap.put(time, 1l);
            }
            counter++;
            if(counter % 100000 ==0){
                System.out.println("Reviews Processed = " + counter);
            }
        }
    }


    public static void main(String[] args) {
        MovieParser parser = new MovieParser();
        parser.parseMovie();
    }
}
