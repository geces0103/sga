package br.com.sgi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProductRequestDTO {
    @NonNull
    private Long id;
    @NonNull
    private String identifier;
    @NonNull
    private String name;
    @NonNull
    private Double price;
    @NonNull
    private LocalDateTime creation;
    @NonNull
    private LocalDateTime updated;

}
