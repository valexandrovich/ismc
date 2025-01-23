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
import ua.com.valexa.afscommon.dto.uploader.UploaderPpRowDto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.mapper.UploadPpMapper;
import ua.com.valexa.importer.model.UploadPp;

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
public class UploadPpJob {

    private static final Logger log = LoggerFactory.getLogger(UploadPpJob.class);

    private StepUpdateDto stepUpdateDto = new StepUpdateDto();

    private final String getQueueCpmsStepUpdate;

    private final RabbitTemplate rabbitTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final DataSource dataSource;

    private final ItemReader<UploaderPpRowDto> uploadPpReader;

    private final ItemProcessor<UploaderPpRowDto, UploadPp> uploadPpProcessor;


    private final JdbcBatchItemWriter<UploadPp> uploadPpWriter;

    public final ItemWriteListener<UploadPp> uploadPpWriterListener;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;

    private long errorsCount;

    public UploadPpJob(String getQueueCpmsStepUpdate, RabbitTemplate rabbitTemplate, JobRepository jobRepository, PlatformTransactionManager transactionManager, NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource dataSource, ItemReader<UploaderPpRowDto> uploadPpReader, ItemProcessor<UploaderPpRowDto, UploadPp> uploadPpProcessor, JdbcBatchItemWriter<UploadPp> uploadPpWriter, ItemWriteListener<UploadPp> uploadPpWriterListener) {
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.rabbitTemplate = rabbitTemplate;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.dataSource = dataSource;
        this.uploadPpReader = uploadPpReader;
        this.uploadPpProcessor = uploadPpProcessor;
        this.uploadPpWriter = uploadPpWriter;
        this.uploadPpWriterListener = uploadPpWriterListener;
    }


    @Bean
    @StepScope
    public FlatFileItemReader<UploaderPpRowDto> uploadPpReader(
            @Value("#{jobParameters['file']}") String file
    ) {
        try {
            return new FlatFileItemReaderBuilder<UploaderPpRowDto>()
                    .resource(new FileUrlResource(file))
                    .name("uploadPpReader")
                    .delimited()
                    .delimiter(",")
                    .names(
                            "lastNameUa", 	"firstNameUa", 	"patronymicNameUa",
                            "lastNameRu", 	"firstNameRu", 	"patronymicNameRu",
                            "lastNameEn", 	"firstNameEn", 	"patronymicNameEn",
                            "birthday", 	"inn", 	"localPassSerial", 	"localPassNum", 	"localPassIssueDate", 	"localPassIssuer",
                            "idPassNumber", 	"idPassRecord", 	"idPassIssueDate", 	"idPassIssuerCode", 	"intPassSerial",
                            "intPassNumber", 	"intPassIssueDate", 	"intPassIssuerCode", 	"othPassName", 	"othPassNumber",
                            "othPassIssueDate", 	"othPassExpiredDate", 	"othPassIssuerName", 	"citizenship", 	"birthplace",
                            "sex", 	"phone", 	"email", 	"addressSimple", 	"addressZip", 	"addressCountry", 	"addressRegion",
                            "addressCounty", 	"addressCityType", 	"addressCityName", 	"addressStreetType", 	"addressStreetName",
                            "addressBuildingNo", 	"addressBuildingPart", 	"addressBuildingLetter", 	"addressApartment",
                            "comment", 	"markId", 	"markEventDate", 	"markStartDate", 	"markEndDate", 	"markTextValue",
                            "markNumberValue", 	"markComment", 	"source", 	"id"
                            )
                    .targetType(UploaderPpRowDto.class)
                    .linesToSkip(skipLines)
                    .build();
        } catch (MalformedURLException e) {

            System.out.println("EXCEPTION IN READER " + e.getMessage());

            throw new RuntimeException(e.getMessage());
        }
    }





