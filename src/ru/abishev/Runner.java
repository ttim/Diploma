package ru.abishev;

import ru.abishev.wiki.preprocessing.CategoriesPreprocessing;
import ru.abishev.wiki.preprocessing.CategoryLinksPreprocessing;
import ru.abishev.wiki.preprocessing.PagesPreprocessor;
import ru.abishev.wiki.preprocessing.WikiSqlToCsvConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Runner {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Runner args: "+ Arrays.toString(args));

        // todo: use apache cli?
        String command = args[0];

        if ("sql2csv".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("sql2csv: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            WikiSqlToCsvConverter.convert(input, output);
        } else if ("filter_pages".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("filter pages: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            PrintWriter out = new PrintWriter(output);
            PagesPreprocessor.process(input, out);
            out.close();
        } else if ("preprocess_categories".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("preprocess categories: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            PrintWriter out = new PrintWriter(output);
            CategoriesPreprocessing.process(input, out);
            out.close();
        } else if ("preprocess_categorylinks".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("preprocess categorylinks: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            CategoryLinksPreprocessing.process(input, output);
        }
    }
}
