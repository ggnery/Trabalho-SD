package br.com.projeto.filamensagem.controller;

import br.com.projeto.filamensagem.dto.ClienteRequest;
import br.com.projeto.filamensagem.dto.ClienteResponse;
import br.com.projeto.filamensagem.dto.PosicaoResponse;
import br.com.projeto.filamensagem.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/queue")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrarNaFila(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/position")
    public ResponseEntity<PosicaoResponse> consultarPosicao(@PathVariable UUID id) {
        PosicaoResponse response = clienteService.consultarPosicao(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarInscricao(@PathVariable UUID id) {
        clienteService.cancelarInscricao(id);
        return ResponseEntity.noContent().build();
    }
}