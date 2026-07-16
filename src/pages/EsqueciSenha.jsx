import { useState } from "react";
import { Link } from "react-router-dom";
import { Mail, ArrowLeft, Feather } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { api } from "@/services/api";

const EsqueciSenha = () => {
  const [email, setEmail] = useState("");
  const [enviado, setEnviado] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      await api.post("/auth/forgot-password", { email: email });
    } catch (err) {
      console.error(err);
      // não revela se o email existe ou não — mostra sucesso de qualquer forma
    } finally {
      setIsLoading(false);
      setEnviado(true);
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center p-4 font-body">
      <div className="w-full max-w-md">
        <Link to="/login" className="inline-flex items-center gap-2 font-mono-ieji text-xs text-[#EAE0C4]/70 mb-4">
          <ArrowLeft className="w-3.5 h-3.5" /> Voltar ao login
        </Link>
        <div className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-8 shadow-[8px_8px_0px_0px_#7A1230]">
          <h1 className="font-display font-bold text-2xl text-[#201A1E] mb-2">Recuperar acesso</h1>
          <p className="font-body text-[#5b5346] mb-6 italic">
            Informe o e-mail cadastrado. Se ele existir em nossos registros, enviaremos um link de redefinição.
          </p>

          {enviado ? (
            <div className="bg-[#3F8574]/10 border-2 border-[#3F8574] text-[#1f4a41] text-sm px-3 py-3 rounded-sm flex items-center gap-2">
              <Feather className="w-4 h-4" /> Se o e-mail existir, o link chegará em instantes.
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-1.5">
                <Label className="font-mono-ieji text-[10px] uppercase flex items-center gap-2">
                  <Mail className="w-3.5 h-3.5" /> E-mail
                </Label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full bg-[#F5EFDD] border-2 border-[#201A1E] rounded-sm p-3 font-mono-ieji text-sm"
                  placeholder="agente@ieji.edu"
                />
              </div>
              <Button type="submit" disabled={isLoading} className="w-full h-12 bg-[#201A1E] text-[#EAE0C4] font-display font-bold">
                {isLoading ? "ENVIANDO..." : "ENVIAR LINK"}
              </Button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default EsqueciSenha;