package br.com.sgi.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "${api.version}/products", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductDTO response = this.productService.create(dto);
        return ResponseEntity.created(getLocation("/products", response.getId())).body(response);
    }

    @PutMapping
    public ResponseEntity<ProductDTO> updateProducts(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(this.productService.update(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productService.findById(id));
    }

    public URI getLocation(String path, Long id) {
        return URI.create(String.format("v1".concat(path).concat("/%s"), id));
    }

}
