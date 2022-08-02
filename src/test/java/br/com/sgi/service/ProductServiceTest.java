package br.com.sgi.service;


import br.com.sgi.controller.dto.ProductRequestDTO;
import br.com.sgi.entity.Product;
import br.com.sgi.entity.transformer.ProductTransformer;
import br.com.sgi.repository.ProductRepository;
import br.com.sgi.service.dto.ProductDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends AbstractTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private static ProductTransformer productTransformer;

    @BeforeAll
    static void setUp() {
        productTransformer = mock(ProductTransformer.class);
        ModelMapper modelMapper = new ModelMapper();
        ReflectionTestUtils.setField(productTransformer, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(productTransformer, "dtoClass", ProductDTO.class);
    }

    @BeforeEach
    void beforeEach() {
        when(productTransformer.toDTO(any(Product.class))).thenCallRealMethod();
    }

    @Test
    void updateWhithInvalidUserIdShouldThrowException() {
        ProductRequestDTO request = createProductRequestDTO();

        doReturn(Optional.empty()).when(productRepository).findById(request.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> productService.update(request));
        assertThat(ex.getMessage()).isEqualTo("Could not find user: " + request.getId());


        verify(productRepository).findById(eq(request.getId()));
    }

    @Test
    void updateWhithValidUserIdShouldUpdate() {
        ProductRequestDTO request = createProductRequestDTO();
        Product product = createProduct();

        doReturn(Optional.of(product)).when(productRepository).findById(anyLong());

        productService.update(request);
        assertThat(product.getName()).isEqualTo(request.getName());

        verify(productRepository).findById(anyLong());
    }

    @Test
    void createWhithUserExistsShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        SQLException mockSqlEx = mock(SQLException.class);
        ProductRequestDTO dto = createProductRequestDTO();

        when(mockEx.getRootCause()).thenReturn(mockSqlEx);
        when(((SQLException) mockEx.getRootCause()).getSQLState()).thenReturn("23505");
        when(productRepository.save(any(Product.class))).thenThrow(mockEx);

        EntityExistsException ex = assertThrows(EntityExistsException.class, () -> productService.create(dto));

        verify(productRepository).save(any(Product.class));
        assertThat(ex.getMessage()).isEqualTo("User already exists: " + dto.getName());
    }

    @Test
    void createWithSQLExceptionShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        SQLException mockSqlEx = mock(SQLException.class);
        ProductRequestDTO dto = createProductRequestDTO();

        when(mockEx.getRootCause()).thenReturn(mockSqlEx);
        when(((SQLException) mockEx.getRootCause()).getSQLState()).thenReturn("anyState");
        when(productRepository.save(any(Product.class))).thenThrow(mockEx);

        verify(mockEx).getRootCause();
        assertThrows(DataIntegrityViolationException.class, () -> productService.create(dto));
    }

    @Test
    void createWhithDataIntegrityViolationShouldThrowException() {
        DataIntegrityViolationException mockEx = mock(DataIntegrityViolationException.class);
        ProductRequestDTO dto = createProductRequestDTO();

        when(productRepository.save(any(Product.class))).thenThrow(mockEx);

        assertThrows(DataIntegrityViolationException.class, () -> productService.create(dto));
    }

    @Test
    void findByIdWithValidIdShouldReturnDTO() {
        Product product = createProduct();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDTO dto = productService.findById(1L);
        assertThat(dto.getName()).isEqualTo(product.getName());

        verify(productRepository).findById(anyLong());
    }

    @Test
    void findByIdWithInvalidIdShouldThrowException() {
        doReturn(Optional.empty()).when(productRepository).findById(anyLong());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> productService.findById(1L));
        assertThat(ex.getMessage()).isEqualTo("Could not find user: 1");

        verify(productRepository).findById(anyLong());
    }


}