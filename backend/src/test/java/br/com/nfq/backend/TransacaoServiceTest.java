package br.com.nfq.backend;

import br.com.nfq.backend.entity.Transacao;
import br.com.nfq.backend.repository.TransacaoRepository;
import br.com.nfq.backend.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService service;

    @Mock
    private TransacaoRepository repository;


    @Test
    public void listAllPorNomeLoja() {
        ///AAA ARRANGE, ACT, ASSERT
        //ARRANGE
        final String loja1 = "Loja 1";
        final String loja2 = "Loja 2";

        var transacao1 = new Transacao(1L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(100), 12345678909L, "12344567895654", new Time(System.currentTimeMillis()), "Dono loja 1", loja1);
        var transacao2 = new Transacao(1L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(90), 44444444444L, "12344567895654", new Time(System.currentTimeMillis()), "Dono loja 2", loja2);
        var transacao3 = new Transacao(1L, 1, new Date(System.currentTimeMillis()), BigDecimal.valueOf(60), 12345678909L, "12344567895654", new Time(System.currentTimeMillis()), "Dono loja 1", loja1);

        when(this.repository.findAllByOrderByNomeLojaAscIdDesc())
        .thenReturn(Arrays.asList(transacao1, transacao2, transacao3));

        //ACT
        var reports = this.service.listAllPorNomeLoja();

        //ASSERT
        assertEquals(2, reports.size());

        reports.forEach(report -> {
            if(report.nomeLoja().equals(loja1)) {
                assertEquals(2, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(160), report.total());
                assertTrue(report.transacoes().contains(transacao1));
                assertTrue(report.transacoes().contains(transacao3));
            } else if(report.nomeLoja().equals(loja2)) {
                assertEquals(1, report.transacoes().size());
                assertEquals(BigDecimal.valueOf(90), report.total());
                assertTrue(report.transacoes().contains(transacao2));
            }
            else {
                fail("Report não agrupou corretamente por loja");
            }
        });
    }

}
