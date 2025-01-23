package ua.com.valexa.scheduler;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import reactor.core.publisher.Flux;
import ua.com.valexa.common.dto.scheduler.StoredJobDto;
import ua.com.valexa.scheduler.mapper.StoredJobMapper;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.service.StoredJobService;

@SpringBootTest
class SchedulerApplicationTests {

    @Autowired
    private StoredJobService storedJobService;

    @Autowired
    StoredJobMapper storedJobMapper;


    @Test
    void contextLoads() {
        Flux<StoredJob> sj = storedJobService.findAll();
        Flux<StoredJob> storedJobFilled = storedJobService.fillSteps(sj);
        Flux<StoredJobDto> storedJobDtoFlux = storedJobFilled.map(storedJobMapper::toDto);
        System.out.println("x");

    }

}
