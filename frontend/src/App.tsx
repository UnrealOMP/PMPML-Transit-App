import { Routes, Route, Link, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import BookingPage from "./pages/BookingPage";
import TicketsPage from "./pages/TicketsPage";
import LiveMapPage from "./pages/LiveMapPage";
import { AuthProvider, useAuth } from "./hooks/useAuth";

function Navbar() {
  const { user, logout } = useAuth();
  return (
    <nav className="navbar">
      <Link to="/" style={{ fontSize: "1.2rem", fontWeight: "bold", color: "white", textDecoration: "none" }}>🚌 PMPML Transit</Link>
      <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
        {user ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/book" className="btn btn-primary" style={{ textDecoration: "none", padding: "0.35rem 1rem", borderRadius: 4, fontSize: "0.85rem" }}>🎫 Book Ticket</Link>
            <Link to="/tickets">My Tickets</Link>
            <Link to="/live-map">Live Map</Link>
            <span style={{ color: "rgba(255,255,255,0.7)", fontSize: "0.85rem" }}>{user.email}</span>
            <button onClick={logout} style={{ background: "rgba(255,255,255,0.15)", color: "white", border: "1px solid rgba(255,255,255,0.3)", padding: "0.4rem 1rem", borderRadius: 4, cursor: "pointer", fontSize: "0.85rem" }}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register" className="btn btn-primary" style={{ textDecoration: "none", padding: "0.4rem 1rem", borderRadius: 4 }}>Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/book" element={<BookingPage />} />
          <Route path="/tickets" element={<TicketsPage />} />
          <Route path="/live-map" element={<LiveMapPage />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}
