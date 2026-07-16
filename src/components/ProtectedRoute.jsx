import { Navigate } from "react-router-dom";

/**
 * Envolve páginas que exigem login.
 * Se não houver token salvo, manda o usuário direto pra tela de login.
 */
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

export default ProtectedRoute;
