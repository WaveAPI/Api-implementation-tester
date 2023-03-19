package org.waveapi.impltester;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.waveapi.impltester.csv.CSV;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {
    public static Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) {
        LOGGER.setUseParentHandlers(false);
        setupLogger();

        LOGGER.info("Welcome to WaveAPI Implementation Tester");
        Configuration config = new Configuration(new File("config.yml"));

        File work = new File("work/");
        if (work.exists() && work.isDirectory() && work.listFiles().length != 0) {
            LOGGER.info("Looks like work directory exists and is not empty. Cleaning up...");
            deleteRecursively(work);
            work.mkdir();
        }

        LOGGER.info("Cloning API repository from " + config.getApiUrl() + "...");
        GitOperator.clone(config.getApiUrl(), new File("work/api/"));

        LOGGER.info("Getting API signatures...");
        File apiSrc = new File("work/api/src/");

        if (!apiSrc.exists()) {
            LOGGER.severe("Unable to locate API src/ directory!");
            return;
        }

        List<String> apiSignatures = JavaParser.getSignaturesRecursively(apiSrc);
        CSV csv = new CSV(apiSignatures);

        for (Map.Entry<String, String> e : config.getImplementations().entrySet()) {
            String name = e.getKey();

            LOGGER.info("Cloning '" + name + "' from " + e.getValue() + "...");
            GitOperator.clone(e.getValue(), new File("work/" + name));

            LOGGER.info("Getting '" + name + "'" + " signatures...");
            File src = new File("work/" + name + "/src/");

            if (!src.exists()) {
                LOGGER.severe("Unable to locate '" + name + "' src/ directory!");
                return;
            }

            List<String> signatures = JavaParser.getSignaturesRecursively(src);

            LOGGER.info("Analyzing '" + name + "'...");
            for (String signature : apiSignatures) {
                boolean impl = signatures.contains(signature);
                csv.setMemberImplemented(name, signature, impl);
                if (!impl)
                    LOGGER.warning(signature + " is not implemented in '" + name + "'!");
            }
        }

        LOGGER.info("Saving CSV into ./info.csv...");
        csv.write(new File("./info.csv"));

        LOGGER.info("Done.");
    }

    private static void setupLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                DateFormat format = new SimpleDateFormat("HH:mm:ss");
                return MessageFormat.format("[{0}] [{1}]: {2}\n", format.format(new Date(record.getMillis())),
                        record.getLevel().toString(), record.getMessage());
            }
        });

        LOGGER.addHandler(handler);
    }

    public static void deleteRecursively(File what) {
        if (what.isDirectory()) {
            File[] files = what.listFiles();
            for (File file : files)
                deleteRecursively(file);
        }

        what.delete();
    }
}
