package com.spring_ai.ai_code_reviewer.controller;

import com.spring_ai.ai_code_reviewer.service.CodeReviewService;
import com.spring_ai.ai_code_reviewer.service.ModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST Controller for AI-powered code review functionality.
 */
@RestController
@RequestMapping("/api/code-review")
public class CodeReviewController {

        @Autowired
        private ModelService modelService;

        @Autowired
        private CodeReviewService codeReviewService;

        @PostMapping
        public Map<String, Object> reviewCode(
                        @RequestBody Map<String, String> request,
                        @RequestParam(defaultValue = "openai") String provider,
                        @RequestParam(defaultValue = "openai/gpt-oss-20b") String model) {
                // Extract input

                String code = request.get("code");
                String language = request.getOrDefault("language", "Java");
                String businessRequirements = request.get("businessRequirements");

                // Validate input

                if (code == null || code.trim().isEmpty()) {
                        return Map.of(
                                        "error", true,
                                        "message", "Code cannot be empty");
                }

                // Check provider-specific requirements

                if (!"openai".equalsIgnoreCase(provider) &&
                                (model == null || model.isEmpty())) {

                        return Map.of(
                                        "error", true,
                                        "message", "AI-Model header is required when using provider: " + provider);
                }

                // Create prompt

                Prompt prompt = codeReviewService.createCodeReviewPrompt(code, language, businessRequirements);

                // Get AI Client

                ChatClient chatClient = modelService.getChatClient(provider);

                // Execute request

                String review = chatClient.prompt()
                                .user(prompt.getContents())
                                .options(OpenAiChatOptions.builder()
                                                .model(model) // Use the model specified in query param
                                                .build())
                                .call()
                                .content();

                // Build response

                return Map.of(
                                "success", true,
                                "language", language,
                                "provider", provider,
                                "model", model,
                                "review", review,
                                "codeLength", code.length(),
                                "hasBusinessRequirements",
                                businessRequirements != null && !businessRequirements.trim().isEmpty());
        }

        /**
         * Health check endpoint.
         * GET /api/code-review/health
         */
        @GetMapping("/health")
        public Map<String, String> health() {
                return Map.of(
                                "status", "healthy",
                                "service", "Code Review Service");
        }
}