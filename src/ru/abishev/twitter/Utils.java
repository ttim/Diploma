package ru.abishev.twitter;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Status> getTweets(String screenName, int beforeFilterCount, boolean filterRetweetsAndReplies) {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            ResponseList<Status> statuses = twitter.getUserTimeline(screenName, new Paging(1, beforeFilterCount));
            if (!filterRetweetsAndReplies) {
                return statuses;
            }

            List<Status> result = new ArrayList<Status>();
            for (Status s : statuses) {
                if (!s.isRetweet()) {
                    if (s.getInReplyToScreenName() == null && s.getInReplyToStatusId() == -1 && s.getInReplyToUserId() == -1) {
                        result.add(s);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
