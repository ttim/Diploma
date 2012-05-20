package ru.abishev.weka;

import com.google.common.base.Joiner;
import ru.abishev.twitter.UserTweets;
import ru.abishev.utils.FileUtils;
import twitter4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class DotDatasetPreparer {
    public static void prepareData(File dataSetFile, File outputFile) throws FileNotFoundException, TwitterException, InterruptedException {
        PrintWriter train = new PrintWriter(new File(outputFile.getAbsolutePath() + ".train.arff"));
        PrintWriter test = new PrintWriter(new File(outputFile.getAbsolutePath() + ".test.arff"));
        PrintWriter all = new PrintWriter(new File(outputFile.getAbsolutePath() + ".all.arff"));

        for (PrintWriter output : Arrays.asList(train, test, all)) {
            output.println("@relation tweet-to-categories");
            output.println("@attribute text string");
        }

        UsersDataset dataset = new UsersDataset(dataSetFile);
        System.out.println(dataset.userToTag);

        for (PrintWriter output : Arrays.asList(train, test, all)) {
            output.println("@attribute _result_category {" + Joiner.on(',').join(new HashSet<String>(dataset.userToTag.values())) + "}");
            output.println("@data");
        }


        for (String user : dataset.userToTag.keySet()) {
            addUser(train, test, all, user,
                    dataset.userToTag.get(user), dataset.userToTrainCount.get(user), dataset.userToTestCount.get(user));
        }

        for (PrintWriter output : Arrays.asList(train, test, all)) {
            output.close();
        }
    }

    private static void addUser(PrintWriter trainFile, PrintWriter testFile, PrintWriter allFile, String user, String tag, int trainCount, int testCount) throws TwitterException, FileNotFoundException {
        // get all tweets text
        List<String> tweets = UserTweets.getUserTweetsTexts(user);
        Collections.shuffle(tweets);

        if (tweets.size() < trainCount + testCount) {
            throw new RuntimeException(user);
        }

        for (int i = 0; i < trainCount; i++) {
            trainFile.println("'" + tweets.get(i) + "', " + tag);
            allFile.println("'" + tweets.get(i) + "', " + tag);
        }
        for (int i = trainCount; i < trainCount + testCount; i++) {
            testFile.println("'" + tweets.get(i) + "', " + tag);
            allFile.println("'" + tweets.get(i) + "', " + tag);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, TwitterException, InterruptedException {
//        prepareData(new File("./train/datasets/thematic.dataset"), new File("./train/thematic"));
        prepareData(new File("./train/datasets/usernewscompany.dataset"), new File("./train/usernewscompany"));
    }
}
