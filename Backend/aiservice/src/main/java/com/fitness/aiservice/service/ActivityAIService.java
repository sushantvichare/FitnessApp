package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromtForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI response is {}", aiResponse);



        return processAiResponse(activity,aiResponse);
    }


    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent= textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall: ");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace: ");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate: ");
            addAnalysisSection(fullAnalysis,analysisNode,"CaloriesBurned","Calories Burned: ");

            List<String> improvements = extractImprovement(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(String.valueOf(activity.getType()))
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safetyGuidelines = new ArrayList<>();
        if (safetyNode.isArray()) {
            for (JsonNode safety : safetyNode) {
                safetyGuidelines.add(safety.asText());
            }
        }
        return safetyGuidelines.isEmpty() ? Collections.singletonList("No specific safety guidelines") : safetyGuidelines;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            for (JsonNode suggestionNode : suggestionsNode) {
                String workout = suggestionNode.path("workout").asText();
                String description = suggestionNode.path("description").asText();
                suggestions.add(String.format("%s:%s", workout, description));
            }
        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions") : suggestions;
    }

    private List<String> extractImprovement(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()) {
            for (JsonNode improvementNode : improvementsNode) {
                String area = improvementNode.path("area").asText();
                String detail= improvementNode.path("recommendation").asText();
                improvements.add(String.format("%s:%s",area,detail));
            }
        }
        return improvements.isEmpty()? Collections.singletonList("No specific improvements suggested") : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String preffix) {
           if(!analysisNode.path(key).isMissingNode()) {
               fullAnalysis.append(preffix).append(analysisNode.path(key).asText())
                       .append("\n\n");
           }
    }


    private String createPromtForActivity(Activity activity) {
        return String.format(""" 
                  Analyze this fitness activity and provide detailed recommendations in the following format
                  {
                      "analysis" : {
                          "overall": "Overall analysis here",
                          "pace": "Pace analysis here",
                          "heartRate": "Heart rate analysis here",
                          "CaloriesBurned": "Calories Burned here"
                      },
                      "improvements": [
                          {
                              "area": "Area name",
                              "recommendation": "Detailed Recommendation"
                          }
                      ],
                      "suggestions" : [
                          {
                              "workout": "Workout name",
                              "description": "Detailed workout description"
                          }
                      ],
                      "safety": [
                          "Safety point 1",
                          "Safety point 2"
                      ]
                  }
                
                  Analyze this activity:
                  Activity Type: %s
                  Duration: %d minutes
                  calories Burned: %d
                  Additional Metrics: %s
                
                  provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines
                  Ensure the response follows the EXACT JSON format shown above.              
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );

    }

}
