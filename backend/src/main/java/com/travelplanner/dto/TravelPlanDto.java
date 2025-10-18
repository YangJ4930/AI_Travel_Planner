package com.travelplanner.dto;

import com.travelplanner.entity.TravelPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private BigDecimal budget;
    
    private TravelPlan.PlanStatus status;
    
    private Boolean isPublic;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long userId;
    
    private String username;
    
    private List<ItineraryDto> itineraries;
    
    // Request DTO for creating travel plan
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        private String description;
        
        @NotBlank(message = "Destination is required")
        private String destination;
        
        @NotNull(message = "Start date is required")
        private LocalDate startDate;
        
        @NotNull(message = "End date is required")
        private LocalDate endDate;
        
        private BigDecimal budget;
        
        private Boolean isPublic = false;
    }
    
    // Request DTO for updating travel plan
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private String destination;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budget;
        private TravelPlan.PlanStatus status;
        private Boolean isPublic;
    }
}