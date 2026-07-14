package br.com.projeto.filamensagem.controller;

import br.com.projeto.filamensagem.dto.AtendimentoResponse;
import br.com.projeto.filamensagem.dto.ClienteFilaDTO;
import br.com.projeto.filamensagem.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ClienteService clienteService;

    public AdminController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/queue/next")
    public ResponseEntity<AtendimentoResponse> chamarProximo(@RequestParam String category) {
        AtendimentoResponse response = clienteService.chamarProximo(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<ClienteFilaDTO>> listarTodosNaFila() {
        List<ClienteFilaDTO> lista = clienteService.listarFilaCompleta();
        return ResponseEntity.ok(lista);
    }
}