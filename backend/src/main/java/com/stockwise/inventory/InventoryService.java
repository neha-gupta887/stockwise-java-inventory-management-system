package com.stockwise.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryService {
    private final ProductRepository products;
    private final OrderRepository orders;
    public InventoryService(ProductRepository products, OrderRepository orders){this.products=products;this.orders=orders;}

    public record ProductInput(String name,String sku,String category,String description,BigDecimal price,int quantity,int minimumStockThreshold) {}
    public record OrderInput(String customerName,Long productId,int quantity) {}
    public record OrderLine(Long productId,String productName,int quantity,BigDecimal unitPrice) {}
    public record OrderView(Long id,String customerName,java.time.LocalDateTime date,PurchaseOrder.Status status,List<OrderLine> items,BigDecimal totalAmount) {}

    @Transactional
    public Product createProduct(ProductInput input){ validate(input); if(products.findBySkuIgnoreCase(input.sku().trim()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"SKU already exists."); return products.save(toProduct(input)); }
    @Transactional
    public Product updateProduct(Long id,ProductInput input){ validate(input); Product p=product(id); products.findBySkuIgnoreCase(input.sku().trim()).filter(other->!other.getId().equals(id)).ifPresent(other->{throw new ResponseStatusException(HttpStatus.CONFLICT,"SKU already exists.");}); p.update(input.name().trim(),input.sku().trim(),input.category(),input.description(),input.price(),input.quantity(),input.minimumStockThreshold()); return p; }
    @Transactional
    public void deleteProduct(Long id){ Product p=product(id); if(orders.existsByProduct_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Products with order history cannot be deleted."); products.delete(p); }
    @Transactional(readOnly=true) public List<Product> allProducts(){return products.findAll();}
    @Transactional(readOnly=true) public Product product(Long id){return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found."));}

    @Transactional
    public OrderView placeOrder(OrderInput input){
        if(input.customerName()==null||input.customerName().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Customer name is required.");
        if(input.quantity()<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantity must be at least 1.");
        Product p=product(input.productId());
        if(p.getQuantity()<input.quantity()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Insufficient stock. Available quantity: "+p.getQuantity());
        p.adjustQuantity(-input.quantity());
        return view(orders.save(new PurchaseOrder(input.customerName().trim(),p,input.quantity())));
    }
    @Transactional(readOnly=true) public List<OrderView> allOrders(){return orders.findAll().stream().map(this::view).toList();}
    @Transactional
    public OrderView cancel(Long id){PurchaseOrder order=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found.")); if(order.getStatus()!=PurchaseOrder.Status.PENDING) throw new ResponseStatusException(HttpStatus.CONFLICT,"Only pending orders can be cancelled."); order.getProduct().adjustQuantity(order.getQuantity()); order.setStatus(PurchaseOrder.Status.CANCELLED); return view(order);}
    private OrderView view(PurchaseOrder o){BigDecimal unitPrice=o.getTotalAmount().divide(BigDecimal.valueOf(o.getQuantity()),2,java.math.RoundingMode.HALF_UP);return new OrderView(o.getId(),o.getCustomerName(),o.getDate(),o.getStatus(),List.of(new OrderLine(o.getProduct().getId(),o.getProduct().getName(),o.getQuantity(),unitPrice)),o.getTotalAmount());}
    private Product toProduct(ProductInput i){return new Product(i.name().trim(),i.sku().trim(),i.category(),i.description(),i.price(),i.quantity(),i.minimumStockThreshold());}
    private void validate(ProductInput i){if(i.name()==null||i.name().isBlank()||i.sku()==null||i.sku().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product name and SKU are required.");if(i.price()==null||i.price().compareTo(BigDecimal.ZERO)<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Price must be greater than zero.");if(i.quantity()<0||i.minimumStockThreshold()<1)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantity cannot be negative and threshold must be at least 1.");}
}
