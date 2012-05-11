package ru.abishev.twitter;

import twitter4j.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Dumper {
    public static void dump(String screenName) throws TwitterException, FileNotFoundException {
        PrintWriter output = new PrintWriter("./train/_" + screenName + ".source.txt");
        Twitter twitter = new TwitterFactory().getInstance();
        for (Status status : twitter.getUserTimeline(screenName, new Paging(1, 300))) {
            output.println(status.getText().replaceAll("\n", " ")+"\t"+status.getId());
        }
        output.close();
    }

    public static void main(String[] args) throws TwitterException, FileNotFoundException {
//        dump("NeilRobbins");
        dump("wycats");
        dump("DHH");
        dump("ladygaga");
        dump("PerezHilton");
    }
}
