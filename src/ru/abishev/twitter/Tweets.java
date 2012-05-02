package ru.abishev.twitter;

import twitter4j.Status;

import java.util.List;

public class Tweets {
    public static List<Status> DHH_200_TWEETS = Utils.getTweets("dhh", 200, true);

    public static void main(String[] args) {
        System.out.println("Count: " + DHH_200_TWEETS.size());
        for (Status status : DHH_200_TWEETS) {
            System.out.println(status.getText());
        }
    }
}
