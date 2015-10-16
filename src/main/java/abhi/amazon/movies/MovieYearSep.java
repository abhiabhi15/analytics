package abhi.amazon.movies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : abhishek
 * Created on 9/23/15.
 */
public class MovieYearSep {

    long counter = 0l;
    private static final String FILE_PATH = "/home/abhishek/Downloads/ncsu/istudy/data/movie/year/";

    public void writeFilesByYear() {


        BufferedReader br = null;
        String sCurrentLine = null;
        FileWriter writer_2000,writer_2001,writer_2002,writer_2003, writer_2004, writer_2005, writer_2006, writer_2007, writer_2008, writer_2009,
                writer_2010, writer_2011, writer_2012 = null;

        try {
            writer_2000 = new FileWriter(FILE_PATH + "review_2000.csv");
            writer_2001 = new FileWriter(FILE_PATH + "review_2001.csv");
            writer_2002 = new FileWriter(FILE_PATH + "review_2002.csv");
            writer_2003 = new FileWriter(FILE_PATH + "review_2003.csv");
            /*writer_2004 = new FileWriter(FILE_PATH + "review_2004.csv");
            writer_2005 = new FileWriter(FILE_PATH + "review_2005.csv");
            writer_2006 = new FileWriter(FILE_PATH + "review_2006.csv");
            writer_2007 = new FileWriter(FILE_PATH + "review_2007.csv");
            writer_2008 = new FileWriter(FILE_PATH + "review_2008.csv");
            writer_2009 = new FileWriter(FILE_PATH + "review_2009.csv");
            writer_2010 = new FileWriter(FILE_PATH + "review_2010.csv");
            writer_2011 = new FileWriter(FILE_PATH + "review_2011.csv");
            writer_2012 = new FileWriter(FILE_PATH + "review_2012.csv");
*/
            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/movie/movies.txt"));

            List<String> lines = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.length() > 2) {
                    lines.add(sCurrentLine);
                } else {
                    counter++;
                    if (lines.size() == 8) {
                        User user = MovieUtils.createUser(lines);
                        String[] qTags = user.getPeriod().trim().split("_");
                        switch (qTags[1]) {
                            case "2000":
                                writeLines(writer_2000, lines);
                                break;
                            case "2001":
                                writeLines(writer_2001, lines);
                                break;
                            case "2002":
                                writeLines(writer_2002, lines);
                                break;
                           case "2003":
                                writeLines(writer_2003, lines);
                                break;
                           /* case "2004":
                                writeLines(writer_2004, lines);
                                break;
                            case "2005":
                                writeLines(writer_2005, lines);
                                break;
                            case "2006":
                                writeLines(writer_2006, lines);
                                break;
                            case "2007":
                                writeLines(writer_2007, lines);
                                break;
                            case "2008":
                                writeLines(writer_2008, lines);
                                break;
                            case "2009":
                                writeLines(writer_2009, lines);
                                break;
                            case "2010":
                                writeLines(writer_2010, lines);
                                break;
                            case "2011":
                                writeLines(writer_2011, lines);
                                break;
                            case "2012":
                                writeLines(writer_2012, lines);
                                break;*/
                        }
                    }
                    lines.clear();
                    if (counter % 100000 == 0) {
                        System.out.println("Processed Reviews == " + counter);
                    }
                }
            }
            writer_2000.close();
            writer_2001.close();
            writer_2002.close();
            writer_2003.close();
            /*writer_2004.close();
            writer_2005.close();
            writer_2006.close();
            writer_2007.close();
            writer_2008.close();
            writer_2009.close();
            writer_2010.close();
            writer_2011.close();
            writer_2012.close();
*/
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

    private void writeLines(FileWriter writer, List<String> lines) throws Exception {

        for (String line : lines) {
            writer.append(line);
            writer.append("\n");
        }
        writer.append("\n");
        writer.flush();
    }


    public static void main(String[] args) {
        MovieYearSep yearSep = new MovieYearSep();
        yearSep.writeFilesByYear();
    }


}
