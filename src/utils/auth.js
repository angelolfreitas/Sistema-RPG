// src/utils/auth.js
import { jwtDecode } from "jwt-decode";

export const getUserAuthorities = () => {
  const token = localStorage.getItem("token");
  if (!token) return [];

  try {
    const decoded = jwtDecode(token);
    return decoded.authorities || decoded.scope?.split(" ") || [];
  } catch (error) {
    console.error("Token inválido:", error);
    return [];
  }
};

export const hasAuthority = (authority) => {
  return getUserAuthorities().includes(authority);
};