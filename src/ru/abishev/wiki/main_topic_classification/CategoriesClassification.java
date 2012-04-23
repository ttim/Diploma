package ru.abishev.wiki.main_topic_classification;

import ru.abishev.wiki.categories.data.Category;

import java.util.*;

public class CategoriesClassification {
    private static double calcSum(Map<Category, Rank> position) {
        double sum = 0;
        for (Rank p : position.values()) {
            sum += p.sumOfRanks();
        }
        return sum;
    }

    private static Map<Category, Rank> iterateRandomWalking(Map<Category, Rank> start, double limit) {
        Map<Category, Rank> rank = new HashMap<Category, Rank>();

        Map<Category, Rank> currentRank = start;

        double sum = calcSum(currentRank);

        while (sum > limit) {
            System.out.println("Current sum " + sum);

            // add currentRank to rank
            for (Category category : currentRank.keySet()) {
                rank.put(category, currentRank.get(category).add(rank.get(category)));
            }

            // update currentRank
            Map<Category, Rank> newRank = new HashMap<Category, Rank>();
            for (Category category : currentRank.keySet()) {
                List<Category> subCats = Utils.getSubcategories(category);
                Rank addition = currentRank.get(category).divide(subCats.size());
                for (Category subCat : subCats) {
                    newRank.put(subCat, addition.add(newRank.get(subCat)));
                }
            }

            currentRank = newRank;
            sum = calcSum(currentRank);
        }

        return rank;
    }


    public static Map<Category, Rank> getCategoriesRanks1() {
        Map<Category, Rank> start = new HashMap<Category, Rank>();

        double initialSum = 1000000;
        for (Category root : Rank.MTC_CATEGORIES) {
            start.put(root, new Rank());
            start.get(root).data[Rank.numOf(root)] = initialSum / Rank.COUNT;
        }

        return iterateRandomWalking(start, 300);
    }

    public static Map<Category, Rank> getCategoriesRanks2() {
        Map<Category, Rank> start = new HashMap<Category, Rank>();

        // straight direction
        double initialSum = 1000000;
        for (Category root : Rank.MTC_CATEGORIES) {
            start.put(root, new Rank());
            start.get(root).data[Rank.numOf(root)] = initialSum / Rank.COUNT;
        }

        Map<Category, Rank> current = iterateRandomWalking(start, 1000);

        // one back step
        // Category rank == 1/3 category itself + 2/3 sum in child
        start = new HashMap<Category, Rank>();
        for (Category category : current.keySet()) {
            Rank result = new Rank();
            for (Category subCat : Utils.getSubcategories(category)) {
                result = result.add(current.get(subCat));
            }
            result = result.divide(1.5).add(current.get(category).divide(3));
            start.put(category, result);
        }

        // and straight again
        return iterateRandomWalking(start, 3000);
    }


    public static void main(String[] args) {
        getCategoriesRanks2();
    }
}
