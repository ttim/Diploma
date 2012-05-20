package ru.abishev.twitter;

import twitter4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTweets {
    public static File CACHE_DIR = new File("./data/tweets/");
    public static int TWEETS_COUNT = 2000;

    private static List<Status> getUserTimeline(Twitter twitter, String screenName, Paging paging, int attempts) throws TwitterException {
        TwitterException cachedException = null;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return twitter.getUserTimeline(screenName, paging);
            } catch (TwitterException e) {
                cachedException = e;
            }
        }
        throw cachedException;
    }

    private static List<Status> dump(String screenName, int count) throws TwitterException {
        List<Status> tweets = new ArrayList<Status>();
        Twitter twitter = new TwitterFactory().getInstance();
        int page = 1;
        while ((page - 1) * 200 < count) {
            List<Status> currentTweets = getUserTimeline(twitter, screenName, new Paging(page++, 200), 3);
            tweets.addAll(currentTweets);
            if (currentTweets.size() == 0) {
                break;
            }
        }
        return tweets;
    }

    private static List<Status> readTweetsFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        List<Status> result = (List<Status>) input.readObject();
        input.close();
        return result;
    }

    public static List<Status> getTweets(String screenName) {
        File tweetsFile = new File(CACHE_DIR, screenName + ".tweets");
        if (tweetsFile.exists()) {
            try {
                return readTweetsFromFile(tweetsFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        List<Status> tweets;
        try {
            tweets = dump(screenName, TWEETS_COUNT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(new FileOutputStream(tweetsFile));
            output.writeObject(tweets);
            output.close();
        } catch (IOException e) {
            // impossible?
            throw new RuntimeException(e);
        }

        System.out.println("Dumped " + tweets.size() + " tweets for user " + screenName);

        return tweets;
    }

    public static void preloadCache() {
        List<String> usersToDump = Arrays.asList(
                "NeilRobbins", "wycats", "DHH", "ladygaga", "PerezHilton", "cnnbrk", "guardiannews",
                "AlgebraFact", "Math_Bits", "mathguide",
                "materion", "CERN", "PhysicsNews",
                "ChemistryBooks", "NatureChemistry", "ChemistryWorld",
                "pzmyers", "BioscienceNews", "tlemberger", "CHoytPhD",
                "codinghorror", "stevenf", "carnage4life", "jasonfried"
        );

        for (String user : usersToDump) {
            getTweets(user);
        }
    }

    public static void main(String[] args) {
        preloadCache();
    }
}
