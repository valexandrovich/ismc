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
import ua.com.valexa.afscommon.dto.red.govua.Govua08Dto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.Govua08Mapper;
import ua.com.valexa.importer.model.Govua08;
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
public class Govua08Job {

    private static final Logger log = LoggerFactory.getLogger(Govua08Job.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<Govua08Dto> govUa08reader;

    private final ItemProcessor<Govua08Dto, Govua08> govUa08processor;

    private final JdbcBatchItemWriter<Govua08> govUa08writer;

    public final ItemWriteListener<Govua08> govUa08writerListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public Govua08Job(RabbitTemplate rabbitTemplate, String getQueueCpmsStepUpdate, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, ItemReader<Govua08Dto> govUa08reader, ItemProcessor<Govua08Dto, Govua08> govUa08processor, JdbcBatchItemWriter<Govua08> govUa08writer, ItemWriteListener<Govua08> govUa08writerListener, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.govUa08reader = govUa08reader;
        this.govUa08processor = govUa08processor;
        this.govUa08writer = govUa08writer;
        this.govUa08writerListener = govUa08writerListener;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }



    public TaskletStep govua08importStep() {
        return new StepBuilder("govua08importStep", jobRepository)
                .<Govua08Dto, Govua08>chunk(1000, transactionManager)
                .reader(govUa08reader)
                .processor(govUa08processor)
                .writer(govUa08writer)
                .listener(govUa08writerListener)
                .listener(govua08importStepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa08skipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public JsonStreamingItemReader<Govua08Dto> govua08Reader(
            @Value("#{jobParameters['file']}") String filePath
    ) throws Exception {
        return new JsonStreamingItemReader<>(filePath, Govua08Dto.class);
    }

    @Bean
    @StepScope
    public ItemProcessor<Govua08Dto, Govua08> govua08processor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {

            @Autowired
            Govua08Mapper mapper;

            @Override
            public Govua08 process(Govua08Dto item) {
                Govua08 result = mapper.mapToEntity(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Govua08> govua08writer(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<Govua08>()
                .sql("insert into red.govua_08 (" +
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
    public ItemWriteListener<Govua08> govua08writerListener(
            @Value("#{jobParameters['stepId']}") String stepId)
    {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends Govua08> items) {
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
    StepExecutionListener govua08importStepExecutionListener(){
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
    public SkipPolicy govuUa08skipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                log.error(t.toString());
                errorsCount++;
            }
            return true;
        };
    }

    @Bean
    public Step govUa08disablingStep() {
        return new StepBuilder("govUa08disabledStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.govua_08 SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }



    @Bean
    public Step govUa08newCountStep() {
        return new StepBuilder("govUa08newCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_08 where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public Step govUa08updateCountStep() {
        return new StepBuilder("govUa08updateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_08 where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa08disableCountStep() {
        return new StepBuilder("govUa08disableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_08 where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean("govUa08job")
    public Job govUa08job(JobRepository jobRepository) {
        return new JobBuilder("govUa08job", jobRepository)
                .start(govua08importStep())
                .next(govUa08disablingStep())
                .next(govUa08newCountStep())
                .next(govUa08updateCountStep())
                .next(govUa08disableCountStep())
                .listener(govUa08jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa08jobExecutionListener() {
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
