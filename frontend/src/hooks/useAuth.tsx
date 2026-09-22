import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { authApi } from "../services/api";

const AuthContext = createContext<any>(null);

export function AuthProvider({ children }: { children: ReactNode }) {


  const [user, setUser] = useState<any>(null);
  useEffect(() => {
    const t = localStorage.getItem("accessToken");
    if (t) setUser({ email: localStorage.getItem("userEmail") });
  }, []);


  const login = async (email: string, password: string) => {
    const res = await authApi.login(email, password);
    localStorage.setItem("accessToken", res.data.accessToken);
    localStorage.setItem("userEmail", res.data.email);
    setUser({ email: res.data.email });
  };

  const register = async (data: {
    fullname: string,
    email: string,
    phoneNumber: string,
    password: string
  }) => {
    const res = await authApi.register(data);
    localStorage.setItem("accessToken", res.data.accessToken);
    localStorage.setItem("userEmail", res.data.email);
    setUser({ email: res.data.email }); 
  }
  const logout = () => { localStorage.clear(); setUser(null); };
  return <AuthContext.Provider value={{ user, login, register, logout }}>{children}</AuthContext.Provider>;
}
export function useAuth() { return useContext(AuthContext); }
