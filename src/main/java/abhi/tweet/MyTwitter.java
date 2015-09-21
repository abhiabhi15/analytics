package abhi.tweet;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Author : abhishek
 * Created on 9/7/15.
 */
public class MyTwitter {

    Twitter twitter;

    public Twitter getInstance(){
        if(twitter == null){
            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(TweetConstants.CONSUMER_KEY, TweetConstants.CONSUMER_KEY_SECRET);
            AccessToken oathAccessToken = new AccessToken(TweetConstants.ACCESS_TOKEN, TweetConstants.ACCESS_TOKEN_SECRET);
            twitter.setOAuthAccessToken(oathAccessToken);
        }
        return twitter;
    }

}
