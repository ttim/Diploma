package ru.abishev;

import ru.abishev.wiki.categories.preprocessing.*;
import ru.abishev.wiki.categories.processing.GraphCreator;
import ru.abishev.wiki.pages.PagesPreparer;
import ru.abishev.wiki.parser.Converter;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

public class Runner {
    public static void main(String[] args) throws Exception {
        System.out.println("Runner args: " + Arrays.toString(args));

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
        } else if ("create_graph".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("create categories graph: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            PrintWriter out = new PrintWriter(output);
            GraphCreator.createGraph(input, out);
            out.close();
        } else if ("filter_pagelinks".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("filter pagelinks: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            PrintWriter out = new PrintWriter(output);
            PagelinksPreprocessor.process(input, out);
            out.close();
        } else if ("prepare_pages".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("prepare pages: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            PagesPreparer.preparePages(input, output);
        } else if ("convert_dump".equals(command)) {
            File input = new File(args[1]), output = new File(args[2]);
            System.out.println("convert dump: " + input.getAbsolutePath() + " to " + output.getAbsolutePath());

            Converter.convertDump(input, output);
        }
    }
}
