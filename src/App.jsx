import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import InventoryApp from "./inventory/InventoryApp";

export default function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/inventory/*" element={<InventoryApp />} />
          <Route path="*" element={<Navigate to="/inventory" replace />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
    </>
  );
}

