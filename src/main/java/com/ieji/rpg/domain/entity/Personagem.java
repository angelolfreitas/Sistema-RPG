package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "personagem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personagem")
    private Integer idPersonagem;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_jogador", length = 100)
    private String nomeJogador;

    @Column(columnDefinition = "TEXT")
    private String aparencia;

    @Column(columnDefinition = "TEXT")
    private String personalidade;

    @Column(columnDefinition = "TEXT")
    private String historico;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(nullable = false)
    private Integer agilidade = 1;

    @Column(nullable = false)
    private Integer forca = 1;

    @Column(nullable = false)
    private Integer intelecto = 1;

    @Column(nullable = false)
    private Integer presenca = 1;

    @Column(nullable = false)
    private Integer vigor = 1;

    @Column(nullable = false)
    private Integer nex = 5;

    @Column(name = "pv_atual", nullable = false)
    private Integer pvAtual;

    @Column(name = "pv_maximo", nullable = false)
    private Integer pvMaximo;

    @Column(name = "san_atual", nullable = false)
    private Integer sanAtual;

    @Column(name = "san_maxima", nullable = false)
    private Integer sanMaxima;

    @Column(name = "pe_atual", nullable = false)
    private Integer peAtual;

    @Column(name = "pe_maximo", nullable = false)
    private Integer peMaximo;

    @Column(nullable = false)
    private Integer defesa;

    @Column(name = "imagem_url")
    private String imagemUrl;




    @ManyToMany
    @JoinTable(
            name = "personagem_aetherys",
            joinColumns = @JoinColumn(name = "id_personagem"),
            inverseJoinColumns = @JoinColumn(name = "id_aetherys")
    )
    private Set<Aetherys> aetherys;



    public static Personagem consturctPersonagem(PersonagemRequest object, Usuario usuarioLogado ) {
        return Personagem.builder()
                .aparencia(object.aparencia())
                .usuario(usuarioLogado)
                .personalidade(object.personalidade())
                .historico(object.historico())
                .objetivo(object.objetivo())
                .agilidade(object.agilidade())
                .forca(object.forca())
                .intelecto(object.intelecto())
                .presenca(object.presenca())
                .vigor(object.vigor())
                .nex(object.nex())
                .pvAtual(object.pvAtual())
                .pvMaximo(object.pvMaximo())
                .sanAtual(object.sanAtual())
                .sanMaxima(object.sanMaxima())
                .peAtual(object.peAtual())
                .peMaximo(object.peMaximo())
                .defesa(object.defesa())
                .nomeJogador(object.nome())
                .build();
    }

    public void setByEntity(PersonagemRequest object) {
        this.setAparencia(object.aparencia());
        this.setPersonalidade(object.personalidade());
        this.setHistorico(object.historico());
        this.setObjetivo(object.objetivo());
        this.setAgilidade(object.agilidade());
        this.setForca(object.forca());
        this.setIntelecto(object.intelecto());
        this.setPresenca(object.presenca());
        this.setVigor(object.vigor());
        this.setNex(object.nex());
        this.setPvAtual(object.pvAtual());
        this.setPvMaximo(object.pvMaximo());
        this.setSanAtual(object.sanAtual());
        this.setSanMaxima(object.sanMaxima());
        this.setPeAtual(object.peAtual());
        this.setPeMaximo(object.peMaximo());
        this.setDefesa(object.defesa());
        this.setNomeJogador(object.nome());
    }

}
