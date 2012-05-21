package ru.abishev.wiki.example3;

import ru.abishev.wiki.example2.ProgrammingDetector;
import twitter4j.*;

import java.util.Arrays;
import java.util.List;

public class TwitterUserRater {
    public static double rateUser(String screenName) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        ResponseList<Status> statuses = twitter.getUserTimeline(screenName, new Paging(1, 300));
        double result = 0;
        for (Status status : statuses) {
            result += ProgrammingDetector.INSTANCE.rate(status.getText());
        }
        return result / statuses.size() * 1000;
    }

    public static void main(String[] args) throws TwitterException {
        List<String> users = Arrays.asList(
                "_ttim",
                "sstephenson",
                "joehewitt",
                "joestump",
                "tolmasky",
                "aza",
                "rands",
                "mjtsai",
                "danielpunkass",
                "justinbieber",
                "google",
                "Oprah",
                "YouTube",
                "rihanna",
                "katyperry",
                "ladygaga",
                "BARACKOBAMA"
        );
        for (String user : users) {
            System.out.println(user + ": " + rateUser(user));
        }
    }
}
