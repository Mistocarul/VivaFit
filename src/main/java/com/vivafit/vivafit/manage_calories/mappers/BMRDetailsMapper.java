package com.vivafit.vivafit.manage_calories.mappers;

import com.vivafit.vivafit.manage_calories.entities.BMRDetails;
import com.vivafit.vivafit.manage_calories.responses.BMRDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BMRDetailsMapper {
    @Mapping(target = "message", constant = "BMR details retrieved successfully")
    BMRDetailsResponse toResponse(BMRDetails bmrDetails);
}
