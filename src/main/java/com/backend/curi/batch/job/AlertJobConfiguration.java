package com.backend.curi.batch.job;

import com.backend.curi.dashboard.repository.OverdueAlertRepository;
import com.backend.curi.dashboard.repository.entity.OverdueAlert;
import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.repository.LaunchedSequenceRepository;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.service.LaunchedSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class AlertJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final OverdueAlertRepository overdueAlertRepository;
    private final LaunchedSequenceRepository launchedSequenceRepository;
    @Value("${batch.chunkSize:100}")
    private int chunkSize;
    @Bean
    public JobParametersIncrementer incrementer() {
        return new RunIdIncrementer();
    }
    @Bean
    public Job sequenceAlertJob() {
        return jobBuilderFactory.get("sequenceAlertJob")
                .incrementer(incrementer())
                .start(sequenceAlertStep())
                .build();
    }
    @Bean
    public Step sequenceAlertStep() {
        return stepBuilderFactory.get("sequenceAlertStep")
                .<LaunchedSequence, OverdueAlert> chunk(chunkSize)
                .reader(sequenceAlertReader(null))
                .processor(sequenceAlertProcessor())
                .writer(sequenceAlertWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<LaunchedSequence> sequenceAlertReader(@Value("#{jobParameters['startTime']}") Date startTime) {
        return new IteratorItemReader<>(launchedSequenceRepository.findAllByStatus(LaunchedStatus.IN_PROGRESS));
    }

    @Bean
    public ItemProcessor<LaunchedSequence, OverdueAlert> sequenceAlertProcessor() {
        return sequence -> {
            if (sequence.getApplyDate().plusDays(1).isBefore(LocalDate.now())) {
                sequence.setStatus(LaunchedStatus.OVERDUE);
                return OverdueAlert.of(sequence);
            }
            return null;
        };
    }

    @Bean
    public ItemWriter<OverdueAlert> sequenceAlertWriter() {
        return overdueAlertRepository::saveAll;
    }
}
