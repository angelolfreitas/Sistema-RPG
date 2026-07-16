// src/pages/CriarUsuarioPrivilegiado.jsx
import { useState } from "react";
import { motion } from "framer-motion";
import { Eye, EyeOff, Lock, Mail, User, ShieldPlus, ArrowLeft, Skull } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Link } from "react-router-dom";
import { api } from "@/services/api";

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
    .font-mono-ieji { font-family: 'JetBrains Mono', monospace; }
  `}</style>
);

const ROLES = [
  { value: "MANAGER", label: "Auxiliar" },
  { value: "ADMIN", label: "Mestre" },
];

const CriarUsuarioPrivilegiado = () => {
  const [username, setUsername] = useState("");
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("MANAGER");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [sucesso, setSucesso] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSucesso(null);
    setIsLoading(true);
    try {
      const response = await api.post("/auth/admin/register", {
        usuario: { id: null, username, login, password },
        role,
      });
      setSucesso(response.data);
      setUsername("");
      setLogin("");
      setPassword("");
    } catch (err) {
      console.error("Erro ao criar usuário privilegiado:", err);
      if (err.response) {
        const body = typeof err.response.data === "string" ? err.response.data : JSON.stringify(err.response.data);
        setError(`Erro ${err.response.status}: ${body || "não foi possível criar."}`);
      } else {
        setError("Não foi possível falar com o servidor.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center p-4 relative overflow-hidden font-body">
      <FontImports />
      <div className="fixed inset-0 pointer-events-none opacity-[0.15] z-0"
           style={{ backgroundImage: 'radial-gradient(#B99A4B 1px, transparent 1px)', backgroundSize: '26px 26px' }} />

      <Link to="/personagem" className="absolute top-6 left-6 z-20 w-9 h-9 rounded-full bg-[#7A1230] border-2 border-[#B99A4B] flex items-center justify-center hover:bg-[#3F8574] transition-colors">
        <ArrowLeft className="w-4 h-4 text-[#EAE0C4]" />
      </Link>

      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
        className="w-full max-w-md relative z-10"
      >
        <div className="absolute -top-9 left-1/2 -translate-x-1/2 z-20">
          <div className="w-16 h-16 rounded-full bg-[#7A1230] border-4 border-[#0B0A0D] shadow-[0_0_0_3px_#B99A4B] flex items-center justify-center">
            <ShieldPlus className="w-7 h-7 text-[#EAE0C4]" strokeWidth={1.5} />
          </div>
        </div>

        <div className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-8 md:p-10 pt-14 shadow-[10px_10px_0px_0px_#7A1230] relative">
          <div className="text-center mb-8">
            <span className="font-mono-ieji text-[10px] tracking-[0.35em] uppercase text-[#7A1230] font-semibold">
              Instituto Eleonora
            </span>
            <h1 className="font-display font-bold text-3xl text-[#201A1E] mt-2 leading-none">
              Novo Integrante
            </h1>
            <p className="font-body text-[#5b5346] text-base mt-3 border-t border-[#B99A4B]/50 pt-3 italic">
              Registre um auxiliar ou mestre do Instituto.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <User className="w-3.5 h-3.5" /> Nome de exibição
              </Label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#7A1230]"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <Mail className="w-3.5 h-3.5" /> Login (e-mail)
              </Label>
              <input
                type="text"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#7A1230]"
                placeholder="mestre@ieji.edu"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <Lock className="w-3.5 h-3.5" /> Senha
              </Label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 pr-12 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#7A1230]"
                  required
                />
                <button type="button" onClick={() => setShowPassword((s) => !s)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-[#5b5346] hover:text-[#7A1230]">
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E]">Cargo</Label>
              <div className="grid grid-cols-2 gap-3">
                {ROLES.map((r) => (
                  <button
                    key={r.value}
                    type="button"
                    onClick={() => setRole(r.value)}
                    className={`font-mono-ieji text-xs uppercase border-2 border-[#201A1E] rounded-sm py-2.5 transition-colors ${
                      role === r.value ? "bg-[#7A1230] text-[#EAE0C4]" : "bg-[#F5EFDD] text-[#201A1E] hover:bg-[#201A1E]/10"
                    }`}
                  >
                    {r.label}
                  </button>
                ))}
              </div>
            </div>

            {error && (
              <div className="bg-[#7A1230]/10 border-2 border-[#7A1230] text-[#7A1230] text-sm font-body px-3 py-2 rounded-sm">
                {error}
              </div>
            )}
            {sucesso && (
              <div className="bg-[#3F8574]/10 border-2 border-[#3F8574] text-[#1f4a41] text-sm font-body px-3 py-2 rounded-sm">
                Criado com sucesso: <strong>{sucesso.username}</strong> (id {sucesso.id})
              </div>
            )}

            <Button
              type="submit"
              disabled={isLoading}
              className="w-full h-12 bg-[#201A1E] text-[#EAE0C4] hover:bg-[#7A1230] border-2 border-[#201A1E] hover:border-[#7A1230] font-display font-bold tracking-wide rounded-sm shadow-[4px_4px_0px_0px_#B99A4B]"
            >
              {isLoading ? "REGISTRANDO..." : "REGISTRAR INTEGRANTE"}
            </Button>
          </form>
        </div>
      </motion.div>
    </div>
  );
};

export default CriarUsuarioPrivilegiado;