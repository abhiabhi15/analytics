package abhi.amazon.movies.movie;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Author : abhishek
 * Created on 11/17/15.
 */
public class MovieReviewCollector {

    List<String> productIds = new LinkedList<>();

    public void getProductIds(){

        String fileName = "/home/abhishek/dstools/datamining/independent-study/amazon-movie-reviews/data/movies_300_plus_reviews.csv";
        try{

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split(",");
                productIds.add(tokens[0]);

            }

            for(String pid : productIds){
                System.out.println(pid);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void collectReviews(){

        List<String> reviewYears = Arrays.asList("2005", "2006", "2007", "2008", "2009", "2010", "2011");
        BufferedReader br = null;
        String sCurrentLine = null;
        List<String> lines = new ArrayList<>();

        try {
            for(String reviewYear : reviewYears){
                System.out.println("Reviewing Year = " + reviewYear);
                br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/movie/year/review_" + reviewYear + ".csv"));
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.length() > 2) {
                        lines.add(sCurrentLine);
                    }else{
                        if (lines.size() == 8) {
                            String productId = getMovieProductId(lines);
                            if(productId != null){
                                if(productIds.contains(productId)){
                                    writeIntoFile(productId, lines);
                                }
                            }
                        }
                        lines.clear();
                    }
                }
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeIntoFile(String productId, List<String> lines) throws  Exception{


        String fileName = "/home/abhishek/Downloads/ncsu/istudy/data/movie/movies_300/" + productId + ".txt";
        File file = new File(fileName);

        if(!file.exists()){
            file.createNewFile();
        }

        //true = append file
        FileWriter fileWriter = new FileWriter(fileName ,true);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(line).append("\n");
        }
        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    private String getMovieProductId(List<String> lines) {

        for(String line : lines){
            String[] tags = line.split(":");
            String[] headers = tags[0].split("/");
            if (headers.length > 1 && headers[1].equalsIgnoreCase("productId")) {
                return  tags[1].trim();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        Integer a = 100, b = 100;
        System.out.println(a == b);
        /*MovieReviewCollector collector =  new MovieReviewCollector();
        collector.getProductIds();
        collector.collectReviews();*/
    }

}
