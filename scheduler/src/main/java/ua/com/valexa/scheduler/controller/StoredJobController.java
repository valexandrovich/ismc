package ua.com.valexa.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.service.SchedulerService;
import ua.com.valexa.scheduler.service.StoredJobService;

@RestController
@RequestMapping("/stored-job")
public class StoredJobController {

    @Autowired
    private StoredJobService storedJobService;

    @Autowired
    private SchedulerService schedulerService;

    @GetMapping
    public Flux<StoredJob> findAll() {
        return storedJobService.fetchAll();
    }

    @PostMapping("/init/{storedJobId}")
    public void initStoredJob(
            @PathVariable Long storedJobId
    ) {
        schedulerService.initStoredJob(storedJobId);
    }

}
