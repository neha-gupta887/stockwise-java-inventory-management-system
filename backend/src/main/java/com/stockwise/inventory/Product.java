package com.stockwise.inventory;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160) private String name;
    @Column(nullable = false, length = 64) private String sku;
    @Column(length = 100) private String category;
    @Column(length = 2000) private String description;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false) private int minimumStockThreshold;

    protected Product() {}
    public Product(String name, String sku, String category, String description, BigDecimal price, int quantity, int minimumStockThreshold) {
        this.name=name; this.sku=sku; this.category=category; this.description=description;
        this.price=price; this.quantity=quantity; this.minimumStockThreshold=minimumStockThreshold;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getSku(){return sku;}
    public String getCategory(){return category;} public String getDescription(){return description;} public BigDecimal getPrice(){return price;}
    public int getQuantity(){return quantity;} public int getMinimumStockThreshold(){return minimumStockThreshold;}
    public void update(String name,String sku,String category,String description,BigDecimal price,int quantity,int threshold){
        this.name=name;this.sku=sku;this.category=category;this.description=description;this.price=price;this.quantity=quantity;this.minimumStockThreshold=threshold;
    }
    public void adjustQuantity(int delta){this.quantity+=delta;}
}
