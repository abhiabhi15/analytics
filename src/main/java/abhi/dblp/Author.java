package abhi.dblp;

import org.bson.Document;

import java.util.List;

/**
 * Author : abhishek
 * Created on 9/11/15.
 */
public class Author {

    String uid;
    String name;
    List<Document> papers;

    public Document getMongoDoc() {

        Document doc = new Document(DblpConstants.UID, uid)
                            .append(DblpConstants.NAME, name)
                            .append(DblpConstants.PAPERS, papers);
        return doc;
    }

    public Document update(Document author) {

        List<Document> allPapers = (List<Document>) author.get(DblpConstants.PAPERS);
        allPapers.addAll(papers);
        return new Document("$set", new Document(DblpConstants.PAPERS, allPapers));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Document> getPapers() {
        return papers;
    }

    public void setPapers(List<Document> papers) {
        this.papers = papers;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
