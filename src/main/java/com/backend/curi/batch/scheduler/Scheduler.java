package com.backend.curi.batch.scheduler;


import com.backend.curi.batch.job.AlertJobConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final JobLauncher jobLauncher;
    private final AlertJobConfiguration jobConfiguration;
    @Scheduled(cron = "${spring.scheduler.alert.cron}")
    public void runJob() throws Exception{
        JobParameters parameters = new JobParametersBuilder()
                .addDate("startTime", new Date())
                .toJobParameters();
        
        jobLauncher.run(jobConfiguration.sequenceAlertJob(), parameters);
    }
}
