package com.ieji.rpg.service.monstro;

import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.monstro.MaterialMonstro;
import com.ieji.rpg.domain.entity.monstro.Monstro;
import com.ieji.rpg.domain.entity.MonstroConhecido;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.MonstroConhecidoRepository;
import com.ieji.rpg.infra.repository.MonstroRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.AutorizacaoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
/// delete(): regra complexa
/// delete: procura os casos em que o mosntro está cadastrado
/// para cada caso em que o msontro está, retira o msontro. Salva no banco o caso.
///
/// deleta o mosntro do banco de registro de conhecimentos.
///
/// Deleta o mosntro com base na abstração do service, agora sem risco de cascata.
///
/// registrarConhecimentoParaTodos():
/// Procura o id requerido. Se nao char nada, finaliza a função.
/// Acha todos os personagens do repositório (talvez devesse ser do caso. Fazer uma consulta SQL em caso que faça isso.)
/// Para cada personagem encontrado na última busca, registra um novo conhecimento se não conhecer o monstor. Persiste
/// no front a existencia do monstro.
///
/// aplicatDano(): procura o mosntro, se existir:
/// Verifica se o monstor terá 0 de vida após receber o dano, faz um corte se a vida for menor que 0 após receber.
/// muda os pontos de via do mosntro e o salva no banco.
///
/// listarParaUsuario(): verifica s eo usuário em questão é mestre 9talves pudesse ser pelo campo, verificar depois)
/// recupera todos os mosntros do banco.
/// para cada mosntro:
///  monta uma resposta de monstro
///  verifiac se para o usuário o mosntro é conhecido
///  dependendo do progresso e da sasutoridades do usuário, msotra o montro com dados totais (mestre), parciais (usuario
/// que viu o monstro) ou ocultos (usuário que não viu o mosntro).
///
/// As demais funções sãoanálogas às anteriormente explicadas (documentar em breve).
@Service
public class MonstroService extends AbstractService<Monstro, Integer, MonstroRequest, MonstroResponse> {


    public MonstroService(MonstroRepository repository, MonstroConhecidoRepository monstroConhecidoRepository, PersonagemRepository personagemRepository, MonstroCacheService monstroCacheService, CasoInvestigacaoRepository casoRepository, AutorizacaoService autorizacaoService) {
        super(repository);
        this.monstroConhecidoRepository = monstroConhecidoRepository;
        this.personagemRepository = personagemRepository;
        this.monstroCacheService = monstroCacheService;
        this.casoRepository = casoRepository;
        this.autorizacaoService = autorizacaoService;
    }

    private final MonstroConhecidoRepository monstroConhecidoRepository;

    private final PersonagemRepository personagemRepository;

    private final MonstroCacheService monstroCacheService;

    private final CasoInvestigacaoRepository casoRepository;

    private final AutorizacaoService autorizacaoService;

    @Override
    @Transactional
    public void delete(Integer id) {
        List<CasoInvestigacao> casosEmBatalha = casoRepository.findByMonstroAtual_IdMonstro(id);
        for (CasoInvestigacao caso : casosEmBatalha) {
            caso.setMonstroAtual(null);
            casoRepository.save(caso);
        }


        monstroConhecidoRepository.deleteByMonstro_IdMonstro(id);

        super.delete(id);
    }

    @Transactional
    public void registrarConhecimentoParaTodos(Integer monstroId) {
        Monstro monstro = repository.findById(monstroId).orElse(null);
        if (monstro == null) return;

        List<Personagem> personagensSemConhecimento =
                personagemRepository.findQuemNaoConheceMonstro(monstroId);

        List<MonstroConhecido> novosConhecimentos = personagensSemConhecimento.stream()
                .map(personagem -> MonstroConhecido.builder()
                        .monstro(monstro)
                        .personagem(personagem)
                        .conhecidoEm(java.time.Instant.now())
                        .build())
                .toList();

        monstroConhecidoRepository.saveAll(novosConhecimentos);
    }


    @CacheEvict(value = "monstros", allEntries = true)
    @Transactional
    public MonstroResponse aplicarDano(Integer id, Integer dano) {
        Monstro monstro = repository.findById(id)//fazer excecao
                .orElseThrow(() -> new EntityNotFoundException("Monstro não encontrado"));
        int novoPv = Math.max(0, monstro.getPv() - dano);
        monstro.setPv(novoPv);
        repository.save(monstro);
        return MonstroResponse.constructByEntity(monstro);
    }

    public List<MonstroResponse> listarParaUsuario(Usuario usuario) {

        boolean ehMestre = autorizacaoService.ehMestre(usuario);

        List<Monstro> todos = monstroCacheService.listarTodosCacheado();

        return todos.stream().map(m -> {
            MonstroResponse monstroResponse = MonstroResponse.constructByEntity(m);

            boolean conhecido = ehMestre ||
                    Boolean.TRUE.equals(m.getEmBatalha()) ||
                    monstroConhecidoRepository.existsByMonstro_IdMonstroAndPersonagem_Usuario_Id(m.getIdMonstro(), usuario.getId());

            if(ehMestre){
                return monstroResponse;
            }else if(conhecido) {
                return MonstroResponse.buildExhibit(m);
            }else {
                return MonstroResponse.buildByUser(m);
            }

        }).toList();
    }
    @CacheEvict(value = "monstros", allEntries = true)
    @Transactional
    public MonstroResponse marcarEmBatalha(Integer id, boolean emBatalha) {
        this.patchEntity(id, Map.of("emBatalha", emBatalha));
        return getById(id);
    }
    @CacheEvict(value = "monstros", allEntries = true)
    @Transactional
    public MonstroResponse alterarVida(Integer id, Integer novoPv) {
        this.patchEntity(id, Map.of("pv", novoPv));
        return getById(id);
    }

    @Override
    protected MonstroResponse construct(MonstroRequest object) {
        Monstro monstro = Monstro.builder()
                .nome(object.nome())
                .pv(object.pv())
                .pvMaximo(object.pvMaximo())
                .san(object.san())
                .ataquesEspeciais(object.ataquesEspeciais())
                .comportamento(object.comportamento())
                .fraquezas(object.fraquezas())
                .imagemUrl(object.imagemUrl())
                .material(object.material() != null ? object.material() : MaterialMonstro.CARNE)
                .build();
        repository.save(monstro);

        return MonstroResponse.constructByEntity(monstro);
    }

    @Override
    protected void updateData(Monstro entity, MonstroRequest object) {
        entity.setNome(object.nome());
        entity.setPv(object.pv());
        entity.setSan(object.san());
        entity.setPvMaximo(object.pvMaximo());
        entity.setAtaquesEspeciais(object.ataquesEspeciais());
        entity.setComportamento(object.comportamento());
        entity.setFraquezas(object.fraquezas());
        entity.setImagemUrl(object.imagemUrl());
        if (object.material() != null) entity.setMaterial(object.material());
    }

    @Override
    protected MonstroResponse convertToResponse(Monstro entity) {
        return MonstroResponse.constructByEntity(entity);
    }



    @CacheEvict(value = "monstros", allEntries = true)
    @Override
    public Optional<MonstroResponse> create(MonstroRequest dto) {
        return super.create(dto);
    }

    @CacheEvict(value = "monstros", allEntries = true)
    @Override
    public MonstroResponse update(MonstroRequest dto) {
        return super.update(dto);
    }



}