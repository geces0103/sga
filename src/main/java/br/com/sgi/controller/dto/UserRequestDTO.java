package br.com.sgi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserRequestDTO {
    @NonNull
    private Long id;
    @NonNull
    private String identifier;
    @NonNull
    private String name;
    @NonNull
    private String document;
    @NonNull
    private LocalDateTime creation;
    @NonNull
    private LocalDateTime updated;

}
