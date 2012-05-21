package ru.abishev.wiki.main_topic_classification;

import org.jetbrains.annotations.Nullable;
import ru.abishev.wiki.categories.data.Categories;
import ru.abishev.wiki.categories.data.Category;

import java.util.Arrays;
import java.util.List;

public class Rank {
    public static final List<Category> MTC_CATEGORIES = Utils.getSubcategories(Categories.RAW.getByName("Main_topic_classifications"));
    public static final int COUNT = MTC_CATEGORIES.size();
    private static final int PRINT_COUNT = 5;

    public final double[] data;

    public Rank(double[] data) {
        this.data = data;
    }

    public Rank() {
        this.data = new double[COUNT];
    }

    public Rank add(@Nullable Rank snd) {
        double[] result = new double[Rank.COUNT];

        for (int i = 0; i < Rank.COUNT; i++) {
            result[i] += data[i];
            if (snd != null) {
                result[i] += snd.data[i];
            }
        }

        return new Rank(result);
    }

    public Rank divide(double value) {
        double[] result = new double[Rank.COUNT];
        for (int i = 0; i < Rank.COUNT; i++) {
            result[i] = data[i] / value;
        }
        return new Rank(result);
    }

    public double sumOfRanks() {
        double sum = 0;
        for (int i = 0; i < COUNT; i++) {
            sum += data[i];
        }
        return sum;
    }

    public static int numOf(Category mainCategory) {
        return MTC_CATEGORIES.indexOf(mainCategory);
    }

    @Override
    public String toString() {
        double[] toSort = Arrays.copyOf(data, COUNT);
        Arrays.sort(toSort);

        StringBuilder s = new StringBuilder();
        for (int i = COUNT - 1; i >= COUNT - PRINT_COUNT; i--) {
            int index = 0;
            for (int j = 0; j < COUNT; j++) {
                if (data[j] == toSort[i]) {
                    index = j;
                    break;
                }
            }
            s.append(String.format("%s: %.2f; ", MTC_CATEGORIES.get(index).name, toSort[i]));
        }

        return s.toString();
    }

    public static void main(String[] args) {

    }
}
