package br.com.sgi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "creation")
    private LocalDateTime creation;

    @Column(name = "updated")
    private LocalDateTime updated;

    @Column(name = "username", length = 128, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "hash_password")
    private String hashPassword;

    @Column(name = "active", nullable = false)
    private Boolean active;

}
