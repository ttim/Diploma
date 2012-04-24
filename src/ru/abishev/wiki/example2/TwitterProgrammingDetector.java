package ru.abishev.wiki.example2;

import twitter4j.*;

import java.io.File;

public class TwitterProgrammingDetector {
    public static void main(String[] args) {
        final ProgrammingDetector detector = ProgrammingDetector.INSTANCE;

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                if (detector.rate(status.getText()) >= 0.03) {
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
    }

}
