package br.com.projeto.filamensagem.dto;

import br.com.projeto.filamensagem.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A categoria é obrigatória (ex: Consulta, Exame)")
        String category,

        @NotNull(message = "A prioridade é obrigatória (NORMAL, PREFERENCIAL, URGENCIA)")
        Prioridade priority
) {
}
