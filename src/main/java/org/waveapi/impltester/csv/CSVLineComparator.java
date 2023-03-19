package org.waveapi.impltester.csv;

import java.util.Comparator;
import java.util.List;

public class CSVLineComparator implements Comparator<CSVLine> {
    @Override
    public int compare(CSVLine line1, CSVLine line2) {
        int line1CheckSum = calculatePositionSum(line1.getCheckPositions());
        int line2CheckSum = calculatePositionSum(line2.getCheckPositions());
        return line2CheckSum - line1CheckSum;
    }

    private int calculatePositionSum(List<Integer> positions) {
        int sum = 0;

        for (int position : positions)
            sum += position;

        return sum;
    }
}
