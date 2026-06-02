package com.example.ubersocketserver.dto;

import com.example.uberentityservice.models.BookingStatus;
import com.example.uberentityservice.models.Driver;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingResponseDto {

    private Long bookingId;
    private BookingStatus status;
    private Driver driver;
}
