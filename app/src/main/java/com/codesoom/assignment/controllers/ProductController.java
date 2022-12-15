package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;
    private final AuthenticationService authenticationService;

    public ProductController(ProductService productService, AuthenticationService authenticationService) {
        this.productService = productService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid ProductData productData
    ) {
        if (!isValidToken(authorization)) {
            throw new InvalidTokenException(authorization);
        }

        return productService.createProduct(productData);
    }

    @PatchMapping("{id}")
    public Product update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody @Valid ProductData productData
    ) {
        if (!isValidToken(authorization)) {
            throw new InvalidTokenException(authorization);
        }

        return productService.updateProduct(id, productData);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        if (!isValidToken(authorization)) {
            throw new InvalidTokenException(authorization);
        }

        productService.deleteProduct(id);
    }

    private boolean isValidToken(String authorization) {
        String accessToken = authorization.substring("Bearer ".length());
        return authenticationService.isValidToken(accessToken);
    }
}
