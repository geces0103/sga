package br.com.sgi.service;

import br.com.sgi.controller.dto.ProductRequestDTO;
import br.com.sgi.controller.dto.UserRequestDTO;
import br.com.sgi.entity.Product;
import br.com.sgi.entity.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;


public abstract class AbstractTest {
    public static final String ANY_NAME = "anyName";
    public static final String ANY_DESCRIPTION = "anyDescription";
    public static final Long ANY_NUMBER = 1L;

    @Value("${api.version}")
    protected String versionApi;

    protected static final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());


    public Product createProduct(){

        return Product.builder()
                .price(1D)
                .identifier(ANY_DESCRIPTION)
                .creation(LocalDateTime.now())
                .updated(LocalDateTime.now().plusDays(15))
                .name(ANY_NAME)
                .id(ANY_NUMBER)
                .build();
    }

    public ProductRequestDTO createProductRequestDTO(){
        return ProductRequestDTO.builder()
                .id(ANY_NUMBER)
                .price(1D)
                .identifier(ANY_DESCRIPTION)
                .creation(LocalDateTime.now())
                .updated(LocalDateTime.now().plusDays(15))
                .name(ANY_NAME)
                .build();
    }

    protected UserRequestDTO createUserRequestDTO() {
        return null;
    }
    protected User createUser() {
        return null;
    }

}