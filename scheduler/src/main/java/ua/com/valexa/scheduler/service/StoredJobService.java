package ua.com.valexa.scheduler.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import ua.com.valexa.common.dto.scheduler.StoredJobDto;
import ua.com.valexa.scheduler.mapper.StoredJobMapper;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.repository.StoredJobRepository;
import ua.com.valexa.scheduler.repository.StoredStepRepository;

@Service
public class StoredJobService {

    @Autowired
    private StoredJobRepository storedJobRepository;

    @Autowired
    private StoredStepRepository storedStepRepository;

    @Autowired
    StoredJobMapper storedJobMapper;

    public Flux<StoredJobDto> findAllFilled() {
        return fillSteps(findAll()).map(storedJobMapper::toDto);
    }

    public Flux<StoredJob> findAll() {
        return storedJobRepository.findAll();

    }

    public Flux<StoredJob> fillSteps(Flux<StoredJob> storedJobFlux) {
        return storedJobFlux.publishOn(Schedulers.boundedElastic()).map(storedJob -> {
            storedJob.setSteps(storedStepRepository.findByStoredJobId(storedJob.getId()).collectList().block());
            return storedJob;
        });
    }

}
