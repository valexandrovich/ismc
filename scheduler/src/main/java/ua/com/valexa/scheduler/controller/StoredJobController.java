package ua.com.valexa.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ua.com.valexa.common.dto.scheduler.StoredJobDto;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.service.StoredJobService;

@RestController
@RequestMapping("/stored-job")
public class StoredJobController {

    @Autowired
    private StoredJobService storedJobService;

    @GetMapping
    public Flux<StoredJobDto> findAll() {
        return storedJobService.findAllFilled();
    }

}
