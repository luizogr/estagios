package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.repositories.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    public void gerarPrimeiroRelatorio(Estagio estagio) {

        Relatorio primeiroRelatorio = new Relatorio();
        primeiroRelatorio.setEstagio(estagio);

        LocalDate prazo = estagio.getDataInicio().plusMonths(6);
        primeiroRelatorio.setDataPrevistaEntrega(prazo);

        relatorioRepository.save(primeiroRelatorio);
    }
}
