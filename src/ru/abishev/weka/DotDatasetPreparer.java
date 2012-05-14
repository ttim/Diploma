package ru.abishev.weka;

import com.google.common.base.Joiner;
import twitter4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class DotDatasetPreparer {
    public static void prepareData(File dataSetFile, File outputFile) throws FileNotFoundException, TwitterException, InterruptedException {
        PrintWriter output = new PrintWriter(outputFile);

        output.println("@relation tweet-to-categories");
        output.println("@attribute text string");

        // read dataset
        Map<String, String> userToTag = new HashMap<String, String>();
        String currentTag = null;

        Scanner input = new Scanner(dataSetFile);
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.startsWith("\t")) {
                userToTag.put(line.trim(), currentTag);
            } else {
                currentTag = line.trim();
            }
        }

        System.out.println(userToTag);

        output.println("@attribute _result_category {" + Joiner.on(',').join(new HashSet<String>(userToTag.values())) + "}");

        output.println("@data");

        for (Map.Entry<String, String> userToTagEntry : userToTag.entrySet()) {
            addUser(output, userToTagEntry.getKey(), userToTagEntry.getValue());
            Thread.sleep(1000*2);
        }

        output.close();
    }

    private static void addUser(PrintWriter output, String user, String tag) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        System.out.println("Processing " + user + " user");
        for (Status status : twitter.getUserTimeline(user, new Paging(1, 200))) {
            String text = status.getText().trim();
            // preprocess here? like removing replies etc?
            text = text.replaceAll("'", "").replaceAll("[\\s]+", " ");
            output.println("'" + text + "', " + tag);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, TwitterException, InterruptedException {
        prepareData(new File("./train/thematic.dataset"), new File("./train/thematic.arff"));
    }
}
