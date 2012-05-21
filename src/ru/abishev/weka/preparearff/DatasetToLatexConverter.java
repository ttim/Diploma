package ru.abishev.weka.preparearff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DatasetToLatexConverter {
    private static final String CHARS_TO_ESCAPE = "_";

    public static String latexEscape(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (CHARS_TO_ESCAPE.indexOf(c) != -1) {
                result.append("\\");
            }
            result.append(c);
        }
        return result.toString();
    }

    public static String convertToLatex(UsersDataset dataset) {
        StringBuilder result = new StringBuilder();

        result.append("\\hline\n");

        List<Map.Entry<String, String>> userToTagEntries = new ArrayList<Map.Entry<String, String>>(dataset.userToTag.entrySet());
        Collections.sort(userToTagEntries, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        for (Map.Entry<String, String> userToTag : userToTagEntries) {
            result.append(latexEscape(userToTag.getKey()));
            result.append(" & ");
            result.append(latexEscape(userToTag.getValue()));
            result.append(" \\\\\n");
            result.append("\\hline\n");
        }

        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
//        System.out.println(convertToLatex(new UsersDataset(new File("./train/datasets/thematic.dataset"))));
        System.out.println(convertToLatex(new UsersDataset(new File("./train/datasets/usernewscompany.dataset"))));
    }
}
