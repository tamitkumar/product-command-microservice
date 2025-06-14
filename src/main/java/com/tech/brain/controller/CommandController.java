package com.tech.brain.controller;

import com.tech.brain.model.Product;
import com.tech.brain.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/command/v1")
public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @Operation(
            summary = "Create a new product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    name = "Product Example",
                                    value = """
                                        {
                                          "productCode": "PROD123",
                                          "name": "Gaming Mouse",
                                          "description": "Ergonomic wireless gaming mouse",
                                          "price": 1499.99,
                                          "version": 1
                                        }
                                        """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/add")
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        return ResponseEntity.ok().body(commandService.createProduct(product));
    }

    @Operation(
            summary = "Update an existing product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    name = "Updated Product Example",
                                    value = """
                                        {
                                          "productCode": "PROD123",
                                          "name": "Updated Gaming Mouse",
                                          "description": "Updated ergonomic gaming mouse",
                                          "price": 1399.99,
                                          "version": 2
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Product updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable long id, @RequestBody Product product){
        return ResponseEntity.accepted().body(commandService.updateProduct(id, product));
    }

    @Operation(
            summary = "Delete a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        return ResponseEntity.ok(commandService.deleteProduct(id));
    }
}
