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
@Table(name = "tb_product")
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @CreatedDate
    @Column(name = "creation")
    private LocalDateTime creation;

    @Column(name = "updated")
    private LocalDateTime updated;

}
