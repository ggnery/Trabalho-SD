package br.com.projeto.filamensagem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_atendimento")
public class HistoricoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "chamado_em", nullable = false, updatable = false)
    private LocalDateTime chamadoEm;

    protected HistoricoAtendimento() {}

    public HistoricoAtendimento(Cliente cliente) {
        this.cliente = cliente;
    }

    @PrePersist
    protected void onCall() {
        this.chamadoEm = LocalDateTime.now();
    }
}