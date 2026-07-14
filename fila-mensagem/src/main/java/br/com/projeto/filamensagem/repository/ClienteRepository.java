package br.com.projeto.filamensagem.repository;

import br.com.projeto.filamensagem.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    @Query(value = """
            SELECT COUNT(id) FROM clientes\s
            WHERE categoria = :categoria\s
              AND status = 'AGUARDANDO'\s
              AND (
                  (prioridade = 'URGENCIA' AND :prioridade IN ('NORMAL', 'PREFERENCIAL'))\s
               OR (prioridade = 'PREFERENCIAL' AND :prioridade = 'NORMAL')\s
               OR (prioridade = :prioridade AND criado_em <= :criadoEm)
              )
           \s""", nativeQuery = true)
    Long calcularPosicao(
            @Param("categoria") String categoria,
            @Param("prioridade") String prioridade,
            @Param("criadoEm") LocalDateTime criadoEm
    );

    java.util.List<Cliente> findByStatus(br.com.projeto.filamensagem.model.StatusCliente status, org.springframework.data.domain.Sort sort);
}