package com.example.foodlens.controllers;

import com.example.foodlens.model.Product;
import com.example.foodlens.model.User;
import com.example.foodlens.services.GeminiService;
import com.example.foodlens.services.ProductService;
import com.example.foodlens.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return ResponseEntity.ok("Product added successfully!");
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(products);
        }
        return ResponseEntity.ok(products);
    }


    @GetMapping("/all")
        public ResponseEntity<List<Product>> getAllProducts() {
            return ResponseEntity.ok(productService.getAllProducts());
        }

        @GetMapping("/category/{category}/product/{productName}")
        public ResponseEntity<String> getProductAnalysis(@PathVariable String category,
                                                         @PathVariable String productName,
                                                         @RequestParam(required = false) String email) {
            try {
                // Fetch product details
                Optional<Product> productOpt = productService.getProductByNameInCategory(category, productName);

                if (productOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
                }

                Product product = productOpt.get();
                String productDetails = formatProductDetails(product);

                // Fetch user details if email is provided
                String userDetails = "No user health details provided.";
                if (email != null && !email.isBlank()) {
                    Optional<User> userOpt = userService.getUserByEmail(email);
                    userDetails = userOpt.map(this::formatUserDetails).orElse("No specific user details provided.");
                }

                // Call Gemini API with product + user details
                String geminiResponse = geminiService.analyzeProductWithUserHealth(productDetails, userDetails);

                return ResponseEntity.ok(geminiResponse);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred: " + e.getMessage());
            }
        }

        private String formatProductDetails(Product product) {
            return "Product Name: " + product.getName() + "\n" +
                    "Category: " + product.getCategory() + "\n" +
                    "Ingredients: " + (product.getIngredients() != null ? product.getIngredients() : "No data") + "\n" +
                    "Nutritional Info: " + (product.getNutritions() != null ? product.getNutritions() : "No data");
        }

        private String formatUserDetails(User user) {
            return "Name: " + user.getName() + "\n" +
                    "Age: " + user.getAge() + "\n" +
                    "Gender: " + user.getGender() + "\n" +
                    "Height: " + user.getHeight() + " cm\n" +
                    "Weight: " + user.getWeight() + " kg\n" +
                    "Blood Group: " + user.getBloodGroup() + "\n" +
                    "Allergies: " + (user.getAllergies() != null && !user.getAllergies().isEmpty() ? user.getAllergies() : "None") + "\n" +
                    "Medical History: " + (user.getMedicalHistory() != null && !user.getMedicalHistory().isEmpty() ? user.getMedicalHistory() : "None");
        }




}

