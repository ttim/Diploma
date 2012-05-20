package ru.abishev.weka.context;

import com.google.common.base.Joiner;
import ru.abishev.twitter.UserTweets;
import ru.abishev.weka.WekaUtils;
import ru.abishev.weka.preparearff.UsersDataset;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

public class ContextClassifier extends Classifier {
    private final Classifier classifier;
    private final Clusterer clusterer;
    private final Filter stringToVector;
    private final Map<String, ClustersContext> userToClusters = new HashMap<String, ClustersContext>();

    public ContextClassifier(Classifier classifier, Clusterer clusterer, Filter stringToVector) {
        // input to classifier - with _user field // for use old classifiers
        // input to clusterer - without any garbage, only text
        this.classifier = new CachedClassifier(classifier);
        this.clusterer = clusterer;
        this.stringToVector = stringToVector;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        classifier.buildClassifier(data);
    }

    private String createDatasetForUser(String user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter output = new PrintWriter(outputStream);

        output.println("@relation tweet-to-categories");
        output.println("@attribute _current_user string");
        output.println("@attribute text string");
        output.println("@attribute _result_category {category}");
        output.println("@data");

        for (String text : UserTweets.getUserTweetsTexts(user)) {
            String instance = "'" + user + "'" + "," + "'" + text.replaceAll("'", "") + "', " + "category";
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
            instances = Filter.useFilter(instances, stringToVector);
            WekaUtils.setupClass(instances);
            userToClusters.put(user, new ClustersContext(user, instances, clusterer));
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
}
