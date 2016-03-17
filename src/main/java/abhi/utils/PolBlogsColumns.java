package abhi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Author : abhishek
 * Created on 11/29/15.
 */
public class PolBlogsColumns {

    private Set<String> sources;
    private List<String> rawSources;

    public void createColumns(){

        sources = new TreeSet<>();
        rawSources = new LinkedList<>();
        BufferedReader br = null;
        String sCurrentLine = null;
        FileWriter writer = null;

        try {
            writer = new FileWriter("/home/abhishek/dstools/datamining/independent-study/polblogs/data/polblogs_small_attr.csv");
            br = new BufferedReader(new FileReader("/home/abhishek/dstools/datamining/independent-study/polblogs/data/raw_small.csv"));

            while ((sCurrentLine = br.readLine()) != null) {
                rawSources.add(sCurrentLine);
                String[] sourceArr = sCurrentLine.split(",");
                sources.addAll(Arrays.asList(sourceArr));
            }

            StringBuilder sb = new StringBuilder();
            for(String source : sources){
                sb.append(source).append(",");
            }
            sb.append("\n");

            for(String rawSource : rawSources){
                List<String> sourceList = Arrays.asList(rawSource.split(","));
                for(String source : sources){
                    if(sourceList.contains(source)){
                        sb.append(1).append(",");
                    }else{
                        sb.append(0).append(",");
                    }
                }
                sb.append("\n");
            }

            writer.write(sb.toString());
            writer.flush();

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


    public static void main(String[] args) {

        PolBlogsColumns columns = new PolBlogsColumns();
        columns.createColumns();
    }

}
