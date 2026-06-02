package com.example.ubersocketserver.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDto {

    private Long passengerId;

    private List<Long> driverIds;

    private Long bookingId;
}