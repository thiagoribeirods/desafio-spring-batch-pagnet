package br.com.nfq.backend.service;

import br.com.nfq.backend.entity.TransacaoReport;
import br.com.nfq.backend.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository repository;

    public List<TransacaoReport> listAllPorNomeLoja() {
        var list = this.repository.findAllByOrderByNomeLojaAscIdDesc();

        var reportMap = new LinkedHashMap<String, TransacaoReport>();

        list.forEach(transacao -> {
            var nomeLoja = transacao.nomeLoja();
            var valor = transacao.valor();
            reportMap.compute(nomeLoja, (key, existing) -> {
                var report = (
                    existing != null ? existing : new TransacaoReport(key, BigDecimal.ZERO, new ArrayList<>())
                );
                return report.addTotal(valor).addTransacao(transacao);
            });
        });

        return new ArrayList<>(reportMap.values());

    }

}
