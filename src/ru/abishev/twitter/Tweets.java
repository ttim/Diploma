package ru.abishev.twitter;

import twitter4j.Status;
import twitter4j.Tweet;

import java.util.List;

public class Tweets {
    public static List<Status> DHH_200_TWEETS = Utils.filterRepliesAndRetweets(Utils.getUserTweets("dhh", 200));
    public static List<Tweet> VISUAL_STUDIO_HASHTAG_TWEETS = Utils.getHashtagTweets("VisualStudio", 100);

    public static void main(String[] args) {
//        System.out.println("Count: " + DHH_200_TWEETS.size());
//        for (Status status : DHH_200_TWEETS) {
//            System.out.println(status.getText());
//        }

        System.out.println("Count: " + VISUAL_STUDIO_HASHTAG_TWEETS.size());
        for (Tweet tweet : VISUAL_STUDIO_HASHTAG_TWEETS) {
            System.out.println(tweet.getText());
        }
    }
}
