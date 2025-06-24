package com.vivafit.vivafit.weight.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.weight.dto.WeightDto;
import com.vivafit.vivafit.weight.services.WeightService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Tag(name = "Weight", description = "Weight Controller")
@RequestMapping("/api/weight")
@RestController
@Validated
public class WeightController {
    @Autowired
    private WeightService weightService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/add-weight")
    public WeightDto addWeight(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,@Valid @RequestBody WeightDto weightDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        weightDto.setUserId(currentUser.getId());
        return weightService.addWeight(weightDto);
    }

    @PutMapping("/update-weight")
    public WeightDto updateWeight(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @RequestBody WeightDto weightDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        weightDto.setUserId(currentUser.getId());
        return weightService.updateWeight(weightDto);
    }

    @GetMapping("/get-weight/{date}")
    public WeightDto getWeight(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String date) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return weightService.getWeightByDate(currentUser.getId(), date);
    }

    @GetMapping("/get-weights-by-dates/{startDate}/{endDate}")
    public List<WeightDto> getWeightsByDates(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @PathVariable String startDate,
                                             @PathVariable String endDate) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return weightService.getWeightsByDates(currentUser.getId(), startDate, endDate);
    }

    @DeleteMapping("/delete-weight/{id}")
    public void deleteWeight(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        weightService.deleteWeight(id, currentUser.getId());
    }

    @GetMapping("/get-all-weights-predicted")
    public List<Double> getAllWeightsPredicted(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Double> lastWeights = weightService.getLast30Weights(currentUser.getId());

        if (lastWeights.isEmpty()) {
            return List.of();
        }

        Map<String, List<Double>> requestPayload = Map.of("greutati", lastWeights);

        RestTemplate restTemplate = new RestTemplate();
        String predictionApiUrl = "http://localhost:9999/api/weight/predict-weight";
        Map<String, List<Double>> response = restTemplate.postForObject(predictionApiUrl, requestPayload, Map.class);

        return response != null ? response.getOrDefault("predictii", List.of()) : List.of();
    }

    @GetMapping("/get-ble-devices")
    public ResponseEntity<Object> getBleDevices(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);

        RestTemplate restTemplate = new RestTemplate();
        String bleDevicesApiUrl = "http://localhost:9999/api/ble/devices";

        ResponseEntity<Object> response = restTemplate.getForEntity(bleDevicesApiUrl, Object.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @GetMapping("/get-weight-from-scale")
    public ResponseEntity<Object> getWeightFromScale(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam String adresaMac) {

        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);

        String fastApiUrl = "http://localhost:9999/api/weight/get-weight-scale?adresa_mac=" + adresaMac;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(fastApiUrl, Object.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}
