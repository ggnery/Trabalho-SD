package br.com.projeto.filamensagem.dto;

import java.util.UUID;

public record ClienteResponse(
        UUID id,
        Long position
) {
}