    @Bean
    @StepScope
    public ItemProcessor<UploaderPpRowDto, UploadPp> uploadPpProcessor(
            @Value("#{jobParameters['stepId']}") String stepId,
            @Value("#{jobParameters['jobId']}") String jobId
    ) {
        return new ItemProcessor<>() {
            @Autowired
            UploadPpMapper mapper;

            @Override
            public UploadPp process(UploaderPpRowDto item) {


                UploadPp result = mapper.mapToEntity(item);



                System.out.println(item);
                result.setCreateRevisionId(Long.valueOf(jobId));
                result.setUpdateRevisionId(Long.valueOf(jobId));
                return result;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<UploadPp> uploadPpWriter(@Value("#{jobParameters['jobId']}") String jobId) {
        return new JdbcBatchItemWriterBuilder<UploadPp>()
                .sql(   "insert into red.upload_pp(hash, create_revision_id, update_revision_id,  create_date, update_date,  last_name_ua, first_name_ua, patronymic_name_ua, last_name_ru, first_name_ru, patronymic_name_ru, last_name_en, first_name_en, patronymic_name_en, birthday, inn, local_pass_serial, local_pass_num, local_pass_issue_date, local_pass_issuer, id_pass_number, id_pass_record, id_pass_issue_date, id_pass_issuer_code, int_pass_serial, int_pass_number, int_pass_issue_date, int_pass_issuer_code, oth_pass_name, oth_pass_number, oth_pass_issue_date, oth_pass_expired_date, oth_pass_issuer_name, citizenship, birthplace, sex, phone, email, address_simple, address_zip, address_country, address_region, address_county, address_city_type, address_city_name, address_street_type, address_street_name, address_building_no, address_building_part, address_building_letter, address_apartment, comment, mark_id, mark_event_date, mark_start_date, mark_end_date, mark_text_value, mark_number_value, mark_comment, source, id) " +
                        "values(:hash, :createRevisionId, :updateRevisionId, :createDate, :updateDate,  :lastNameUa, :firstNameUa, :patronymicNameUa, :lastNameRu, :firstNameRu, :patronymicNameRu, :lastNameEn, :firstNameEn, :patronymicNameEn, :birthday, :inn, :localPassSerial, :localPassNum, :localPassIssueDate, :localPassIssuer, :idPassNumber, :idPassRecord, :idPassIssueDate, :idPassIssuerCode, :intPassSerial, :intPassNumber, :intPassIssueDate, :intPassIssuerCode, :othPassName, :othPassNumber, :othPassIssueDate, :othPassExpiredDate, :othPassIssuerName, :citizenship, :birthplace, :sex, :phone, :email, :addressSimple, :addressZip, :addressCountry, :addressRegion, :addressCounty, :addressCityType, :addressCityName, :addressStreetType, :addressStreetName, :addressBuildingNo, :addressBuildingPart, :addressBuildingLetter, :addressApartment, :comment, :markId, :markEventDate, :markStartDate, :markEndDate, :markTextValue, :markNumberValue, :markComment, :source, :id) " +
                        "ON CONFLICT (hash) DO UPDATE SET update_date = now(), update_revision_id = :createRevisionId")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriteListener<UploadPp> uploadPpWriterListener(
            @Value("#{jobParameters['stepId']}") String stepId) {
        return new ItemWriteListener<>() {
            @Override
            public void afterWrite(Chunk<? extends UploadPp> items) {
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

    public TaskletStep uploadPpImportStep() {
        return new StepBuilder("uploadPpImportStep", jobRepository)
                .<UploaderPpRowDto, UploadPp>chunk(1000, transactionManager)
                .reader(uploadPpReader)
//                .processor(loggingProcessor)
                .processor(uploadPpProcessor)
                .writer(uploadPpWriter)
                .listener(uploadPpWriterListener)
                .listener(uploadPpImportStepExecutionListener())
                .faultTolerant()
                .skipPolicy(uploadPpskipPolicy())
                .build();
    }

    @Bean
    StepExecutionListener uploadPpImportStepExecutionListener() {
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
    public Step uploadPpNewCountStep() {
        return new StepBuilder("uploadPpNewCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);

                    String sql = "select count(*) from red.upload_pp where create_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);

                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("newCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step uploadPpUpdateCountStep() {
        return new StepBuilder("uploadPpUpdateCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);

                    String sql = "select count(*) from red.upload_pp where update_revision_id = :jobId and create_revision_id <> :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);

                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("updateCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step uploadPpDisableCountStep() {
        return new StepBuilder("uploadPpDisableCountStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    stepUpdateDto.setComment("Аналіз нових записів");
                    sendStepUpdate(stepUpdateDto);
                    String sql = "select count(*) from red.upload_pp where disable_revision_id = :jobId";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
                    chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("disableCount", 0);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step uploadPpDisablingStep() {
        return new StepBuilder("uploadPpDisablingStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Long jobId = Long.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getString("jobId"));
                    String sql = "UPDATE red.upload_pp SET disable_date = now(), disable_revision_id = :jobId where (create_revision_id <> :jobId or create_revision_id is null) and (update_revision_id<> :jobId or update_revision_id is null) and disable_revision_id is null;";
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("jobId", jobId);
                    int rowsAffected = namedParameterJdbcTemplate.update(sql, paramMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public SkipPolicy uploadPpskipPolicy() {
        return (t, skipCount) -> {
            if (!(t instanceof DuplicateKeyException)) {
                errorsCount++;
            }
            return true;
        };
    }

    @Bean("upload_pp_job")
    public Job uploadPpJob(JobRepository jobRepository) {
        return new JobBuilder("upload_pp_job", jobRepository)
                .start(uploadPpImportStep())
//                .next(uploadPpDisablingStep())
                .next(uploadPpNewCountStep())
                .next(uploadPpUpdateCountStep())
                .next(uploadPpDisableCountStep())
                .listener(uploadPpJobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener uploadPpJobExecutionListener() {
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
