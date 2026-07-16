import { useState } from "react";
import { motion } from "framer-motion";
import { Eye, EyeOff, Lock, Mail, ScrollText, ArrowRight, Skull } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Link, useNavigate } from "react-router-dom";
import { api } from "@/services/api";

/*
  CODEX ELEONORA — design tokens (kept consistent across Login / Register / Ficha / Inventario / Aetherys)
  ink        #0B0A0D  page background
  panel      #15121A  raised dark panel
  parchment  #EAE0C4  card fill (dossier paper)
  blood      #7A1230  primary accent / hard-shadow
  gold       #B99A4B  border + secondary accent
  ghost      #3F8574  paranormal / supernatural accent
  ink-text   #201A1E  text on parchment
  Fonts: Cinzel (display), Cormorant Garamond (body serif), JetBrains Mono (stats/mono)
*/

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
    .font-mono-ieji { font-family: 'JetBrains Mono', monospace; }
  `}</style>
);

const Login = () => {
  const navigate = useNavigate();
  const [login, setLoginField] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);
    try {
      const response = await api.post("/auth/login", { login, password });
      const { token, id } = response.data;
      
      localStorage.setItem("token", token);
      localStorage.setItem("usuarioId", id);
      navigate("/personagem");
    } catch (err) {
      console.error("Erro no login:", err);
      localStorage.removeItem("token");
      if (err.response) {
        // o back-end respondeu, mas com erro — mostra o que ele mandou
        const body = typeof err.response.data === "string" ? err.response.data : JSON.stringify(err.response.data);
        setError(`Erro ${err.response.status}: ${body || "credenciais inválidas."}`);
      } else if (err.request) {
        // a requisição nem chegou a ter resposta (rede, CORS, servidor fora do ar)
        setError("Não foi possível falar com o servidor. Ele está rodando? Veja o console (F12) para detalhes de CORS/rede.");
      } else {
        setError(`Erro: ${err.message}`);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center p-4 relative overflow-hidden font-body selection:bg-[#7A1230] selection:text-[#EAE0C4]">
      <FontImports />

      {/* textura de fundo: grão + vinheta */}
      <div className="fixed inset-0 pointer-events-none opacity-[0.15] z-0"
           style={{ backgroundImage: 'radial-gradient(#B99A4B 1px, transparent 1px)', backgroundSize: '26px 26px' }} />
      <div className="fixed inset-0 pointer-events-none z-0"
           style={{ background: 'radial-gradient(ellipse at center, transparent 35%, #0B0A0D 100%)' }} />

      {/* faixa decorativa superior */}
      <div className="absolute top-[8%] left-1/2 -translate-x-1/2 bg-[#7A1230] border-y-2 border-[#B99A4B]/40 py-2 -rotate-2 z-0 hidden md:flex justify-center items-center opacity-70"
           style={{ width: '200vw' }}>
        <div className="flex gap-10 whitespace-nowrap font-mono-ieji font-semibold text-[11px] tracking-[0.3em] text-[#EAE0C4] uppercase">
          <span>Instituto Eleonora · Redemptio vel Maledictio</span>
          <span>Instituto Eleonora · Redemptio vel Maledictio</span>
          <span>Instituto Eleonora · Redemptio vel Maledictio</span>
          <span>Instituto Eleonora · Redemptio vel Maledictio</span>
        </div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
        className="w-full max-w-md relative z-10"
      >
        {/* selo de cera */}
        <div className="absolute -top-9 left-1/2 -translate-x-1/2 z-20">
          <div className="w-16 h-16 rounded-full bg-[#7A1230] border-4 border-[#0B0A0D] shadow-[0_0_0_3px_#B99A4B] flex items-center justify-center">
            <Skull className="w-7 h-7 text-[#EAE0C4]" strokeWidth={1.5} />
          </div>
        </div>

        <div className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-8 md:p-10 pt-14 shadow-[10px_10px_0px_0px_#7A1230] relative">
          <div className="text-center mb-8">
            <span className="font-mono-ieji text-[10px] tracking-[0.35em] uppercase text-[#7A1230] font-semibold">
              Instituto Eleonora
            </span>
            <h1 className="font-display font-bold text-4xl text-[#201A1E] mt-2 leading-none">
              Acesso ao Dossiê
            </h1>
            <p className="font-body text-[#5b5346] text-base mt-3 border-t border-[#B99A4B]/50 pt-3 italic">
              Somente estudantes matriculados no IEJI podem prosseguir.
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <Mail className="w-3.5 h-3.5" /> Login (e-mail)
              </Label>
              <input
                type="text"
                value={login}
                onChange={(e) => setLoginField(e.target.value)}
                className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#7A1230] focus:shadow-[3px_3px_0px_0px_#7A1230] transition-all placeholder:text-[#a89f8a]"
                placeholder="agente@ieji.edu"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label className="font-mono-ieji text-[10px] uppercase tracking-widest text-[#201A1E] flex items-center gap-2">
                <Lock className="w-3.5 h-3.5" /> Senha
              </Label>
                <div className="flex justify-end -mt-2">
                  <Link to="/esqueci-senha" className="font-mono-ieji text-[10px] text-[#7A1230] hover:underline">
                    Esqueceu a senha?
                  </Link>
                </div>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 pr-12 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#7A1230] focus:shadow-[3px_3px_0px_0px_#7A1230] transition-all placeholder:text-[#a89f8a]"
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-[#5b5346] hover:text-[#7A1230] transition-colors"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {error && (
              <div className="bg-[#7A1230]/10 border-2 border-[#7A1230] text-[#7A1230] text-sm font-body px-3 py-2 rounded-sm">
                {error}
              </div>
            )}

            <Button
              type="submit"
              disabled={isLoading}
              className="w-full h-12 bg-[#201A1E] text-[#EAE0C4] hover:bg-[#7A1230] border-2 border-[#201A1E] hover:border-[#7A1230] font-display font-bold tracking-wide rounded-sm shadow-[4px_4px_0px_0px_#B99A4B] hover:shadow-[2px_2px_0px_0px_#B99A4B] hover:translate-x-[2px] hover:translate-y-[2px] transition-all flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <span className="font-mono-ieji text-sm animate-pulse">ABRINDO O DOSSIÊ...</span>
              ) : (
                <>ENTRAR <ArrowRight className="w-4 h-4" /></>
              )}
            </Button>
          </form>

          <div className="relative my-7">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t border-dashed border-[#B99A4B]" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-[#EAE0C4] px-3 text-[#5b5346] font-mono-ieji tracking-widest">Novo migle?</span>
            </div>
          </div>

          <Link to="/register" className="block w-full">
            <div className="bg-transparent border-2 border-[#201A1E] px-4 py-3 rounded-sm font-display font-semibold text-[#201A1E] hover:bg-[#201A1E] hover:text-[#EAE0C4] transition-all flex items-center justify-center gap-2">
              <ScrollText className="w-4 h-4" />
              CRIAR FICHA DE AGENTE
            </div>
          </Link>
        </div>

        <div className="text-center mt-6 font-mono-ieji text-[10px] tracking-[0.3em] text-[#5b5346] uppercase">
          Codex Eleonora — O Despertar das Sombras
        </div>
      </motion.div>
    </div>
  );
};

export default Login;
