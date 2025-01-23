package ua.com.valexa.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.repository.StoredJobRepository;
import ua.com.valexa.scheduler.service.SchedulerService;

import java.util.List;

@RestController
public class SchedulerController {

    @Autowired
    StoredJobRepository storedJobRepository;

    @Autowired
    SchedulerService schedulerService;

    @PostMapping("/stored-job")
    public void initStoredJob(@RequestParam Long id, @RequestParam String initiatorName){
        schedulerService.initStoredJob(id, initiatorName);
    }

    @GetMapping("/stored-job")
    public List<StoredJob> getStoredJobs(){
        return storedJobRepository.findAll();
    }

}
