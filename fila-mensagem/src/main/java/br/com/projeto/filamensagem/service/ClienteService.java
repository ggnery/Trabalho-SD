package br.com.projeto.filamensagem.service;

import br.com.projeto.filamensagem.config.RabbitMQConfig;
import br.com.projeto.filamensagem.dto.*;
import br.com.projeto.filamensagem.model.Cliente;
import br.com.projeto.filamensagem.model.HistoricoAtendimento;
import br.com.projeto.filamensagem.model.StatusCliente;
import br.com.projeto.filamensagem.repository.ClienteRepository;
import br.com.projeto.filamensagem.repository.HistoricoRepository;
import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final HistoricoRepository historicoRepository;
    private final RabbitTemplate rabbitTemplate;

    public ClienteService(ClienteRepository repository, HistoricoRepository historicoRepository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public ClienteResponse registrarCliente(ClienteRequest request) {
        Cliente cliente = new Cliente(request.name(), request.category(), request.priority());
        cliente = repository.save(cliente);

        Long posicao = repository.calcularPosicao(
                cliente.getCategoria(),
                cliente.getPrioridade().name(),
                cliente.getCriadoEm()
        );

        String payload = cliente.getId().toString();

        Cliente finalCliente = cliente;
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ATENDIMENTO,
                cliente.getCategoria(),
                payload,
                message -> {
                    message.getMessageProperties().setPriority(finalCliente.getPrioridade().getPesoRabbitMq());
                    return message;
                }
        );

        return new ClienteResponse(cliente.getId(), posicao);
    }

    public PosicaoResponse consultarPosicao(UUID id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (cliente.getStatus() != br.com.projeto.filamensagem.model.StatusCliente.AGUARDANDO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cliente já foi atendido ou cancelado.");
        }

        Long posicao = repository.calcularPosicao(
                cliente.getCategoria(),
                cliente.getPrioridade().name(),
                cliente.getCriadoEm()
        );

        int tempoMedio = cliente.getCategoria().equalsIgnoreCase("Consulta") ? 10 : 5;
        long tempoEstimadoMinutos = posicao * tempoMedio;

        String tempoFormatado = tempoEstimadoMinutos + " minutes";

        return new PosicaoResponse(posicao, tempoFormatado);
    }

    @Transactional
    public void cancelarInscricao(UUID id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        if (cliente.getStatus() != br.com.projeto.filamensagem.model.StatusCliente.AGUARDANDO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível cancelar. O cliente já foi atendido ou a inscrição já estava cancelada.");
        }

        cliente.cancelar();
        repository.save(cliente);
    }

    @Transactional
    public AtendimentoResponse chamarProximo(String categoriaFila) {

        String nomeFila = categoriaFila.equalsIgnoreCase("Consulta")
                ? RabbitMQConfig.FILA_CONSULTA
                : RabbitMQConfig.FILA_EXAME;

        return rabbitTemplate.execute(channel -> {

            GetResponse response = channel.basicGet(nomeFila, false);

            if (response == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há clientes aguardando na fila de " + categoriaFila);
            }

            String idClienteStr = new String(response.getBody(), StandardCharsets.UTF_8);
            UUID clienteId = UUID.fromString(idClienteStr);

            Cliente cliente = repository.findById(clienteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Inconsistência: Cliente na fila não existe no banco"));

            if (cliente.getStatus() == StatusCliente.CANCELADO) {
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                return chamarProximo(categoriaFila);
            }

            cliente.marcarComoAtendido();
            repository.save(cliente);

            historicoRepository.save(new HistoricoAtendimento(cliente));

            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);

            return new AtendimentoResponse(cliente.getId(), cliente.getNome(), cliente.getCategoria());
        });
    }

    public List<ClienteFilaDTO> listarFilaCompleta() {
        Sort ordenacao = Sort.by(Sort.Direction.DESC, "prioridade")
                .and(Sort.by(Sort.Direction.ASC, "criadoEm"));

        List<Cliente> clientesAguardando = repository.findByStatus(StatusCliente.AGUARDANDO, ordenacao);

        return clientesAguardando.stream()
                .map(cliente -> new ClienteFilaDTO(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getPrioridade(),
                        cliente.getCategoria()
                ))
                .toList();
    }
}
