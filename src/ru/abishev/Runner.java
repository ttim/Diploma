package ru.abishev;

import ru.abishev.wiki.preprocessing.WikiSqlToCsvConverter;

import java.io.File;
import java.io.FileNotFoundException;
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
        }
    }
}
