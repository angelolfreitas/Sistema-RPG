import { Link } from "react-router-dom";
import { ShieldAlert } from "lucide-react";

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
  `}</style>
);

const ContaRemovida = () => {
  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] flex items-center justify-center px-4">
      <FontImports />
      <div className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-8 max-w-md text-center shadow-[8px_8px_0px_0px_#7A1230]">
        <ShieldAlert className="w-12 h-12 text-[#7A1230] mx-auto mb-4" />
        <h1 className="font-display font-bold text-2xl text-[#201A1E] mb-2">Sessão Encerrada</h1>
        <p className="font-body text-[#5b5346] text-base leading-snug mb-6">
          Sua sessão não é mais válida. Isso pode acontecer se sua conta foi removida por um administrador
          ou se o acesso expirou. Faça login novamente para continuar.
        </p>
        <Link
          to="/login"
          className="inline-block bg-[#7A1230] text-[#EAE0C4] font-display font-bold px-6 py-3 rounded-sm border-2 border-[#0B0A0D] hover:bg-[#0B0A0D] transition-colors"
        >
          Voltar ao Login
        </Link>
      </div>
    </div>
  );
};

export default ContaRemovida;