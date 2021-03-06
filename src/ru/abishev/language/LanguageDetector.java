package ru.abishev.language;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import java.io.File;
import java.util.ArrayList;

public class LanguageDetector {
    static {
        try {
            DetectorFactory.loadProfile(new File("./lib/langdetect-profiles"));
        } catch (LangDetectException e) {
            throw new RuntimeException();
        }

    }

    public static String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }

    public static ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }
}
