package ru.abishev.wiki.example1;

import ru.abishev.utils.CsvUtils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.*;

import static ru.abishev.utils.StringUtils.splitOnPunctuation;

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
            if (w.length() > 3 && stat.containsKey(w.toLowerCase())) {
                count += stat.get(w.toLowerCase());
            }
        }
        return count;
    }

    public static void main(String[] args) {
        final FunctionalDetector detector = new FunctionalDetector(new File("./data/fprog_anchors.csv"));

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                if (detector.rate(status.getText()) >= 1000) {
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
