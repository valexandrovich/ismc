package ua.com.valexa.importer.batch;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.dto.red.govua.Govua10Dto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.Govua10Mapper;
import ua.com.valexa.importer.model.Govua10;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@Setter
public class Govua10Job {

    private static final Logger log = LoggerFactory.getLogger(Govua10Job.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<Govua10Dto> GovUa10reader;

    private final ItemProcessor<Govua10Dto, Govua10> GovUa10processor;

    private final JdbcBatchItemWriter<Govua10> GovUa10writer;

    public final ItemWriteListener<Govua10> govUa10writerListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public Govua10Job(String getQueueCpmsStepUpdate, JobRepository jobRepository, PlatformTransactionManager transactionManager, NamedParameterJdbcTemplate namedParameterJdbcTemplate, RabbitTemplate rabbitTemplate, DataSource dataSource, ItemReader<Govua10Dto> GovUa10reader, ItemProcessor<Govua10Dto, Govua10> GovUa10processor, JdbcBatchItemWriter<Govua10> GovUa10writer, ItemWriteListener<Govua10> govUa10writerListener) {
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.dataSource = dataSource;
        this.GovUa10reader = GovUa10reader;
        this.GovUa10processor = GovUa10processor;
        this.GovUa10writer = GovUa10writer;
        this.govUa10writerListener = govUa10writerListener;
    }


    @Bean
    @StepScope
    public FlatFileItemReader<Govua10Dto> govUa10reader(
            @Value("#{jobParameters['file']}") String file
    ) {
        try {
            return new FlatFileItemReaderBuilder<Govua10Dto>()
                    .resource(new FileUrlResource(file))
                    .name("govUa10reader")
                    .delimited()
                    .delimiter(";")
                    .names("nn", "status", "series", "number", "date_edit")
                    .targetType(Govua10Dto.class)
                    .linesToSkip(skipLines)
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Bean
    @StepScope
    public ItemProcessor<Govua10Dto, Govua10> govUa10processor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {
            @Autowired
            Govua10Mapper mapper;

            @Override
            public Govua10 process(Govua10Dto item) {
                Govua10 result = mapper.mapToEntity(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Govua10> govUa10writer(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<Govua10>()
                .sql("insert into red.govua_10 (" +
                        "hash, create_date, update_date,  create_revision_id, update_revision_id, nn, status, series, number, date_edit) " +
                        "VALUES (:hash, :createDate, :updateDate,  :createRevisionId, :updateRevisionId,  :nn, :status, :series, :number, :dateEdit) " +
                        " ON CONFLICT (hash) DO UPDATE SET update_date = now(), update_revision_id = :createRevisionId")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriteListener<Govua10> govUa10writerListener(
            @Value("#{jobParameters['stepId']}") String stepId) {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends Govua10> items) {
                handledRowsCount += items.size();
                long divident = handledRowsCount + errorsCount;
                double progress = (double) divident / totalRowsCount;
                String comment = "Всього записів - " + totalRowsCount + "; Оброблено - " + handledRowsCount + "; Помилок - " + errorsCount + ";";
                stepUpdateDto.setComment(comment);
                stepUpdateDto.setProgress(progress);
                sendStepUpdate(stepUpdateDto);
            }
        };
    }

    public TaskletStep govUa10importStep() {
        return new StepBuilder("govUa10importStep", jobRepository)
                .<Govua10Dto, Govua10>chunk(1000, transactionManager)
                .reader(GovUa10reader)
                .processor(GovUa10processor)
                .writer(GovUa10writer)
                .listener(govUa10writerListener)
                .listener(govua10importStepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa10skipPolicy())
                .build();
    }

    @Bean
    StepExecutionListener govua10importStepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                String inputFilePath = stepExecution.getJobExecution().getJobParameters().getString("file");
                stepUpdateDto.setComment("Аналіз файлу");
                sendStepUpdate(stepUpdateDto);
                File inputFile = new File(inputFilePath);
                try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                    while (br.readLine() != null) {
                        totalRowsCount++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                totalRowsCount = totalRowsCount - skipLines;
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return ExitStatus.COMPLETED;
            }
        };
    }

    @Bean
    public Step govUa10newCountStep() {
        return new StepBuilder("govUa10newCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);

                    String sql = "select count(*) from red.govua_10 where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);

                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa10updateCountStep() {
        return new StepBuilder("govUa10updateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);

                    String sql = "select count(*) from red.govua_10 where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);

                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa10disableCountStep() {
        return new StepBuilder("govUa10disableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_10 where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa10disablingStep() {
        return new StepBuilder("govUa10disabledStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.govua_10 SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public SkipPolicy govuUa10skipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                errorsCount++;
            }
            return true;
        };
    }

    @Bean("govUa10job")
    public Job govUa10job(JobRepository jobRepository) {
        return new JobBuilder("govUa10job", jobRepository)
                .start(govUa10importStep())
                .next(govUa10disablingStep())
                .next(govUa10newCountStep())
                .next(govUa10updateCountStep())
                .next(govUa10disableCountStep())
                .listener(govUa10jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa10jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                handledRowsCount = 0;
                errorsCount = 0;
                totalRowsCount = 0;
                Long stepId = Long.valueOf(jobExecution.getJobParameters().getString("stepId"));
                stepUpdateDto.setStepId(Long.valueOf(stepId));
                stepUpdateDto.setComment("Початок імпорту");
                stepUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
                stepUpdateDto.setProgress(0.0);
                sendStepUpdate(stepUpdateDto);
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                stepUpdateDto.setStepId(Long.valueOf(jobExecution.getJobParameters().getString("stepId")));
                if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
                    String comment =
                            "Імпорт завершено. Нових записів - " + jobExecution.getExecutionContext().get("newCount")
                                    + "; Дублікатів - " + jobExecution.getExecutionContext().get("updateCount")
                                    + "; Деактивованих записів - " + jobExecution.getExecutionContext().get("disableCount") + ";";
                    stepUpdateDto.setComment(comment);
                    stepUpdateDto.setProgress(1.0);
                    sendStepUpdate(stepUpdateDto);
                } else {
                    stepUpdateDto.setComment(jobExecution.getExitStatus().getExitDescription());
                    stepUpdateDto.setStatus(TaskStatus.FAILED);
                }
                sendStepUpdate(stepUpdateDto);
            }
        };
    }

    private void sendStepUpdate(StepUpdateDto stepUpdateDto) {
        log.debug("Step ID: {}; Sending StepUpdate: {}", stepUpdateDto.getStepId(), stepUpdateDto);
        rabbitTemplate.convertAndSend(getQueueCpmsStepUpdate, stepUpdateDto);
    }
}
