package com.vivafit.vivafit.ai.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataScalerService {
    private Map<String, Double> means = new HashMap<>();
    private Map<String, Double> stdDevs = new HashMap<>();

    public void loadScallerData(String scalerFilePath) throws IOException {
        Path path = Paths.get(scalerFilePath);
        try (Reader reader = Files.newBufferedReader(path)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);

            for (CSVRecord record : records) {
                String feature = record.get("feature");
                double mean = Double.parseDouble(record.get("medie"));
                double stdDev = Double.parseDouble(record.get("deviatie_standard"));

                means.put(feature, mean);
                stdDevs.put(feature, stdDev);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading scaler data", e);
        }
    }

    public double[] scaleData(double[] data) {
        double[] scaledData = new double[data.length];

        String features[] = {"Gender", "Age", "Height", "Weight", "Duration", "Heart_Rate", "Body_Temp", "Calories"};

        for (int i = 0; i < data.length; i++) {
            String feature = features[i];
            double mean = means.get(feature);
            double stdDev = stdDevs.get(feature);

            scaledData[i] = (data[i] - mean) / stdDev;
        }
        return scaledData;
    }
}
