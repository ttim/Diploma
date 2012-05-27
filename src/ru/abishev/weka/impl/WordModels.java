package ru.abishev.weka.impl;

import ru.abishev.weka.api.WordModelFactory;
import ru.abishev.weka.wikitextmodel.WikiTextModel;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;

import static ru.abishev.weka.WekaUtils.readObjectFromFile;

public class WordModels {
    public static WordModelFactory SIMPLE_WORD_MODEL = new WordModelFactory() {
        @Override
        public Filter getWordModel() {
            StringToWordVector transform = (StringToWordVector) readObjectFromFile(new File("./weka/string_to_word_vector"));
            transform.setAttributeIndices("2");
            return transform;
        }

        @Override
        public String getFullName() {
            return "simple-word-model";
        }
    };

    public static WordModelFactory WIKI_WORD_MODEL = new WordModelFactory() {
        @Override
        public Filter getWordModel() {
            return WikiTextModel.getWikiTextModel(1, "2");
        }

        @Override
        public String getFullName() {
            return "wiki-word-model";
        }
    };

    public static WordModelFactory[] WORD_MODELS = new WordModelFactory[]{SIMPLE_WORD_MODEL, WIKI_WORD_MODEL};
}
