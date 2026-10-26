package com.stockwise.inventory;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InventoryController {
    private final InventoryService service;
    private final OrderRepository orders;
    public InventoryController(InventoryService service,OrderRepository orders){this.service=service;this.orders=orders;}
    @GetMapping("/products") public List<Product> products(){return service.allProducts();}
    @PostMapping("/products") public Product create(@RequestBody InventoryService.ProductInput input){return service.createProduct(input);}
    @PutMapping("/products/{id}") public Product update(@PathVariable Long id,@RequestBody InventoryService.ProductInput input){return service.updateProduct(id,input);}
    @DeleteMapping("/products/{id}") public void delete(@PathVariable Long id){service.deleteProduct(id);}
    @GetMapping("/orders") public List<InventoryService.OrderView> orders(){return service.allOrders();}
    @PostMapping("/orders") public InventoryService.OrderView place(@RequestBody InventoryService.OrderInput input){return service.placeOrder(input);}
    @PostMapping("/orders/{id}/cancel") public InventoryService.OrderView cancel(@PathVariable Long id){return service.cancel(id);}
    @GetMapping("/analytics/sales") public List<SalesPoint> sales(){return orders.findAll().stream().filter(o->o.getStatus()==PurchaseOrder.Status.COMPLETED).collect(java.util.stream.Collectors.groupingBy(o->o.getDate().toLocalDate(),java.util.TreeMap::new,java.util.stream.Collectors.reducing(java.math.BigDecimal.ZERO,PurchaseOrder::getTotalAmount,java.math.BigDecimal::add))).entrySet().stream().map(e->new SalesPoint(e.getKey().toString(),e.getValue())).toList();}
    public record SalesPoint(String date,java.math.BigDecimal sales){}
}
