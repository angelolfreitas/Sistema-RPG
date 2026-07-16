package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.Monstro;
import com.ieji.rpg.domain.entity.MonstroConhecido;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.MonstroConhecidoRepository;
import com.ieji.rpg.infra.repository.MonstroRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonstroService extends AbstractService<Monstro, Integer, MonstroRequest, MonstroResponse> {


    public MonstroService(MonstroRepository repository) {
        super(repository);
    }
    @Autowired
    private MonstroConhecidoRepository monstroConhecidoRepository;
    @Autowired
    private PersonagemRepository personagemRepository;
    @Transactional
    public void registrarConhecimentoParaUsuarios(Integer monstroId, List<Integer> usuariosIds) {
        Monstro monstro = repository.findById(monstroId).orElse(null);
        if (monstro == null || usuariosIds.isEmpty()) return;

        for (Integer usuarioId : usuariosIds) {
            List<Personagem> personagens = personagemRepository.findByUsuarioId(usuarioId);

            for (Personagem p : personagens) {
                boolean jaConhece = monstroConhecidoRepository
                        .existsByMonstro_IdMonstroAndPersonagem_IdPersonagem(monstroId, p.getIdPersonagem());

                if (!jaConhece) {
                    monstroConhecidoRepository.save(MonstroConhecido.builder()
                            .monstro(monstro)
                            .personagem(p)
                            .conhecidoEm(java.time.Instant.now())
                            .build());
                }
            }
        }
    }


    public List<MonstroResponse> listarParaUsuario(Usuario usuario) {
        boolean ehMestre = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin::write") || a.getAuthority().equals("admin::write"));

        List<Monstro> todos = repository.findAll();

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
    @Transactional
    public MonstroResponse alterarVida(Integer id, Integer novoPv) {
        this.patchEntity(id, Map.of("pv", novoPv));

        return getById(id);
    }

    @Override
    MonstroResponse construct(MonstroRequest object) {
        Monstro monstro = Monstro.builder()
                .nome(object.nome())
                .pv(object.pv())
                .pvMaximo(object.pvMaximo())
                .san(object.san())
                .ataquesEspeciais(object.ataquesEspeciais())
                .comportamento(object.comportamento())
                .fraquezas(object.fraquezas())
                .imagemUrl(object.imagemUrl())
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
    }

    @Override
    protected MonstroResponse convertToResponse(Monstro entity) {
        return MonstroResponse.constructByEntity(entity);
    }
    @Transactional
    public MonstroResponse marcarEmBatalha(Integer id, boolean emBatalha) {
        this.patchEntity(id, Map.of("emBatalha", emBatalha));
        return getById(id);
    }

}