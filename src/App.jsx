import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import FichaPersonagem from "./pages/FichaPersonagem.jsx";
import Inventario from "./pages/Inventario.jsx";
import Aetherys from "./pages/Aetherys.jsx";
import SessaoRPG from "./pages/SessaoRPG.jsx";
import CasosList from "./pages/CasosList.jsx";
import Bestiario from "./pages/Bestiario.jsx";
import CriarUsuarioPrivilegiado from "@/pages/CriarUsuarioPrivilegiado";
import ContaRemovida from "@/pages/ContaRemovida";
import EsqueciSenha from "./pages/EsqueciSenha.jsx";
import ResetarSenha from "./pages/ResetarSenha.jsx";

function App() {
  const token = localStorage.getItem("token");

  return (
    <Routes>
      {/* raiz: manda pra ficha se já estiver logado, senão pro login */}
      <Route path="/" element={<Navigate to={token ? "/personagem" : "/login"} replace />} />

      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/sessao/:id" element={<SessaoRPG />} />
      <Route path="/casos" element={
        <ProtectedRoute>
          <CasosList />
        </ProtectedRoute>
      } />
      <Route path="/bestiario" element={<Bestiario />} />
      <Route path="/sessao-invalida" element={<ContaRemovida />} />

      <Route
        path="/personagem"
        element={
          <ProtectedRoute>
            <FichaPersonagem />
          </ProtectedRoute>
        }
      />
      <Route
        path="/inventario"
        element={
          <ProtectedRoute>
            <Inventario />
          </ProtectedRoute>
        }
      />
      <Route
        path="/aetherys"
        element={
          <ProtectedRoute>
            <Aetherys />
          </ProtectedRoute>
        }
      />
      <Route path="/esqueci-senha" element={<EsqueciSenha />} />
      <Route path="/resetar-senha" element={<ResetarSenha />} />

        <Route path="/admin/criar-usuario" element={<CriarUsuarioPrivilegiado />} />
      {/* qualquer rota desconhecida volta pra raiz */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
