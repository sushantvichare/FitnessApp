package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repo.ActivityRepo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepo activityRepo;

    public ActivityResponse trackActivity(ActivityRequest request) {
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepo.save(activity);


        return mapToResponse(savedActivity);

    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setUserId(activity.getUserId());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;
    }


    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activities = activityRepo.findByUserId(userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());


    }

    public ActivityResponse getActivity(String activityId) {
        return activityRepo.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()->new RuntimeException("Not found"));


    }
}
