package ru.abishev.weka.context;

import ru.abishev.twitter.UserTweets;
import ru.abishev.weka.WekaUtils;
import ru.abishev.weka.api.ClassifierFactory;
import ru.abishev.weka.api.ClustererFactory;
import ru.abishev.weka.api.WordModelFactory;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

public class ContextClassifier extends Classifier {
    private Classifier classifier;
    private Clusterer clusterer;
    private Filter wordModel;

    private Map<String, ClustersContext> userToClusters = new HashMap<String, ClustersContext>();

    private String idForClusters;

    public ContextClassifier(ClassifierFactory classifier, ClustererFactory clusterer, String wordModelName, Filter wordModel) {
        // input to classifier - with _user field // for use old classifiers
        // input to clusterer - without any garbage, only text

        this.idForClusters = clusterer.getFullName() + "-" + wordModelName;

        this.classifier = new CachedClassifier(classifier.getClassifier());
        this.clusterer = clusterer.getClusterer();
        this.wordModel = wordModel;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        userToClusters.clear();
        classifier.buildClassifier(data);
    }

    private static String createDatasetForUser(String user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter output = new PrintWriter(outputStream);

        output.println("@relation tweet-to-categories");
        output.println("@attribute _current_user string");
        output.println("@attribute text string");
        output.println("@attribute _result_category {category}");
        output.println("@data");

        for (String text : UserTweets.getUserTweetsTexts(user)) {
            String instance = "'" + user + "'" + "," + "'" + text.replaceAll("[\\\\]", " ").replaceAll("'", "") + "', " + "category";
            output.println(instance);
        }

        output.close();

        return outputStream.toString();
    }

    @Override
    public double classifyInstance(Instance tweet) throws Exception {
        String user = tweet.stringValue(0);

        if (!userToClusters.containsKey(user)) {
            String tweets = createDatasetForUser(user);
            Instances instances = new Instances(new StringReader(tweets));
            instances = Filter.useFilter(instances, wordModel);
            WekaUtils.setupClass(instances);
            userToClusters.put(user, new ClustersContext(user, instances, clusterer, idForClusters));
        }

        List<Instance> context = userToClusters.get(user).getContextInstances(tweet);

        // majority vote...
        Map<Double, Integer> resultToCount = new HashMap<Double, Integer>();
        for (Instance instance : context) {
            double classifyResult = classifier.classifyInstance(instance);
            if (resultToCount.containsKey(classifyResult)) {
                resultToCount.put(classifyResult, resultToCount.get(classifyResult) + 1);
            } else {
                resultToCount.put(classifyResult, 1);
            }
        }

        // find max
        double maxKey = resultToCount.keySet().iterator().next();
        for (double key : resultToCount.keySet()) {
            if (resultToCount.get(key) > resultToCount.get(maxKey)) {
                maxKey = key;
            }
        }
        return maxKey;
    }

    public static void main(String[] args) throws IOException {
        String tweets = createDatasetForUser("pzmyers");
        System.out.println(tweets);
        Instances instances = new Instances(new StringReader(tweets));
    }
}
