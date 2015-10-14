package abhi.medline;

import org.bson.Document;

import java.util.Map;

/**
 * Author : abhishek
 * Created on 10/12/15.
 */
public class MedlineAuthor {

    private String authorID;
    private String name;
    private int numArticles;
    private String year;
    private Document meshMajor;
    private Document meshMinor;

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumArticles() {
        return numArticles;
    }

    public void setNumArticles(int numArticles) {
        this.numArticles = numArticles;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Document getMeshMajor() {
        return meshMajor;
    }

    public void setMeshMajor(Document meshMajor) {
        this.meshMajor = meshMajor;
    }

    public Document getMeshMinor() {
        return meshMinor;
    }

    public void setMeshMinor(Document meshMinor) {
        this.meshMinor = meshMinor;
    }

    @Override
    public String toString() {
        return "MedlineAuthor{" +
                "authorID='" + authorID + '\'' +
                ", name='" + name + '\'' +
                ", numArticles=" + numArticles +
                ", year='" + year + '\'' +
                ", meshMajor=" + meshMajor +
                ", meshMinor=" + meshMinor +
                '}';
    }

    public Object getMongoDoc() {
        Document doc = new Document(MedlineConstants.A_ID, authorID)
                .append(MedlineConstants.NAME, name)
                .append(MedlineConstants.NUM_OF_ARTICLES, numArticles)
                .append(MedlineConstants.MESH_MAJOR, meshMajor)
                .append(MedlineConstants.MESH_MINOR, meshMajor);
        return doc;
    }

    public Document update(Document medlineAuthor) {

        int totNumArticles = (int) medlineAuthor.get(MedlineConstants.NUM_OF_ARTICLES);
        numArticles += totNumArticles;

        Document totalMeshMajor = (Document) medlineAuthor.get(MedlineConstants.MESH_MAJOR);
        Document totalMeshMinor = (Document) medlineAuthor.get(MedlineConstants.MESH_MINOR);

        for(Map.Entry<String, Object> meshHeading : totalMeshMajor.entrySet()){
            int value = (int) meshHeading.getValue();
            if(meshMajor.containsKey(meshHeading.getKey())){
                value += (int)meshMajor.get(meshHeading.getKey());
                meshMajor.put(meshHeading.getKey(), value);
            }else{
                meshMajor.put(meshHeading.getKey(), value);
            }
        }
        for(Map.Entry<String, Object> meshHeading : totalMeshMinor.entrySet()){
            int value = (int) meshHeading.getValue();
            if(meshMinor.containsKey(meshHeading.getKey())){
                value += (int)meshMinor.get(meshHeading.getKey());
                meshMinor.put(meshHeading.getKey(), value);
            }else{
                meshMinor.put(meshHeading.getKey(), value);
            }
        }

        Document update = new Document().append(MedlineConstants.NUM_OF_ARTICLES, numArticles)
                                        .append(MedlineConstants.MESH_MAJOR, meshMajor)
                                        .append(MedlineConstants.MESH_MINOR, meshMinor);

        return new Document("$set", update);
    }
}
