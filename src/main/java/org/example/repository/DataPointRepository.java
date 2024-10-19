package org.example.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataPointRepository {

    private static Map<Float, Float> dataPoint = new HashMap<>();

    public static Map<Float, Float> getDataPoint() {
        return dataPoint;
    }

    public static void setDataPoint(Map<Float, Float> dataPoint) {
        DataPointRepository.dataPoint = dataPoint;
    }
}