package org.waveapi;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.Thread.sleep;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> file = yaml.load(new FileReader("config.yml"));

        Util.recursivelyDelete(new File("run"));

        new File("run").mkdirs();

        Map<String, Object> apiSettings = (Map<String, Object>) file.get("API");

        Util.clone(new File("run"), (String) apiSettings.get("clone"), "api");

        Util.build(new File(new File("run"), "api"));
        Set<String> apiFeatures = buildFeatures(new File(new File(new File("run"), "api"), "build/docs/javadoc"));
        System.out.println(Arrays.toString(apiFeatures.toArray()));

        Map<String, Object> impls = (Map<String, Object>) file.get("implementations");

        Map<String, Set<String>> implFeatures = new LinkedHashMap<>();

        for (Map.Entry<String, Object> impl : impls.entrySet()) {
            Map<String, Object> sets = (Map<String, Object>) impl.getValue();
            Util.clone(new File("run"), (String) sets.get("clone"), impl.getKey().replaceAll("[^a-zA-Z0-9]", ""));

            Util.build(new File(new File("run"), impl.getKey().replaceAll("[^a-zA-Z0-9]", "")));
            Set<String> features = buildFeatures(new File(new File(new File("run"), impl.getKey().replaceAll("[^a-zA-Z0-9]", "")), "build/docs/javadoc"));
            System.out.println(Arrays.toString(features.toArray()));

            implFeatures.put(impl.getKey(), features);

            for (String feature : apiFeatures) {
                if (!features.contains(feature)) {
                    System.out.println(impl.getKey() + " doesn't contain [" + feature + "]");
                }
            }
        }

        String[] lines = new String[apiFeatures.size() + 1];
        lines[0] = "\"Feature\",";
        for (String name : implFeatures.keySet()) {
            lines[0]+= "\""+name+"\",";
        }
        int line = 1;
        for (String feature : apiFeatures) {
            lines[line] = "\"" + feature + "\",";
            for (String name : implFeatures.keySet()) {
                lines[line]+= "\"" + ( implFeatures.get(name).contains(feature) ? "✅" : "❌" ) + "\",";
            }
            line++;
        }

        int[] forSort = new int[lines.length - 1];
        for (int i = 1 ; i < lines.length ; i++) {
            forSort[i-1] = lines[i].split("✅").length - 1;
        }

        for (int x = 0 ; x < forSort.length ; x++) {
            for (int y = 0 ; y < forSort.length -1 ; y++) {
                if (forSort[y] < forSort[y+1]) {
                    int temp = forSort[y];
                    forSort[y] = forSort[y+1];
                    forSort[y+1] = temp;

                    String tempS = lines[y+1];
                    lines[y+1] = lines[y+2];
                    lines[y+2] = tempS;
                }
            }
        }

        StringBuilder csvText = new StringBuilder();
        for (String lin : lines) {
            csvText.append(lin + "\n");
        }

        new File("info").mkdirs();

        Files.write(new File("info/feature-table.csv").toPath(), csvText.toString().getBytes());

    }

    public static Set<String> buildFeatures(File dir) throws IOException, InterruptedException, ClassNotFoundException {

        Set<String> features = new HashSet<>();

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                features.addAll(buildFeatures(f));
            } else if (f.getName().endsWith(".html")) {
                String text = new String(Files.readAllBytes(f.toPath()));
                StringBuilder className = new StringBuilder(f.getName().replaceAll(".html", ""));
                File cur = f.getParentFile();
                while (!cur.getName().equals("javadoc")) {

                    className.insert(0, cur.getName() + ".");

                    cur = cur.getParentFile();
                }
                String[] fromConst = text.split("<section class=\"detail\" id=\"");
                if (fromConst.length > 1) {
                    for (int i = 1; i < fromConst.length; i++) {
                        String start = fromConst[i];
                        String smallPart = start.split("\">")[0];

                        boolean constructor = false;
                        if (smallPart.contains("&gt;") || smallPart.contains("&lt;")) {
                            constructor = true;
                            smallPart = smallPart.replace("&gt;", "").replace("&lt;", "");
                        }

                        String modif = fromConst[i].split("<span class=\"modifiers\">")[1].split("</span>")[0];
                        String[] reSplit = fromConst[i].split("<span class=\"return-type\">");
                        String re = "";
                        if (reSplit.length > 1) {
                            re = " -> " + reSplit[1].split("</span>")[0].replaceAll("<(.*?)>", "");
                        }
                        if (modif.contains("public")) {
                            if (modif.contains("static")) {
                                features.add(className + "." + smallPart + re);
                            } else if (re.length() > 0 && !constructor) {
                                features.add(className + "#" + smallPart + re);
                            } else if (!smallPart.equals("init()")) {
                                features.add(className + ":" + smallPart);
                            }
                        }
                    }
                }
            }
        }

        return features;
    }

}