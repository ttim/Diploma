package ru.abishev.weka.api;

import weka.filters.Filter;

public interface WordModelFactory {
    public Filter getWordModel();

    public String getFullName();
}
