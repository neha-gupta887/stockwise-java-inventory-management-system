package com.stockwise.inventory;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.math.BigDecimal;

@Configuration
@Profile("demo")
public class DemoDataConfiguration {
    @Bean ApplicationRunner demoProducts(ProductRepository products,OrderRepository orders){return args->{
        if(products.count()>0)return;
        Product mouse=products.save(new Product("Wireless Mouse","WM-100","Accessories","Ergonomic wireless mouse",new BigDecimal("24.99"),8,15));
        Product keyboard=products.save(new Product("Mechanical Keyboard","KB-210","Accessories","Compact mechanical keyboard",new BigDecimal("89.50"),23,10));
        products.save(new Product("USB-C Hub","HUB-7IN1","Accessories","Seven port USB-C hub",new BigDecimal("49.99"),5,8));
        products.save(new Product("27-inch Monitor","MON-27","Displays","27 inch QHD display",new BigDecimal("249.00"),0,4));
        products.save(new Product("Aluminum Laptop Stand","ST-ALU","Office","Adjustable aluminum stand",new BigDecimal("38.00"),32,8));
        products.save(new Product("HD Webcam","CAM-HD","Electronics","1080p USB webcam",new BigDecimal("59.00"),12,10));
        keyboard.adjustQuantity(-2);
        products.save(keyboard);
        orders.save(new PurchaseOrder("Avery Morgan",keyboard,2));
    };}
}
