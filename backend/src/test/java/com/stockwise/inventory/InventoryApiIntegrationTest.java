package com.stockwise.inventory;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class InventoryApiIntegrationTest {
    @Autowired TestRestTemplate http;
    @Test
    void createsProductsAndOrdersAndRestoresStockOnCancellation() {
        JsonNode products = http.getForObject("/api/products", JsonNode.class);
        int originalQuantity = product(products, 1).path("quantity").asInt();
        assertThat(product(products, 2).path("quantity").asInt()).isEqualTo(21);

        ResponseEntity<JsonNode> created = http.postForEntity("/api/products", Map.of(
                "name", "Test Stand", "sku", "TEST-STAND", "category", "Test", "description", "Created by API test",
                "price", 10.50, "quantity", 4, "minimumStockThreshold", 2), JsonNode.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        long testProductId = created.getBody().path("id").asLong();
        ResponseEntity<JsonNode> updated = http.exchange("/api/products/" + testProductId, HttpMethod.PUT,
                new HttpEntity<>(Map.of("name", "Test Stand Updated", "sku", "TEST-STAND", "category", "Test",
                        "description", "Updated by API test", "price", 11.00, "quantity", 4, "minimumStockThreshold", 2)), JsonNode.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        http.delete("/api/products/" + testProductId);

        ResponseEntity<JsonNode> placed = http.postForEntity("/api/orders", Map.of(
                "customerName", "Integration Test", "productId", 1, "quantity", 2), JsonNode.class);
        assertThat(placed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(placed.getBody().path("status").asText()).isEqualTo("PENDING");
        assertThat(product(http.getForObject("/api/products", JsonNode.class), 1).path("quantity").asInt())
                .isEqualTo(originalQuantity - 2);

        long orderId = placed.getBody().path("id").asLong();
        ResponseEntity<JsonNode> cancelled = http.postForEntity("/api/orders/" + orderId + "/cancel", null, JsonNode.class);
        assertThat(cancelled.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cancelled.getBody().path("status").asText()).isEqualTo("CANCELLED");
        assertThat(product(http.getForObject("/api/products", JsonNode.class), 1).path("quantity").asInt())
                .isEqualTo(originalQuantity);

        ResponseEntity<String> insufficient = http.postForEntity("/api/orders", Map.of(
                "customerName", "Integration Test", "productId", 1, "quantity", originalQuantity + 1), String.class);
        assertThat(insufficient.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private JsonNode product(JsonNode products, long id) {
        for (JsonNode product : products) if (product.path("id").asLong() == id) return product;
        throw new AssertionError("Product " + id + " not found in API response");
    }
}
