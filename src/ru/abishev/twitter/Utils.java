package ru.abishev.twitter;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Utils {
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

    public static List<Status> getUserTweets(String screenName, int count) {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            return twitter.getUserTimeline(screenName, new Paging(1, count));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Tweet> getHashtagTweets(String tag, int count) {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            Query query = new Query("#"+tag);
            query.setPage(1);
            query.setRpp(count);
            return twitter.search(query).getTweets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
