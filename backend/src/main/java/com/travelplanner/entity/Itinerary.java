package com.travelplanner.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("itineraries")
public class Itinerary {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotBlank(message = "Activity name is required")
    @TableField("activity_name")
    private String activityName;
    
    @TableField("description")
    private String description;
    
    @NotNull(message = "Date is required")
    @TableField("date")
    private LocalDate date;
    
    @TableField("start_time")
    private LocalTime startTime;
    
    @TableField("end_time")
    private LocalTime endTime;
    
    @TableField("location_name")
    private String locationName;
    
    @TableField("location_address")
    private String locationAddress;
    
    @TableField("location_latitude")
    private Double locationLatitude;
    
    @TableField("location_longitude")
    private Double locationLongitude;
    
    @TableField("activity_type")
    private ActivityType activityType;
    
    @TableField("cost")
    private BigDecimal cost;
    
    @TableField("booking_reference")
    private String bookingReference;
    
    @TableField("notes")
    private String notes;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField("travel_plan_id")
    private Long travelPlanId;
    
    @TableField(exist = false)
    private TravelPlan travelPlan;
    
    public enum ActivityType {
        ACCOMMODATION, TRANSPORTATION, SIGHTSEEING, DINING, SHOPPING, ENTERTAINMENT, OTHER
    }
}