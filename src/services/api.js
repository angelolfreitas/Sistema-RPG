import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const api = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    // 401: token ausente/expirado. 403: aqui tratado como sessão inválida
    // (ex: conta excluída) — ver observação sobre esse trade-off.
    if (status === 401 || status === 403) {
      const jaEstaNaTelaDeErro = window.location.pathname === "/sessao-invalida";
      const estaFazendoLoginOuRegistro = ["/login", "/register"].includes(window.location.pathname);

      if (!jaEstaNaTelaDeErro && !estaFazendoLoginOuRegistro) {
        localStorage.removeItem("token");
        localStorage.removeItem("usuarioId");
        localStorage.removeItem("personagemSelecionadoId");
        localStorage.removeItem("idCasoAtivo");
        window.location.href = "/sessao-invalida";
      }
    }

    return Promise.reject(error);
  }
);