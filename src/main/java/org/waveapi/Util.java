package org.waveapi;

import java.io.File;
import java.io.IOException;

public class Util {
    public static void clone(File path, String repo, String name) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "clone", repo, name);
        pb.directory(path);

        Process p = pb.start();

        while (p.isAlive()) {}
    }

    public static void recursivelyDelete(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                recursivelyDelete(f);
            }
        }
        dir.delete();
    }

    public static void build(File path) throws IOException, InterruptedException {
        setPerms(path, "gradlew");
        ProcessBuilder pb = new ProcessBuilder("./gradlew", "javadoc");
        pb.directory(path);

        pb.start().waitFor();
    }

    public static void setPerms(File path, String name) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("chmod", "+x", name);
        pb.directory(path);

        pb.start().waitFor();
    }
}
