package com.fitforge.fitforge_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

// This is the standard error shape every API error will return
@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}