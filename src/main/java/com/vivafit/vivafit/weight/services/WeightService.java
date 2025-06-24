package com.vivafit.vivafit.weight.services;

import com.vivafit.vivafit.weight.dto.WeightDto;
import com.vivafit.vivafit.weight.entities.Weight;
import com.vivafit.vivafit.weight.repositories.WeightRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
public class WeightService {
    @Autowired
    private WeightRepository weightRepository;

    public WeightDto addWeight(WeightDto weightDto) {
        Weight weight = Weight.builder()
                .date(weightDto.getDate())
                .value(weightDto.getValue())
                .userId(weightDto.getUserId())
                .build();

        Weight savedWeight = weightRepository.save(weight);

        return WeightDto.builder()
                .id(savedWeight.getId())
                .date(savedWeight.getDate())
                .value(savedWeight.getValue())
                .userId(savedWeight.getUserId())
                .build();
    }

    public WeightDto updateWeight(WeightDto weightDto) {
        Weight existingWeight = weightRepository.findById(weightDto.getId())
                .orElseThrow(() -> new RuntimeException("Weight not found with ID: " + weightDto.getId()));

        existingWeight.setDate(weightDto.getDate());
        existingWeight.setValue(weightDto.getValue());
        existingWeight.setUserId(weightDto.getUserId());

        Weight updatedWeight = weightRepository.save(existingWeight);

        return WeightDto.builder()
                .id(updatedWeight.getId())
                .date(updatedWeight.getDate())
                .value(updatedWeight.getValue())
                .userId(updatedWeight.getUserId())
                .build();
    }

    public WeightDto getWeightByDate(Integer userId, String date) {
        return weightRepository.findByUserIdAndDate(userId, LocalDate.parse(date))
                .map(weight -> WeightDto.builder()
                        .id(weight.getId())
                        .date(weight.getDate())
                        .value(weight.getValue())
                        .userId(weight.getUserId())
                        .build())
                .orElse(WeightDto.builder().build());
    }

    public List<WeightDto> getWeightsByDates(Integer userId, String startDate, String endDate) {
        List<Weight> weights = weightRepository.findByUserIdAndDateBetween(
                        userId, LocalDate.parse(startDate), LocalDate.parse(endDate))
                .orElse(List.of());

        return weights.stream()
                .map(weight -> WeightDto.builder()
                        .id(weight.getId())
                        .date(weight.getDate())
                        .value(weight.getValue())
                        .userId(weight.getUserId())
                        .build())
                .toList();
    }

    public void deleteWeight(Integer weightId, Integer userId) {
        Weight weight = weightRepository.findById(weightId)
                .orElseThrow(() -> new RuntimeException("Weight not found with ID: " + weightId));

        if (!weight.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this weight entry.");
        }

        weightRepository.delete(weight);
    }

    public List<Double> getLast30Weights(Integer userId) {
        return weightRepository.findTop30ByUserIdOrderByDateDesc(userId)
                .stream()
                .map(Weight::getValue)
                .toList();
    }
}
