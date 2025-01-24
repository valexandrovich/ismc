package ua.com.valexa.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.model.StoredStep;
import ua.com.valexa.scheduler.repository.StoredJobRepository;
import ua.com.valexa.scheduler.repository.StoredStepParameterRepository;
import ua.com.valexa.scheduler.repository.StoredStepRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

@Service
public class StoredJobService {

    @Autowired
    private StoredJobRepository storedJobRepository;

    @Autowired
    private StoredStepRepository storedStepRepository;
    @Autowired
    private StoredStepParameterRepository storedStepParameterRepository;

    public Flux<StoredJob> fetchAll() {
        return storedJobRepository.findAll()
                .flatMap(this::fillStoredSteps);
    }

    public Mono<StoredJob> fillStoredSteps(StoredJob storedJob) {
        return storedStepRepository
                .findByStoredJobId(storedJob.getId())
                .flatMap(this::fillStoredStepParameters)
                .collectList()
                .map(storedSteps -> {
                    storedSteps.sort(Comparator.comparingInt(StoredStep::getStepOrder));
                    storedJob.setSteps(new ArrayList<>(storedSteps));
                    return storedJob;
                });
    }
    
    

    public Mono<StoredStep> fillStoredStepParameters(StoredStep storedStep) {
        return storedStepParameterRepository
                .findByStoredStepId(storedStep.getId())
                .collectList()
                .map(
                        storedStepParameters -> {
                            storedStep.setParameters(new HashSet<>(storedStepParameters));
                            return storedStep;
                        }
                );
    }

}
