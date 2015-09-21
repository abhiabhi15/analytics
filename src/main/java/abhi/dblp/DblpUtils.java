package abhi.dblp;

import org.bson.Document;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : abhishek
 * Created on 9/11/15.
 */
public class DblpUtils {


    public static List<Author> parseAuthors(JSONArray jsonArray) {

        JSONArray authorArray = (JSONArray) jsonArray.get(2);
        String journal =  (String)jsonArray.get(5);
        String title =  (String)jsonArray.get(4);

        List<Author> authors = new ArrayList<>();

        for(int i = 0; i < authorArray.size(); i++){

            String authorName = (String)authorArray.get(i);
            Author author = new Author();
            author.setUid(authorName.replaceAll("\\s",""));
            author.setName(authorName);

            List<Document> publications = new ArrayList<>();
            Document publication = new Document();
            publication.append(DblpConstants.JOURNAL, journal);
            publication.append(DblpConstants.TITLE, title);

            JSONArray coAuthors = getCoAuthors(authorArray, i);
            if(coAuthors.size() > 0){
                publication.append(DblpConstants.CO_AUTHORS, getCoAuthors(authorArray, i));
            }
            publications.add(publication);

            author.setPapers(publications);
            authors.add(author);
        }
        return authors;
    }

    private static JSONArray getCoAuthors(JSONArray authorArray, int exclude) {

        JSONArray coAuthors = new JSONArray();
        for(int i = 0; i < authorArray.size(); i++){
            if(i == exclude){
                continue;
            }
            coAuthors.add((String) authorArray.get(i));
        }
        return coAuthors;
    }

}
