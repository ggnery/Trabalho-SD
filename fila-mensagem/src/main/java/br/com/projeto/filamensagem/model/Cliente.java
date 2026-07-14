package br.com.projeto.filamensagem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCliente status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Cliente() {}

    public Cliente(String nome, String categoria, Prioridade prioridade) {
        this.nome = nome;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.status = StatusCliente.AGUARDANDO;
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    public void marcarComoAtendido() {
        this.status = StatusCliente.ATENDIDO;
    }

    public void cancelar() {
        this.status = StatusCliente.CANCELADO;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public Prioridade getPrioridade() { return prioridade; }
    public StatusCliente getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}