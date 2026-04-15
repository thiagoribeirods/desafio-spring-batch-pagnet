package br.com.nfq.backend.web;

import br.com.nfq.backend.entity.TransacaoReport;
import br.com.nfq.backend.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @GetMapping
    List<TransacaoReport> listAll() {
        return this.transacaoService.listAllPorNomeLoja();
    }

}
