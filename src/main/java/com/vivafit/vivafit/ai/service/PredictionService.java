package com.vivafit.vivafit.ai.service;

import com.vivafit.vivafit.ai.dto.PredictionRequestDto;
import com.vivafit.vivafit.ai.responses.PredictionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PredictionService {
    private final String modelPath = "src/main/resources/static/AI_resources/model_calorii";
    private final String scalerPath = "src/main/resources/static/AI_resources/scaler_calorii.csv";

    @Autowired
    private DataScalerService dataScalerService;

    public PredictionResponse predictCalories(PredictionRequestDto request) {
        try (SavedModelBundle model = SavedModelBundle.load(
                new File(modelPath).getAbsolutePath(), "serve")) {

            dataScalerService.loadScallerData(scalerPath);

            double[] inputData = {
                    request.getGender(),
                    request.getAge(),
                    request.getHeightInCm(),
                    request.getWeightInKg(),
                    request.getDurationInMinutes(),
                    request.getHeartRateInBpm(),
                    request.getBodyTemperatureInCelsius()
            };

            double[] scaledData = dataScalerService.scaleData(inputData);

            float[] floatScaledData = new float[scaledData.length];
            for (int i = 0; i < scaledData.length; i++) {
                floatScaledData[i] = (float) scaledData[i];
            }

            try (Tensor<Float> inputTensor = Tensor.create(
                    new long[]{1, floatScaledData.length},
                    FloatBuffer.wrap(floatScaledData))) {

                Tensor<?> outputTensor = model.session()
                        .runner()
                        .feed("serving_default_dense_input:0", inputTensor)
                        .fetch("StatefulPartitionedCall:0")
                        .run()
                        .get(0);

                float[][] outputValues = new float[1][1];
                outputTensor.copyTo(outputValues);

                PredictionResponse response = new PredictionResponse();
                response.setPredictedCalories(Math.round(outputValues[0][0] * 100.0) / 100.0);

                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during prediction", e);
        }
    }
}
