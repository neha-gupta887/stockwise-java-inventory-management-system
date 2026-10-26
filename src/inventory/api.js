import { demoInventory } from "../data/demo/inventory";

const base = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api").replace(/\/$/, "");
// Demo and user-created local workspaces use separate keys and are never merged.
const mode = localStorage.getItem("stockwise.mode") || import.meta.env.VITE_API_MODE || "demo";
const local = mode !== "spring";
const storageKey = mode === "demo" ? "stockwise.inventory.demo.v1" : "stockwise.inventory.local.v1";
const writeStore = (store) => localStorage.setItem(storageKey, JSON.stringify(store));
const readStore = () => {
  try {
    const saved = localStorage.getItem(storageKey);
    if (saved !== null) return JSON.parse(saved);
  } catch { /* Start a clean workspace when stored data cannot be read. */ }
  const initial = mode === "demo" ? structuredClone(demoInventory) : { products: [], orders: [] };
  writeStore(initial);
  return initial;
};
const localSales = () => {
  const grouped = new Map();
  readStore().orders.filter(order => order.status === "COMPLETED").forEach(order => {
    const date = String(order.date || order.createdAt || "").slice(0, 10);
    if (date) grouped.set(date, (grouped.get(date) || 0) + Number(order.totalAmount ?? order.total ?? 0));
  });
  return [...grouped].sort(([a], [b]) => a.localeCompare(b)).map(([date, sales]) => ({ date, sales }));
};
const unavailable = () => new Error("AI service not connected.");

async function request(path, options = {}) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 10000);
  try {
    const response = await fetch(`${base}${path}`, { ...options, signal: controller.signal, headers: { "Content-Type": "application/json", ...options.headers } });
    if (!response.ok) {
      let detail = "";
      try { const body = await response.json(); detail = body.message || body.detail || body.error || ""; } catch { /* Use a status-based message when the response has no JSON body. */ }
      const messages = { 400: "The request contains invalid information.", 401: "Please sign in to continue.", 403: "You do not have permission to do that.", 404: "This inventory service is not available.", 409: "This change conflicts with current data.", 500: "The server could not complete the request." };
      throw new Error(detail || messages[response.status] || `Request failed (${response.status}).`);
    }
    if (response.status === 204) return null;
    return response.json();
  } catch (error) {
    if (error.name === "AbortError") throw new Error("The request timed out. Please try again.", { cause: error });
    if (error instanceof TypeError) throw new Error("Unable to reach the inventory service. Check that the Spring Boot API is running.", { cause: error });
    throw error;
  } finally { clearTimeout(timer); }
}

export const inventoryApi = {
  mode: local ? mode : "spring",
  switchMode: (next) => { if (!["demo", "local", "spring"].includes(next)) throw new Error("Unknown workspace mode."); localStorage.setItem("stockwise.mode", next); window.location.reload(); },
  clearLocalWorkspace: () => { if (!local) throw new Error("Local workspace controls are unavailable in Spring API mode."); localStorage.removeItem(storageKey); },
  products: async () => local ? readStore().products : request("/products"),
  orders: async () => local ? readStore().orders : request("/orders"),
  sales: async () => local ? localSales() : request("/analytics/sales"),
  createProduct: async (product) => {
    if (!local) return request("/products", { method: "POST", body: JSON.stringify(product) });
    const store=readStore(); if(store.products.some(p=>p.sku.toLowerCase()===product.sku.toLowerCase())) throw new Error("A product with this SKU already exists."); const saved={...product,id:crypto.randomUUID()}; store.products.unshift(saved); writeStore(store); return saved;
  },
  updateProduct: async (id, product) => {
    if (!local) return request(`/products/${id}`, { method: "PUT", body: JSON.stringify(product) });
    const store=readStore(); if(store.products.some(p=>String(p.id)!==String(id)&&p.sku.toLowerCase()===product.sku.toLowerCase())) throw new Error("A product with this SKU already exists."); store.products=store.products.map(p=>String(p.id)===String(id)?{...p,...product,id:p.id}:p); writeStore(store); return store.products.find(p=>String(p.id)===String(id));
  },
  deleteProduct: async (id) => {
    if (!local) return request(`/products/${id}`, { method: "DELETE" });
    const store=readStore(); if(store.orders.some(o=>o.status==="PENDING"&&o.items.some(i=>String(i.productId)===String(id)))) throw new Error("This product is part of an open order and cannot be deleted."); store.products=store.products.filter(p=>String(p.id)!==String(id)); writeStore(store);
  },
  createOrder: async (order) => {
    if (!local) return request("/orders", { method: "POST", body: JSON.stringify(order) });
    const store=readStore(); const p=store.products.find(product=>String(product.id)===String(order.productId)); const qty=Number(order.quantity);
    if(!p) throw new Error("Select a valid product."); if(qty<1) throw new Error("Quantity must be at least 1."); if(Number(p.quantity??p.currentQuantity??0)<qty) throw new Error(`Insufficient stock. Available quantity: ${p.quantity??p.currentQuantity??0}`);
    p.quantity=Number(p.quantity??p.currentQuantity)-qty;
    const saved={id:mode==="demo"?`DEMO-${Date.now()}`:crypto.randomUUID(),customerName:order.customerName,date:new Date().toISOString(),status:"PENDING",items:[{productId:p.id,productName:p.name??p.productName,quantity:qty,unitPrice:Number(p.price)}],totalAmount:qty*Number(p.price)};
    store.orders.unshift(saved); writeStore(store); return saved;
  },
  cancelOrder: async (id) => {
    if (!local) return request(`/orders/${id}/cancel`, { method: "POST" });
    const store=readStore(); const order=store.orders.find(o=>String(o.id)===String(id)); if(!order) throw new Error("Order was not found."); if(order.status!=="PENDING") throw new Error("Only pending orders can be cancelled.");
    order.items.forEach(item=>{const p=store.products.find(product=>String(product.id)===String(item.productId)); if(p) p.quantity=Number(p.quantity??p.currentQuantity??0)+Number(item.quantity);}); order.status="CANCELLED"; writeStore(store); return order;
  },
  askAssistant: async (question) => local ? Promise.reject(unavailable()) : request("/ai/assistant", { method: "POST", body: JSON.stringify({ question }) }),
};
