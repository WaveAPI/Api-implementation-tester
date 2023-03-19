package org.waveapi.impltester.csv;

import java.util.ArrayList;
import java.util.List;

public class CSVLine {
    private final StringBuilder sb;

    private int currentPosition = 0;
    private final List<Integer> checkPositions;
    private final List<Integer> crossPositions;

    public CSVLine() {
        sb = new StringBuilder();

        checkPositions = new ArrayList<>();
        crossPositions = new ArrayList<>();
    }

    public void addAllStrings(Iterable<String> strings) {
        for (String string : strings)
            addString(string);
    }

    public void addString(String string) {
        sb.append("\"").append(string).append("\",");
        currentPosition++;
    }

    public void addCheck() {
        checkPositions.add(currentPosition);
        addString("✅");
    }

    public void addCross() {
        crossPositions.add(currentPosition);
        addString("❌");
    }

    public String toString() {
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        return sb.append("\n").toString();
    }

    protected List<Integer> getCheckPositions() {
        return checkPositions;
    }

    protected List<Integer> getCrossPositions() {
        return crossPositions;
    }
}
