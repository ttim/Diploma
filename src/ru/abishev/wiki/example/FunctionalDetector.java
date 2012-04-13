package ru.abishev.wiki.example;

import ru.abishev.utils.CsvUtils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.*;

public class FunctionalDetector {
    public Map<String, Integer> stat = new HashMap<String, Integer>();

    public FunctionalDetector(File anchorsFile) {
        for (List<String> data : CsvUtils.readCsv(anchorsFile, '|', '"')) {
            for (String _w : splitOnPunctuation(data.get(0))) {
                String w = _w.toLowerCase();
                if (!stat.containsKey(w)) {
                    stat.put(w, 1);
                } else {
                    stat.put(w, stat.get(w) + 1);
                }
            }
        }
        System.out.println("Words loaded: " + stat.size());
    }

    public int rate(String s) {
        int count = 0;
        for (String w : splitOnPunctuation(s)) {
            Integer curCount = stat.get(w.toLowerCase());
            if (curCount != null && curCount > 1 && w.length() > 3) {
                count++;
            }
        }

        return count;
    }

    private String[] splitOnPunctuation(String w) {
        return w.split("[ ,.!?:\"]");
    }

    public static void main(String[] args) {
        final FunctionalDetector detector = new FunctionalDetector(new File("./data/fprog_anchors.csv"));

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey("Y1hyeYgJbvh1pc9gytNoLQ")
                .setOAuthConsumerSecret("B3bXWDNlTuGGMCuNuSXmN7rA7P6TSNxJr1Mey77lNE")
                .setOAuthAccessToken("9549002-R08HVFqreXkrZqYCtksAkSO94sRH9QmjhqkFFeiZ0")
                .setOAuthAccessTokenSecret("hyfromxOP0hIq0H1wr9fPJw5x4TPoCQmyE8dsZGtt0");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                if (detector.rate(status.getText()) > 2) {
                    System.out.println(detector.rate(status.getText()) + "@" + status.getUser().getScreenName() + " - " + status.getText());
                }
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();

        System.out.println(detector.rate("Clippy for Java Users: \"It appears you are trying to download the entire internet. Would you like help building your Java Project?\""));
        System.out.println(detector.rate("No our language is not OO even though its based on #Ruby. That's like saying a language must be imperative if it is implemented in C"));
    }


}
