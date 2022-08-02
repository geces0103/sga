package br.com.sgi.controller;


import br.com.sgi.controller.dto.ProductRequestDTO;
import br.com.sgi.entity.Product;
import br.com.sgi.repository.ProductRepository;
import br.com.sgi.service.AbstractTest;
import br.com.sgi.service.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIT extends AbstractTest {

    public static final String ANY_NAME = "anyName";
    private static final String ENCODING = "utf-8";
    public static final String ANY_DESCRIPTION = "anyDescription";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    private static final String BASE_URI = "/products";

    @Test
    void createUserWithValidParametersShouldCreateUser() throws Exception {
        ProductRequestDTO productRequestDTO = createProductRequestDTO();

        MockHttpServletResponse response = mvc.perform(post(versionApi + BASE_URI)
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse();

        ProductDTO dto = mapper.readValue(response.getContentAsString(), ProductDTO.class);

        assertThat(dto.getName()).isEqualTo(ANY_NAME);
        assertThat(dto.getPrice()).isEqualTo(1D);
    }

    @Test
    void updateUserWithValidParameterShouldUpdateUser() throws Exception {
        Product product = createProductToTest();
        ProductRequestDTO productRequestDTO = ProductRequestDTO.builder()
                .id(product.getId())
                .identifier(ANY_DESCRIPTION)
                .creation(LocalDateTime.now())
                .updated(LocalDateTime.now().plusDays(15))
                .name(ANY_NAME)
                .price(1D)
                .build();

        MockHttpServletResponse response = mvc.perform(put(versionApi + BASE_URI)
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ProductDTO dto = mapper.readValue(response.getContentAsString(), ProductDTO.class);

        assertThat(dto.getName()).isEqualTo(ANY_NAME);
    }

    @Test
    void getUserByIdWithValidIdShouldReturnDTO() throws Exception {
        Product product = createProduct();

        MockHttpServletResponse response = mvc.perform(get(versionApi + BASE_URI + "/" + product.getId())
                        .characterEncoding(ENCODING)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse();

        ProductDTO dto = mapper.readValue(response.getContentAsString(), ProductDTO.class);

        assertThat(dto.getId()).isEqualTo(product.getId());
    }

    private Product createProductToTest() {
        Product product = createProduct();
        product.setId(null);
        product.setIdentifier(ANY_DESCRIPTION);
        product.setPrice(1D);
        product.setName(ANY_NAME);
        product.setCreation(LocalDateTime.now());
        product.setUpdated(LocalDateTime.now().plusDays(15));

        return productRepository.save(product);
    }

}