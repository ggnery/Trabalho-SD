package br.com.projeto.filamensagem.dto;

import java.util.UUID;

public record AtendimentoResponse(
        UUID id,
        String name,
        String category
) {
}