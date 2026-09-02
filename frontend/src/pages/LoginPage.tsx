import { useState } from "react";
import { useNavigate, Link, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const nav = useNavigate();
  const location = useLocation();
  const registered = location.state?.registered;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try { await login(email, password); nav("/dashboard"); }
    catch (err: any) { setError(err.response?.data?.message || "Login failed. Check your credentials."); }
    finally { setLoading(false); }
  };
  return (
    <div style={{maxWidth:400,margin:"4rem auto"}}>
      <div className="card">
        <h2 style={{marginBottom:"0.5rem"}}>Welcome Back</h2>
        <p style={{color:"gray",marginBottom:"1.5rem"}}>Sign in to PMPML Transit</p>
        {registered && (
          <div style={{color:"#1b5e20",background:"#e8f5e9",padding:"0.75rem",borderRadius:4,marginBottom:"1rem",fontSize:"0.9rem"}}>
            Account created successfully! Please login.
          </div>
        )}
        {error && (
          <div style={{color:"red",background:"#ffeef0",padding:"0.75rem",borderRadius:4,marginBottom:"1rem",fontSize:"0.9rem"}}>
            {error}
          </div>
        )}
        <form onSubmit={handleSubmit}>
          <div className="form-group"><label>Email</label><input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com" required/></div>
          <div className="form-group"><label>Password</label><input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Your password" required/></div>
          <button type="submit" className="btn btn-primary" style={{width:"100%",opacity:loading?0.7:1}} disabled={loading}>{loading ? "Signing in..." : "Login"}</button>
        </form>
        <p style={{marginTop:"1.5rem",textAlign:"center",color:"gray"}}>
          Don't have an account? <Link to="/register" style={{color:"#1a237e",fontWeight:500}}>Register</Link>
        </p>
      </div>
    </div>
  );
}
