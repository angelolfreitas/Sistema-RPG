import { useState } from "react";
import { motion } from "framer-motion";
import { UserPlus, Mail, Lock, User, ArrowLeft, ShieldCheck, Feather } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { Label } from "@/components/ui/label";
import { api } from "@/services/api";

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
    .font-mono-ieji { font-family: 'JetBrains Mono', monospace; }
  `}</style>
);

const Register = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    username: "",
    login: "",
    password: "",
    confirmPassword: "",
  });

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (formData.password !== formData.confirmPassword) {
      setError("As senhas não coincidem.");
      return;
    }

    setIsLoading(true);
    try {
      // LoginRequest(username, login, password)
      await api.post("/auth/register", {
        username: formData.username,
        login: formData.login,
        password: formData.password,
      });

      setSuccess(true);
      setTimeout(() => navigate("/login"), 1400);
    } catch (err) {
      console.error("Erro ao registrar:", err);
      setError(err.response?.data?.message || err.response?.data || "Não foi possível concluir o registro.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center p-4 relative overflow-hidden font-body selection:bg-[#7A1230] selection:text-[#EAE0C4]">
      <FontImports />

      <div className="fixed inset-0 pointer-events-none opacity-[0.15] z-0"
           style={{ backgroundImage: 'radial-gradient(#B99A4B 1px, transparent 1px)', backgroundSize: '26px 26px' }} />
      <div className="fixed inset-0 pointer-events-none z-0"
           style={{ background: 'radial-gradient(ellipse at center, transparent 35%, #0B0A0D 100%)' }} />

      <div className="absolute top-[12%] left-1/2 -translate-x-1/2 bg-[#3F8574] border-y-2 border-[#B99A4B]/40 py-2 rotate-2 z-0 hidden md:flex justify-center items-center opacity-60"
           style={{ width: '200vw' }}>
        <div className="flex gap-10 whitespace-nowrap font-mono-ieji font-semibold text-[11px] tracking-[0.3em] text-[#EAE0C4] uppercase">
          <span>Nova matrícula · Ficha de Agente</span>
          <span>Nova matrícula · Ficha de Agente</span>
          <span>Nova matrícula · Ficha de Agente</span>
          <span>Nova matrícula · Ficha de Agente</span>
        </div>
      </div>

      <motion.div
        initial={{ y: 40, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
        className="w-full max-w-lg relative z-10"
      >
        <Link to="/login" className="absolute -top-10 left-0 z-0">
          <div className="bg-[#EAE0C4] border-2 border-[#0B0A0D] px-4 py-2 rounded-t-sm hover:bg-[#201A1E] hover:text-[#EAE0C4] transition-colors flex items-center gap-2 font-mono-ieji text-xs font-semibold">
            <ArrowLeft className="w-3.5 h-3.5" /> VOLTAR
          </div>
        </Link>

        <div className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-6 md:p-10 shadow-[10px_10px_0px_0px_#3F8574] relative z-10">
          <div className="absolute -top-8 -right-4 md:-right-8 bg-[#3F8574] border-4 border-[#0B0A0D] w-14 h-14 md:w-16 md:h-16 rounded-full flex items-center justify-center shadow-[3px_3px_0px_0px_#B99A4B]">
            <UserPlus className="w-7 h-7 text-[#EAE0C4]" strokeWidth={1.5} />
          </div>

          <div className="mb-8">
            <span className="font-mono-ieji text-[10px] tracking-[0.35em] uppercase text-[#3F8574] font-semibold">
              Matrícula V.1
            </span>
            <h1 className="font-display font-bold text-4xl md:text-5xl text-[#201A1E] leading-none mt-2">
              Ficha de <br /> Recruta
            </h1>
            <p className="font-body italic text-lg text-[#5b5346] mt-3 border-l-2 border-[#3F8574] pl-3">
              Preencha seus dados para ingressar no Instituto Eleonora.
            </p>
          </div>

          <form onSubmit={handleRegister} className="space-y-5">
            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <User className="w-3.5 h-3.5" /> Nome de Agente
              </Label>
              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
                className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#3F8574] focus:shadow-[3px_3px_0px_0px_#3F8574] transition-all placeholder:text-[#a89f8a]"
                placeholder="Ex: Alice Duarte"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <Mail className="w-3.5 h-3.5" /> E-mail institucional
              </Label>
              <input
                type="email"
                name="login"
                value={formData.login}
                onChange={handleInputChange}
                className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#3F8574] focus:shadow-[3px_3px_0px_0px_#3F8574] transition-all placeholder:text-[#a89f8a]"
                placeholder="nome@ieji.edu"
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                  <Lock className="w-3.5 h-3.5" /> Senha
                </Label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#3F8574] focus:shadow-[3px_3px_0px_0px_#3F8574] transition-all"
                  placeholder="••••••"
                  required
                />
              </div>
              <div className="space-y-1.5">
                <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                  <ShieldCheck className="w-3.5 h-3.5" /> Confirmar
                </Label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#3F8574] focus:shadow-[3px_3px_0px_0px_#3F8574] transition-all"
                  placeholder="••••••"
                  required
                />
              </div>
            </div>

            <div className="flex items-start gap-3 p-3 bg-[#3F8574]/10 border border-dashed border-[#3F8574] rounded-sm">
              <input id="terms" type="checkbox" className="w-4 h-4 mt-1 accent-[#3F8574] cursor-pointer" required />
              <label htmlFor="terms" className="text-sm font-body text-[#5b5346] cursor-pointer leading-tight">
                Aceito o código do Instituto Eleonora e assumo os riscos do contato com o Outro Lado.
              </label>
            </div>

            {error && (
              <div className="bg-[#7A1230]/10 border-2 border-[#7A1230] text-[#7A1230] text-sm font-body px-3 py-2 rounded-sm">
                {String(error)}
              </div>
            )}
            {success && (
              <div className="bg-[#3F8574]/10 border-2 border-[#3F8574] text-[#3F8574] text-sm font-body px-3 py-2 rounded-sm flex items-center gap-2">
                <Feather className="w-4 h-4" /> Recruta registrado! Redirecionando ao acesso...
              </div>
            )}

            <Button
              type="submit"
              disabled={isLoading}
              className="w-full h-12 bg-[#3F8574] text-[#EAE0C4] hover:bg-[#201A1E] border-2 border-[#201A1E] font-display font-bold tracking-wide rounded-sm shadow-[4px_4px_0px_0px_#B99A4B] hover:shadow-[2px_2px_0px_0px_#B99A4B] hover:translate-x-[2px] hover:translate-y-[2px] transition-all"
            >
              {isLoading ? (
                <span className="font-mono-ieji text-sm animate-pulse">PROCESSANDO...</span>
              ) : (
                "CONFIRMAR INSCRIÇÃO"
              )}
            </Button>
          </form>
        </div>

        <div className="text-center mt-6 font-mono-ieji text-[10px] tracking-[0.3em] text-[#5b5346] uppercase">
          Instituto Eleonora · Redemptio vel Maledictio
        </div>
      </motion.div>
    </div>
  );
};

export default Register;
