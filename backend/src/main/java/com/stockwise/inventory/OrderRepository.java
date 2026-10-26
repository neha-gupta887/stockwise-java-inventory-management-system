package com.stockwise.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<PurchaseOrder,Long> { boolean existsByProduct_Id(Long productId); }
