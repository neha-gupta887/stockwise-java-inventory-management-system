// Clearly labeled presentation data. Values are not connected to a real inventory.
export const demoInventory = {
  products: [
    { id: "demo-p-1", name: "Wireless Mouse", sku: "WM-100", category: "Accessories", description: "Ergonomic wireless mouse", price: 24.99, quantity: 8, minimumStockThreshold: 15 },
    { id: "demo-p-2", name: "Mechanical Keyboard", sku: "KB-210", category: "Accessories", description: "Compact mechanical keyboard", price: 89.5, quantity: 23, minimumStockThreshold: 10 },
    { id: "demo-p-3", name: "USB-C Hub", sku: "HUB-7IN1", category: "Accessories", description: "Seven port USB-C hub", price: 49.99, quantity: 5, minimumStockThreshold: 8 },
    { id: "demo-p-4", name: "27-inch Monitor", sku: "MON-27", category: "Displays", description: "27 inch QHD display", price: 249, quantity: 0, minimumStockThreshold: 4 },
    { id: "demo-p-5", name: "Aluminum Laptop Stand", sku: "ST-ALU", category: "Office", description: "Adjustable aluminum stand", price: 38, quantity: 32, minimumStockThreshold: 8 },
    { id: "demo-p-6", name: "HD Webcam", sku: "CAM-HD", category: "Electronics", description: "1080p USB webcam", price: 59, quantity: 12, minimumStockThreshold: 10 },
  ],
  orders: [
    { id: "DEMO-1048", customerName: "Avery Morgan", date: "2026-10-24T10:30:00.000Z", status: "COMPLETED", items: [{ productId: "demo-p-2", productName: "Mechanical Keyboard", quantity: 2, unitPrice: 89.5 }], totalAmount: 179 },
    { id: "DEMO-1047", customerName: "Taylor Brooks", date: "2026-10-23T14:12:00.000Z", status: "COMPLETED", items: [{ productId: "demo-p-5", productName: "Aluminum Laptop Stand", quantity: 3, unitPrice: 38 }], totalAmount: 114 },
    { id: "DEMO-1046", customerName: "Riley Parker", date: "2026-10-21T09:05:00.000Z", status: "PROCESSING", items: [{ productId: "demo-p-6", productName: "HD Webcam", quantity: 1, unitPrice: 59 }], totalAmount: 59 },
    { id: "DEMO-1045", customerName: "Casey Jordan", date: "2026-10-20T16:42:00.000Z", status: "PENDING", items: [{ productId: "demo-p-1", productName: "Wireless Mouse", quantity: 2, unitPrice: 24.99 }], totalAmount: 49.98 },
    { id: "DEMO-1044", customerName: "Quinn Ellis", date: "2026-10-18T11:20:00.000Z", status: "CANCELLED", items: [{ productId: "demo-p-3", productName: "USB-C Hub", quantity: 1, unitPrice: 49.99 }], totalAmount: 49.99 },
  ],
};
