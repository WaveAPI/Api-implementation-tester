package org.waveapi.impltester.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSV {
    private Map<String, Map<String, Boolean>> data;
    private List<String> apiSignatures;

    public CSV(List<String> apiSignatures) {
        data = new LinkedHashMap<>();
        this.apiSignatures = apiSignatures;
    }

    public void setMemberImplemented(String implementation, String signature, boolean implemented) {
        Map<String, Boolean> impl = data.getOrDefault(implementation, new HashMap<>());
        impl.put(signature, implemented);
        data.put(implementation, impl);
    }

    public void write(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateString() {
        CSVLine header = new CSVLine();
        header.addString("Member");
        header.addAllStrings(data.keySet());

        List<CSVLine> lines = new ArrayList<>();

        for (String signature : apiSignatures) {
            CSVLine line = new CSVLine();
            line.addString(signature);

            for (Map<String, Boolean> impl : data.values()) {
                if (impl.get(signature))
                    line.addCheck();
                else
                    line.addCross();
            }

            lines.add(line);
        }

        lines.sort(new CSVLineComparator());

        StringBuilder sb = new StringBuilder();
        sb.append(header);

        for (CSVLine line : lines)
            sb.append(line);

        return sb.toString();
    }
}
