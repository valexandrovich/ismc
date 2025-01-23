package ua.com.valexa.cpms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.valexa.cpms.model.Job;
import ua.com.valexa.cpms.repository.JobRepository;

import java.security.cert.PKIXReason;
import java.util.List;

@RestController
public class    CpmsController {

    final  JobRepository jobRepository;

    public CpmsController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

}
