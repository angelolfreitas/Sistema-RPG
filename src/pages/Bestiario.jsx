import { Link } from "react-router-dom";
import { ArrowLeft, Skull } from "lucide-react";
import MonstroSessao from "@/components/MonstroSessao";
import { hasAuthority } from "@/utils/auth";

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
    .font-mono-ieji { font-family: 'JetBrains Mono', monospace; }
  `}</style>
);
 
const Bestiario = () => {
  // ID do caso ativo, salvo ao entrar na investigação (ver CasosList / SessaoRPG)
  const idCaso = localStorage.getItem("idCasoAtivo");
  // Reservado para quando a criação/edição de monstros pelo mestre for ligada na UI
  const podeCriar = hasAuthority("admin::write");
  const voltarPara = "/personagem";

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] font-body relative">
      <FontImports />
      <div
        className="fixed inset-0 pointer-events-none opacity-[0.12] z-0"
        style={{ backgroundImage: 'radial-gradient(#7A1230 1px, transparent 1px)', backgroundSize: '26px 26px' }}
      />

      <header className="sticky top-0 z-30 bg-[#15121A] border-b-2 border-[#7A1230]/40 px-4 md:px-8 py-4 flex justify-between items-center">
        <div className="flex items-center gap-3 min-w-0">
          <Link
            to={voltarPara}
            className="w-9 h-9 rounded-full bg-[#7A1230] border-2 border-[#B99A4B] flex items-center justify-center shrink-0 hover:bg-[#3F8574] transition-colors"
          >
            <ArrowLeft className="w-4 h-4 text-[#EAE0C4]" />
          </Link>
          <div className="min-w-0">
            <h1 className="font-display font-bold text-base sm:text-lg text-[#EAE0C4] leading-none truncate">
              BESTIÁRIO
            </h1>
            <span className="font-mono-ieji text-[9px] sm:text-[10px] text-[#7A1230] tracking-widest">
              AMEAÇAS CATALOGADAS
            </span>
          </div>
        </div>
        <div className="w-9 h-9 rounded-full bg-[#201A1E] border-2 border-[#7A1230] flex items-center justify-center shrink-0">
          <Skull className="w-4 h-4 text-[#7A1230]" />
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 md:px-8 py-6 sm:py-10 relative z-10">
        <div className="mb-6 sm:mb-10 bg-[#15121A] border border-[#7A1230]/40 rounded-sm p-4 sm:p-5 flex items-start gap-3">
          <Skull className="w-5 h-5 text-[#B99A4B] shrink-0 mt-0.5" />
          <p className="font-body text-[#B4AC9A] text-sm sm:text-base leading-snug">
            Arquivos de criaturas e ameaças registradas pelo Instituto Eleonora. Agentes dedicados ao caso podem visualizar informações das bestas abaixo.
          </p>
        </div>

        {idCaso ? (
          <MonstroSessao idCaso={idCaso} />
        ) : (
          <div className="bg-[#EAE0C4] border-4 border-dashed border-[#7A1230] rounded-sm p-10 sm:p-16 text-center">
            <Skull className="w-10 h-10 text-[#7A1230] mx-auto mb-4" strokeWidth={1.3} />
            <p className="font-display font-bold text-lg sm:text-xl text-[#201A1E]">
              Nenhum caso ativo selecionado
            </p>
            <p className="font-body text-[#5b5346] mt-1 text-sm sm:text-base">
              Entre em uma investigação nos Arquivos da Ordem para ver as ameaças ligadas a ela.
            </p>
            <Link to="/casos" className="inline-block mt-5">
              <span className="font-mono-ieji text-[10px] sm:text-xs uppercase tracking-wider bg-[#7A1230] text-[#EAE0C4] border-2 border-[#0B0A0D] rounded-sm px-4 py-2.5 inline-block">
                Ir para Arquivos da Ordem
              </span>
            </Link>
          </div>
        )}
      </main>
    </div>
  );
};

export default Bestiario;
