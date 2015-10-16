package abhi.amazon.movies.graph;

import org.bson.Document;

/**
 * Author : abhishek
 * Created on 10/15/15.
 */
public class UserNode {

    private String userId;
    private String name;
    private int numReviews;
    private float avgScore;
    private float help_score;
    private Document genreDoc;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public float getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(float avgScore) {
        this.avgScore = avgScore;
    }

    public float getHelp_score() {
        return help_score;
    }

    public void setHelp_score(float help_score) {
        this.help_score = help_score;
    }

    public Document getGenreDoc() {
        return genreDoc;
    }

    public void setGenreDoc(Document genreDoc) {
        this.genreDoc = genreDoc;
    }
}
