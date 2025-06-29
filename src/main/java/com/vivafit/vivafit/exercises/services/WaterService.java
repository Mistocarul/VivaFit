package com.vivafit.vivafit.exercises.services;

import com.vivafit.vivafit.exercises.dto.WaterDto;
import com.vivafit.vivafit.exercises.entities.Water;
import com.vivafit.vivafit.exercises.repositories.WaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WaterService {
    @Autowired
    private WaterRepository waterRepository;

    public List<WaterDto> getAllWaterByUser(Integer userId, LocalDate date) {
        List<Water> waterList = Optional.ofNullable(waterRepository.findByUserIdAndDate(userId, date))
                .orElse(List.of());

        if (waterList.isEmpty()) {
            Water newWater = Water.builder()
                    .userId(userId)
                    .amount(0.00)
                    .date(date)
                    .build();
            waterRepository.save(newWater);
            waterList = List.of(newWater);
        }

        return waterList.stream()
                .map(water -> WaterDto.builder()
                        .id(water.getId())
                        .userId(water.getUserId())
                        .amount(water.getAmount())
                        .date(water.getDate())
                        .build())
                .collect(Collectors.toList());
    }

    public WaterDto updateWater(WaterDto waterDto) {
        Optional<Water> existingWaterOpt = waterRepository.findById(waterDto.getId());

        if (existingWaterOpt.isPresent()) {
            Water existingWater = existingWaterOpt.get();
            double updatedAmount = existingWater.getAmount() + waterDto.getAmount();

            existingWater.setAmount(Math.max(updatedAmount, 0.00));
            waterRepository.save(existingWater);

            return WaterDto.builder()
                    .id(existingWater.getId())
                    .userId(existingWater.getUserId())
                    .amount(existingWater.getAmount())
                    .date(existingWater.getDate())
                    .build();
        }
        return null;
    }
}
