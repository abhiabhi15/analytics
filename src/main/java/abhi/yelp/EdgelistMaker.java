package abhi.yelp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Author : abhishek
 * Created on 3/19/16.
 */
public class EdgelistMaker {

    Map<String, Integer> userIdMap = new HashMap<>();
    Map<String, Map<String, Set<Integer>>> usrReviewMap = new TreeMap<>();
    static Integer ucount = 1;
    static int lcount = 1;
    static final int START_YEAR = 2004;

    private void makeEdgeList() {

        BufferedReader br = null;
        try{
            String sCurrentLine = null;
            br = new BufferedReader(new FileReader(YelpConstants.FILENAME));
            while ((sCurrentLine = br.readLine()) != null) {
                parseAndAnalyzeReviewJson(sCurrentLine);
                if (lcount % 20000 == 0){
                    System.out.println("Processed Lines = " + lcount);
                }
                lcount++;
            }
            for( Map.Entry<String, Map<String, Set<Integer>>> entry : usrReviewMap.entrySet() ){
                String year = entry.getKey();
                System.out.println("Writing EdgeList for year = " + year);
                writeEdgeList(year, "");
                writeEdgeList(year, ".cum");
            }
        writeUserIdMap();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeEdgeList(String year, String append) {

        FileWriter writer = null;
        int fyear = Integer.parseInt(year);
        int syear = fyear;

        if (append != ""){
            syear = START_YEAR;
        }

        try{
            writer = new FileWriter(YelpConstants.FILE_PATH + "graph/yelp_" + year + append + ".edgelist");
            while (syear <= fyear){
                String yearStr = String.valueOf(syear);
                for (Map.Entry<String, Set<Integer>> entry : usrReviewMap.get(yearStr).entrySet()){
                    List<Integer> userList = new ArrayList<>();
                    userList.addAll(entry.getValue());
                    for (int i = 0; i < userList.size()-1; i++){
                        for (int j= i+1; j < userList.size(); j++){
                            writer.append(String.valueOf(userList.get(i)));
                            writer.append("\t");
                            writer.append(String.valueOf(userList.get(j)));
                            writer.append("\n");
                        }
                    }
                }
                syear++;
            }
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeUserIdMap() {
        System.out.println("Writing User Id Map in File");
        FileWriter writer = null;
        try{
            writer = new FileWriter(YelpConstants.FILE_PATH + "yelp_user.txt");
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

    private void parseAndAnalyzeReviewJson(String line) {

        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(line);
            JSONObject jsonObject = (JSONObject) obj;
            String type = (String) jsonObject.get("type");
            if (type.equalsIgnoreCase("review")){

                String userId = (String) jsonObject.get("user_id");
                String businessId = (String) jsonObject.get("business_id");
                String date = (String) jsonObject.get("date");
                String year = date.split("-")[0];
                if (!userIdMap.containsKey(userId)){
                    userIdMap.put(userId, ucount++);
                }
                updateReviewMap(year, businessId, userId);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void updateReviewMap(String year, String businessId, String userId) {
         Map<String, Set<Integer>> usrListMap = null;
         if (!usrReviewMap.containsKey(year)){
             usrListMap = new LinkedHashMap<>();
             usrReviewMap.put(year, usrListMap);
         }
         usrListMap = usrReviewMap.get(year);
         Set<Integer> uidSet = null;
         if (usrListMap.containsKey(businessId)){
             uidSet = usrListMap.get(businessId);
         }else{
             uidSet = new HashSet<>();
         }
         uidSet.add(userIdMap.get(userId));
         usrListMap.put(businessId, uidSet);
    }

    public static void main(String[] args) {
        EdgelistMaker maker = new EdgelistMaker();
        maker.makeEdgeList();
    }

}
