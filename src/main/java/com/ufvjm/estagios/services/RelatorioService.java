package com.ufvjm.estagios.services;

import com.ufvjm.estagios.entities.Aditivo;
import com.ufvjm.estagios.entities.Estagio;
import com.ufvjm.estagios.entities.Relatorio;
import com.ufvjm.estagios.entities.enums.StatusAditivo;
import com.ufvjm.estagios.entities.enums.StatusEstagio;
import com.ufvjm.estagios.repositories.AditivoRepository;
import com.ufvjm.estagios.repositories.EstagioRepository;
import com.ufvjm.estagios.repositories.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private EstagioRepository estagioRepository;
    @Autowired
    private AditivoRepository aditivoRepository;

    public void gerarPrimeiroRelatorio(Estagio estagio) {

        Relatorio primeiroRelatorio = new Relatorio();
        primeiroRelatorio.setEstagio(estagio);

        LocalDate prazo = estagio.getDataInicio().plusMonths(6);
        primeiroRelatorio.setDataPrevistaEntrega(prazo);

        relatorioRepository.save(primeiroRelatorio);
    }

    @Transactional
    public void sincronizarRelatoriosPendentes() {
        List<Estagio> estagiosAtivos = estagioRepository.findByStatusEstagio(StatusEstagio.ATIVO);
        for (Estagio estagio : estagiosAtivos) {
            LocalDate dataFim = getDataFim(estagio);
            LocalDate dataInicio = estagio.getDataInicio();

            long mesesDesdeInicio = ChronoUnit.MONTHS.between(dataInicio, LocalDate.now());
            int relatoriosNecessarios = (int) (mesesDesdeInicio / 6) + 1;

            long relatoriosExistentes = relatorioRepository.countByEstagio(estagio);

            for (long i = relatoriosExistentes + 1; i <= relatoriosNecessarios; i++) {
                LocalDate dataPrazoEntrega = dataInicio.plusMonths(i * 6);

                if (!dataPrazoEntrega.isAfter(dataFim)) {
                    Relatorio relatorio = new Relatorio();
                    relatorio.setEstagio(estagio);
                    relatorio.setDataPrevistaEntrega(dataPrazoEntrega);

                    relatorioRepository.save(relatorio);
                } else {
                    break;
                }

            }
        }
    }

    public LocalDate getDataFim(Estagio estagio) {
        Optional<Aditivo> ultimoAditivoAprovado = aditivoRepository
                .findTopByEstagioAndStatusOrderByNovaDataTerminoDesc(estagio, StatusAditivo.APROVADO);
        if (ultimoAditivoAprovado.isPresent()) {
            return ultimoAditivoAprovado.get().getNovaDataTermino();
        } else {
            return estagio.getDataTermino();
        }
    }
}