package com.project.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(name = "Team", description = "A team")
public record TeamDTO(
        UUID id,
        @NotBlank
        @Size(min = 3, max = 20)
        String name) { }

