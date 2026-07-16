import { useState } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import { Lock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { api } from "@/services/api";

const ResetarSenha = () => {
  const [params] = useSearchParams();
  const token = params.get("token");
  const navigate = useNavigate();
  const [senha, setSenha] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [confirmTouched, setConfirmTouched] = useState(false);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // Só mostra o aviso depois que o campo "confirmar" perde o foco
  // (padrão moderno: não interrompe o usuário enquanto ele ainda está digitando)
  const senhasDiferentes = confirmTouched && confirmar.length > 0 && senha !== confirmar;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (senha !== confirmar) {
      setConfirmTouched(true);
      return setError("As senhas não coincidem.");
    }
    if (!token) return setError("Link inválido ou expirado.");

    setIsLoading(true);
    try {
      await api.post("/auth/reset-password", { token, novaSenha: senha });
      navigate("/login");
    } catch (err) {
      setError(err.response?.data?.message || "Não foi possível redefinir a senha. O link pode ter expirado.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center p-4 font-body">
      <div className="w-full max-w-md bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-8 shadow-[8px_8px_0px_0px_#7A1230]">
        <h1 className="font-display font-bold text-2xl text-[#201A1E] mb-6">Nova senha</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1.5">
            <Label className="font-mono-ieji text-[10px] uppercase flex items-center gap-2">
              <Lock className="w-3.5 h-3.5" /> Nova senha
            </Label>
            <input
              type="password"
              required
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm"
            />
          </div>

          <div className="space-y-1.5">
            <Label className="font-mono-ieji text-[10px] uppercase">Confirmar senha</Label>
            <input
              type="password"
              required
              value={confirmar}
              onChange={(e) => setConfirmar(e.target.value)}
              onBlur={() => setConfirmTouched(true)}
              className={`w-full bg-[#F5EFDD] border-2 rounded-sm p-3 font-mono-ieji text-sm transition-colors ${
                senhasDiferentes ? "border-[#7A1230] focus:outline-none" : "border-[#201A1E]"
              }`}
            />
            {senhasDiferentes && (
              <div className="bg-[#7A1230]/10 border-2 border-[#7A1230] text-[#7A1230] font-body text-sm font-semibold px-3 py-2 mt-2 rounded-sm w-full">
                As senhas não coincidem.
              </div>
            )}
          </div>

          {error && <div className="bg-[#7A1230]/10 border-2 border-[#7A1230] text-[#7A1230] text-sm px-3 py-2 rounded-sm">{error}</div>}

          <Button
            type="submit"
            disabled={isLoading || senhasDiferentes}
            className="w-full h-12 bg-[#201A1E] text-[#EAE0C4] font-display font-bold disabled:opacity-50"
          >
            {isLoading ? "SALVANDO..." : "REDEFINIR SENHA"}
          </Button>
        </form>
        <Link to="/login" className="block text-center mt-4 font-mono-ieji text-xs text-[#5b5346]">Voltar ao login</Link>
      </div>
    </div>
  );
};

export default ResetarSenha;
