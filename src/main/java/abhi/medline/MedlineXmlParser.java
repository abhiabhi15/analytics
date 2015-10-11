package abhi.medline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 10/11/15.
 */
public class MedlineXmlParser {

    private static final String MEDLINE_SAMPLE_XML_FILE_NAME = "/home/abhishek/dstools/analytics/src/main/resources/medsamp2015a.xml";

    Map<String, Integer> createdDateMap = new HashMap<>();

    public void parseXml() throws FileNotFoundException, IOException {
        InputStream sampleXmlStream = new FileInputStream(MEDLINE_SAMPLE_XML_FILE_NAME);
        MedlineXmlDeserializer medlineDeserializer = new MedlineXmlDeserializer(sampleXmlStream);


        while (medlineDeserializer.hasNext()) {
            Object next = medlineDeserializer.next();
            if (next instanceof MedlineCitation) {
                MedlineCitation nextCitation = (MedlineCitation) next;
                getCreatedDateMap(nextCitation);
            }
        }

        for (Map.Entry<String, Integer> entry : createdDateMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private void getCreatedDateMap(MedlineCitation nextCitation) {

        String year = nextCitation.getDateCreated().getYear();
        if (createdDateMap.containsKey(year)) {
            createdDateMap.put(year, createdDateMap.get(year) + 1);
        } else {
            createdDateMap.put(year, 1);
        }
    }

    public static void main(String[] args) {

        MedlineXmlParser xmlParser = new MedlineXmlParser();
        try {
            xmlParser.parseXml();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
