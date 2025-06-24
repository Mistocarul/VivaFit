package com.vivafit.vivafit.specialist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagesSpecialistsDto {
    private Integer id;
    private Integer userId;
    private Integer specialistId;
    private String userCompletName;
    private String userPhoneNumber;
    private String message;
    private String userEmail;
    private String createdAt;
}
