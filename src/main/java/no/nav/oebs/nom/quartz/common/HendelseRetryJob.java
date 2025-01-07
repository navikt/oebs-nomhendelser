package no.nav.oebs.nom.quartz.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.db.entity.BaseHendelse;

/**
 * Quartz-basisjobb for rekjøring av hendelser som har feilet.
 *
 * @param <T>
 *            Hendelsetypen. Må være en subtype av {@link BaseHendelse}.
 */
@Slf4j
public class HendelseRetryJob<T extends BaseHendelse> extends QuartzJobBean {

    private HendelseRetryManager<T> retryManager;

    public HendelseRetryJob(HendelseRetryManager<T> retryManager) {
        this.retryManager = retryManager;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.debug("{} startet {}", context.getJobDetail().getKey().getName(), toLocalDateTime(context.getFireTime()));

        retryManager.retryHendelser();

        if (context.getNextFireTime() != null) {
            log.debug("Neste kjøring av {} er planlagt {}", context.getJobDetail().getKey().getName(),
                    toLocalDateTime(context.getNextFireTime()));
        } else {
            log.debug("Ingen ny kjøring av {} er planlagt.", context.getJobDetail().getKey().getName()); // Kun test
        }
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
