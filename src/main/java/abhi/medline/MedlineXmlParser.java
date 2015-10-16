package abhi.medline;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author : abhishek
 * Created on 10/11/15.
 */
public class MedlineXmlParser {

    private static final String FILE_PATH = "/home/abhishek/dstools/analytics/src/main/resources/";
    private static final String MEDLINE_SAMPLE_XML_FILE_NAME = FILE_PATH + "medline/medsamp2015c.xml";

    private static final String YEAR_OF_INVESTIGATION = "1955";

    MongoDatabase db;
    BufferedWriter bufferedWriter = null;

    private Map<String, Integer> dateMap = new TreeMap<>();
    private Map<String, MedlineAuthor> authorMap = new HashMap<>();

    public void parseXmlForDates() throws Exception{

        InputStream sampleXmlStream = new FileInputStream(MEDLINE_SAMPLE_XML_FILE_NAME);
        MedlineXmlDeserializer medlineDeserializer = new MedlineXmlDeserializer(sampleXmlStream);

        while (medlineDeserializer.hasNext()) {

            Object next = medlineDeserializer.next();
            if (next instanceof MedlineCitation) {
                MedlineCitation nextCitation = (MedlineCitation) next;
                getDateMap(MedlineConstants.DATE_PUBLISHED, nextCitation);
            }
        }

        for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

    }

    public void parseXml() throws Exception {

        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(MedlineConstants.MEDLINE);
        MongoCollection collection = db.getCollection("m_"+YEAR_OF_INVESTIGATION);
        collection.createIndex(new Document(MedlineConstants.A_ID, 1));

        InputStream sampleXmlStream = new FileInputStream(MEDLINE_SAMPLE_XML_FILE_NAME);
        MedlineXmlDeserializer medlineDeserializer = new MedlineXmlDeserializer(sampleXmlStream);

        String fileName = "m_" + YEAR_OF_INVESTIGATION + ".csv";
        bufferedWriter = new BufferedWriter(new FileWriter(FILE_PATH + fileName, true));

        while (medlineDeserializer.hasNext()) {

            Object next = medlineDeserializer.next();
            if (next instanceof MedlineCitation) {
                MedlineCitation nextCitation = (MedlineCitation) next;
                if(nextCitation.getDateCreated().getYear().equalsIgnoreCase(YEAR_OF_INVESTIGATION)){
                    if(nextCitation.getArticle().getAuthorList() != null){
                        createAuthorNetwork(nextCitation);
                        createCitationNetwork(nextCitation);
                    }
                }
            }
        }

        BasicDBObject whereQuery = null;
        for (Map.Entry<String, MedlineAuthor> entry : authorMap.entrySet()) {

            MedlineAuthor medlineAuthor = entry.getValue();
            whereQuery = new BasicDBObject();
            whereQuery.put(MedlineConstants.A_ID, medlineAuthor.getAuthorID());
            Document record = (Document) collection.find(whereQuery).first();
            if(record != null){
                collection.updateOne(new Document(MedlineConstants.A_ID, medlineAuthor.getAuthorID()),
                        medlineAuthor.update(record));
            }else {
                collection.insertOne(medlineAuthor.getMongoDoc());
            }
        }

    }

    private void createAuthorNetwork(MedlineCitation citation) {

        List<MedlineCitation.Author> authorList = citation.getArticle().getAuthorList().getAuthors();

        for(MedlineCitation.Author author : authorList){

            String authorID = MedlineUtils.getAuthorID(author);
            MedlineAuthor medlineAuthor = null;
            Document meshMajor = null;
            Document meshMinor = null;

            if(authorMap.containsKey(authorID)){
                medlineAuthor = authorMap.get(authorID);
                medlineAuthor.setNumArticles(medlineAuthor.getNumArticles() + 1);

            }else{
                medlineAuthor = new MedlineAuthor();
                medlineAuthor.setAuthorID(authorID);
                medlineAuthor.setName(MedlineUtils.getAuthorName(author));
                medlineAuthor.setYear(YEAR_OF_INVESTIGATION);
                medlineAuthor.setNumArticles(1);

                meshMajor = new Document();
                meshMinor = new Document();

                medlineAuthor.setMeshMajor(meshMajor);
                medlineAuthor.setMeshMinor(meshMinor);
            }
            populateAuthorMapAttrs(citation, medlineAuthor);
            authorMap.put(authorID, medlineAuthor);
        }
    }

    private void populateAuthorMapAttrs(MedlineCitation citation, MedlineAuthor medlineAuthor) {

        if(citation.getMeshHeadingList() != null && citation.getMeshHeadingList().getMeshHeadings() != null){
            for(MedlineCitation.MeshHeading meshHeading : citation.getMeshHeadingList().getMeshHeadings()){

                String meshDescrptName = MedlineUtils.formatMesh(meshHeading.getDescriptorName().getName());
                populateDocument(medlineAuthor, meshDescrptName, meshHeading.getDescriptorName().getMajorTopicYn());

                if(meshHeading.getQualifierNames() != null){
                    for(MedlineCitation.MeshQualifierName qualifierName : meshHeading.getQualifierNames()){
                        String qName = MedlineUtils.formatMesh(qualifierName.getName());
                        populateDocument(medlineAuthor, qName, qualifierName.getMajorTopicYn());
                    }
                }
            }
        }

    }

    private void populateDocument(MedlineAuthor medlineAuthor, String name, String majorTopicYn) {

        if(majorTopicYn.equalsIgnoreCase("Y")){
            if(medlineAuthor.getMeshMajor().containsKey(name)){
                medlineAuthor.getMeshMajor().put(name, (int)medlineAuthor.getMeshMajor().get(name) +1);
            }else{
                medlineAuthor.getMeshMajor().put(name, 1);
            }
        }else{
            if(medlineAuthor.getMeshMinor().containsKey(name)){
                medlineAuthor.getMeshMinor().put(name, (int)medlineAuthor.getMeshMinor().get(name) +1);
            }else{
                medlineAuthor.getMeshMinor().put(name, 1);
            }
        }

    }

    private void createCitationNetwork(MedlineCitation citation) throws Exception{

        List<MedlineCitation.Author> authors = citation.getArticle().getAuthorList().getAuthors();
        if(authors.size() < 2){
            return;
        }
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < authors.size(); i++){
            for(int j = i+1; j < authors.size(); j++){
                sb.append(MedlineUtils.getAuthorID(authors.get(i))).append(",");
                sb.append(MedlineUtils.getAuthorID(authors.get(j))).append(",");
                sb.append(citation.getPmid().getPmid()).append(",");
                sb.append(citation.getDateCreated().getYear()).append("\n");
            }
        }
        bufferedWriter.write(sb.toString());
        bufferedWriter.flush();
    }


    private void getDateMap(String dateType, MedlineCitation nextCitation) {

        String year = null;
        switch (dateType){
            case MedlineConstants.DATE_CREATED :
                year = nextCitation.getDateCreated().getYear();
                break;
            case MedlineConstants.DATE_COMPLETED:
                year = nextCitation.getDateCompleted().getYear();
                break;
            case MedlineConstants.DATE_PUBLISHED:
                year = nextCitation.getArticle().getJournal().getJournalIssue().getPubDate().getYear();
                break;
        }

        if (dateMap.containsKey(year)) {
            dateMap.put(year, dateMap.get(year) + 1);
        } else {
            dateMap.put(year, 1);
        }
    }

    public static void main(String[] args) {

        MedlineXmlParser xmlParser = new MedlineXmlParser();
        try {
          //  xmlParser.parseXml();
            xmlParser.parseXmlForDates();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
