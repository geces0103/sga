package br.com.sgi.service;

import br.com.sgi.controller.dto.ProductRequestDTO;
import br.com.sgi.entity.Product;
import br.com.sgi.entity.transformer.ProductTransformer;
import br.com.sgi.repository.ProductRepository;
import br.com.sgi.service.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTransformer productTransformer;

    public ProductDTO create (ProductRequestDTO dto) {
        try {
            return productTransformer.toDTO(
                    productRepository.save(Product.builder()
                            .identifier(dto.getIdentifier())
                            .name(dto.getName())
                            .creation(LocalDateTime.now())
                            .updated(dto.getUpdated()).build()));

        } catch (DataIntegrityViolationException ex) {
            if (ex.getRootCause() instanceof SQLException) {
                final SQLException rootCause = (SQLException) ex.getRootCause();
                if (rootCause != null && rootCause.getSQLState().equals("23505")) {
                    throw new EntityExistsException("User already exists: " + dto.getName());
                } else {
                    throw ex;
                }
            } else {
                throw ex;
            }
        }
    }

    public ProductDTO update (ProductRequestDTO dto) {
        return productTransformer.toDTO(update(productRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Could not find user: " + dto.getId())), dto));
    }

    private Product update(Product product, ProductRequestDTO dto) {
        product.setCreation(dto.getCreation());
        product.setIdentifier(dto.getIdentifier());
        product.setUpdated(dto.getUpdated());
        product.setCreation(dto.getCreation());
        return productRepository.save(product);
    }

    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(productTransformer::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user: " + id));
    }


}
