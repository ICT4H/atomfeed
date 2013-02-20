package org.ict4h.atomfeed.integration.motech.openmrs.listener;

import org.ict4h.atomfeed.integration.motech.openmrs.mapper.EventMapper;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.pillreminder.api.service.PillReminderService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;

@Component
public class PillRegimen {
    public static final String EVENT_FROM_OPEN_MRS = "eventFromOpenMRS";
    private final PillReminderService pillReminderService;
    private final MotechSchedulerService schedulerService;
    private String cronExpression;
    private EventMapper mapper;

    public PillRegimen(PillReminderService pillReminderService, MotechSchedulerService motechSchedulerService, String cronExpression, EventMapper mapper) {
        this.pillReminderService = pillReminderService;
        this.schedulerService = motechSchedulerService;
        this.cronExpression = cronExpression;
        this.mapper = mapper;
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
    public void create(MotechEvent motechEvent) throws IOException {
        Map<String,Object> parameters = motechEvent.getParameters();
        String contents = (String) parameters.get("contents");
        System.out.println(String.format("before -  %s - contents",contents));
        contents = contents.replaceFirst("^<!\\[CDATA\\[","").replaceFirst("\\]\\]>$","");
        System.out.println(String.format("after -  %s - contents",contents));
        pillReminderService.createNew(mapper.toDailyPillRegimenRequest(contents));
        System.out.println("If you see this - go check the couch installation to view the created Pill Regimen");
    }

}
