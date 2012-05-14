package ru.abishev.twitter;

import twitter4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTweetsDumper {
    public static List<Status> getUserTimeline(Twitter twitter, String screenName, Paging paging, int attempts) throws TwitterException {
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

    public static void dump(String screenName, int count) throws TwitterException, FileNotFoundException, InterruptedException {
        if (new File("./train/tweets/" + screenName + ".tweets").exists()) {
            return;
        }

        List<Status> tweets = new ArrayList<Status>();
        Twitter twitter = new TwitterFactory().getInstance();
        int page = 1;
        while ((page - 1) * 200 < count) {
            List<Status> currentTweets = getUserTimeline(twitter, screenName, new Paging(page++, 200), 3);
            tweets.addAll(currentTweets);
            if (currentTweets.size() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        PrintWriter output = new PrintWriter("./train/tweets/" + screenName + ".tweets");
        for (Status status : tweets) {
            output.println(status.getText().replaceAll("[\\s]+", " ") + "\t" + status.getId());
        }
        output.close();

        System.out.println("Dumped " + tweets.size() + " tweets for user " + screenName);
    }

    public static void main(String[] args) throws TwitterException, FileNotFoundException, InterruptedException {
        List<String> usersToDump = Arrays.asList(
                "NeilRobbins", "wycats", "DHH", "ladygaga", "PerezHilton", "cnnbrk", "guardiannews",
                "AlgebraFact", "Math_Bits", "mathguide",
                "materion", "CERN", "PhysicsNews",
                "ChemistryBooks", "NatureChemistry", "ChemistryWorld",
                "pzmyers", "BioscienceNews", "tlemberger", "CHoytPhD",
                "codinghorror", "stevenf", "carnage4life", "jasonfried"
        );

        for (String user : usersToDump) {
            dump(user, 2000);
        }
    }
}
