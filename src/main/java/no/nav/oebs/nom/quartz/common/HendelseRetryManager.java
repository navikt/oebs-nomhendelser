package no.nav.oebs.nom.quartz.common;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.db.entity.BaseHendelse;
import no.nav.oebs.nom.db.repository.HendelseRepository;
import no.nav.oebs.nom.service.HendelseRetryService;

/**
 * Basisklasse som administrerer rekjøring av hendelser.
 *
 * @param <T>
 *            Hendelsetypen. Må være en subtype av {@link BaseHendelse}.
 */
public class HendelseRetryManager<T extends BaseHendelse> {

    private HendelseRetryService<T> retryService;

    private HendelseRepository<T, Long> hendelseRepository;

    public HendelseRetryManager(HendelseRetryService<T> retryService, HendelseRepository<T, Long> hendelseRepository) {
        this.retryService = retryService;
        this.hendelseRepository = hendelseRepository;
    }

    /**
     * Rekjører alle hendelser som har status RETRY og retry-tidspunktet er nådd.
     * <p>
     * HendelseRepository-metoden bruker pessimistisk låsing og krever dermed at en transaksjon er startet.
     */
    @Transactional
    public void retryHendelser() {
        List<T> hendelseList = hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(BaseHendelse.STATUS_RETRY,
                LocalDateTime.now());

        hendelseList.stream() //
                .sorted(Comparator.comparing(BaseHendelse::getId)) //
                .forEach(hendelse -> {
                    try {
                        MdcOperations.put(MdcOperations.MDC_CORRELATION_ID, MdcOperations.generateCorrelationId());

                        retryService.retryHendelse(hendelse);
                    } finally {
                        MdcOperations.remove(MdcOperations.MDC_CORRELATION_ID);
                    }
                });
    }
}
