package org.ict4h.atomfeed.integration.motech.openmrs;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

@Component
public class PillRegimen {
    public static final String EVENT_FROM_OPEN_MRS = "eventFromOpenMRS";
    private final PillReminderService pillReminderService;
    private final MotechSchedulerService schedulerService;
    private String cronExpression;

    public PillRegimen(PillReminderService pillReminderService, MotechSchedulerService motechSchedulerService, String cronExpression) {
        this.pillReminderService = pillReminderService;
        this.schedulerService = motechSchedulerService;
        this.cronExpression = cronExpression;
    }

    @PostConstruct
    public void startScheduler(){
        schedulerService.scheduleJob(new CronSchedulableJob(new MotechEvent(EVENT_FROM_OPEN_MRS),cronExpression));
    }

    @PreDestroy
    public void stopScheduler(){
        schedulerService.safeUnscheduleAllJobs(EVENT_FROM_OPEN_MRS);
    }

    @MotechListener(subjects = EVENT_FROM_OPEN_MRS)
    public void create(MotechEvent motechEvent){
        Map<String,Object> parameters = motechEvent.getParameters();
        System.out.println("marker - marker - marker");
    }
}
