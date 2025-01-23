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
import ua.com.valexa.afscommon.dto.red.govua.Govua09Dto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.Govua09Mapper;
import ua.com.valexa.importer.model.Govua09;
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
public class Govua09Job {

    private static final Logger log = LoggerFactory.getLogger(Govua09Job.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<Govua09Dto> govUa09reader;

    private final ItemProcessor<Govua09Dto, Govua09> govUa09processor;

    private final JdbcBatchItemWriter<Govua09> govUa09writer;

    public final ItemWriteListener<Govua09> govUa09writerListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public Govua09Job(RabbitTemplate rabbitTemplate, String getQueueCpmsStepUpdate, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, ItemReader<Govua09Dto> govUa09reader, ItemProcessor<Govua09Dto, Govua09> govUa09processor, JdbcBatchItemWriter<Govua09> govUa09writer, ItemWriteListener<Govua09> govUa09writerListener, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.govUa09reader = govUa09reader;
        this.govUa09processor = govUa09processor;
        this.govUa09writer = govUa09writer;
        this.govUa09writerListener = govUa09writerListener;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }



    public TaskletStep govua09importStep() {
        return new StepBuilder("govua09importStep", jobRepository)
                .<Govua09Dto, Govua09>chunk(1000, transactionManager)
                .reader(govUa09reader)
                .processor(govUa09processor)
                .writer(govUa09writer)
                .listener(govUa09writerListener)
                .listener(govua09importStepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa09skipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public JsonStreamingItemReader<Govua09Dto> govua09Reader(
            @Value("#{jobParameters['file']}") String filePath
    ) throws Exception {
        return new JsonStreamingItemReader<>(filePath, Govua09Dto.class);
    }

    @Bean
    @StepScope
    public ItemProcessor<Govua09Dto, Govua09> govua09processor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {

            @Autowired
            Govua09Mapper mapper;

            @Override
            public Govua09 process(Govua09Dto item) {
                Govua09 result = mapper.mapToEntity(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Govua09> govua09writer(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<Govua09>()
                .sql("insert into red.govua_09 (" +
                        "hash, create_date, update_date,  create_revision_id, update_revision_id, id, ovd, category, first_name_ua, last_name_ua, patronymic_name_ua, first_name_ru, last_name_ru, patronymic_name_ru, first_name_en, last_name_en, patronymic_name_en, birthday, sex, lost_date, lost_place, article_crim, restraint, contact, photoid) " +
                        "VALUES (:hash, :createDate, :updateDate,  :createRevisionId, :updateRevisionId,  :id, :ovd, :category, :firstNameUa, :lastNameUa, :patronymicNameUa, :firstNameRu, :lastNameRu, :patronymicNameRu, :firstNameEn, :lastNameEn, :patronymicNameEn, :birthday, :sex, :lostDate, :lostPlace, :articleCrim, :restraint, :contact, :photoid ) " +
                        " ON CONFLICT (hash) DO UPDATE SET update_date = now(), update_revision_id = :createRevisionId")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }


    @Bean
    @StepScope
    public ItemWriteListener<Govua09> govua09writerListener(
            @Value("#{jobParameters['stepId']}") String stepId)
    {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends Govua09> items) {
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
    StepExecutionListener govua09importStepExecutionListener(){
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
    public SkipPolicy govuUa09skipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                log.error(t.toString());
                errorsCount++;
            }
            return true;
        };
    }

    @Bean
    public Step govUa09disablingStep() {
        return new StepBuilder("govUa09disabledStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.govua_09 SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }



    @Bean
    public Step govUa09newCountStep() {
        return new StepBuilder("govUa09newCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_09 where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public Step govUa09updateCountStep() {
        return new StepBuilder("govUa09updateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_09 where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa09disableCountStep() {
        return new StepBuilder("govUa09disableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_09 where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean("govUa09job")
    public Job govUa09job(JobRepository jobRepository) {
        return new JobBuilder("govUa09job", jobRepository)
                .start(govua09importStep())
                .next(govUa09disablingStep())
                .next(govUa09newCountStep())
                .next(govUa09updateCountStep())
                .next(govUa09disableCountStep())
                .listener(govUa09jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa09jobExecutionListener() {
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
