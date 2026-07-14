package br.com.projeto.filamensagem.model;

public enum Prioridade {
    NORMAL(1),
    PREFERENCIAL(5),
    URGENCIA(10);

    private final int pesoRabbitMq;

    Prioridade(int pesoRabbitMq) {
        this.pesoRabbitMq = pesoRabbitMq;
    }

    public int getPesoRabbitMq() {
        return pesoRabbitMq;
    }
}
