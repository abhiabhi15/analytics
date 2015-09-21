package abhi.utils;

import org.json.simple.JSONObject;

import java.io.*;

/**
 * Author : abhishek
 * Created on 9/14/15.
 */
public class WriteToJson {


    public static void writeJSON(){

        String fileLoc = "/home/abhishek/dstools/streaming-apps/apis/";
        String csvFile = fileLoc + "mooc_user.csv";
        FileWriter writer = null;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            writer = new FileWriter(fileLoc + "mooc.json");
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();

            JSONObject userObj = new JSONObject();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] userData = line.split(cvsSplitBy);
                JSONObject tzObj = new JSONObject();
                tzObj.put("lat", userData[1]);
                tzObj.put("lon", userData[2]);
                userObj.put(userData[0], tzObj);
            }
            jsonObject.put("timezone", userObj);
            writer.write(jsonObject.toJSONString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        writeJSON();
    }

}
