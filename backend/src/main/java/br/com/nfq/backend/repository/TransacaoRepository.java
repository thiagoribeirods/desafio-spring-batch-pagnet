package br.com.nfq.backend.repository;

import br.com.nfq.backend.entity.Transacao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByNomeLojaAscIdDesc();

}
