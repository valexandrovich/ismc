package ua.com.valexa.scheduler.service;

import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    public void initStoredJob(Long id) {
        System.out.println("initing job with id: " + id);
    }

}
