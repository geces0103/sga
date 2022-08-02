package br.com.sgi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserRequestDTO {
    @NonNull
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String description;
    @NonNull
    private String email;

    private LocalDateTime creation;

    private LocalDateTime updated;

    @NonNull
    private String hashPassword;

    private Boolean active;

}
