package com.fitforge.fitforge_backend.chatbot;

import com.fitforge.fitforge_backend.chatbot.dto.ChatRequest;
import com.fitforge.fitforge_backend.chatbot.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(
                chatbotService.chat(userDetails.getUsername(), request));
    }
}