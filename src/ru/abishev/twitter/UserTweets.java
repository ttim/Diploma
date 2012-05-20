package ru.abishev.twitter;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class UserTweets {
    public static List<Status> filterRepliesAndRetweets(Iterable<Status> source) {
        List<Status> result = new ArrayList<Status>();

        for (Status s : source) {
            if (!s.isRetweet()) {
                if (s.getInReplyToScreenName() == null && s.getInReplyToStatusId() == -1 && s.getInReplyToUserId() == -1) {
                    result.add(s);
                }
            }
        }

        return result;
    }

    public static List<Tweet> getHashtagTweets(String tag, int count) {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            Query query = new Query("#" + tag);
            query.setPage(1);
            query.setRpp(count);
            return twitter.search(query).getTweets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Status> getUserTweets(String screenName) {
        return UserTweetsInner.getTweets(screenName);
    }

    public static List<String> getUserTweetsTexts(String screenName) {
        List<String> texts = new ArrayList<String>();
        for (Status status : getUserTweets(screenName)) {
            texts.add(status.getText().replaceAll("[\\s]+", " "));
        }
        return texts;
    }
}
