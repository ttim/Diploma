package ru.abishev.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UsersDataset {
    public final Map<String, String> userToTag = new HashMap<String, String>();
    public final Map<String, Integer> userToTestCount = new HashMap<String, Integer>();
    public final Map<String, Integer> userToTrainCount = new HashMap<String, Integer>();

    public UsersDataset(File file) throws FileNotFoundException {
        String currentTag = null;

        Scanner input = new Scanner(file);
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.startsWith("\t") || line.startsWith(" ")) {
                line = line.trim();
                String[] es = line.split(" ");
                if (es.length != 3) {
                    throw new RuntimeException(es.toString());
                }
                userToTag.put(es[0], currentTag);
                userToTrainCount.put(es[0], Integer.parseInt(es[1]));
                userToTestCount.put(es[0], Integer.parseInt(es[2]));
            } else {
                currentTag = line.trim();
            }
        }
    }
}
