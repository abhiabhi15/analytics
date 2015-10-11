package abhi.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Author : abhishek
 * Created on 9/12/15.
 */
public class TimeZoneDB {

    private static final TimeZoneDB instance = new TimeZoneDB();
    private String timezoneDbKey;

    public static TimeZoneDB getInstance() {
        return instance;
    }

    public void setKey(String key) {
        this.timezoneDbKey = key;
    }

    public String getTimezoneId(double latitude, double longitude) {

        StringBuffer buf = new StringBuffer();
        try {
            URL url = new URL("http://api.timezonedb.com/?lat=" + latitude
                    + "&lng=" + longitude + "&format=xml"
                    + "&key=" + timezoneDbKey);

            URLConnection con = url.openConnection();
            con.setConnectTimeout(15000); // 5s
            con.setReadTimeout(16000);
            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            reader.close();

            int start = buf.indexOf("<gmtOffset>", 0) + "<gmtOffset>".length();
            int end = buf.indexOf("</gmtOffset>", 0);

            return buf.substring(start, end);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getGMTOffsets() {

        String csvFile = "/home/abhishek/dstools/streaming-apps/apis/mooc_world_user.csv";
        FileWriter writer = null;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        TimeZoneDB timeZoneDB = TimeZoneDB.getInstance();
        timeZoneDB.setKey("5525QNGWUIKJ");


        try {
            writer = new FileWriter("/home/abhishek/Downloads/offset2.csv");
            br = new BufferedReader(new FileReader(csvFile));
            int counter = 0;
            int i = 0;
            int faults = 17;
            br.readLine();
            while (i < (faults + 7370)) {
                br.readLine();
                i++;
            }

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] userData = line.split(cvsSplitBy);
                Double lat, lng = null;
                String tz = "UTC";

                if (userData.length != 3) {
                    faults++;
                    System.out.println("Faults = " + faults);
                    System.out.println(line);
                    tz = "NA";
                    if (userData.length == 0) {
                        continue;
                    }
                } else {
                    try {

                        lat = Double.parseDouble(userData[1]);
                        lng = Double.parseDouble(userData[2]);
                        String gmtOffset = timeZoneDB.getTimezoneId(lat, lng);
                        Float gmt = Float.parseFloat(gmtOffset);
                        if (gmt >= 0) {
                            tz += "+";
                        }
                        tz += String.format("%.2f", gmt / 3600f);
                    } catch (NumberFormatException ex) {
                        System.err.println("Error in line lat = " + userData[1] + ", and lng = " + userData[2]);
                        tz += "-4.00";
                        System.out.println("Processed Request = " + counter);
                    }
                }

                writer.append(userData[0]);
                writer.append(",");
                writer.append(tz);
                writer.append('\n');
                writer.flush();
                counter++;
                if (counter % 50 == 0) {
                    System.out.println("Processed Request = " + counter);
                }
            }
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

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
        getGMTOffsets();
    }

}
