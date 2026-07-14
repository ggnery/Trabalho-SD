package br.com.projeto.filamensagem.dto;

import br.com.projeto.filamensagem.model.Prioridade;
import java.util.UUID;

public record ClienteFilaDTO(
        UUID id,
        String name,
        Prioridade priority,
        String category
) {
}