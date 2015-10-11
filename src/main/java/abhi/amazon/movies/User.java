package abhi.amazon.movies;

import org.bson.Document;

import java.util.List;

/**
 * Author : abhishek
 * Created on 9/20/15.
 */
public class User {

    String userId;
    String profileName;
    String period;
    List<Document> reviews;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<Document> getReviews() {
        return reviews;
    }

    public void setReviews(List<Document> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", reviews=" + reviews +
                '}';
    }

    public Document update(Document user) {
        List<Document> allReviews = (List<Document>) user.get(MovieConstants.REVIEWS);
        allReviews.addAll(this.reviews);
        return new Document("$set", new Document(MovieConstants.REVIEWS, allReviews));
    }


    public Document getMongoDoc() {

        Document doc = new Document(MovieConstants.UID, userId)
                .append(MovieConstants.PROFILE_NAME, profileName)
                .append(MovieConstants.REVIEWS, reviews);
        return doc;
    }
}
