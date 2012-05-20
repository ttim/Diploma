package ru.abishev.weka.wikitextmodel;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.SimpleStreamFilter;

public class WikiTextModel extends SimpleStreamFilter {
    public WikiTextModel(String textFieldName) {

    }

    @Override
    public String globalInfo() {
        return "Wiki-based text model";
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        Instances result = new Instances("result", null, 10000);
        return result;
    }

    @Override
    protected Instance process(Instance input) throws Exception {
//        SparseInstance instance = new SparseInstance(input.weight(), attValues, indices, 10000);
        SparseInstance instance = new SparseInstance(input.weight(), null, null, 10000);
        return instance;
    }
}
