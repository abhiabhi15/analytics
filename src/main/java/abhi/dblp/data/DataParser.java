package abhi.dblp.data;

import abhi.dblp.DblpConstants;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Author : abhishek
 * Created on 3/5/16.
 */
public class DataParser {

    MongoDatabase db;
    Map<String, Integer> authorIdMap;
    Map<String, DblpAuthor> authorDataMap;
    Map<String, DblpAuthor> authorCumDataMap;
    Map<String, Boolean> edgeMap;
    Map<String, Boolean> edgeCumMap;
    Map<String, List<Field>> journalFieldMap;
    static Integer counter;
    private static final String FILE_PATH = "/home/abhishek/Downloads/ncsu/istudy/data/dblp/attributed_graph/";

    void populateDataMap(){

        authorDataMap = new LinkedHashMap<>();
        authorCumDataMap = new LinkedHashMap<>();
        authorIdMap = new HashMap<>();
        edgeMap = new LinkedHashMap<>();
        edgeCumMap = new LinkedHashMap<>();
        populateJournalMap();
        counter = 1;

        List<String> timeStr = new LinkedList<>(Arrays.asList("a_1991", "a_1992", "a_1993", "a_1994",
                "a_1995", "a_1996", "a_1997", "a_1998", "a_1999", "a_2000"));

//        List<String> timeStr = new LinkedList<>(Arrays.asList("a_1991", "a_1992"));

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(DblpConstants.DBLP);

        for(String colString : timeStr){
            FindIterable<Document> iter = db.getCollection(colString).find();
            iter.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    processDoc(document);
                }
            });

            writeEdgeList(colString);
            writeAttrList(colString);

            edgeMap.clear();
            authorDataMap.clear();
        }

    }

    private void populateJournalMap() {
        journalFieldMap = new HashMap<>();
        BufferedReader br = null;
        String sCurrentLine = null;
        try{
            br = new BufferedReader(new FileReader("/home/abhishek/Downloads/ncsu/istudy/data/dblp/journal.tsv"));
            br.readLine();
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.length() > 2) {
                    String[] items = sCurrentLine.split("\t");
                    String journal = items[0].trim();
                    List<Field> fieldList = new ArrayList<>();
                    for(int j = 1; j < items.length; j++){
                        fieldList.add(Field.getValue(items[j]));
                    }
                    journalFieldMap.put(journal, fieldList);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void processDoc(Document document) {
          String uid = document.getString(DblpConstants.UID);
          updateAuthorIdMap(uid);
          List<Document> papers = (List<Document>) document.get(DblpConstants.PAPERS);
          for(Document paper: papers){
              List<String> coAuthors = (List<String>) paper.get(DblpConstants.CO_AUTHORS);
              String journal = paper.getString(DblpConstants.JOURNAL);
              updateAuthorDataMap(uid, journal);
              if(coAuthors != null){
                  for (String coAuthor : coAuthors){
                      coAuthor = coAuthor.replaceAll("\\s","");
                      updateAuthorIdMap(coAuthor);
                      updateAuthorDataMap(coAuthor, journal);
                  }
                  coAuthors.add(uid);
                  createEdges(coAuthors);
              }
          }
    }

    private void updateAuthorDataMap(String uid, String journal) {

        //System.out.println("Author ID = " + uid + " Journal = " + journal);

        DblpAuthor author = null;
        List<Field> fieldList = journalFieldMap.get(journal);
        if (authorDataMap.containsKey(uid)){
            author = authorDataMap.get(uid);
            author.setNumArticles(author.getNumArticles()+1);
        }else{
            author = new DblpAuthor();
            author.setUserId(uid);
            author.setId(authorIdMap.get(uid));
            author.setNumArticles(1);
        }
        author.updateJournalMap(fieldList);
        authorDataMap.put(uid, author);

        if (authorCumDataMap.containsKey(uid)){
            author = authorCumDataMap.get(uid);
            author.setNumArticles(author.getNumArticles()+1);
        }else{
            author = new DblpAuthor();
            author.setUserId(uid);
            author.setId(authorIdMap.get(uid));
            author.setNumArticles(1);
        }
        author.updateJournalMap(fieldList);
        authorCumDataMap.put(uid, author);
    }

    private void createEdges(List<String> coAuthors) {

         if (coAuthors.size() < 2){
             return;
         }

         for (int i= 0; i < coAuthors.size(); i++){
             for (int j = i+1; j < coAuthors.size(); j++ ){
                 String edge = authorIdMap.get(coAuthors.get(i).replaceAll("\\s", "")) + "\t" + authorIdMap.get(coAuthors.get(j).replaceAll("\\s", ""));
                 if(!edgeMap.containsKey(edge)){
                     edgeMap.put(edge, Boolean.TRUE);
                 }
                 if(!edgeCumMap.containsKey(edge)){
                     edgeCumMap.put(edge, Boolean.TRUE);
                 }
             }
         }
    }

    private void updateAuthorIdMap(String uid) {
        if (!authorIdMap.containsKey(uid)){
            authorIdMap.put(uid, counter++);
        }
    }

    private void writeEdgeList(String timeStr){

        FileWriter writer = null;
        FileWriter cumwriter = null;
        try{
            writer = new FileWriter(FILE_PATH + timeStr + ".edgelist");

            for( Map.Entry<String, Boolean> entry: edgeMap.entrySet()){
                writer.append(entry.getKey());
                writer.append("\n");
            }
            writer.flush();

            cumwriter = new FileWriter(FILE_PATH + timeStr + ".cum.edgelist");

            for( Map.Entry<String, Boolean> entry: edgeCumMap.entrySet()){
                cumwriter.append(entry.getKey());
                cumwriter.append("\n");
            }
            cumwriter.flush();


        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeAttrList(String timeStr) {

        FileWriter writer = null;
        FileWriter cumwriter = null;
        try{
            writer = new FileWriter(FILE_PATH + timeStr + ".attrlist");
            writer.append("id");writer.append("\t");writer.append("numOfArticles");writer.append("\t");
            for( Field field : Field.values()){
                writer.append(field.toString());
                writer.append("\t");
            }
            writer.append("\n");

            for( Map.Entry<String, DblpAuthor> entry: authorCumDataMap.entrySet()){
                writer.append(String.valueOf(entry.getValue().getId()));
                writer.append("\t");
                if(authorDataMap.containsKey(entry.getKey())){
                    writer.append(String.valueOf(authorDataMap.get(entry.getKey()).getNumArticles()));
                }else{
                    writer.append("0");
                }
                writer.append("\t");
                for( Field field : Field.values()){
                    if(authorDataMap.containsKey(entry.getKey())){
                        if (authorDataMap.get(entry.getKey()).fieldMap.containsKey(field)){
                            writer.append(String.valueOf(authorDataMap.get(entry.getKey()).fieldMap.get(field)));
                        }else {
                            writer.append("0");
                        }
                    }else{
                        writer.append("0");
                    }
                    writer.append("\t");
                }
                writer.append("\n");
            }
            writer.flush();

            cumwriter = new FileWriter(FILE_PATH + timeStr + ".cum.attrlist");
            cumwriter.append("id");cumwriter.append("\t");cumwriter.append("numOfArticles");cumwriter.append("\t");
            for( Field field : Field.values()){
                cumwriter.append(field.toString());
                cumwriter.append("\t");
            }
            cumwriter.append("\n");

            for( Map.Entry<String, DblpAuthor> entry: authorCumDataMap.entrySet()){
                cumwriter.append(String.valueOf(entry.getValue().getId()));
                cumwriter.append("\t");
                cumwriter.append(String.valueOf(entry.getValue().getNumArticles()));
                cumwriter.append("\t");
                for( Field field : Field.values()){
                    if (entry.getValue().fieldMap.containsKey(field)){
                        cumwriter.append(String.valueOf(entry.getValue().fieldMap.get(field)));
                    }else{
                        cumwriter.append("0");
                    }
                    cumwriter.append("\t");
                }
                cumwriter.append("\n");
            }
            cumwriter.flush();


        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        DataParser parser = new DataParser();
        parser.populateDataMap();
    }

}

