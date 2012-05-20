package ru.abishev.weka.context;

import weka.clusterers.Clusterer;
import weka.clusterers.FilteredClusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClustersContext {
    // everything here with _user field
    // but clusterer knows about it too
    private final Clusterer clusterer;

    private final Map<Integer, List<Instance>> clusters = new HashMap<Integer, List<Instance>>();

    public ClustersContext(String userName, Instances userTweets, Clusterer clusterer) throws Exception {
        FilteredClusterer filteredClusterer = new FilteredClusterer();
        filteredClusterer.setClusterer(cloneClusterer(clusterer));
        Remove remove = new Remove();
        remove.setAttributeIndices("1-2");
        filteredClusterer.setFilter(remove);

        filteredClusterer.buildClusterer(userTweets);
        this.clusterer = filteredClusterer;

        for (int i = 0; i < userTweets.numInstances(); i++) {
            Instance instance = userTweets.instance(i);
            int cluster = filteredClusterer.clusterInstance(instance);

            if (!clusters.containsKey(cluster)) {
                clusters.put(cluster, new ArrayList<Instance>());
            }

            clusters.get(cluster).add(instance);
        }

        System.out.println("Builded clusters context for " + userName);
    }

    public List<Instance> getContextInstances(Instance tweet) throws Exception {
        int cluster = this.clusterer.clusterInstance(tweet);
        List<Instance> result = new ArrayList<Instance>();
        result.add(tweet);
        result.add(tweet);
        if (clusters.containsKey(cluster)) {
            result.addAll(clusters.get(cluster));
        }
        return result;
    }

    private Clusterer cloneClusterer(Clusterer clusterer) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
            objectOutputStream.writeObject(clusterer);
            objectOutputStream.close();

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            Object result = objectInputStream.readObject();
            objectInputStream.close();

            return (Clusterer) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
