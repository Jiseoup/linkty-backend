package com.linkty.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.response.MessageResponse;
import com.linkty.services.UrlManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlManagementController {

    private final UrlManagementService urlManagementService;

    // Toggle URL active status.
    @PatchMapping("/{urlId}/active")
    public ResponseEntity<MessageResponse> toggleActive(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long urlId) {

        MessageResponse response =
                urlManagementService.toggleUrlActive(authToken, urlId);
        return ResponseEntity.ok(response);
    }
}
