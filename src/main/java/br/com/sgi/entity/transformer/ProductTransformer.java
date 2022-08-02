package br.com.sgi.entity.transformer;

import org.springframework.stereotype.Component;

@Component
public class ProductTransformer extends AbstractTransformer<Product, ProductDTO>{
    protected ProductTransformer() {
        super(Product.class, ProductDTO.class);
    }
}