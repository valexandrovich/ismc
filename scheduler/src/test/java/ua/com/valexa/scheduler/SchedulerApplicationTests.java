package ua.com.valexa.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.model.StoredStep;
import ua.com.valexa.scheduler.repository.StoredJobRepository;
import ua.com.valexa.scheduler.repository.StoredStepRepository;

@SpringBootTest
class SchedulerApplicationTests {

    @Autowired
    StoredStepRepository storedStepRepository;

    @Autowired
    StoredJobRepository storedJobRepository;

    @Test
    void govua01() {
        StoredJob sj = new StoredJob();
        sj.setShortName("govua_01");
        sj.setName("Відомості про справи про банкрутство");
        sj.setSource("Державна судова адміністрація України");
        sj.setIsEnabled(true);
        sj = storedJobRepository.save(sj);

        StoredStep s = new StoredStep();
        s.setService("downloader");
        s.setWorker("govua01");
        s.setIsEnabled(true);
        s.setIsSkippable(false);
        s.setStepOrder(1);

        s.getParameters().put("packageId", "vidomosti-pro-spravi-pro-bankrutstvo-1");
        s.getParameters().put("retries", "10");
        s.getParameters().put("timeoutSec", "30");
        s.setStoredJob(sj);
        s = storedStepRepository.save(s);

        StoredStep s1 = new StoredStep();
        s1.setService("importer");
        s1.setWorker("govua01");
        s1.setIsEnabled(true);
        s1.setIsSkippable(false);
        s1.setStepOrder(2);
        s1.setStoredJob(sj);
        s1 = storedStepRepository.save(s1);

    }

}
