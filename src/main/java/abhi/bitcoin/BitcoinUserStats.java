package abhi.bitcoin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 10/25/15.
 */
public class BitcoinUserStats {

    public static final String FILE_PATH = "/home/abhishek/Downloads/ncsu/istudy/data/bitcoin/";
    public static final String PRICE_FILE_PATH = "/home/abhishek/dstools/datamining/independent-study/data/bitcoin/";

    Map<String, BitcoinUser> userMap = new HashMap<>();
    Map<String, Float> conversionMap = new HashMap<>();

    public static final String FILTER_YEAR = "2010";
    public static final String FILTER_MONTH = "07";
    public static final String FILTER_DAY = "18";

    private void parseFile() {

        BufferedReader br;
        String line;
        float usdAmt;

        try {
            loadBTCToUSDConversionMap();
            br = new BufferedReader(new FileReader(FILE_PATH + "user_edges.txt"));
            long counter = 0;
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(",");
                String date = getDate(tokens[3]);
                if(allowedDate(date)){
                    BitcoinUser sender = userMap.get(tokens[1]);
                    BitcoinUser receiver = userMap.get(tokens[2]);
                    usdAmt = exchangeBTCToUSD(date, tokens[4]);

                    if(sender == null){
                        sender = new BitcoinUser();
                        sender.setUserId(tokens[1]);
                        sender.setTotalTxnsAsSender(1);
                        sender.setHighestTxnUSD(usdAmt);
                        sender.setTotalUSDSent(usdAmt);
                    }else{
                        sender.setTotalTxnsAsSender(sender.getTotalTxnsAsSender() + 1);
                        if(sender.getHighestTxnUSD() < usdAmt){
                            sender.setHighestTxnUSD(usdAmt);                         }
                        sender.setTotalUSDSent(sender.getTotalUSDSent() + usdAmt);
                    }

                    if(receiver == null){
                        receiver = new BitcoinUser();
                        receiver.setUserId(tokens[2]);
                        receiver.setTotalTxnsAsReceiver(1);
                        receiver.setHighestTxnUSD(usdAmt);
                        receiver.setTotalUSDReceived(usdAmt);
                    }else{
                        receiver.setTotalTxnsAsReceiver(receiver.getTotalTxnsAsReceiver() + 1);
                        if(receiver.getHighestTxnUSD() < usdAmt){
                            receiver.setHighestTxnUSD(usdAmt);
                        }
                        receiver.setTotalUSDReceived(receiver.getTotalUSDReceived() + usdAmt);
                    }
                    userMap.put(tokens[1], sender);
                    userMap.put(tokens[2], receiver);

                    if(++counter % 100000 == 0){
                        System.out.println("Line processed = " + counter);
                    }
                }
            }
            fileWriter();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void fileWriter() {

        BufferedWriter bw;
        BitcoinUser bitcoinUser;
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(0);

        sb.append("userid").append(",").append("total_usd_sent").append(",").append("total_usd_received").append(",");
        sb.append("num_sent_txns").append(",").append("num_received_txns").append(",").append("highest_usd_txn").append("\n");

        long counter = 0;
        try{
            bw = new BufferedWriter(new FileWriter(PRICE_FILE_PATH + "users-stats.csv"));
            for(Map.Entry<String, BitcoinUser> entry : userMap.entrySet()){
                 bitcoinUser = entry.getValue();
                 sb.append(bitcoinUser.getUserId()).append(",");
                 sb.append(df.format(bitcoinUser.getTotalUSDSent())).append(",");
                 sb.append(df.format(bitcoinUser.getTotalUSDReceived())).append(",");
                 sb.append(df.format(bitcoinUser.getTotalTxnsAsSender())).append(",");
                 sb.append(df.format(bitcoinUser.getTotalTxnsAsReceiver())).append(",");
                 sb.append(df.format(bitcoinUser.getHighestTxnUSD())).append("\n");

                if(++counter % 1000 == 0){
                    bw.write(sb.toString());
                    bw.flush();
                    sb.setLength(0);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadBTCToUSDConversionMap() {
        BufferedReader br;
        String line = null;

        try{
            br = new BufferedReader(new FileReader(PRICE_FILE_PATH + "bitcoin-usd-price.csv"));
            br.readLine();
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(",");
                if(tokens.length > 1){
                    conversionMap.put(tokens[0], Float.parseFloat(tokens[1]));
                }else{
                    System.out.println(line);
                }

            }
        }catch (Exception ex){
            System.out.println(line);
            ex.printStackTrace();
        }
    }

    private float exchangeBTCToUSD(String dateString, String btc) {

        Float usdRate = conversionMap.get(dateString);
        if(usdRate == null){
            System.out.println("Date = " +  dateString);
        }
        return usdRate * Float.parseFloat(btc);
    }

    private boolean allowedDate(String date) {

        String[] dateStr = date.split("-");
        if(Integer.parseInt(dateStr[0]) > Integer.parseInt(FILTER_YEAR)){
            return true;
        }

        if(Integer.parseInt(dateStr[0]) == Integer.parseInt(FILTER_YEAR) && Integer.parseInt(dateStr[1]) > Integer.parseInt(FILTER_MONTH)){
            return true;
        }

        return Integer.parseInt(dateStr[0]) == Integer.parseInt(FILTER_YEAR) &&
                Integer.parseInt(dateStr[1]) == Integer.parseInt(FILTER_MONTH) &&
                Integer.parseInt(dateStr[2]) > Integer.parseInt(FILTER_DAY);

    }

    private String getDate(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(token.substring(0, 4)).append("-");
        sb.append(token.substring(4, 6)).append("-");
        sb.append(token.substring(6, 8));
        return sb.toString();
    }

    public static void main(String[] args) {
        BitcoinUserStats userStats = new BitcoinUserStats();
        userStats.parseFile();
    }

}