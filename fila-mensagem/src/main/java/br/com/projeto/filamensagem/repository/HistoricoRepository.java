package br.com.projeto.filamensagem.repository;

import br.com.projeto.filamensagem.model.HistoricoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoricoRepository extends JpaRepository<HistoricoAtendimento, UUID> {
}
