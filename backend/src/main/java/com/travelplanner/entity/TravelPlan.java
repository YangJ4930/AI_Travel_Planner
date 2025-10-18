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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("travel_plans")
public class TravelPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @TableField("title")
    private String title;
    
    @TableField("description")
    private String description;
    
    @NotBlank(message = "Destination is required")
    @TableField("destination")
    private String destination;
    
    @NotNull(message = "Start date is required")
    @TableField("start_date")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @TableField("end_date")
    private LocalDate endDate;
    
    @TableField("budget")
    private BigDecimal budget;
    
    @TableField("status")
    private PlanStatus status = PlanStatus.DRAFT;
    
    @TableField("is_public")
    private Boolean isPublic = false;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private List<Itinerary> itineraries;
    
    public enum PlanStatus {
        DRAFT, PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}