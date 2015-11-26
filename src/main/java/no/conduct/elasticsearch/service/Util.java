package no.conduct.elasticsearch.service;

import java.io.*;

import no.conduct.elasticsearch.main.Main;

/**
 * Some util methods
 *
 * Created by paalk on 08.06.15.
 */
public abstract class Util {

    public static int getRandomInt(int max) {
        return (int) (Math.random() * max) + 1;
    }

    public static boolean getRandomBool() {
        return Math.random() >= 0.5D;
    }

    public static boolean isNotBlank(String s) {
        return s != null && !s.isEmpty();
    }

    public static InputStream getInputStream(String fileName) throws IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName is null");
        }
        boolean found = false;
        InputStream inputStream = null;
        File file = new File(fileName);
        if (file.exists() && file.canRead() && file.isFile()) {
            inputStream = new FileInputStream(file);
            found = true;
        }
        if (!found) {
            inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
            found = inputStream != null;
        }
        if (!found) {
            inputStream = Main.class.getClassLoader().getResourceAsStream("/" + fileName);
            found = inputStream != null;
        }
        if (!found) {
            throw new FileNotFoundException(String.format("File %s not found in classpath or filesystem!", fileName));
        }
        return inputStream;
    }

}
