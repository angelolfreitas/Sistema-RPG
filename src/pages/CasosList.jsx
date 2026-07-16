import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Plus, X, ArrowLeft, Search, Skull, FolderSearch, AlertTriangle, ShieldCheck
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Link } from "react-router-dom";
import { api } from "@/services/api";
import { hasAuthority } from "@/utils/auth";

const FontImports = () => (
  <style>{`
    @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;900&family=Cormorant+Garamond:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap');
    .font-display { font-family: 'Cinzel', serif; }
    .font-body { font-family: 'Cormorant Garamond', serif; }
    .font-mono-ieji { font-family: 'JetBrains Mono', monospace; }
  `}</style>
);
const selecionarCaso = (id) => {
    localStorage.setItem("idCasoAtivo", id);
    navigate(`/sessao/${id}`);
};
const CasosList = () => {
  const [casos, setCasos] = useState([]);
  const [search, setSearch] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  
  // Apenas quem tem a authority manager::write (ou admin) pode criar um caso
  const podeCriarCaso = hasAuthority("manager::write") || hasAuthority("admin::write");

  const fetchCasos = async () => {
    try {
      const response = await api.get("/casos");
      setCasos(response.data);
    } catch (error) {
      console.error("Erro ao carregar casos:", error);
    }
  };

  useEffect(() => {
    fetchCasos();
  }, []);

  const handleCreateCaso = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    const form = e.target;
    
    const payload = {
      nomeCaso: form.nomeCaso.value,
      resumo: form.resumo.value,
      objetivo: form.objetivo.value,
      urgencia: form.urgencia.value,
      rodadasRestantes: Number(form.rodadas.value)
    };

    try {
      const response = await api.post("/casos", payload);
      setCasos((prev) => [...prev, response.data]);
      setIsModalOpen(false);
      form.reset();
    } catch (error) {
      console.error("Erro ao criar caso:", error);
    } finally {
      setIsSaving(false);
    }
  };

  const filtered = casos.filter((c) =>
    c.nomeCaso?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen w-full bg-[#0B0A0D] font-body relative">
      <FontImports />
      <div className="fixed inset-0 pointer-events-none opacity-[0.12] z-0"
           style={{ backgroundImage: 'radial-gradient(#3F8574 1px, transparent 1px)', backgroundSize: '26px 26px' }} />

      <header className="sticky top-0 z-30 bg-[#15121A] border-b-2 border-[#3F8574]/40 px-4 md:px-8 py-4 flex justify-between items-center">
        <div className="flex items-center gap-3">
          <Link to="/personagem" className="w-9 h-9 rounded-full bg-[#3F8574] border-2 border-[#B99A4B] flex items-center justify-center shrink-0 hover:bg-[#7A1230] transition-colors">
            <ArrowLeft className="w-4 h-4 text-[#EAE0C4]" />
          </Link>
          <div>
            <h1 className="font-display font-bold text-lg text-[#EAE0C4] leading-none">ARQUIVOS DO INSTITUTO</h1>
            <span className="font-mono-ieji text-[10px] text-[#3F8574] tracking-widest">CASOS DE INVESTIGAÇÃO</span>
          </div>
        </div>
        <FolderSearch className="w-6 h-6 text-[#3F8574]" />
      </header>

      <main className="max-w-6xl mx-auto px-4 md:px-8 py-10 relative z-10">
        
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
          <div className="relative w-full md:w-80">
            <Search className="w-4 h-4 text-[#5b5346] absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar caso pelo nome..."
              className="w-full bg-[#EAE0C4] border-2 border-[#0B0A0D] rounded-sm pl-9 pr-3 py-2.5 font-mono-ieji text-sm text-[#201A1E] focus:outline-none focus:border-[#3F8574]"
            />
          </div>
          
          {podeCriarCaso && (
            <Button onClick={() => setIsModalOpen(true)} className="w-full md:w-auto bg-[#3F8574] text-[#EAE0C4] hover:bg-[#EAE0C4] hover:text-[#201A1E] border-2 border-[#0B0A0D] font-display font-bold gap-2 shadow-[3px_3px_0px_0px_#B99A4B]">
              <Plus className="w-4 h-4" /> ABRIR NOVO CASO
            </Button>
          )}
        </div>

        {filtered.length === 0 ? (
          <div className="bg-[#EAE0C4] border-4 border-dashed border-[#3F8574] rounded-sm p-16 text-center shadow-[6px_6px_0px_0px_#0B0A0D]">
            <FolderSearch className="w-10 h-10 text-[#3F8574] mx-auto mb-4" strokeWidth={1.3} />
            <p className="font-display font-bold text-2xl text-[#201A1E]">Nenhum caso encontrado</p>
            <p className="font-body text-[#5b5346] mt-1 text-lg">Os arquivos estão vazios. Por hora, o instituto está seguro.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <AnimatePresence>
              {filtered.map((caso) => (
                <motion.div
                  key={caso.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  whileHover={{ y: -4 }}
                  className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm overflow-hidden shadow-[6px_6px_0px_0px_#3F8574] flex flex-col transition-all"
                >
                  <div className="bg-[#201A1E] px-4 py-3 flex justify-between items-center border-b-2 border-[#0B0A0D]">
                    <span className="font-mono-ieji text-[10px] text-[#3F8574] uppercase tracking-widest truncate flex items-center gap-1.5">
                      <ShieldCheck className="w-3.5 h-3.5" /> Caso #{caso.id}
                    </span>
                    <span className={`font-mono-ieji text-[9px] uppercase px-2 py-0.5 rounded-sm border ${
                      caso.status === 'ENCERRADA' ? 'bg-[#7A1230]/20 text-[#7A1230] border-[#7A1230]' : 'bg-[#3F8574]/20 text-[#3F8574] border-[#3F8574]'
                    }`}>
                      {caso.status || "ABERTA"}
                    </span>
                  </div>
                  
                  <div className="p-5 flex-1 flex flex-col">
                    <h3 className="font-display font-bold text-xl text-[#201A1E] leading-tight mb-2">
                      {caso.nomeCaso}
                    </h3>
                    <p className="font-body text-sm text-[#5b5346] line-clamp-3 mb-4 flex-1 border-l-2 border-[#B99A4B] pl-2 italic">
                      {caso.resumo || "Sem descrição disponível..."}
                    </p>
                    
                    <div className="mt-auto pt-4 border-t border-dashed border-[#B99A4B]">
                      <Link to={`/sessao/${caso.id}`} className="block w-full">
                        <Button className="w-full bg-transparent border-2 border-[#0B0A0D] text-[#201A1E] hover:bg-[#0B0A0D] hover:text-[#EAE0C4] font-mono-ieji font-bold text-xs uppercase tracking-wider transition-colors" 
                            onClick={() => localStorage.setItem("idCasoAtivo", caso.id)}>
                          ENTRAR NA INVESTIGAÇÃO
                        </Button>
                      </Link>
                    </div>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        )}

      </main>

      {/* MODAL PARA O MESTRE CRIAR UM CASO NOVO */}
      <AnimatePresence>
        {isModalOpen && podeCriarCaso && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              className="absolute inset-0 bg-black/80 backdrop-blur-sm" onClick={() => setIsModalOpen(false)} />
            <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
              className="bg-[#EAE0C4] border-4 border-[#0B0A0D] rounded-sm p-6 shadow-[8px_8px_0px_0px_#3F8574] w-full max-w-lg relative z-10 max-h-[90vh] overflow-y-auto">
              
              <div className="flex justify-between items-center mb-5 border-b-2 border-[#0B0A0D] pb-2">
                <h3 className="font-display font-bold text-2xl text-[#201A1E] flex items-center gap-2">
                  <AlertTriangle className="w-6 h-6 text-[#7A1230]" /> NOVO CASO
                </h3>
                <button onClick={() => setIsModalOpen(false)}><X className="w-5 h-5 text-[#5b5346] hover:text-[#7A1230]" /></button>
              </div>
              
              <form onSubmit={handleCreateCaso} className="space-y-4">
                <div className="space-y-1">
                  <Label className="font-mono-ieji text-[10px] uppercase">Título do caso</Label>
                  <input name="nomeCaso" required className="w-full bg-[#F5EFDD] border-2 border-[#0B0A0D] rounded-sm p-2.5 font-display font-bold text-lg text-[#201A1E]" placeholder="Ex: Morte de Nakamura" />
                </div>
                
                <div className="space-y-1">
                  <Label className="font-mono-ieji text-[10px] uppercase">Resumo</Label>
                  <textarea name="resumo" rows={2} required className="w-full bg-[#F5EFDD] border-2 border-[#0B0A0D] rounded-sm p-2.5 font-body text-base resize-none" placeholder="O que está acontecendo?" />
                </div>

                <div className="space-y-1">
                  <Label className="font-mono-ieji text-[10px] uppercase">Objetivo Principal</Label>
                  <textarea name="objetivo" rows={2} required className="w-full bg-[#F5EFDD] border-2 border-[#0B0A0D] rounded-sm p-2.5 font-body text-base resize-none" placeholder="O que os estudantes devem fazer?" />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <Label className="font-mono-ieji text-[10px] uppercase">Nível de Urgência</Label>
                    <select name="urgencia" required className="w-full bg-[#F5EFDD] border-2 border-[#0B0A0D] rounded-sm p-2.5 font-mono-ieji text-xs">
                      <option value="BAIXA">Baixa</option>
                      <option value="MEDIA">Média</option>
                      <option value="ALTA">Alta</option>
                      <option value="EXTREMA">Extrema (Morte Iminente)</option>
                    </select>
                  </div>
                  <div className="space-y-1">
                    <Label className="font-mono-ieji text-[10px] uppercase">Rodadas Estimadas</Label>
                    <input name="rodadas" type="number" defaultValue={10} min={1} required className="w-full bg-[#F5EFDD] border-2 border-[#0B0A0D] rounded-sm p-2.5 font-mono-ieji text-sm text-center" />
                  </div>
                </div>

                <div className="pt-4 flex justify-end gap-3 mt-4 border-t border-dashed border-[#B99A4B]">
                  <Button type="button" onClick={() => setIsModalOpen(false)} className="bg-transparent border-2 border-[#0B0A0D] text-[#201A1E] font-mono-ieji text-xs">CANCELAR</Button>
                  <Button type="submit" disabled={isSaving} className="bg-[#7A1230] text-[#EAE0C4] border-2 border-[#0B0A0D] font-display font-bold">
                    {isSaving ? "CRIANDO..." : "ABRIR ARQUIVO"}
                  </Button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

    </div>
  );
};

export default CasosList;