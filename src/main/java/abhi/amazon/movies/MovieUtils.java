package abhi.amazon.movies;

import abhi.utils.CalendarUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : abhishek
 * Created on 9/21/15.
 */
public class MovieUtils {

    public static User createUser(List<String> lines) throws Exception {

        User user = new User();
        Document review = new Document();
        List<Document> reviewList = new ArrayList<>();
        for (String line : lines) {
            try {
                String[] tags = line.split(":");
                String[] headers = tags[0].split("/");
                if (headers.length > 1) {
                    String field = headers[1].trim();
                    String value = tags[1].trim();
                    switch (field) {
                        case "userId":
                            user.setUserId(value);
                            break;

                        case "profileName":
                            user.setProfileName(value);
                            break;

                        case "productId":
                            review.append(MovieConstants.PRODUCT_ID, value);
                            break;

                        case "helpfulness":
                            review.append(MovieConstants.HELPFULNESS, value);
                            break;

                        case "score":
                            review.append(MovieConstants.SCORE, value);
                            break;

                        case "time":
                            review.append(MovieConstants.TIME, Long.parseLong(value) * 1000l);
                            user.setPeriod(CalendarUtils.getUserTimeSpecs(Long.parseLong(value) * 1000l));
                            break;

                        case "summary":
                            review.append(MovieConstants.SUMMARY, value);
                            break;
                    }

                }

            } catch (Exception ex) {
                System.err.println("Error Line = " + line);
                ex.printStackTrace();
            }
        }
        reviewList.add(review);
        user.setReviews(reviewList);
        return user;
    }
}
