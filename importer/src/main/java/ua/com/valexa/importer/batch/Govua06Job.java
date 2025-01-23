package ua.com.valexa.importer.batch;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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
import ua.com.valexa.afscommon.dto.red.govua.Govua06Dto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.Govua06Mapper;
import ua.com.valexa.importer.model.Govua06;

import javax.sql.DataSource;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@Setter
public class Govua06Job {

    private static final Logger log = LoggerFactory.getLogger(Govua06Job.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<Govua06Dto> govUa06reader;

    private final ItemProcessor<Govua06Dto, Govua06> govUa06processor;

    private final JdbcBatchItemWriter<Govua06> govUa06writer;

    public final ItemWriteListener<Govua06> govUa06writerListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public Govua06Job(RabbitTemplate rabbitTemplate, String getQueueCpmsStepUpdate, JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, ItemReader<Govua06Dto> govUa06reader, ItemProcessor<Govua06Dto, Govua06> govUa06processor, JdbcBatchItemWriter<Govua06> govUa06writer, ItemWriteListener<Govua06> govUa06writerListener, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.govUa06reader = govUa06reader;
        this.govUa06processor = govUa06processor;
        this.govUa06writer = govUa06writer;
        this.govUa06writerListener = govUa06writerListener;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Bean
    public TaskletStep govUa06unzipStep() {
        return new StepBuilder("govUa06unzipStep", jobRepository)
                .tasklet(unzipFile(), transactionManager)
                .build();
    }

    public Tasklet unzipFile() {
        return (contribution, chunkContext) -> {
            String filePathStr = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("file");
            File file = new File(filePathStr);
            stepUpdateDto.setComment("Розпаковка файла: " + filePathStr);
            sendStepUpdate(stepUpdateDto);
            String csvFullPath = filePathStr.replaceAll("\\.zip$", ".csv");
            String extractedFilePath = null;
            try (ZipFile zipFile = new ZipFile(file, "CP1251")) {
                Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
                while (entries.hasMoreElements()) {
                    ZipArchiveEntry entry = entries.nextElement();
                    File newFile = new File(csvFullPath);
                    if (entry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        try (InputStream inputStream = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(newFile)) {
                            IOUtils.copy(inputStream, fos);
                        }
                        extractedFilePath = newFile.getAbsolutePath();
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                throw e;
            }
            stepUpdateDto.setComment("Файл розпаковано");
            sendStepUpdate(stepUpdateDto);
            chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .put("extractedFile", extractedFilePath);
            return RepeatStatus.FINISHED;
        };
    }

    public TaskletStep govUa06importStep() {
        return new StepBuilder("govUa06importStep", jobRepository)
                .<Govua06Dto, Govua06>chunk(1000, transactionManager)
                .reader(govUa06reader)
                .processor(govUa06processor)
                .writer(govUa06writer)
                .listener(govUa06writerListener)
                .listener(govua06importStepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa06skipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Govua06Dto> govUa06reader(
            @Value("#{jobExecutionContext['extractedFile']}") String file
    ) {
        try {
            return new FlatFileItemReaderBuilder<Govua06Dto>()
                    .resource(new FileUrlResource(file))
                    .name("govUa06reader")
                    .encoding("cp1251")
                    .delimited()
                    .delimiter(",")
                    .names("debtor_name", "debtor_birthdate", "debtor_code", "creditor_name", "creditor_code", "vp_ordernum", "vp_begindate", "vp_state", "org_name", "dvs_code", "phone_num", "email_addr", "bank_account")
                    .targetType(Govua06Dto.class)
                    .linesToSkip(skipLines)
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @StepScope
    public ItemProcessor<Govua06Dto, Govua06> govUa06processor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {
            @Autowired
            Govua06Mapper mapper;

            @Override
            public Govua06 process(Govua06Dto item) throws Exception {
                Govua06 result = mapper.mapToEntity(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Govua06> govUa06writer(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<Govua06>()
                .sql("insert into red.govua_06 (" +
                        "hash, create_date, update_date,  create_revision_id, update_revision_id,  debtor_name, debtor_birthdate, debtor_code, creditor_name, creditor_code, vp_ordernum, vp_begindate, vp_state, org_name, dvs_code, phone_num, email_addr, bank_account) " +
                        "VALUES (:hash, :createDate, :updateDate,  :createRevisionId, :updateRevisionId,  :debtorName, :debtorBirthdate, :debtorCode, :creditorName, :creditorCode, :vpOrdernum, :vpBegindate, :vpState, :orgName, :dvsCode, :phoneNum, :emailAddr, :bankAccount) " +
                        " ON CONFLICT (hash) DO UPDATE SET update_date = now(), update_revision_id = :createRevisionId")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriteListener<Govua06> govUa06writerListener(@Value("#{jobParameters['stepId']}") String stepId) {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends Govua06> items) {
                handledRowsCount += items.size();
                long divident = handledRowsCount + errorsCount;
                double progress = (double) divident / totalRowsCount;
                String comment = "Всього записів - " + totalRowsCount + "; Оброблено - " + handledRowsCount + "; Помилок - " + errorsCount + ";";
                stepUpdateDto.setComment(comment);
                stepUpdateDto.setProgress(progress);
                sendStepUpdate(stepUpdateDto);
            }


            @Override
            public void onWriteError(Exception exception, Chunk<? extends Govua06> items) {
                log.error(exception.toString());
            }
        };


    }

    @Bean
    StepExecutionListener govua06importStepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                String inputFilePath = stepExecution.getJobExecution().getExecutionContext().getString("extractedFile");
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
    @StepScope
    public SkipPolicy govuUa06skipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                log.error(t.toString());
                errorsCount++;
            }
            return true;
        };
    }

    @Bean
    public Step govUa06disablingStep() {
        return new StepBuilder("govUa06disabledStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.govua_06 SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa06newCountStep() {
        return new StepBuilder("govUa06newCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_06 where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public Step govUa06updateCountStep() {
        return new StepBuilder("govUa06updateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_06 where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step govUa06disableCountStep() {
        return new StepBuilder("govUa06disableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.govua_06 where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean("govUa06job")
    public Job govUa06job(JobRepository jobRepository) {
        return new JobBuilder("govUa06job", jobRepository)
                .start(govUa06unzipStep())
                .next(govUa06importStep())
                .next(govUa06disablingStep())
                .next(govUa06newCountStep())
                .next(govUa06updateCountStep())
                .next(govUa06disableCountStep())
                .listener(govUa06jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa06jobExecutionListener() {
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
