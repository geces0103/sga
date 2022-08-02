package br.com.sgi.entity.transformer;

import br.com.sgi.entity.Product;
import br.com.sgi.service.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductTransformer extends AbstractTransformer<Product, ProductDTO>{
    protected ProductTransformer() {
        super(Product.class, ProductDTO.class);
    }
}