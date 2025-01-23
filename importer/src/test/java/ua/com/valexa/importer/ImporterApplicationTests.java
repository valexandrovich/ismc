package ua.com.valexa.importer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.importer.model.Govua06;
import ua.com.valexa.importer.repository.Govua06Repository;
import ua.com.valexa.importer.service.govua.Govua01Importer;
import ua.com.valexa.importer.service.govua.Govua06Importer;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class ImporterApplicationTests {

    @Autowired
    Govua06Importer govua06Importer;

    @Autowired
    Govua06Repository govua06Repository;

    @Test
    void contextLoads() {


        Optional<Govua06> e = govua06Repository.findById(UUID.fromString("f1b02b85-091b-322a-9e76-e94ed49171d3"));
        System.out.println(e);

//        StepRequestDto stepRequestDto = new StepRequestDto();
//        stepRequestDto.setStepId(777L);
//        stepRequestDto.setWorker("govua06");
//        stepRequestDto.getParameters().put("jobId", "77");
//        stepRequestDto.getParameters().put("file", "D:\\AFS_STORAGE\\28-ex_csv_asvp.zip");
//
//
//        govua06Importer.handleStepRequest(stepRequestDto);


    }

}
