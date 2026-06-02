package com.example.ubersocketserver.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideResponseDto {

    private Boolean response;
    private Long bookingId;

}