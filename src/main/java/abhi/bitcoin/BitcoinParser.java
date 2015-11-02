package abhi.bitcoin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author : abhishek
 * Created on 10/25/15.
 */
public class BitcoinParser {

   public static final String FILE_PATH = "/home/abhishek/Downloads/ncsu/istudy/data/bitcoin/";

   Map<String, Float> dayMap = new TreeMap<>();

    private void parseFile() {

        BufferedReader br = null;
        Calendar calendar = Calendar.getInstance();
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        NumberFormat formatter = new DecimalFormat("00");

        try {
             br = new BufferedReader(new FileReader(FILE_PATH + "user_edges.txt"));
              long counter = 0;
              while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                setDate(calendar, tokens[3]);

                stringBuilder.setLength(0);
                stringBuilder.append(calendar.get(Calendar.YEAR)).append("_");
                stringBuilder.append(formatter.format(calendar.get(Calendar.MONTH) + 1)).append("_");
                stringBuilder.append(formatter.format(calendar.get(Calendar.DAY_OF_MONTH)));
                String key = stringBuilder.toString();

                if(dayMap.containsKey(key)){
                    dayMap.put(key, dayMap.get(key)+ Float.parseFloat(tokens[4]));
                }else{
                    dayMap.put(key, Float.parseFloat(tokens[4]));
                }

                counter++;
                if(counter % 100000 == 0){
                    System.out.println("Line processed = " + counter);
                }

              }

              StringBuilder sb = new StringBuilder();
              sb.append("Date").append(",");
              sb.append("value").append("\n");
              for(Map.Entry<String, Float> entry : dayMap.entrySet()){

                  String[] tokens = entry.getKey().split("_");
                  sb.append(tokens[0]).append("-").append(tokens[1]).append("-").append(tokens[2]).append(",");
                  sb.append(entry.getValue()).append("\n");
              }
              System.out.println(sb.toString());

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void setDate(Calendar calendar, String token) {

        calendar.set(Calendar.YEAR, Integer.parseInt(token.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(token.substring(4,6)) -1 );
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(token.substring(6,8)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(token.substring(8,10)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(token.substring(10,12)));
        calendar.set(Calendar.SECOND, Integer.parseInt(token.substring(12,14)));

    }


    public static void main(String[] args) {
        BitcoinParser parser = new BitcoinParser();
        parser.parseFile();
    }




}
