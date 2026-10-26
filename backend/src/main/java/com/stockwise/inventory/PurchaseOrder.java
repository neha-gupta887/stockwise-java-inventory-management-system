package com.stockwise.inventory;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    public enum Status { PENDING, PROCESSING, COMPLETED, CANCELLED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String customerName;
    @Column(nullable=false) private LocalDateTime date;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status;
    @ManyToOne(optional=false) private Product product;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal totalAmount;
    protected PurchaseOrder() {}
    public PurchaseOrder(String customerName, Product product, int quantity){
        this.customerName=customerName;this.product=product;this.quantity=quantity;this.date=LocalDateTime.now();
        this.status=Status.PENDING;this.totalAmount=product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
    public Long getId(){return id;} public String getCustomerName(){return customerName;} public LocalDateTime getDate(){return date;}
    public Status getStatus(){return status;} public Product getProduct(){return product;} public int getQuantity(){return quantity;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setStatus(Status status){this.status=status;}
}
