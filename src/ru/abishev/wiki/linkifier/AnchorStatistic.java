package ru.abishev.wiki.linkifier;

import ru.abishev.utils.CsvUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnchorStatistic {
    public final String text;
    public final int pageId;
    public final int count;

    public AnchorStatistic(String text, int pageId, int count) {
        this.text = text;
        this.pageId = pageId;
        this.count = count;
    }

    @Override
    public String toString() {
        return "{" +
                "text='" + text + '\'' +
                ", pageId=" + pageId +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnchorStatistic that = (AnchorStatistic) o;

        if (count != that.count) return false;
        if (pageId != that.pageId) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + pageId;
        result = 31 * result + count;
        return result;
    }

    public static AnchorStatistic[] readStatistics(File file) {
        List<AnchorStatistic> statistics = new ArrayList<AnchorStatistic>();

        for (List<String> statistic : CsvUtils.readCsv(file, '|', '"')) {
            statistics.add(new AnchorStatistic(statistic.get(0), Integer.parseInt(statistic.get(1)), Integer.parseInt(statistic.get(2))));
        }

        return statistics.toArray(new AnchorStatistic[]{});
    }
}
