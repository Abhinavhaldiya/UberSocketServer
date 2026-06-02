package com.example.ubersocketserver.controller;

import com.example.ubersocketserver.dto.RideRequestDto;
import com.example.ubersocketserver.dto.RideResponseDto;
import com.example.ubersocketserver.dto.UpdateBookingRequestDto;
import com.example.ubersocketserver.dto.UpdateBookingResponseDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    // TTL-based cache prevents unbounded growth; entries expire 10 min after booking is claimed
    private final Cache<Long, Boolean> assignedBookings = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public DriverRequestController(SimpMessagingTemplate messagingTemplate, RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    @PostMapping("/newride")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDto requestDto) {
        sendDriversNewRideRequest(requestDto);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    public void sendDriversNewRideRequest(RideRequestDto requestDto) {
        requestDto.getDriverIds().forEach(driverId ->
                messagingTemplate.convertAndSend("/topic/rideRequest/" + driverId, requestDto)
        );
    }

    @MessageMapping("/rideResponse/{userId}")
    public void rideResponseHandler(@DestinationVariable String userId, RideResponseDto rideResponseDto) {
        Long bookingId = rideResponseDto.getBookingId();

        if (!Boolean.TRUE.equals(rideResponseDto.getResponse())) {
            System.out.println("Driver " + userId + " rejected booking " + bookingId);
            return;
        }

        // atomically claim this booking — only the first accepted response wins
        if (assignedBookings.asMap().putIfAbsent(bookingId, Boolean.TRUE) != null) {
            System.out.println("Booking " + bookingId + " already claimed — ignoring accept from driver " + userId);
            return;
        }

        System.out.println("Driver " + userId + " accepted booking " + bookingId + " — assigning...");
        UpdateBookingRequestDto requestDto = UpdateBookingRequestDto.builder()
                .driverId(Long.parseLong(userId))
                .status("DRIVER_ASSIGNED")
                .build();
        String bookingServiceUrl = getServiceUrl("UberBookingService");
        ResponseEntity<UpdateBookingResponseDto> result = this.restTemplate.postForEntity(
                bookingServiceUrl + "/api/v1/booking/" + bookingId,
                requestDto,
                UpdateBookingResponseDto.class
        );
        System.out.println("Booking service response: " + result.getStatusCode());

        // notify the passenger that a driver has been assigned
        if (result.getBody() != null) {
            messagingTemplate.convertAndSend("/topic/rideStatus/" + bookingId, result.getBody());
        }
    }

    private String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances found for service: " + serviceName);
        }
        return instances.get(0).getUri().toString();
    }
}
