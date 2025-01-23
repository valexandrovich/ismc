package ua.com.valexa.importer.batch;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.dto.red.govua.Govua12Dto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.Govua12Mapper;
import ua.com.valexa.importer.model.Govua12;
import ua.com.valexa.importer.service.JsonStreamingItemReader;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@Setter
public class Govua12Job {

    private static final Logger log = LoggerFactory.getLogger(Govua12Job.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<Govua12Dto> govUa12reader;

    private final ItemProcessor<Govua12Dto, Govua12> govUa12processor;

    private final JdbcBatchItemWriter<Govua12> govUa12writer;

    public final ItemWriteListener<Govua12> govUa12writerListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public Govua12Job(RabbitTemplate rabbitTemplate, String getQueueCpmsStepUpdate, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, ItemReader<Govua12Dto> govUa12reader, ItemProcessor<Govua12Dto, Govua12> govUa12processor, JdbcBatchItemWriter<Govua12> govUa12writer, ItemWriteListener<Govua12> govUa12writerListener, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.govUa12reader = govUa12reader;
        this.govUa12processor = govUa12processor;
        this.govUa12writer = govUa12writer;
        this.govUa12writerListener = govUa12writerListener;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }



    public TaskletStep govua12importStep() {
        return new StepBuilder("govua12importStep", jobRepository)
                .<Govua12Dto, Govua12>chunk(1000, transactionManager)
                .reader(govUa12reader)
                .processor(govUa12processor)
                .writer(govUa12writer)
                .listener(govUa12writerListener)
                .listener(govua12importStepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa12skipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public JsonStreamingItemReader<Govua12Dto> govua12Reader(
            @Value("#{jobParameters['file']}") String filePath
    ) throws Exception {
        return new JsonStreamingItemReader<>(filePath, Govua12Dto.class);
    }

    @Bean
    @StepScope
    public ItemProcessor<Govua12Dto, Govua12> govua12processor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {

            @Autowired
            Govua12Mapper mapper;

            @Override
            public Govua12 process(Govua12Dto item) {
                Govua12 result = mapper.mapToEntity(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Govua12> govua12writer(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<Govua12>()
                .sql("insert into red.govua_12 (" +
                        "hash, create_date, update_date,  create_revision_id, update_revision_id, id, ovd, series, number, type, status, theft_date, insert_date) " +
                        "VALUES (:hash, :createDate, :updateDate,  :createRevisionId, :updateRevisionId,  :id, :ovd, :series, :number, :type, :status, :theftDate, :insertDate) " +
                        " ON CONFLICT (hash) DO UPDATE SET update_date = now(), update_revision_id = :createRevisionId")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }


    @Bean
    @StepScope
    public ItemWriteListener<Govua12> govua12writerListener(
            @Value("#{jobParameters['stepId']}") String stepId)
    {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends Govua12> items) {
                handledRowsCount += items.size();
                long divident = handledRowsCount  + errorsCount;
                double progress = (double)  divident / totalRowsCount;
                String comment = "Всього записів - " + totalRowsCount + "; Оброблено - " + handledRowsCount + "; Помилок - " +errorsCount+ ";";
                stepUpdateDto.setComment(comment);
                stepUpdateDto.setProgress(progress);
                sendStepUpdate(stepUpdateDto);
            }
        };
    }


    @Bean
    StepExecutionListener govua12importStepExecutionListener(){
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {

                String inputFilePath = stepExecution.getJobExecution().getJobParameters().getString("file");
                Long stepId = Long.valueOf(stepExecution.getJobExecution().getJobParameters().getString("stepId"));
                stepUpdateDto.setComment("Аналіз файлу");
                sendStepUpdate(stepUpdateDto);
                File inputFile = new File(inputFilePath);
                int totalObjectsCount = 0;
                try (JsonParser jsonParser = new JsonFactory().createParser(inputFile)) {
                    if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                totalRowsCount++;
                                jsonParser.skipChildren();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("File not found: " + inputFilePath, e);
                } catch (IOException e) {
                    throw new RuntimeException("Error reading JSON file: " + inputFilePath, e);
                }
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return ExitStatus.COMPLETED;
            }
        };
    }


    @Bean
    @StepScope
    public SkipPolicy govuUa12skipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                log.error(t.toString());
                errorsCount++;
            }
            return true;
        };
    }

    @Bean
    public Step govUa12disablingStep() {
        return new StepBuilder("govUa12disabledStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.govua_12 SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }



    @Bean
    public Step govUa12newCountStep() {
        return new StepBuilder("govUa12newCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_12 where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public Step govUa12updateCountStep() {
        return new StepBuilder("govUa12updateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_12 where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa12disableCountStep() {
        return new StepBuilder("govUa12disableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_12 where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean("govUa12job")
    public Job govUa12job(JobRepository jobRepository) {
        return new JobBuilder("govUa12job", jobRepository)
                .start(govua12importStep())
                .next(govUa12disablingStep())
                .next(govUa12newCountStep())
                .next(govUa12updateCountStep())
                .next(govUa12disableCountStep())
                .listener(govUa12jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa12jobExecutionListener() {
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
