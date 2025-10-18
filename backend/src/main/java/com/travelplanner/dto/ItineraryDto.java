package com.travelplanner.dto;

import com.travelplanner.entity.Itinerary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDto {
    
    private Long id;
    
    @NotBlank(message = "Activity name is required")
    private String activityName;
    
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String locationName;
    
    private String locationAddress;
    
    private Double locationLatitude;
    
    private Double locationLongitude;
    
    private Itinerary.ActivityType activityType;
    
    private BigDecimal cost;
    
    private String bookingReference;
    
    private String notes;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long travelPlanId;
    
    // Request DTO for creating itinerary
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Activity name is required")
        private String activityName;
        
        private String description;
        
        @NotNull(message = "Date is required")
        private LocalDate date;
        
        private LocalTime startTime;
        
        private LocalTime endTime;
        
        private String locationName;
        
        private String locationAddress;
        
        private Double locationLatitude;
        
        private Double locationLongitude;
        
        @NotNull(message = "Activity type is required")
        private Itinerary.ActivityType activityType;
        
        private BigDecimal cost;
        
        private String bookingReference;
        
        private String notes;
        
        private Integer sortOrder;
    }
    
    // Request DTO for updating itinerary
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String activityName;
        private String description;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String locationName;
        private String locationAddress;
        private Double locationLatitude;
        private Double locationLongitude;
        private Itinerary.ActivityType activityType;
        private BigDecimal cost;
        private String bookingReference;
        private String notes;
        private Integer sortOrder;
    }
}