package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcivityMessageListner {


    private final ActivityAIService activityAIService;

    private final RecommendationRepo recommendationRepo;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) {
        log.info("Processing activity {}", activity);

        Recommendation recommendation = activityAIService.generateRecommendation(activity);
        recommendationRepo.save(recommendation);
    }

}
